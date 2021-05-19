package pl.rjsk.librarymanagement.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "book_ratings",
        uniqueConstraints = @UniqueConstraint(columnNames =
                {"user_id", "book_id"}))
@Data
public class BookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private Integer rating;

    public BookRating setRating(int rating) {
        this.rating = rating;
        return this;
    }
}