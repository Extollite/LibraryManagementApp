package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.AuthorMapper;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.entity.Author;
import pl.rjsk.librarymanagement.repository.AuthorRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public Author getByFirstLastName(String firstName, String lastName) {
        return authorRepository.findAuthorByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unable to find author with given firstName: " + firstName + " and lastName: " + lastName));
    }

    @Transactional
    public void delete(long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Unable to delete author with given id: " + authorId));
        for (var book : author.getBooks()) {
            book.getAuthors().remove(author);
        }
        authorRepository.delete(author);
    }

    @Transactional
    public AuthorDto save(AuthorDto authorDto) {
        Author author = authorRepository.save(authorMapper.mapToEntity(authorDto));

        return authorMapper.mapToDto(author);
    }
    
    @Transactional
    public List<AuthorDto> saveAll(Iterable<AuthorDto> authorDtos) {
        List<Author> authors = StreamSupport.stream(authorDtos.spliterator(), false)
                .map(authorMapper::mapToEntity)
                .collect(Collectors.toList());
        return authorMapper.mapAsList(authorRepository.saveAll(authors));
    }
}
