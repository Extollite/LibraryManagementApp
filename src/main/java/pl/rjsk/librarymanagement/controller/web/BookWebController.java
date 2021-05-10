package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookService;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/books", "/", ""})
@Slf4j
public class BookWebController {

    private final BookService bookService;
    private final GenreService genreService;
    private final AuthorService authorService;

    @ModelAttribute("module")
    private String module() {
        return "books";
    }

    @GetMapping
    public String listBooks(Model model,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<BookWithCopiesDto> books = bookService.getAllBooksWithInstances(PageRequest.of(page - 1, size));

        model.addAttribute("books", books);

        if (books.hasContent()) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, books.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "books";
    }
    
    @PostMapping("/add")
    public String addBook(Model model) {
        BookWithKeywordsDto bookDto = new BookWithKeywordsDto();
        List<Genre> genres = genreService.getAllGenres();
        List<Author> authors = authorService.getAllAuthors();
        
        model.addAttribute("book", bookDto);
        model.addAttribute("genres", genres);
        model.addAttribute("authors", authors);
        
        return "bookAdd";
    }
    
    @PostMapping("/add/save")
    public String saveBook(@ModelAttribute(value = "book") BookWithKeywordsDto bookDto) {
        bookService.save(bookDto);
        
        log.info(bookDto.toString());
        
        return "redirect:/books";
    }
}
