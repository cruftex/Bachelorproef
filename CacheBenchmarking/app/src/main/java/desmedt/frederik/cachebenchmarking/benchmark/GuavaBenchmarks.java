package desmedt.frederik.cachebenchmarking.benchmark;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;

/**
 * Contains a collection of Guava benchmarks as inner classes.
 */
public class GuavaBenchmarks {

    public static final String CACHE_TAG = "Guava";

    private static Cache<Integer, Integer> createCache(int cacheSize) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .recordStats()
                .build();
    }

    public static class RandomRead extends BaseBenchmark.RandomRead<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public RandomRead(int cacheSize, int lowerBound, int upperBound) {
            super("Guava", cacheSize, lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.getIfPresent(key) != null;
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
            cache = GuavaBenchmarks.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.invalidateAll();
            cache = null;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read((int) cache.stats().hitCount(), (int) cache.stats().missCount(), getCacheSize(), (int) cache.size());
        }
    }

    public static class ZipfRead extends BaseBenchmark.ZipfRead<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public ZipfRead(int cacheSize, int lowerBound, int upperBound) {
            super("Guava", cacheSize, lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.getIfPresent(key) != null;
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
            cache = GuavaBenchmarks.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.invalidateAll();
            cache = null;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read((int) cache.stats().hitCount(), (int) cache.stats().missCount(), getCacheSize(), (int) cache.size());
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private final Random random = new Random();
        private Cache<Integer, Integer> cache;

        private int nextKey = 0;

        public Insert(int cacheSize, int lowerBound, int upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
        }

        @Override
        protected void removeElement(Integer key) {
            cache.invalidate(key);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            cache = GuavaBenchmarks.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.invalidateAll();
            cache = null;
        }

        @Override
        public boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) cache.size());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public Delete(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
            cache = GuavaBenchmarks.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.invalidateAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.invalidate(key);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) cache.size());
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Random random = new Random();
        private Cache<Integer, Integer> cache;

        public Update(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
            cache = GuavaBenchmarks.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.invalidateAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) cache.size());
        }
    }
}
