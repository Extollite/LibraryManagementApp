package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BookWithCopiesDto {

    private long id;
    private String title;
    private List<AuthorDto> authors;
    private long genreId;
    private int yearOfFirstRelease;
    private String description;
    private Set<Long> bookCopyIds;
}
