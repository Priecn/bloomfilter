package org.example.hash;

public interface HashingStrategy {
    int hash(String key, int size);
}
