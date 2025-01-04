package main

import (
	"fmt"
	"hash"
	"math/rand"

	"github.com/google/uuid"
	"github.com/spaolacci/murmur3"
)

var mHasher hash.Hash32

func init() {
	mHasher = murmur3.New32WithSeed(uint32(rand.Int()))
}

func murmurhash(key string, size uint32) uint32 {
	mHasher.Write([]byte(key))
	result := mHasher.Sum32() % size
	mHasher.Reset()
	return result
}

type BloomFilter struct {
	filter []int8
	size   uint32
}

func NewBloomFilter(size uint32) *BloomFilter {
	return &BloomFilter{
		filter: make([]int8, size),
		size:   size,
	}
}

func (b *BloomFilter) Add(key string) {
	idx := murmurhash(key, b.size)

	// as each int8 can have 8 bits
	arrIndex := idx / 8
	bitIndex := idx % 8

	b.filter[arrIndex] = b.filter[arrIndex] | (1 << bitIndex)
}

func (b *BloomFilter) Exists(key string) bool {
	idx := murmurhash(key, b.size)

	// as each int8 can have 8 bits
	arrIndex := idx / 8
	bitIndex := idx % 8

	return (b.filter[arrIndex] & (1 << bitIndex)) > 0
}

func main() {

	list_to_add := make([]string, 0)
	list_to_check := make([]string, 0)

	for i := 0; i < 1000; i++ {
		u := uuid.New()
		list_to_add = append(list_to_add, u.String())
	}

	for i := 0; i < 500; i++ {
		u := uuid.New()
		list_to_check = append(list_to_check, u.String())
	}

	for j := 1000; j < 100000; j += 1000 {

		bloom := NewBloomFilter(uint32(j))

		for _, key := range list_to_add {
			bloom.Add(key)
		}

		match_count := 0

		for _, key := range list_to_check {
			if bloom.Exists(key) {
				match_count++
			}
		}

		fmt.Println(j, " - ", float32(match_count)/float32(len(list_to_check))*100.0)
	}

}
