package org.example;

import org.example.hash.HashingStrategy;

import java.util.Arrays;
import java.util.List;

public class BloomFilter {
    private final byte[] filter;
    private final int size;
    private final List<HashingStrategy> hashingStrategies;

    public BloomFilter(int size, List<HashingStrategy> hashingStrategies) {
        this.size = size;
        filter = new byte[size];
        this.hashingStrategies = hashingStrategies;
    }

    public void add(String key) {
        hashingStrategies
                .forEach(hashingStrategy -> add(hashingStrategy, key));
    }

    public void add(HashingStrategy hashingStrategy, String key) {
        int hash = hashingStrategy.hash(key, size);

        // as each byte can have 8 bits
        int arrIndex = hash / 8;
        int bitIndex = hash % 8;

        filter[arrIndex] = (byte)(filter[arrIndex] | (1 << bitIndex));
    }

    public boolean doesExist(String key) {
        for (HashingStrategy hashingStrategy: hashingStrategies) {
            if (!doesExist(hashingStrategy, key)) return false;
        }
        return true;
    }


    public boolean doesExist(HashingStrategy hashingStrategy, String key) {
        int hash = hashingStrategy.hash(key, size);

        // as each byte can have 8 bits
        int arrIndex = hash / 8;
        int bitIndex = hash % 8;

        return (filter[arrIndex] & (1 << bitIndex)) > 0;
    }

    public void printFilter() {
        System.out.println(Arrays.toString(filter));
    }

}
