package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookService;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/details")
public class BookDetailsWebController {

    private final BookService bookService;
    private final GenreService genreService;
    private final AuthorService authorService;

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
        // TODO: get real book
        BookKeywordsDto bookDto = new BookKeywordsDto();
        bookDto.setId(id);
        bookDto.setTitle("XD");
        bookDto.setAuthors(Set.of(1L));
        bookDto.setGenreId(2);
        bookDto.setYearOfFirstRelease(2020);
        bookDto.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla risus turpis, volutpat id iaculis non, blandit ut enim. Interdum et malesuada fames ac ante ipsum primis in faucibus. Cras sed malesuada leo. Donec vestibulum mauris et felis maximus, in tempus est facilisis. Aliquam auctor ornare sapien eu maximus. Nulla facilisi. Aliquam sapien turpis, bibendum ac vestibulum quis, lobortis a dolor.\n" +
                "\n" +
                "Morbi accumsan justo quis dui mattis consequat. Ut sagittis magna sit amet ante feugiat, eget aliquam turpis luctus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Ut vitae nunc diam. Nulla ullamcorper, dui id ultrices ultricies, sem lacus efficitur nulla, quis fermentum orci tortor vitae lacus. Vestibulum egestas mi lacus, posuere pharetra neque rutrum eu. Quisque varius nisl sed blandit venenatis. Mauris dignissim dolor id libero ultricies, vel porta tellus euismod. Maecenas eget volutpat felis. Cras consequat iaculis lobortis. Vestibulum ac posuere diam, id faucibus ex. Mauris vel elit est. Maecenas vel enim eget tellus gravida auctor eu eget purus. Proin eget nunc eu ex sagittis vulputate sed non justo.");
        model.addAttribute("book", bookDto);

        List<Genre> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);

        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);

        return "edit";
    }

    @PostMapping("edit/save")
    public String editDetails(@ModelAttribute(value="book") BookKeywordsDto bookDto) {
        // TODO: save logic
        return "redirect:/books/details?id="+bookDto.getId();
    }
}
