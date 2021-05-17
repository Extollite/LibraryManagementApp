package pl.rjsk.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RandomPasswordGeneratorService {

    private final SecureRandom secureRandom;

    public String generateSecureRandomPassword() {
        List<Character> charList = Stream.of(
                getRandomNumbers(2),
                getRandomSpecialChars(2),
                getRandomAlphabets(2, true),
                getRandomAlphabets(4, false))
                .flatMapToInt(i -> i)
                .mapToObj(character -> (char) character)
                .collect(Collectors.toList());
        Collections.shuffle(charList);
        return charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private IntStream getRandomAlphabets(int count, boolean upperCase) {
        if (upperCase) {
            return secureRandom.ints(count, 65, 90);
        }
        return secureRandom.ints(count, 97, 122);
    }

    private IntStream getRandomNumbers(int count) {
        return secureRandom.ints(count, 48, 57);
    }

    private IntStream getRandomSpecialChars(int count) {
        return secureRandom.ints(count, 33, 45);
    }
}
