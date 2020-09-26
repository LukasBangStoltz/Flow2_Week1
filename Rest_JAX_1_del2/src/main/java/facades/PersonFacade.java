/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import utils.EMF_Creator;

/**
 *
 * @author lukas
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    private PersonFacade() {

    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = new Person(fName, lName, phone);
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();

            return new PersonDTO(person);

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException("Personen du ønskede at fjerne, eksisterer ikke");
        } else {

            try {
                em.getTransaction().begin();
                em.remove(person);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
            return new PersonDTO(person);
        }
    }

    @Override
    public PersonDTO getPerson(long id) throws PersonNotFoundException {

        EntityManager em = emf.createEntityManager();

        Person person = em.find(Person.class, id);
        if(id==13){
            System.out.println(13/0);
        }
        if (person == null) {
            
            throw new PersonNotFoundException("Det givne id matcher ikke en person");
        }
        return new PersonDTO(person);

    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createNamedQuery("Person.getAll");
            List<Person> persons = query.getResultList();

            return new PersonsDTO(persons);

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, p.getId());
        if (person == null) {
            throw new PersonNotFoundException("Personen du ville redigere, findes ikke");
        }

        person.setFirstName(p.getFirstName());
        person.setLastName(p.getLastName());
        person.setPhoneNumber(p.getPhoneNumber());
        try {

            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);

    }

}
