package pl.rjsk.librarymanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.repository.AuthorRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    private static final long AUTHOR_ID = 1L;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void delete_thrownException() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.delete(AUTHOR_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Unable to delete author with given id: " + AUTHOR_ID);

        verify(authorRepository).findById(eq(AUTHOR_ID));
    }

    @Test
    void delete() {
        var author = new Author(AUTHOR_ID);

        var book = new Book();
        book.setAuthors(new HashSet<>(Set.of(author)));

        author.setBooks(Set.of(book));

        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        authorService.delete(AUTHOR_ID);

        assertThat(book.getAuthors())
                .isEmpty();

        verify(authorRepository).findById(eq(AUTHOR_ID));
        verify(authorRepository).delete(eq(author));
    }
}