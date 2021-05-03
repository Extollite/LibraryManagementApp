package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookService;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/details")
public class BookDetailsWebController {

    private final BookService bookService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final BookMapper bookMapper;

    @ModelAttribute("module")
    private String module() {
        return "books";
    }

    @GetMapping
    public String displayMainView(Model model, @RequestParam long id) {
        model.addAttribute("id", id);
        return "bookDetails";
    }

    @GetMapping("/edit")
    public String editDetails(Model model, @RequestParam long id) {
        BookWithKeywordsDto bookDto = bookService.getBookWithKeywordsById(id);
        List<Genre> genres = genreService.getAllGenres();
        List<Author> authors = authorService.getAllAuthors();
        
        model.addAttribute("book", bookDto);
        model.addAttribute("genres", genres);
        model.addAttribute("authors", authors);

        return "edit";
    }

    @PostMapping("edit/save")
    public String editDetails(@ModelAttribute(value="book") BookWithKeywordsDto bookDto) {
        // TODO: save logic
        return "redirect:/books/details?id="+bookDto.getId();
    }
}
