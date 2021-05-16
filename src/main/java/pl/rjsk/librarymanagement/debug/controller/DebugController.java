package pl.rjsk.librarymanagement.debug.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.debug.BooksInfoSaver;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debug")
public class DebugController {
    private final BooksInfoSaver booksInfoSaver;
    private final GenreService genreService;
    
    @GetMapping("/gapi/queryBooks")
    public List<BookWithKeywordsDto> queryGoogleApi(@RequestParam String query, @RequestParam long genreId) {
        Genre genre = genreService.getById(genreId);
        return booksInfoSaver.saveBooksFromQuery(genre, query);
    }
}
