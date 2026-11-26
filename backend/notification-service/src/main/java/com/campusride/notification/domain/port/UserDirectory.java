package com.campusride.notification.domain.port;

import com.campusride.notification.domain.model.UserContact;
import java.util.Optional;

public interface UserDirectory {
    /**
     * @return les coordonnees, ou Optional.empty() si l'utilisateur n'existe pas (404)
     * @throws com.campusride.notification.domain.exception.ContactLookupException si le service est indisponible
     */
    Optional<UserContact> findContact(String userId);
}
