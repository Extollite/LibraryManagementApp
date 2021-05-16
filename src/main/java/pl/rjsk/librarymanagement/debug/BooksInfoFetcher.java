package pl.rjsk.librarymanagement.debug;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import pl.rjsk.librarymanagement.model.dto.AuthorDto;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.Genre;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BooksInfoFetcher {
    
    private final static String API_KEY = "AIzaSyD3vKbaBMG_t_mfEzTpJt2_GmqR3Nu8LCs";
    
    

    private static List<BookDto> queryGoogleBooks(JsonFactory jsonFactory, String query, long categoryId) throws Exception {
        final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setApplicationName("BooksInfoFetcher")
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();

        System.out.println("Query: [" + query + "]");
        Books.Volumes.List volumesList = books.volumes().list(query);
        volumesList.setPrintType("books");
        volumesList.setLangRestrict("en");
        volumesList.setMaxResults(40L);

        Volumes volumes = volumesList.execute();


        for (var volume : volumes.getItems()) {

            var volumeInfo = volume.getVolumeInfo();
            System.out.println(volumeInfo.getTitle());
            System.out.println();
        }
        
        return volumes.getItems().stream()
                .map(v -> mapVolumeToBook(v, categoryId))
                .filter(b -> !b.getAuthors().isEmpty())
                .collect(Collectors.toList());
    }

    private static BookDto mapVolumeToBook(Volume volume, long categoryId) {
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

    public static List<BookDto> fetchBooksFromAPI(String title, Genre genre) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            
        String categoryName = genre.getName();
        long categoryId = genre.getId();

        String query = title + "+subject:" + categoryName;

        try {
            return queryGoogleBooks(jsonFactory, query, categoryId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } 
    
        return Collections.emptyList();
    }
}