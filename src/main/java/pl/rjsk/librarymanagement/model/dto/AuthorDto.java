package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AuthorDto {
    
    @EqualsAndHashCode.Exclude
    private long id;
    private String firstName;
    private String lastName;
}
