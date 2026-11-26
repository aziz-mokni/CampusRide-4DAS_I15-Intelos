# notification-service

Microservice **Notifications et Messagerie** de CampusRide (port **8085**, MongoDB).

## Lancer (sans Docker)
1. MongoDB local sur `localhost:27017`.
2. (optionnel) RabbitMQ sur `localhost:5672`, Eureka sur `localhost:8761`, Keycloak sur `localhost:8180`.
3. `mvn spring-boot:run` (ou le triangle vert d'IntelliJ).

Variables utiles : `EUREKA_ENABLED=false`, `RABBIT_LISTENER_ENABLED=false`, `USERS_URL=http://localhost:8081`,
`KEYCLOAK_CLIENT_SECRET=...`, `MAIL_ENABLED=true`.

Swagger : http://localhost:8085/swagger-ui.html

## API (prefixe /api/v1, JWT Keycloak obligatoire)
| Methode | Endpoint | Description | Codes |
|---|---|---|---|
| GET | /notifications?status,channel,page,size | Mes notifications | 200, 401 |
| GET | /notifications/{id} | Detail | 200, 401, 403, 404 |
| PATCH | /notifications/{id} | Marquer comme lue | 200, 401, 403, 404 |
| DELETE | /notifications/{id} | Supprimer | 204, 401, 403, 404 |
| POST | /notifications | Creer (ADMIN) | 201, 400, 401, 403 |
| POST | /conversations | Creer une conversation | 201, 400, 401 |
| GET | /conversations | Mes conversations | 200, 401 |
| GET | /conversations/{id}/messages | Historique du chat | 200, 401, 403, 404 |
| POST | /conversations/{id}/messages | Envoyer un message | 201, 400, 401, 403, 404 |

## Evenements consommes (exchange `covoiturage.events`, queue `notification.queue`)
user.registered, booking.confirmed, booking.rejected, booking.cancelled, payment.completed,
payment.failed, trip.cancelled, review.published, sanction.applied.
Aucun evenement publie.
