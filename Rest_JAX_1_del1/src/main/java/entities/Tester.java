/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
/**
 *
 * @author lukas
 */
public class Tester {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        Person pers1 = new Person("lars", "andersen", "44444444");
        Person pers2 = new Person("test", "test", "awe");
        Address ad1 = new Address("Hovmarksvej", "2920", "charlottenblud");
        Address ad2 = new Address("GodVej", "2912", "aarhus");
        pers1.setAddress(ad1);
        pers2.setAddress(ad2);
        
        
        em.getTransaction().begin();
        em.persist(pers1);
        em.persist(pers2);
        em.getTransaction().commit();
    }
}
