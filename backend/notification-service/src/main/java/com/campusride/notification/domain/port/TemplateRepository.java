package com.campusride.notification.domain.port;

import com.campusride.notification.domain.model.Template;
import java.util.Optional;

public interface TemplateRepository {
    Optional<Template> findByCode(String code);
    Template save(Template template);
}
