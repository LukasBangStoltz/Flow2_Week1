package facades;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import exceptions.PersonNotFoundException;
import java.util.List;

public interface IPersonFacade {

    public PersonDTO addPerson(String fName, String lName, String phone);

    public PersonDTO deletePerson(long id) throws PersonNotFoundException;

    public PersonDTO getPerson(long id) throws PersonNotFoundException;

    public PersonsDTO getAllPersons();

    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException;
}
