package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.BirthEvent;

@ApplicationScoped
public class BirthEventRepository implements PanacheRepository<BirthEvent> {

    public Optional<BirthEvent> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }

    public Optional<BirthEvent> findByChildName(String childName) {
        return find("childName", childName).firstResultOptional();
    }

    public List<BirthEvent> findByFatherName(String fatherName) {
        return list("fatherName", fatherName);
    }

    public List<BirthEvent> findByMotherName(String motherName) {
        return list("motherName", motherName);
    }
}
