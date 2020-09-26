package rest;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.FacadeExample;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("id/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPerson(@PathParam("id") Long id) throws PersonNotFoundException {
        
        PersonDTO person = FACADE.getPerson(id);
        
        return GSON.toJson(person);
        
    }

    @Path("all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons() {

        PersonsDTO persons = FACADE.getAllPersons();

        return GSON.toJson(persons);
    }

    @Path("addperson")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(String person) {
        PersonDTO personDTO = GSON.fromJson(person, PersonDTO.class);
        FACADE.addPerson(personDTO.getFirstName(), personDTO.getLastName(), personDTO.getPhoneNumber());

        return Response.ok(personDTO).build();
    }

    @Path("edit/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPerson(@PathParam("id") long id, String person) throws PersonNotFoundException{
        PersonDTO personDTO = GSON.fromJson(person, PersonDTO.class);
        personDTO.setId(id);
        PersonDTO edit = FACADE.editPerson(personDTO);
        

        return Response.ok(edit).build();
    }
    
    @Path("delete/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePerson(@PathParam("id") int id )throws PersonNotFoundException{
        PersonDTO deletedPerson = FACADE.deletePerson(id);
        return "{\"status\": \"removed\"}";
}
    
}
