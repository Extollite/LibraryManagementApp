package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.service.AuthorService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorWebController {

    private final AuthorService authorService;

    @ModelAttribute("module")
    private String module() {
        return "authors";
    }

    @GetMapping
    public String listAuthors(Model model,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Author> authors = authorService.getAllAuthors(PageRequest.of(page - 1, size));

        model.addAttribute("authors", authors);

        if (authors.hasContent()) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, authors.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "authors";
    }

    @DeleteMapping("/delete")
    public String deleteAuthor(@RequestParam long authorId) {
        authorService.delete(authorId);

        return "redirect:/authors";
    }

    @GetMapping("/add")
    public String addAuthor(Model model) {
        AuthorDto authorDto = new AuthorDto();

        model.addAttribute("author", authorDto);

        return "authorAdd";
    }

    @PostMapping("/add/save")
    public String addAuthor(@ModelAttribute(value = "author") AuthorDto authorDto) {
        authorService.save(authorDto);

        return "redirect:/authors";
    }
}
