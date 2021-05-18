package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.service.BookRatingService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/books/rating")
@RequiredArgsConstructor
public class BookRatingController {

    private final BookRatingService bookRatingService;

    @PostMapping("/add")
    public void updateBookRating(
            Principal principal,
            @RequestParam long bookId,
            @RequestParam int rating) {
        long userId = ((User) principal).getId();
        bookRatingService.updateOrSave(userId, bookId, rating);
    }

    @GetMapping("/getAll")
    public List<BookRating> getAll(
            Principal principal) {
        long userId = ((User) principal).getId();
        return bookRatingService.getAll(userId);
    }
}
