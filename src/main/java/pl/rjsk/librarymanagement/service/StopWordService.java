package pl.rjsk.librarymanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class StopWordService {
    private final Set<String> keywordStopWords = new HashSet<>();

    @Value("${keywords.stopwords.filename}")
    private String keywordsStopWordsFilename;

    @PostConstruct
    private void loadKeywordStopWordsFromFile() {
        try {
            File file = ResourceUtils.getFile(keywordsStopWordsFilename);
            List<String> lines = Files.readAllLines(file.toPath());
            keywordStopWords.addAll(lines);
        } catch (IOException ex) {
            log.error("Exception: ", ex);
        }
    }

    public Set<String> getAllStopWords() {
        return new HashSet<>(keywordStopWords);
    }
}
