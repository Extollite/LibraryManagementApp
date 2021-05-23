package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.dto.BookRatingDto;
import pl.rjsk.librarymanagement.model.dto.BookWithRatingDto;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.security.data.LibraryUserDetails;
import pl.rjsk.librarymanagement.service.BookRatingService;
import pl.rjsk.librarymanagement.service.BookRecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/books/rating")
@RequiredArgsConstructor
public class BookRatingController {

    private final BookRatingService bookRatingService;
    private final BookRecommendationService bookRecommendationService;

    @PostMapping("/add")
    public BookRatingDto updateBookRating(
            Authentication auth,
            @RequestParam long bookId,
            @RequestParam int rating) {
        var userInfo = (LibraryUserDetails) auth.getPrincipal();
        User user = userInfo.getLibraryUser();

        BookRatingDto bookRating = bookRatingService.updateOrSave(user, bookId, rating);

        bookRecommendationService.recalculateRecommendations(user);

        return bookRating;
    }

    @GetMapping("/get")
    public BookRatingDto get(
            Authentication auth,
            @RequestParam long bookId) {
        var userDetails = (LibraryUserDetails) auth.getPrincipal();
        User user = userDetails.getLibraryUser();

        return bookRatingService.get(user, bookId);
    }

    @GetMapping("/getAll")
    public List<BookWithRatingDto> getAll(Authentication auth) {
        var userDetails = (LibraryUserDetails) auth.getPrincipal();
        User user = userDetails.getLibraryUser();

        return bookRatingService.getAll(user);
    }
}
