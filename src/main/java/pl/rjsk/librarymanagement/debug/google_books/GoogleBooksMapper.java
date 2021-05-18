package pl.rjsk.librarymanagement.debug.google_books;

import com.google.api.services.books.v1.model.Volume;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.dto.BookCopyDueDateDto;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.dto.BookWithKeywordsDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GoogleBooksMapper {

    public static BookDto mapVolumeToBook(Volume volume, long categoryId) {
        BookDto bookWithKeywordsDto = new BookDto();
        Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

        List<AuthorDto> authors = parseAuthors(volumeInfo.getAuthors());
        int releaseYear = Integer.parseInt(volumeInfo.getPublishedDate().substring(0, 4));

        bookWithKeywordsDto.setTitle(volumeInfo.getTitle());
        bookWithKeywordsDto.setAuthors(authors);
        bookWithKeywordsDto.setGenreId(categoryId);
        bookWithKeywordsDto.setYearOfFirstRelease(releaseYear);
        bookWithKeywordsDto.setDescription(volumeInfo.getDescription());
        bookWithKeywordsDto.setNumberOfAvailableCopies(0);

        return bookWithKeywordsDto;
    }

    private static List<AuthorDto> parseAuthors(List<String> authorsToParse) {
        return authorsToParse.stream()
                .map(a -> a.split(" "))
                .filter(a -> a.length >= 2)
                .map(a -> {
                    AuthorDto author = new AuthorDto();
                    String firstName = "";
                    for (int i = 0; i < a.length - 1; i++) {
                        firstName += a[i] + " ";
                    }
                    author.setFirstName(firstName.stripTrailing());
                    author.setLastName(a[a.length - 1]);

                    return author;
                })
                .collect(Collectors.toList());
    }

    public static BookWithKeywordsDto mapToBookWithKeywordsDto(BookDto bookDto, Set<Long> authorsIds) {
        BookWithKeywordsDto bookWithKeywordsDto = new BookWithKeywordsDto();

        bookWithKeywordsDto.setTitle(bookDto.getTitle());
        bookWithKeywordsDto.setGenreId(bookDto.getGenreId());
        bookWithKeywordsDto.setAuthorsIds(authorsIds);
        bookWithKeywordsDto.setYearOfFirstRelease(bookDto.getYearOfFirstRelease());
        bookWithKeywordsDto.setDescription(bookDto.getDescription());
        bookWithKeywordsDto.setKeywords("");

        return bookWithKeywordsDto;
    }

    public static BookCopyDueDateDto mapToBookCopyDueDateDto(Volume volume, BookWithKeywordsDto bookWithKeywordsDto) {
        Volume.VolumeInfo volInfo = volume.getVolumeInfo();
        String publishedDate = volInfo.getPublishedDate();
        int releaseYear = publishedDate != null ? Integer.parseInt(publishedDate.substring(0, 4)) : 0;
        String publisher = volInfo.getPublisher() == null ? "unknown" : volInfo.getPublisher();
        BookCopyDueDateDto bookCopyDto = new BookCopyDueDateDto();

        bookCopyDto.setAlternativeTitle(volInfo.getSubtitle());
        bookCopyDto.setBookId(bookWithKeywordsDto.getId());
        bookCopyDto.setLanguageCode(volInfo.getLanguage());
        bookCopyDto.setPublisherName(publisher);
        bookCopyDto.setYearOfRelease(releaseYear);
        bookCopyDto.setPagesCount(volInfo.getPageCount() != null ? volInfo.getPageCount() : 0);
        bookCopyDto.setAvailable(true);
        bookCopyDto.setDueDate(null);

        return bookCopyDto;
    }
}
