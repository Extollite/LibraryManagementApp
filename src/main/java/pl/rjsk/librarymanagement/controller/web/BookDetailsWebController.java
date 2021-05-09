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
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.AuthorService;
import pl.rjsk.librarymanagement.service.BookCopyService;
import pl.rjsk.librarymanagement.service.BookService;
import pl.rjsk.librarymanagement.service.GenreService;

import java.time.OffsetDateTime;
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

    @PostMapping("edit/save")
    public String updateDetails(@ModelAttribute(value = "book") BookWithKeywordsDto bookDto) {
        bookService.updateBook(bookDto);
        return "redirect:/books/details?id=" + bookDto.getId();
    }

    @GetMapping("/copies")
    public String editCopies(Model model, @RequestParam long bookId) {
        List<BookCopyDueDateDto> copies = bookCopyService.getAllByBookId(bookId);

        model.addAttribute("bookCopies", copies);
        model.addAttribute("bookId", bookId);

        return "copies";
    }

    @DeleteMapping("/copies/delete")
    public String deleteCopy(@RequestParam long bookId, @RequestParam long copyId) {
        bookCopyService.deleteBookCopy(copyId);

        return "redirect:/books/details?id=" + bookId;
    }

    @PostMapping("/copies/add")
    public String addCopy(Model model, @RequestParam long bookId) {
        BookDto bookDto = bookService.getBookById(bookId);

        BookCopyDueDateDto bookCopyDueDateDto = new BookCopyDueDateDto();
        bookCopyDueDateDto.setBookId(bookId);
        bookCopyDueDateDto.setYearOfRelease(OffsetDateTime.now().getYear());
        bookCopyDueDateDto.setPagesCount(1);

        model.addAttribute("book", bookDto);
        model.addAttribute("bookCopy", bookCopyDueDateDto);

        return "bookCopyAdd";
    }

    @PostMapping("/copies/add/save")
    public String saveCopies(@ModelAttribute(value = "bookCopy") BookCopyDueDateDto bookCopyDto) {
        bookCopyService.saveBookCopy(bookCopyDto);
        return "redirect:/books/details?id=" + bookCopyDto.getBookId();
    }

    @PostMapping("/copies/edit")
    public String editCopy(Model model, @RequestParam long copyId) {
        BookCopyDueDateDto bookCopyDto = bookCopyService.getByCopyId(copyId);

        if (bookCopyDto.getDueDate() == null) {
            bookCopyDto.setDueDate(OffsetDateTime.now().plusDays(7));
        }

        BookDto bookDto = bookService.getBookById(bookCopyDto.getBookId());

        model.addAttribute("book", bookDto);
        model.addAttribute("bookCopy", bookCopyDto);

        return "bookCopyDetails";
    }

    @PostMapping("/copies/edit/save")
    public String updateCopy(@ModelAttribute(value = "bookCopy") BookCopyDueDateDto bookCopyDto) {
        bookCopyService.updateBookCopy(bookCopyDto);

        return "redirect:/books/details?id=" + bookCopyDto.getBookId();
    }
}
