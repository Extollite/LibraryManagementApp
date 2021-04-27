package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.service.BookCopyService;

import java.util.List;

@RestController
@RequestMapping("api/copies")
@RequiredArgsConstructor
public class BookCopyController {
    
    private final BookCopyService bookCopyService;
     
    @GetMapping("/byBookId")
    public List<BookCopyDueDateDto> getBookCopiesByBookId(@RequestParam long bookId) {
        return bookCopyService.getAllByBookId(bookId);
    }
}
