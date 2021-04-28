package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rjsk.librarymanagement.mapper.BookCopyMapper;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.repository.BookCopyRepository;
import pl.rjsk.librarymanagement.repository.BookHistoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookHistoryRepository bookHistoryRepository;
    private final BookCopyMapper bookCopyMapper;

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
}
