package pl.rjsk.librarymanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
public class BookCopyDueDateDto {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private long id;
    private String alternativeTitle;
    private long bookId;
    private String languageCode;
    private String publisherName;
    private int yearOfRelease;
    private int pagesCount;
    private boolean available;
    private OffsetDateTime dueDate;

    @JsonIgnore
    public String getDueDateAsLocalTime() {
        return formatter.format(dueDate);
    }

    @JsonIgnore
    public void setDueDateLocalTime(String dueDateLocalTime) {
        var localDateTime = LocalDateTime.parse(dueDateLocalTime, formatter);
        dueDate = localDateTime.atOffset(ZoneOffset.UTC);
    }
}
