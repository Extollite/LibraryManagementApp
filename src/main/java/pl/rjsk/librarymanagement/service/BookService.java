package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithCopiesDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookCopy;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.List;
import java.util.Set;
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
        return bookMapper.mapIterableToDto(bookRepository.findAll())
                .stream()
                .map(this::addNumberOfAvailableCopies)
                .collect(Collectors.toList());
    }

    private BookDto addNumberOfAvailableCopies(BookDto bookDto) {
        List<Long> bookCopyIds =
                bookCopyRepository.findAllByBookId(bookDto.getId())
                        .stream()
                        .map(BookCopy::getId)
                        .collect(Collectors.toList());
        List<Long> notAvailableBookCopyIds = bookHistoryRepository.findAllNotAvailable(bookCopyIds);
        bookCopyIds.removeAll(notAvailableBookCopyIds);

        bookDto.setNumberOfAvailableCopies(bookCopyIds.size());

        return bookDto;
    }

    @Transactional
    public Page<BookWithCopiesDto> getAllBooksWithInstances(Pageable paging) {
        Page<Book> bookPage = bookRepository.findAll(paging);

        List<BookWithCopiesDto> bookList = bookMapper.mapPageToDtoWithCopies(bookPage)
                .stream()
                .map(this::addBookCopies)
                .collect(Collectors.toList());

        return new PageImpl<>(bookList, bookPage.getPageable(), bookPage.getTotalElements());
    }

    private BookWithCopiesDto addBookCopies(BookWithCopiesDto bookWithCopiesDto) {
        Set<Long> bookCopyIds =
                bookCopyRepository.findAllByBookId(bookWithCopiesDto.getId())
                        .stream()
                        .map(BookCopy::getId)
                        .collect(Collectors.toSet());

        bookWithCopiesDto.setBookCopyIds(bookCopyIds);

        return bookWithCopiesDto;
    }
}
