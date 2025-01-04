package org.example;

import org.example.hash.HashingStrategy;
import org.example.hash.MurMurHashing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {

        List<String> stringToAdd = IntStream.range(0, 1000)
                .mapToObj(i -> UUID.randomUUID().toString()).toList();

        List<String> stringToCheck = IntStream.range(0, 500)
                .mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList());

//        IntStream.iterate(1000, i -> i < 100000, i -> i + 1000)
//                .forEach(n -> checkWithVariableSizeOfBloomFilter(stringToAdd, stringToCheck, n));

        IntStream.iterate(1, i -> i <= 100, i -> i + 1)
                .forEach(n -> checkWithMultipleHashFunctions(stringToAdd, stringToCheck, n));
    }

    private static void checkWithMultipleHashFunctions(List<String> stringToAdd, List<String> stringToCheck, int hashSize) {
        List<HashingStrategy> hashFunctions = IntStream.range(0, hashSize)
                .mapToObj(i -> (HashingStrategy) new MurMurHashing()).toList();

        BloomFilter bloomFilter = new BloomFilter(10000, hashFunctions);

        stringToAdd.forEach(bloomFilter::add);

//        bloomFilter.printFilter();

        Map<Boolean, Long> collect = stringToCheck.stream().map(bloomFilter::doesExist)
                .collect(Collectors.partitioningBy(k -> (Boolean) k, Collectors.counting()));


        double percentage = (collect.get(Boolean.TRUE) / (double) stringToCheck.size()) * 100;
        System.out.println(hashFunctions.size() + " - " + percentage);

    }

    private static void checkWithVariableSizeOfBloomFilter(List<String> stringToAdd, List<String> stringToCheck, int size) {
        HashingStrategy murMurHashing = new MurMurHashing();
        BloomFilter bloomFilter = new BloomFilter(size, List.of(murMurHashing));

        stringToAdd.forEach(bloomFilter::add);

//        bloomFilter.printFilter();

        Map<Boolean, Long> collect = stringToCheck.stream().map(bloomFilter::doesExist)
                .collect(Collectors.partitioningBy(k -> (Boolean) k, Collectors.counting()));


        double percentage = (collect.get(Boolean.TRUE) / (double) stringToCheck.size()) * 100;
        System.out.println(size + " - " + percentage);
    }
}