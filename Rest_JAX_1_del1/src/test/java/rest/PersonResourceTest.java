package rest;

import DTO.PersonDTO;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
Person p1;    
Person p2;    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         //Don't forget this, if you called its counterpart in @BeforeAll
         EMF_Creator.endREST_TestWithDB();
         httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
            p1 = new Person("Lars", "Larsen", "40404040");
            p2 = new Person("Jens", "Jensen", "20202020");
       
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNativeQuery("alter table PERSON AUTO_INCREMENT = 1").executeUpdate();
            em.persist(p1);
            em.persist(p2); 
            em.getTransaction().commit();
        } finally { 
            em.close();
        }
    }
    
    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }
   
    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
        .contentType("application/json")
        .get("/person/").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("msg", equalTo("Hello World"));   
    }
    
  
    
    @Test 
    public void testGetPerson() throws Exception{
        given()
                .contentType("application/json")
                .get("person/id/{id}", p1.getP_id()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("Lars"));
                
    }
    
    @Test
    public void testGetAllPersons() throws Exception{
        List<PersonDTO> personsDTOs;

        personsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/person/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);

        assertThat(personsDTOs, containsInAnyOrder(p1DTO, p2DTO));
    }
    
    @Test
    public void testDeletePerson() throws Exception{
        PersonDTO person = new PersonDTO(p1);
        
        given()
                .contentType("application/json")
                .delete("/person/delete/" + person.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        
        List<PersonDTO> personsDTOs;
        personsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/person/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);
                
        
                PersonDTO person2 = new PersonDTO(p2);
        assertThat(personsDTOs, containsInAnyOrder(person2));
    }
    
    @Test
    public void testAddPerson() throws Exception{
        given()
                .contentType(ContentType.JSON)
                .body(new PersonDTO("frans", "færdinan", "40394949"))
                .when()
                .post("/person/addperson")
                .then()
                .body("firstName", equalTo("frans"))
                .body("lastName", equalTo("færdinan"))
                .body("phoneNumber",equalTo("40394949"))
                ;
    }
    
    @Test
    public void testEditPerson() throws Exception{
        PersonDTO person = new PersonDTO(p1);
        person.setPhoneNumber("11111111");
        
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .put("person/edit/" + person.getId())
                .then()
                .body("firstName", equalTo("Lars"))
                .body("lastName", equalTo("Larsen"))
                .body("phoneNumber", equalTo("11111111"))
                .body("id", equalTo((int) person.getId()));
        
                
    }
}
