package desmedt.frederik.cachebenchmarking.benchmark;

import android.support.v4.util.LruCache;
import android.util.Pair;

import java.util.Random;
import java.util.StringTokenizer;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * Contains a collection of native LRU benchmarks as inner classes.
 */
public class NativeLruBenchmarks {

    public static final String CACHE_TAG = "NativeLru";

    public static class Update extends BaseBenchmark.Update<Integer> {

        private LruCache cache;
        private final Random random = new Random();

        public Update(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected void addToCache(int key, Integer value) {
            cache.put(key, value);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            cache = new LruCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.evictAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.UPDATE, cache.size(), cache.maxSize());
        }
    }

    public static class Read extends BaseBenchmark.Read<Integer> {

        private LruCache cache;
        private final Random random = new Random();

        public Read(String traceTag, Generator<Integer> traceGenerator, double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, traceTag, traceGenerator, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected void addToCache(Integer key, Integer value) {
            cache.put(key, value);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            cache = new LruCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.evictAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.get(key) != null;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read(cache.hitCount(), cache.missCount(), cache.maxSize(), cache.size());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private LruCache cache;
        private final Random random = new Random();

        public Delete(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        public void addToCache(int key, Integer value) {
            cache.put(key, value);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            cache = new LruCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.evictAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.remove(key);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.DELETE, cache.maxSize(), cache.size());
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private LruCache cache;
        private final Random random = new Random();

        public Insert(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected void removeElement(Integer key) {
            cache.remove(key);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            cache = new LruCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.evictAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.INSERT, cache.maxSize(), cache.size());
        }
    }
}
