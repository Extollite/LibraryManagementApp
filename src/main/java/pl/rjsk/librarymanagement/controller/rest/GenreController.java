package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }
}
