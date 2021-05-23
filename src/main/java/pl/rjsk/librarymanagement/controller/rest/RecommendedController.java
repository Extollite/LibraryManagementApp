package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.security.data.LibraryUserDetails;
import pl.rjsk.librarymanagement.service.BookRatingService;
import pl.rjsk.librarymanagement.service.RecommendedService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books/recommended")
@RequiredArgsConstructor
public class RecommendedController {
    
    private final RecommendedService recommendedService;
    private final BookRatingService bookRatingService;
    
    @GetMapping("/count")
    public Map<String, Long> getRatingCount(Authentication auth) {
        var userInfo = (LibraryUserDetails) auth.getPrincipal();
        User user = userInfo.getLibraryUser();
        
        long rated = bookRatingService.getRatingCount(user);
        long required = recommendedService.getRequiredRatingsCount();
        
        return Map.of("rated", rated, "required", required);
    }
    
    @GetMapping("/get")
    public List<BookWithCopiesDto> getRecommendedBooks(Authentication auth) {
        var userInfo = (LibraryUserDetails) auth.getPrincipal();
        User user = userInfo.getLibraryUser();
        
        return recommendedService.getRecommendedBooks(user);
    }
}
