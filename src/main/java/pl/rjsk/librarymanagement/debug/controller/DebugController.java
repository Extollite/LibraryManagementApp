package pl.rjsk.librarymanagement.debug.controller;

import com.google.api.services.books.v1.model.Volume;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.debug.google_books.GoogleBooksSaver;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debug")
public class DebugController {
    
    private final GoogleBooksSaver googleBooksSaver;
    private final GenreService genreService;

    @GetMapping("/gapi/queryBooks")
    public List<Volume> queryGoogleApi(@RequestParam String query, @RequestParam long genreId) {
        Genre genre = genreService.getById(genreId);
        return googleBooksSaver.saveBooksFromQuery(genre, query);
    }
}
