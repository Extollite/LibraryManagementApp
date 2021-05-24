package pl.rjsk.librarymanagement.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
public class Author {

    @Id
    // Change for debug/gapi to works
//    @EqualsAndHashCode.Exclude
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
    private String firstName;

    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
    private String lastName;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    public Author(long id) {
        this.id = id;
    }
}