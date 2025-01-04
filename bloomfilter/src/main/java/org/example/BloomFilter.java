package org.example;

import org.example.hash.HashingStrategy;

import java.util.Arrays;

public class BloomFilter {
    private final byte[] filter;
    private final int size;
    private final HashingStrategy hashingStrategy;

    public BloomFilter(int size, HashingStrategy hashingStrategy) {
        this.size = size;
        filter = new byte[size];
        this.hashingStrategy = hashingStrategy;
    }

    public void add(String key) {
        int hash = hashingStrategy.hash(key, size);

        // as each byte can have 8 bits
        int arrIndex = hash / 8;
        int bitIndex = hash % 8;

        filter[arrIndex] = (byte)(filter[arrIndex] | (1 << bitIndex));
    }

    public boolean doesExist(String key) {
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
