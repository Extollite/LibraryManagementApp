package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookHistoryRepository bookHistoryRepository;
    private final BookMapper bookMapper;

    @Transactional
    public List<BookDto> getAllBooksToDisplay() {
        return bookMapper.mapAsList(bookRepository.findAll())
                .stream()
                .map(this::addNumberOfAvailableCopies)
                .collect(Collectors.toList());
    }

    private BookDto addNumberOfAvailableCopies(BookDto bookDto) {
        List<Long> bookInstanceIds =
                bookCopyRepository.findAllByBookId(bookDto.getId())
                        .stream()
                        .map(BookCopy::getId)
                        .collect(Collectors.toList());
        List<Long> notAvailableBookInstanceIds = bookHistoryRepository.findAllNotAvailable(bookInstanceIds);
        bookInstanceIds.removeAll(notAvailableBookInstanceIds);

        bookDto.setNumberOfAvailableCopies(bookInstanceIds.size());

        return bookDto;
    }
}
