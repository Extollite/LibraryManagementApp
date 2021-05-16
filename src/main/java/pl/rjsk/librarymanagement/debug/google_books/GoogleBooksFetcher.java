package pl.rjsk.librarymanagement.debug.google_books;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import pl.rjsk.librarymanagement.model.entity.Genre;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleBooksFetcher {

    private final static String API_KEY = "AIzaSyD3vKbaBMG_t_mfEzTpJt2_GmqR3Nu8LCs";

    private static List<Volume> queryGoogleBooks(JsonFactory jsonFactory, String query, long categoryId) throws Exception {
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
                .filter(v -> !v.getVolumeInfo().getAuthors().isEmpty())
                .collect(Collectors.toList());
    }

    public static List<Volume> fetchBooksFromAPI(String title, Genre genre) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        String genreName = genre.getName();
        long genreId = genre.getId();

        String query = title + "+subject:" + genreName;

        try {
            return queryGoogleBooks(jsonFactory, query, genreId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return Collections.emptyList();
    }
}