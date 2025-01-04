package main

import (
	"fmt"
	"hash"
	"math/rand"

	"github.com/google/uuid"
	"github.com/spaolacci/murmur3"
)

var hashFuncs []hash.Hash32

func init() {
	hashFuncs = make([]hash.Hash32, 0)

}

func createHash(size int) {
	for i := 0; i < size; i++ {
		hashFuncs = append(hashFuncs, murmur3.New32WithSeed(rand.Uint32()))
	}
}

func murmurhash(key string, size uint32, idx int) uint32 {
	hashFuncs[idx].Write([]byte(key))
	result := hashFuncs[idx].Sum32() % size
	hashFuncs[idx].Reset()
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

func (b *BloomFilter) Add(key string, numOfHashFns int) {

	for i := 0; i < numOfHashFns; i++ {
		idx := murmurhash(key, b.size, i)

		// as each int8 can have 8 bits
		arrIndex := idx / 8
		bitIndex := idx % 8

		b.filter[arrIndex] = b.filter[arrIndex] | (1 << bitIndex)
	}
}

func (b *BloomFilter) Exists(key string, numOfHashFns int) bool {
	for i := 0; i < numOfHashFns; i++ {
		idx := murmurhash(key, b.size, i)

		// as each int8 can have 8 bits
		arrIndex := idx / 8
		bitIndex := idx % 8

		exist := b.filter[arrIndex]&(1<<bitIndex) > 0
		if !exist {
			return false
		}

	}

	return true
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

	// checkForVariableSizeOfFilter(list_to_add, list_to_check)
	checkForMultipleHashes(list_to_add, list_to_check)

}

func checkForMultipleHashes(list_to_add []string, list_to_check []string) {
	for j := 1; j < 100; j++ {

		createHash(j)

		bloom := NewBloomFilter(uint32(10000))

		for _, key := range list_to_add {
			bloom.Add(key, j)
		}

		match_count := 0

		for _, key := range list_to_check {
			if bloom.Exists(key, j) {
				match_count++
			}
		}

		fmt.Println(j, " - ", float32(match_count)/float32(len(list_to_check))*100.0)
	}
}

func checkForVariableSizeOfFilter(list_to_add []string, list_to_check []string) {
	for j := 1000; j < 100000; j += 1000 {

		createHash(1)

		bloom := NewBloomFilter(uint32(j))

		for _, key := range list_to_add {
			bloom.Add(key, 1)
		}

		match_count := 0

		for _, key := range list_to_check {
			if bloom.Exists(key, 1) {
				match_count++
			}
		}

		fmt.Println(j, " - ", float32(match_count)/float32(len(list_to_check))*100.0)
	}
}
