package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.service.BookCopyService;

import java.util.List;

@RestController
@RequestMapping("api/books/copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @GetMapping("/availabilityByBookId")
    public List<BookCopyDueDateDto> getBookCopiesByBookId(@RequestParam long bookId) {
        return bookCopyService.getAllByBookId(bookId);
    }
}
