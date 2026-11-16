# CampusRide

Plateforme de covoiturage réservée aux étudiants, conçue autour d'une
architecture microservices. CampusRide permet de publier et rechercher
des trajets, gérer les réservations et paiements, consulter les avis et
la réputation des utilisateurs, et échanger des notifications et
messages.

> **Projet universitaire --- Module Architecture Web Distribuée (AWD)**\
> Ce README décrit l'architecture et les choix techniques présentés dans
> le dossier technique CampusRide. Les commandes et paramètres exacts de
> lancement doivent être adaptés aux fichiers réellement présents dans
> le dépôt.

## Sommaire

-   [Objectifs](#objectifs)
-   [Architecture](#architecture)
-   [Microservices](#microservices)
-   [Technologies](#technologies)
-   [Communication entre services](#communication-entre-services)
-   [Sécurité](#sécurité)
-   [Ports](#ports)
-   [Lancement du projet](#lancement-du-projet)
-   [Principes de conception](#principes-de-conception)
-   [Équipe et répartition](#équipe-et-répartition)

## Objectifs

CampusRide est une plateforme de covoiturage destinée à la communauté
universitaire. Elle couvre les principaux besoins suivants :

-   inscription, vérification de l'adresse e-mail et gestion des profils
    ;
-   publication et recherche multicritère de trajets ;
-   réservation de places, paiement, annulation et remboursement ;
-   avis, notes, score de confiance et modération ;
-   notifications et messagerie entre conducteurs et passagers.

L'application web Angular et l'application mobile consomment la même
API, en passant par l'API Gateway.

## Architecture

Le système est organisé en cinq microservices métier indépendants et un
socle commun.

``` text
Applications Web (Angular) / Mobile
                 |
                 v
        API Gateway :8080
                 |
     +-----------+-----------+
     |           |           |
   Eureka     Keycloak   Config Server
   :8761       :8180        :8888
     |
     +---- Découverte des services
                 |
   +-------------+-----------------------------+
   |             |             |               |
 Users/Auth    Trips        Bookings       Reputation
   :8081       :8082          :8083           :8084
   |             |             |               |
PostgreSQL   PostgreSQL    PostgreSQL        MongoDB
                 |
          Notifications
              :8085
                |
              MySQL

       RabbitMQ — exchange covoiturage.events
       Passerelle de paiement externe
```

Le schéma est une vue logique simplifiée. Les communications synchrones
passent par REST/OpenFeign lorsqu'une réponse immédiate est nécessaire.
Les événements métier sont échangés de manière asynchrone via RabbitMQ.

### Socle commun

  -----------------------------------------------------------------------
  Composant                           Rôle
  ----------------------------------- -----------------------------------
  API Gateway                         Point d'entrée unique, routage
                                      `/api/v1/**`, validation du JWT et
                                      limitation de débit

  Eureka Server                       Registre des services ;
                                      enregistrement et découverte des
                                      instances

  Config Server                       Configuration centralisée,
                                      récupérée depuis un dépôt Git
                                      `config-repo`

  Keycloak                            Fournisseur d'identité et émission
                                      des JWT

  RabbitMQ                            Bus d'événements asynchrones,
                                      exchange `covoiturage.events`

  Spring Boot Actuator                Exposition de l'état de santé et
                                      des métriques des services
  -----------------------------------------------------------------------

## Microservices

  -------------------------------------------------------------------------------
  Microservice                      Port Base de données  Responsabilités
                                                          principales
  ---------------- --------------------- ---------------- -----------------------
  Utilisateurs et                 `8081` PostgreSQL       Comptes, inscription,
  Auth                                                    vérification e-mail,
                                                          profils, véhicules et
                                                          statut utilisateur

  Trajets                         `8082` PostgreSQL       Publication, recherche
                                                          multicritère, gestion
                                                          des places et cycle de
                                                          vie des trajets

  Réservations et                 `8083` PostgreSQL       Réservations,
  Paiements                                               paiements, annulations,
                                                          remboursements et saga
                                                          de réservation

  Réputation et                   `8084` MongoDB          Avis, notes, score de
  Modération                                              confiance, signalements
                                                          et sanctions

  Notifications et                `8085` MySQL            Notifications, modèles
  Messagerie                                              de messages,
                                                          conversations et chat
                                                          conducteur--passagers
  -------------------------------------------------------------------------------

**Règle de persistance :** chaque microservice possède ses propres
données. Aucun service ne lit directement la base de données d'un autre
service. Les échanges de données interservices passent par les API ou
les événements.

## Technologies

  Domaine                    Technologies
  -------------------------- ---------------------------------
  Backend                    Java, Spring Boot, Spring Cloud
  Appels synchrones          REST, Spring Cloud OpenFeign
  Résilience                 Resilience4j
  Documentation des API      OpenAPI / Swagger
  Communication asynchrone   RabbitMQ
  Bases de données           PostgreSQL, MongoDB, MySQL
  Sécurité                   Keycloak, JWT
  Frontend web               Angular
  Infrastructure             Docker, Docker Compose
  Gestion de projet          Git, GitHub, GitHub Projects

## Communication entre services

### Communication synchrone

REST et OpenFeign sont utilisés lorsqu'un service a besoin d'une réponse
immédiate d'un autre service. Les appels interservices documentés
comprennent notamment :

-   Trajets vers Utilisateurs ;
-   Réservations vers Trajets ;
-   Réputation vers Réservations ;
-   Notifications vers Utilisateurs.

Resilience4j contribue à gérer les erreurs et les indisponibilités des
appels synchrones.

### Communication asynchrone

RabbitMQ diffuse les événements métier via l'exchange :

``` text
covoiturage.events
```

Exemples d'événements mentionnés dans le dossier technique :

-   `user.registered`
-   `booking.created`
-   `booking.confirmed`
-   `booking.rejected`
-   `booking.cancelled`
-   `payment.completed`
-   `payment.failed`
-   `trip.cancelled`
-   `trip.completed`
-   `review.published`
-   `sanction.applied`
-   `user.blocked`

Les événements permettent notamment de découpler les services et de
coordonner les traitements distribués, dont la saga de réservation.

## Sécurité

-   L'inscription est réservée aux adresses e-mail universitaires.
-   Un compte reste `PENDING` tant que l'adresse e-mail n'a pas été
    vérifiée.
-   Keycloak authentifie l'utilisateur et signe le JWT.
-   Le client transmet le jeton dans l'en-tête
    `Authorization: Bearer <token>`.
-   L'API Gateway vérifie la signature et l'expiration du JWT et
    applique la limitation de débit.
-   Les rôles `USER` et `ADMIN` sont portés par le JWT.
-   Les services appliquent les règles d'autorisation métier.
-   Les données d'identité sont isolées dans le service Utilisateurs.
-   Les données de carte bancaire ne sont pas conservées par CampusRide
    : le paiement est délégué à une passerelle externe.

## Ports

  Composant                             Port
  --------------------------------- --------
  API Gateway                         `8080`
  Utilisateurs et Auth                `8081`
  Trajets                             `8082`
  Réservations et Paiements           `8083`
  Réputation et Modération            `8084`
  Notifications et Messagerie         `8085`
  Eureka Server                       `8761`
  Config Server                       `8888`
  Keycloak                            `8180`
  RabbitMQ (AMQP, valeur usuelle)     `5672`

Le port AMQP `5672` est une valeur usuelle de RabbitMQ ; vérifiez le
mapping réellement défini dans votre configuration Docker. Les ports
publiés pour les bases de données et l'interface d'administration
RabbitMQ/Keycloak doivent également être vérifiés dans les fichiers de
configuration du projet.

## Lancement du projet

Le dossier technique prévoit Docker et Docker Compose pour lancer
l'infrastructure et les services. Le dépôt doit contenir un fichier
Compose configuré pour que la commande ci-dessous soit applicable.

1.  Installer les outils nécessaires au projet : Docker et Docker
    Compose, ainsi que les outils de développement correspondant aux
    modules (Java/Spring Boot et Node.js/Angular).
2.  Récupérer le dépôt Git du projet.
3.  Vérifier les variables d'environnement, les identifiants locaux et
    les paramètres des bases de données. Ne jamais versionner de secrets
    réels.
4.  Vérifier que le dépôt de configuration Git `config-repo` est
    accessible au Config Server.
5.  Depuis le répertoire contenant le fichier Compose, démarrer les
    conteneurs :

``` bash
docker compose up -d --build
```

6.  Vérifier l'état des conteneurs :

``` bash
docker compose ps
```

7.  Pour arrêter les conteneurs :

``` bash
docker compose down
```

> **Important :** les noms de services Compose, les fichiers `.env`, les
> profils Spring et les commandes de lancement individuelles ne sont pas
> détaillés ici, car ils doivent correspondre à la structure effective
> de votre dépôt. Consultez le `docker-compose.yml` et les fichiers
> `application.yml`/`application.properties` avant d'exécuter le projet.

## Principes de conception

Chaque microservice suit le principe de Clean Architecture. Les
dépendances sont orientées vers le domaine :

1.  **Domaine** : entités et règles métier, sans dépendance à Spring, à
    la base de données ou à RabbitMQ.
2.  **Application** : cas d'utilisation et ports (interfaces vers la
    persistance et le bus d'événements).
3.  **Infrastructure** : contrôleurs REST, repositories, clients
    OpenFeign et adaptateurs RabbitMQ.

Flux simplifié d'une requête :

``` text
Client → Contrôleur → Service applicatif → Domaine → Persistance → Base de données
```

Les six principes recherchés sont la responsabilité unique, l'autonomie,
le découplage, la communication par API, le déploiement indépendant et
la scalabilité ciblée.

## Équipe et répartition

  -----------------------------------------------------------------------
  Membre                              Microservice
  ----------------------------------- -----------------------------------
  Tasnime Kaabachi                    Réservations et Paiements
                                      (`booking-service`)

  Imen Hammami                        Notifications et Messagerie
                                      (`notification-service`)

  Aziz Mokni                          Réputation et Modération
                                      (`reputation-service`)

  NourEl Houda Nasfi                  Utilisateurs et Auth
                                      (`user-auth-service`)

  Sahar Raach                         Trajets (`trip-service`)

  Toute l'équipe                      Socle commun : API Gateway, Eureka,
                                      Config Server, Keycloak et RabbitMQ
  -----------------------------------------------------------------------

## État et prochaines étapes

Le dossier technique prévoit les étapes suivantes :

-   mise en place de l'infrastructure commune et des bases avec Docker
    Compose ;
-   développement des workflows essentiels de chaque microservice ;
-   intégration des appels REST et des événements RabbitMQ ;
-   intégration du frontend Angular ;
-   tests de bout en bout et préparation de la démonstration.

------------------------------------------------------------------------

**CampusRide --- Plateforme de covoiturage universitaire**
