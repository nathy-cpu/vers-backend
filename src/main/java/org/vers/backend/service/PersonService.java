package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.Person;
import org.vers.backend.repository.PersonRepository;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepository;

    public Optional<Person> findById(Long id) {
        return Optional.of(personRepository.findById(id));
    }

    public Person createOrUpdatePerson(Person person) {
        if (!personRepository.findById(person.id).isEmpty()) {
            personRepository.persist(person);
        } else {
            personRepository.getEntityManager().merge(person);
        }
        return person;
    }

    public List<Person> findAllPersons() {
        return personRepository.listAll();
    }
}
