/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dat3.jpademo.entities;

import DTO.PersonStyleDTO;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author lukas
 */
public class Tester {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Person p1 = new Person("Jønke", 1963);
        Person p2 = new Person("Blondie", 1959);

        Address a1 = new Address("Store Torv 1", 2323, "Snede");
        Address a2 = new Address("Langgade 34", 1212, "Valby");

        p1.setAddress(a1);
        p2.setAddress(a2);

        Fee f1 = new Fee(100);
        Fee f2 = new Fee(200);
        Fee f3 = new Fee(300);

        p1.AddFee(f1);
        p1.AddFee(f3);
        p2.AddFee(f2);

        SwimStyle s1 = new SwimStyle("Crawl");
        SwimStyle s2 = new SwimStyle("ButterFly");
        SwimStyle s3 = new SwimStyle("Bryst");
        
        p1.AddSwimStyle(s1);
        p1.AddSwimStyle(s3);
        p2.AddSwimStyle(s2);
        
        
        
        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.getTransaction().commit();
        
        
        
        
        em.getTransaction().begin();
        //p1.removeSwimStyle(s3);
        em.getTransaction().commit();
        

        System.out.println("p1: " + p1.getP_id() + ", " + p1.getName());
        System.out.println("p1: " + p2.getP_id() + ", " + p2.getName());

        System.out.println("Lad os se om to-vejs virker: " + a1.getPerson().getName());

        System.out.println("Hvem har betalt f2? Det har: " + f2.getPerson().getName());

        System.out.println("Hvad er der betalt: ");

        TypedQuery<Fee> q1 = em.createQuery("SELECT f FROM Fee f", Fee.class);
        List<Fee> fees = q1.getResultList();

        for (Fee f : fees) {
            System.out.println(f.getPerson().getName() + ": " + f.getAmount() + "kr. Den: " + f.getPayDate() + " ADR: " + f.getPerson().getAddress().getCity());
        }
        
        // Create JPQL with constructor projections
        System.out.println("-- JPQL JOINS --");
        Query q3 = em.createQuery("SELECT new DTO.PersonStyleDTO(p.name, p.year, s.styleName) FROM Person p JOIN p.styles s");
        List<PersonStyleDTO> personDetails = q3.getResultList();
        
        for (PersonStyleDTO ps: personDetails){
            System.out.println("Navn:" + ps.getName());
            System.out.println("År: " + ps.getYear());
            System.out.println("Stil: " + ps.getSwimStyle());
        }
    }
}
