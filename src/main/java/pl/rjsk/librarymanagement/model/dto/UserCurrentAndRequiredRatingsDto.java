package pl.rjsk.librarymanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCurrentAndRequiredRatingsDto {
    private long currentCount;
    private long requiredCount;
}
