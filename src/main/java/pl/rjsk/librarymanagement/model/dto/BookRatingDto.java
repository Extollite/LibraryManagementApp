package pl.rjsk.librarymanagement.model.dto;

import lombok.Data;

@Data
public class BookRatingDto {
    private long id;
    private long userId;
    private long bookId;
    private int rating;
}
