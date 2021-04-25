package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDisplayDto;
import pl.rjsk.librarymanagement.model.entity.BookInstance;
import pl.rjsk.librarymanagement.repository.BookInstanceRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookInstanceRepository bookInstanceRepository;
    private final BookMapper bookMapper;

    @Transactional
    public List<BookDisplayDto> getAllBooksToDisplay() {
        return bookMapper.mapAsList(bookRepository.findAll())
                .stream()
                .map(this::addBookInstanceIds)
                .collect(Collectors.toList());
    }

    private BookDisplayDto addBookInstanceIds(BookDisplayDto bookDisplayDto) {
        List<Long> bookInstanceIds =
                bookInstanceRepository.findAllByBookId(bookDisplayDto.getId())
                        .stream()
                        .map(BookInstance::getId)
                        .collect(Collectors.toList());
        bookDisplayDto.setBookInstanceIds(bookInstanceIds);

        return bookDisplayDto;
    }
}
