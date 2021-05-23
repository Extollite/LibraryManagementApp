package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.entity.User;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendedService {
    private static final long requiredRatingsCount = 10;
    
    public long getRequiredRatingsCount() {
        return requiredRatingsCount;
    }
    
    public List<BookWithCopiesDto> getRecommendedBooks(User user) {
        return Collections.emptyList();
    }
}
