package io.github.lama06.llamagames.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {
    private static final Random RANDOM = new Random();

    public static <T> List<T> pickRandomElements(Collection<T> source, int limit, Random random) {
        List<T> sourceCopy = new ArrayList<>(source);
        List<T> result = new ArrayList<>();

        while ((result.size() < limit || limit == -1) && !sourceCopy.isEmpty()) {
            T randomElement = pickRandomElement(sourceCopy, random);
            sourceCopy.remove(randomElement);
            result.add(randomElement);
        }

        return result;
    }

    public static <T> List<T> pickRandomElements(Collection<T> source, int limit) {
        return pickRandomElements(source, limit, RANDOM);
    }

    public static <T> T pickRandomElement(Set<T> source, Random random) {
        if (source.isEmpty()) {
            return null;
        }

        int targetIndex = random.nextInt(source.size());
        int currentIndex = 0;

        for (T element : source) {
            if (currentIndex == targetIndex) {
                return element;
            }

            currentIndex++;
        }

        throw new IllegalStateException();
    }

    public static <T> T pickRandomElement(Set<T> source) {
        return pickRandomElement(source, RANDOM);
    }

    public static <T> T pickRandomElement(List<T> source, Random random) {
        if (source.isEmpty()) {
            return null;
        }

        return source.get(random.nextInt(source.size()));
    }

    public static <T> T pickRandomElement(List<T> list) {
        return pickRandomElement(list, RANDOM);
    }

    @SafeVarargs
    public static <T> Set<T> combineSets(Set<T>... sets) {
        return Stream.of(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }
}
