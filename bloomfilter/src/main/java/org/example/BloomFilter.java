package org.example;

import org.example.hash.HashingStrategy;

import java.util.Arrays;

public class BloomFilter {
    private final boolean[] filter;
    private final int size;
    private final HashingStrategy hashingStrategy;

    public BloomFilter(int size, HashingStrategy hashingStrategy) {
        this.size = size;
        filter = new boolean[size];
        this.hashingStrategy = hashingStrategy;
    }

    public void add(String key) {
        filter[hashingStrategy.hash(key, size)] = true;
    }

    public boolean doesExist(String key) {
        return filter[hashingStrategy.hash(key, size)];
    }

    public void printFilter() {
        System.out.println(Arrays.toString(filter));
    }

}
