package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDisplayDto;
import pl.rjsk.librarymanagement.model.entity.BookRepresentation;
import pl.rjsk.librarymanagement.repository.BookRepository;
import pl.rjsk.librarymanagement.repository.BookRepresentationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookRepresentationRepository bookRepresentationRepository;
    private final BookMapper bookMapper;

    @Transactional
    public List<BookDisplayDto> getAllBooksToDisplay() {
        return bookMapper.mapAsList(bookRepository.findAll())
                .stream()
                .map(this::addBookRepresentationIds)
                .collect(Collectors.toList());
    }

    private BookDisplayDto addBookRepresentationIds(BookDisplayDto bookDisplayDto) {
        List<Long> bookRepresentationIds =
                bookRepresentationRepository.findAllByBookId(bookDisplayDto.getId())
                        .stream()
                        .map(BookRepresentation::getId)
                        .collect(Collectors.toList());
        bookDisplayDto.setBookRepresentationIds(bookRepresentationIds);

        return bookDisplayDto;
    }
}
