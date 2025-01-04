package org.example.hash;


import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MurMurHashing implements HashingStrategy {

    private final HashFunction hashFunction;
    public MurMurHashing() {
        this.hashFunction = Hashing.murmur3_32_fixed(new Random().nextInt());
    }

    @Override
    public int hash(String key, int size) {

        return (this.hashFunction
                .newHasher()
                .putString(key, StandardCharsets.UTF_8)
                .hash().hashCode() & Integer.MAX_VALUE) % size;
    }
}
