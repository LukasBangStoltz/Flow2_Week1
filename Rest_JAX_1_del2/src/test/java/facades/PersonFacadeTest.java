package facades;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import utils.EMF_Creator;
import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    Person p1;
    Person p2;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM PERSON").executeUpdate();
            em.createNativeQuery("alter table PERSON AUTO_INCREMENT = 1").executeUpdate();
            p1 = new Person("Lars", "Larsen", "44444444");
            p2 = new Person("Jens", "Jensen", "88888888");
            em.persist(p1);
            em.persist(p2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testGetPerson() throws PersonNotFoundException {
        PersonDTO person = facade.getPerson(p1.getP_id());
        String exp = "Lars";
        assertEquals(person.getFirstName(), exp);
    }

    @Test
    public void testGetAllPersons() {
        PersonsDTO persons = facade.getAllPersons();
        int expSize = 2;

        assertEquals(persons.getAll().size(), expSize);
    }

    @Test
    public void testDeletePerson() throws PersonNotFoundException {
        PersonDTO person = facade.deletePerson(p1.getP_id());
        String expName = "Lars";
        assertEquals(person.getFirstName(), expName);

    }

    @Test
    public void testEditPerson() throws PersonNotFoundException {
        PersonDTO person = new PersonDTO(p1);
        person.setFirstName("newFName");

        PersonDTO editPerson = facade.editPerson(person);
        String expFname = "newFName";
        assertEquals(editPerson.getFirstName(), expFname);

    }

    @Test
    public void testAddPerson() {
        PersonDTO person = facade.addPerson("oste", "frans", "49585858");
        PersonDTO person2 = facade.addPerson("oste2", "frans2", "495858582");
        PersonDTO person3 = facade.addPerson("oste3", "frans3", "495858581");
        PersonsDTO persons = facade.getAllPersons();
        int expSize = 5;
        assertEquals(persons.getAll().size(), expSize);
    }

    @Test
    public void negativeTestDeletePerson() throws PersonNotFoundException {
        long id = 15;
        try {
            PersonDTO person = facade.deletePerson(id);
            assert false;
        } catch (PersonNotFoundException e) {
            assert true;
        }
    }
    
    
     @Test
    public void negativeTestEditPerson() throws PersonNotFoundException {
        long id = 15;
        try {
            PersonDTO pDTO = new PersonDTO(new Person("Jens", "lange", "4444444"));
            PersonDTO personEdit = facade.editPerson(pDTO);
            assert false;
        } catch (PersonNotFoundException e) {
            assert true;
        }
    }
    
     @Test
    public void negativeTestGetPerson() throws PersonNotFoundException {
        try {
            long id = 10000;
            PersonDTO personEdit = facade.getPerson(id);
            assert false;
        } catch (PersonNotFoundException e) {
            assert true;
        }
    }
    
    @Test
    public void negativeTestGetPerson500() {
        try {
            long id = 13;
            PersonDTO personEdit = facade.getPerson(id);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }
}
