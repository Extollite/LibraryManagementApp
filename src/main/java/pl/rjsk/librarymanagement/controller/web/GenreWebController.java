package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

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

    @DeleteMapping("/delete")
    public String deleteGenre(@RequestParam long id) {
        genreService.deleteGenre(id);

        return "redirect:/genres";
    }
}
