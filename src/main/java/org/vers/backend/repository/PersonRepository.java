package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import org.vers.backend.entity.Person;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    public List<Person> findByName(String firstName, String lastName) {
        return list("firstName = ?1 and lastName = ?2", firstName, lastName);
    }
}
