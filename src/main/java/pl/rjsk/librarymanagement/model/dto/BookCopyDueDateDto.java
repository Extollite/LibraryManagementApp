package pl.rjsk.librarymanagement.model.dto;


import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BookCopyDueDateDto {
    private long id;
    private String alternativeTitle;
    private long bookId;
    private String languageCode;
    private String publisherName;
    private int yearOfRelease;
    private int pagesCount;
    private boolean available;
    private OffsetDateTime dueDate;
}
