package pl.rjsk.librarymanagement.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "phones")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id; // Each phone will be given an auto-generated unique identifier when stored

    @Column(nullable = false)
    private String phoneName; // Save the name of the phone

    @Column(nullable = false)
    private String os; // Save the operating system running in the phone

    // Standard getters and setters
}