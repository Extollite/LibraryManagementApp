package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.model.entity.Genre;
import pl.rjsk.librarymanagement.repository.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

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
}