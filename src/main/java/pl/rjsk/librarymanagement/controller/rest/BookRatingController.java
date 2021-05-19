package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.security.data.LibraryUserDetails;
import pl.rjsk.librarymanagement.service.BookRatingService;

import java.util.List;

@RestController
@RequestMapping("/api/books/rating")
@RequiredArgsConstructor
public class BookRatingController {

    private final BookRatingService bookRatingService;

    @PostMapping("/add")
    public void updateBookRating(
            Authentication auth,
            @RequestParam long bookId,
            @RequestParam int rating) {
        var user = (LibraryUserDetails) auth.getPrincipal();
        long userId = user.getLibraryUser().getId();

        bookRatingService.updateOrSave(userId, bookId, rating);
    }

    @GetMapping("/getAll")
    public List<BookRating> getAll(Authentication auth) {
        var user = (LibraryUserDetails) auth.getPrincipal();
        long userId = user.getLibraryUser().getId();

        return bookRatingService.getAll(userId);
    }
}
