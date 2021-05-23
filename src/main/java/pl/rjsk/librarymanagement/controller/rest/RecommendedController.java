package pl.rjsk.librarymanagement.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.security.data.LibraryUserDetails;
import pl.rjsk.librarymanagement.service.BookRatingService;
import pl.rjsk.librarymanagement.service.BookRecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/books/recommended")
@RequiredArgsConstructor
public class RecommendedController {
    
    private final BookRecommendationService bookRecommendationService;
    private final BookRatingService bookRatingService;
    
    @GetMapping("/count")
    public UserCurrentAndRequiredRatings getRatingCount(Authentication auth) {
        var userInfo = (LibraryUserDetails) auth.getPrincipal();
        User user = userInfo.getLibraryUser();
        
        return new UserCurrentAndRequiredRatings(
                bookRatingService.getRatingCount(user),
                bookRecommendationService.getMinRatedBookToCalculate()
        );
    }
    
    @GetMapping
    public List<BookDto> getRecommendedBooks(Authentication auth) {
        var userInfo = (LibraryUserDetails) auth.getPrincipal();
        User user = userInfo.getLibraryUser();
        
        return bookRecommendationService.getRecommendedBooks(user);
    }
    
    @Data
    @AllArgsConstructor
    private final static class UserCurrentAndRequiredRatings {
        long currentCount;
        long requiredCount;
    }
}
