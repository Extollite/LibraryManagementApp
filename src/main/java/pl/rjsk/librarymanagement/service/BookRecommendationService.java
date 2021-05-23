package pl.rjsk.librarymanagement.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rjsk.librarymanagement.mapper.BookMapper;
import pl.rjsk.librarymanagement.model.dto.BookDto;
import pl.rjsk.librarymanagement.model.entity.Book;
import pl.rjsk.librarymanagement.model.entity.BookRating;
import pl.rjsk.librarymanagement.model.entity.BookRecommendation;
import pl.rjsk.librarymanagement.model.entity.Keyword;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.repository.BookRatingRepository;
import pl.rjsk.librarymanagement.repository.BookRecommendationRepository;
import pl.rjsk.librarymanagement.repository.BookRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookRecommendationService {

    private final BookMapper bookMapper;
    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final BookRecommendationRepository bookRecommendationRepository;

    //Set default value for testing purposes
    @Value("${recommendation.rated.books.min-amount:20}")
    private long minRatedBookToCalculate = 3;
    @Value("${recommendation.recommended.books.amount:20}")
    private long recommendedBooksAmount = 3;

    @Transactional
    public List<BookRecommendation> recalculateRecommendations(User user) {
        List<BookRating> bookRatings = bookRatingRepository.findAllByUser(user);

        if (bookRatings.size() < minRatedBookToCalculate) {
            return Collections.emptyList();
        }

        bookRecommendationRepository.deleteAllByUser(user);

        Map<Keyword, CurrentAvg> keywordRating = calculateKeywordWeights(bookRatings);

        double userKeywordsWeights = Math.sqrt(keywordRating.values()
                .stream()
                .mapToDouble(CurrentAvg::getCurrentAvg)
                .sum());

        Set<Long> ratedBooksIds = bookRatings
                .stream()
                .map(BookRating::getBook)
                .map(Book::getId)
                .collect(Collectors.toSet());

        List<BookRecommendation> bookRecommendations = new ArrayList<>();

        for (var book : bookRepository.findAllByIdNotIn(ratedBooksIds)) {
            double similarity = calculateSimilarity(book.getKeywords(), keywordRating, userKeywordsWeights);
            if (similarity != 0D) {
                var recommendation = new BookRecommendation();
                recommendation.setBook(book);
                recommendation.setUser(user);
                recommendation.setSimilarityRatio(similarity);

                bookRecommendations.add(recommendation);
            }
        }

        bookRecommendations = bookRecommendations
                .stream()
                .sorted(Comparator.comparingDouble(BookRecommendation::getSimilarityRatio).reversed())
                .limit(recommendedBooksAmount)
                .collect(Collectors.toList());

//        log.info("Start");
//        for (var similar : bookRecommendations) {
//            Set<Keyword> same = new HashSet<>(similar.getBook().getKeywords());
//            same.retainAll(keywordRating.keySet());
//
//            log.info(similar.getBook().getTitle() + " " + similar.getSimilarityRatio() + "\n"
//                    + similar.getBook().getKeywords().stream().map(Keyword::getName).sorted().collect(Collectors.joining(" ")) + "\n" +
//                    keywordRating.keySet().stream().map(Keyword::getName).sorted().collect(Collectors.joining(" ")) + "\n" +
//                    same.stream().map(Keyword::getName).sorted().collect(Collectors.joining(" ")));
//        }

        return bookRecommendationRepository.saveAll(bookRecommendations);
    }

    public List<BookDto> getRecommendedBooks(User user) {
         return bookRecommendationRepository.getAllByUserOrderBySimilarityRatioDesc(user).stream()
                .map(BookRecommendation::getBook)
                .map(bookMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public long getMinRatedBookToCalculate() {
        return minRatedBookToCalculate;
    }

    private Map<Keyword, CurrentAvg> calculateKeywordWeights(List<BookRating> bookRatings) {
        Map<Keyword, CurrentAvg> keywordRating = new HashMap<>();

        for (var rating : bookRatings) {
            for (var keyword : rating.getBook().getKeywords()) {
                CurrentAvg currentAvg = keywordRating.getOrDefault(keyword, new CurrentAvg());

                CurrentAvg avg = calculateCurrentAverage(currentAvg, rating.getRating());

                keywordRating.put(keyword, avg);
            }
        }

        return keywordRating;
    }

    private CurrentAvg calculateCurrentAverage(CurrentAvg currentAvg, long rating) {
        double avg = currentAvg.getCurrentAvg() + rating;
        long elements = currentAvg.getElements() + 1;
        currentAvg.setCurrentAvg(avg / elements);
        currentAvg.setElements(elements);

        return currentAvg;
    }

    private double calculateSimilarity(Set<Keyword> bookKeywords, Map<Keyword, CurrentAvg> userKeywordsProfile, double userKeywordsWeights) {
        Set<Keyword> sameKeywords = new HashSet<>(bookKeywords);
        sameKeywords.retainAll(userKeywordsProfile.keySet());

        if (sameKeywords.isEmpty()) {
            return 0D;
        }

        List<Double> sameKeywordsWeightsList = sameKeywords
                .stream()
                .map(keyword -> userKeywordsProfile.get(keyword).getCurrentAvg())
                .sorted()
                .collect(Collectors.toList());
        double sameKeywordsWeightsSum = sameKeywordsWeightsList
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        double sameKeywordsWeightsMedian = getListMedian(sameKeywordsWeightsList);

        var defaultAvg = new CurrentAvg(sameKeywordsWeightsMedian, 0);

        double bookKeywordsWeights = Math.sqrt(bookKeywords
                .stream()
                .mapToDouble(keyword -> userKeywordsProfile.getOrDefault(keyword, defaultAvg).getCurrentAvg())
                .sum());

        return sameKeywordsWeightsSum / (userKeywordsWeights * bookKeywordsWeights);
    }

    private double getListMedian(List<Double> keywordsWeights) {
        int listSize = keywordsWeights.size();

        if (listSize % 2 != 0)
            return keywordsWeights.get(listSize / 2);

        return (keywordsWeights.get((listSize - 1) / 2) + keywordsWeights.get(listSize / 2)) / 2.0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CurrentAvg {

        private double currentAvg = 0D;
        private long elements = 0;
    }
}
