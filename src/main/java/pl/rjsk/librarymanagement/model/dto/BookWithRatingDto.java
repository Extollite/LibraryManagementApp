package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class BookWithRatingDto {
    private long id;
    private String title;
    private Set<Long> authorsIds;
    private long genreId;
    private int yearOfFirstRelease;
    private String description;
    private Integer rating;
}
