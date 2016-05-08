package desmedt.frederik.cachebenchmarking.benchmark;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.benchmark.BaseBenchmark.Read;
import desmedt.frederik.cachebenchmarking.generator.Generator;

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

    public static class Read extends BaseBenchmark.Read<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public Read(String traceTag, Generator<Integer> traceGenerator, double cacheRatio, Integer lowerBound, Integer upperBound) {
            super("Guava", traceTag, traceGenerator, cacheRatio, lowerBound, upperBound);
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

        public Insert(double cacheRatio, int lowerBound, int upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
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
            return CacheStats.nonRead(StatType.INSERT, getCacheSize(), (int) cache.size());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

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
            return CacheStats.nonRead(StatType.DELETE, getCacheSize(), (int) cache.size());
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Random random = new Random();
        private Cache<Integer, Integer> cache;

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
            return CacheStats.nonRead(StatType.UPDATE, getCacheSize(), (int) cache.size());
        }
    }
}
