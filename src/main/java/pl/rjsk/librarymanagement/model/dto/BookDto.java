package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {

    private long id;
    private String title;
    private List<AuthorDto> authors;
    private long genreId;
    private int yearOfFirstRelease;
    private String description;
    private int numberOfAvailableCopies;
}
