package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.model.Template;
import com.campusride.notification.domain.port.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/** Insere les modeles de messages par defaut s'ils n'existent pas. */
@Component
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

    private final TemplateRepository templates;

    @Override
    public void run(String... args) {
        List<Template> defaults = List.of(
            new Template("USER_REGISTERED", "Bienvenue sur CampusRide", "Bienvenue sur CampusRide ! Votre compte est cree. Bon covoiturage !"),
            new Template("BOOKING_CONFIRMED", "Reservation confirmee", "Votre reservation {{bookingId}} est confirmee. Vous pouvez maintenant la payer."),
            new Template("BOOKING_REJECTED", "Reservation refusee", "Desole, il n'y a plus assez de places pour la reservation {{bookingId}}."),
            new Template("BOOKING_CANCELLED", "Reservation annulee", "La reservation {{bookingId}} a ete annulee."),
            new Template("PAYMENT_COMPLETED", "Paiement recu", "Le paiement de la reservation {{bookingId}} a bien ete effectue."),
            new Template("PAYMENT_FAILED", "Paiement echoue", "Le paiement de la reservation {{bookingId}} a echoue. Veuillez reessayer."),
            new Template("TRIP_CANCELLED", "Trajet annule", "Le conducteur a annule le trajet {{tripId}}."),
            new Template("REVIEW_PUBLISHED", "Nouvel avis recu", "Vous avez recu un nouvel avis sur le trajet {{tripId}}."),
            new Template("SANCTION_APPLIED", "Sanction appliquee", "Une sanction ({{type}}) a ete appliquee a votre compte.")
        );
        defaults.forEach(t -> {
            if (templates.findByCode(t.getCode()).isEmpty()) templates.save(t);
        });
    }
}
