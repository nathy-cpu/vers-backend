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
        return personRepository.findByIdOptional(id);
    }

    public Person createOrUpdatePerson(Person person) {
        if (person.id == null) {
            personRepository.persist(person);
            return person;
        }
        return personRepository.getEntityManager().merge(person);
    }

    public List<Person> findAllPersons() {
        return personRepository.listAll();
    }
}
