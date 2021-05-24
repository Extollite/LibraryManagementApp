package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pl.rjsk.librarymanagement.exception.ResourceNotFoundException;
import pl.rjsk.librarymanagement.mapper.BookCopyMapper;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.model.entity.BookHistory;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookHistoryRepository bookHistoryRepository;
    private final BookCopyMapper bookCopyMapper;
    private final Clock systemClock;

    @Transactional
    public void deleteBookCopy(long copyId) {
        bookHistoryRepository.deleteAllByBookCopyId(copyId);
        bookCopyRepository.deleteById(copyId);
    }

    public List<BookCopyDueDateDto> getAllByBookId(long bookId) {
        return bookCopyMapper.mapAsList(bookCopyRepository.findAllByBookId(bookId))
                .stream()
                .map(this::addDueOrReturnedDate)
                .collect(Collectors.toList());
    }

    private BookCopyDueDateDto addDueOrReturnedDate(BookCopyDueDateDto bookCopyDto) {
        var dueDate =
                bookHistoryRepository.findDueDate(bookCopyDto.getId());
        if (dueDate.isPresent()) {
            bookCopyDto.setDueDate(dueDate.get());
            bookCopyDto.setAvailable(false);
        } else {
            bookCopyDto.setAvailable(true);
        }

        return bookCopyDto;
    }

    @Transactional
    public BookCopyDueDateDto getByCopyId(long id) {
        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to fetch book copy with given id: " + id));

        return addDueOrReturnedDate(bookCopyMapper.mapToDto(bookCopy));
    }

    @Transactional
    public BookCopyDueDateDto saveBookCopy(BookCopyDueDateDto bookCopyDto) {
        if (!StringUtils.hasLength(bookCopyDto.getAlternativeTitle())) {
            bookCopyDto.setAlternativeTitle(null);
        }

        BookCopy bookCopy = bookCopyMapper.mapToEntity(bookCopyDto);

        bookCopyRepository.save(bookCopy);

        return bookCopyMapper.mapToDto(bookCopy);
    }

    @Transactional
    public BookCopy updateBookCopy(BookCopyDueDateDto bookCopyDto) {
        log.info(bookCopyDto.toString());
        var bookCopyToUpdate = bookCopyRepository.findById(bookCopyDto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unable to fetch book copy with given id: "
                                + bookCopyDto.getId()));

        if (!StringUtils.hasLength(bookCopyDto.getAlternativeTitle())) {
            bookCopyDto.setAlternativeTitle(null);
        }

        bookCopyToUpdate.setAlternativeTitle(bookCopyDto.getAlternativeTitle());
        bookCopyToUpdate.setLanguageCode(bookCopyDto.getLanguageCode());
        bookCopyToUpdate.setPublisherName(bookCopyDto.getPublisherName());
        bookCopyToUpdate.setYearOfRelease(bookCopyDto.getYearOfRelease());
        bookCopyToUpdate.setPagesCount(bookCopyDto.getPagesCount());

        var bookHistoryOptional =
                bookHistoryRepository.findNotReturnedByBookId(bookCopyDto.getId());
        if (bookHistoryOptional.isPresent()) {
            var bookHistory = bookHistoryOptional.get();
            if (bookCopyDto.isAvailable()) {
                bookHistory.setReturnedDate(OffsetDateTime.now(systemClock));
            } else if (bookCopyDto.getDueDate() != null
                    && bookHistory.getDueDate().isBefore(bookCopyDto.getDueDate())) {
                bookHistory.setDueDate(bookCopyDto.getDueDate());
            }
        } else if (!bookCopyDto.isAvailable()) {
            BookHistory bookHistory = new BookHistory();
            bookHistory.setBookCopy(bookCopyToUpdate);
            bookHistory.setDueDate(bookCopyDto.getDueDate());
            bookHistoryRepository.save(bookHistory);
        }

        return bookCopyToUpdate;
    }
}
