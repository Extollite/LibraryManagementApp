package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional
    public void deleteGenre(long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unable to delete genre with given id: " + id));

        Iterable<Book> books = bookRepository.findAll();

        for (Book book : books) {
            if (book.getGenre().equals(genre)) {
                book.setGenre(null);
            }
        }

        genreRepository.deleteById(id);
    }

    @Transactional
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }
}
