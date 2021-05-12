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
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreWebController {
    private final GenreService genreService;

    @ModelAttribute("module")
    private String module() {
        return "genres";
    }

    @GetMapping
    public String listGenres(Model model) {
        List<Genre> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);

        return "genres";
    }

    @GetMapping("/add")
    public String save(Model model) {
        Genre genre = new Genre();

        model.addAttribute("genre", genre);

        return "genreAdd";
    }

    @PostMapping("/add/save")
    public String save(@ModelAttribute(value = "genre") Genre genre) {
        genreService.save(genre);

        log.info(genre.toString());

        return "redirect:/genres";
    }

    @DeleteMapping("/delete")
    public String deleteGenre(@RequestParam long id) {
        genreService.delete(id);

        return "redirect:/genres";
    }
}
