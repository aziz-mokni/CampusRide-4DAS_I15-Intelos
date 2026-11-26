package com.campusride.notification.infrastructure.persistence;

import com.campusride.notification.domain.model.Template;
import com.campusride.notification.domain.port.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TemplateRepositoryAdapter implements TemplateRepository {

    private final SpringDataTemplateRepository repo;

    @Override
    public Optional<Template> findByCode(String code) {
        return repo.findById(code).map(d -> new Template(d.getCode(), d.getSubject(), d.getBody()));
    }

    @Override
    public Template save(Template t) {
        repo.save(new TemplateDocument(t.getCode(), t.getSubject(), t.getBody()));
        return t;
    }
}
