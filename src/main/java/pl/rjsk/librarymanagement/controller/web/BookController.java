package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.service.BookService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public String litBooks(Model model,
                           @RequestParam("page") int page,
                           @RequestParam("size") int size) {
        Page<BookWithCopiesDto> books = bookService.getAllBooksWithInstances(PageRequest.of(page - 1, size));

        model.addAttribute("books", books.getContent());

        if (books.hasContent()) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, books.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "books";
    }
}
