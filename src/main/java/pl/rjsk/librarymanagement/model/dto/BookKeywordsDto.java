package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BookKeywordsDto {

    private long id;
    private String title;
    private Set<Long> authors;
    private long genreId;
    private int yearOfFirstRelease;
    private String description;
    private String keywords;
}
