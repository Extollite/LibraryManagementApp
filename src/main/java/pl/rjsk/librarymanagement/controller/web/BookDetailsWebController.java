package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookCopyService;
import pl.rjsk.librarymanagement.service.BookService;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/details")
@Slf4j
public class BookDetailsWebController {

    private final BookService bookService;
    private final BookCopyService bookCopyService;
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
        BookWithKeywordsDto bookDto = bookService.getBookWithKeywordsById(id);
        List<Genre> genres = genreService.getAllGenres();
        List<Author> authors = authorService.getAllAuthors();

        model.addAttribute("book", bookDto);
        model.addAttribute("genres", genres);
        model.addAttribute("authors", authors);

        return "edit";
    }

    @GetMapping("/copies")
    public String editCopies(Model model, @RequestParam long bookId) {
        List<BookCopyDueDateDto> copies = bookCopyService.getAllByBookId(bookId);

        model.addAttribute("bookCopies", copies);

        return "copies";
    }

    @DeleteMapping("/copies/delete")
    public String deleteCopy(@RequestParam long bookId, @RequestParam long copyId) {
        bookCopyService.deleteBookCopy(copyId);

        return "redirect:/books/details?id=" + bookId;
    }

    @PostMapping("edit/save")
    public String editDetails(@ModelAttribute(value = "book") BookWithKeywordsDto bookDto) {
        bookService.updateBook(bookDto);
        return "redirect:/books/details?id=" + bookDto.getId();
    }
}
