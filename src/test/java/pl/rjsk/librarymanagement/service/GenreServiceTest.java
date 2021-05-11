package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    private static final long GENRE_ID = 1L;
    
    @Mock
    private GenreRepository genreRepository;
    
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GenreService genreService;

    @Test
    void getAllGenres() {
        var genre = new Genre();

        when(genreRepository.findAll()).thenReturn(List.of(genre));

        List<Genre> result = genreService.getAllGenres();

        assertThat(result)
                .hasSize(1)
                .containsExactly(genre);

        verify(genreRepository).findAll();
    }
    
    @Test
    void delete_thrownException() {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> genreService.delete(GENRE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to delete genre with given id: " + GENRE_ID);
        
        verify(genreRepository).findById(eq(GENRE_ID));
    }
    
    @Test
    void delete() {
        var genre = new Genre(GENRE_ID);
        
        var book = new Book();
        book.setGenre(genre);
        
        when(genreRepository.findById(anyLong())).thenReturn(Optional.of(genre));
        when(bookRepository.findAll()).thenReturn(List.of(book));
        
        genreService.delete(GENRE_ID);
        
        assertThat(book.getGenre())
                .isNull();
        
        verify(genreRepository).findById(eq(GENRE_ID));
        verify(bookRepository).findAll();
    }
}