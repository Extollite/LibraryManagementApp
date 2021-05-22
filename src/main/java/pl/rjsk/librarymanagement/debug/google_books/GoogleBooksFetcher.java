package pl.rjsk.librarymanagement.debug.google_books;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import pl.rjsk.librarymanagement.model.entity.Genre;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GoogleBooksFetcher {

    private final static String API_KEY = "AIzaSyD3vKbaBMG_t_mfEzTpJt2_GmqR3Nu8LCs";

    private static List<Volume> queryGoogleBooks(JsonFactory jsonFactory, String query, long categoryId) throws Exception {
        final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setApplicationName("BooksInfoFetcher")
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();

        log.info("Query: [" + query + "]");
        Books.Volumes.List volumesList = books.volumes().list(query);
        volumesList.setPrintType("books");
        volumesList.setLangRestrict("en");
        volumesList.setMaxResults(20L);

        Volumes volumes = volumesList.execute();

        for (var volume : volumes.getItems()) {

            var volumeInfo = volume.getVolumeInfo();
            log.info(volumeInfo.getTitle());
        }

        return volumes.getItems()
                .stream()
                .filter(v -> !CollectionUtils.isEmpty(v.getVolumeInfo().getAuthors()))
                .collect(Collectors.toList());
    }

    public static List<Volume> fetchBooksFromAPI(String title, Genre genre) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        String genreName = genre.getName();
        long genreId = genre.getId();

        String query = title + "+subject:" + genreName;

        try {
            return queryGoogleBooks(jsonFactory, query, genreId);
        } catch (Exception ex) {
            log.error("Exception: ", ex);
        }

        return Collections.emptyList();
    }
}