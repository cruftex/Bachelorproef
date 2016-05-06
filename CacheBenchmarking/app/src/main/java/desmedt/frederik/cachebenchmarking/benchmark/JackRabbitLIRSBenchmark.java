package desmedt.frederik.cachebenchmarking.benchmark;

import com.google.common.cache.Cache;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.cache.CacheLIRS;
import desmedt.frederik.cachebenchmarking.generator.Generator;

/**
 * Contains a collection of LIRS cache benchmarks, by JackRabbit, as inner classes.
 */
public class JackRabbitLIRSBenchmark {

    private static final String CACHE_TAG = "LIRS";

    private static Cache<Integer, Integer> createCache(int maxSize) {
        return new CacheLIRS<>(maxSize);
    }

    public static class RandomRead extends BaseBenchmark.RandomRead<Integer> {

        private Random random = new Random();
        private Cache<Integer, Integer> lirsCache;

        public RandomRead(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            lirsCache = JackRabbitLIRSBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            lirsCache.invalidateAll();
            lirsCache = null;
        }

        @Override
        protected void addToCache(Integer key, Integer value) {
            lirsCache.put(key, value);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return lirsCache.getIfPresent(key) != null;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read((int) lirsCache.stats().hitCount(), (int) lirsCache.stats().missCount(), getCacheSize(), (int) lirsCache.size());
        }
    }

    public static class Read extends BaseBenchmark.Read<Integer> {

        private Random random = new Random();
        private Cache<Integer, Integer> lirsCache;

        public Read(String traceTag, Generator<Integer> traceGenerator, double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, traceTag, traceGenerator, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            lirsCache = JackRabbitLIRSBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            lirsCache.invalidateAll();
            lirsCache = null;
        }

        @Override
        protected void addToCache(Integer key, Integer value) {
            lirsCache.put(key, value);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            Integer result = lirsCache.getIfPresent(key);
            boolean present = result != null;
            return present;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read((int) lirsCache.stats().hitCount(), (int) lirsCache.stats().missCount(), getCacheSize(), (int) lirsCache.size());
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Insert(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected void removeElement(Integer key) {
            lirsCache.invalidate(key);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            lirsCache = JackRabbitLIRSBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            lirsCache.invalidateAll();
            lirsCache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return false;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) lirsCache.size());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Delete(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        public void addToCache(int key, Integer value) {
            lirsCache.put(key, value);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            lirsCache = JackRabbitLIRSBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            lirsCache.invalidateAll();
            lirsCache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            lirsCache.invalidate(key);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) lirsCache.size());
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Update(double cacheRatio, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheRatio, lowerBound, upperBound);
        }

        @Override
        protected void addToCache(int key, Integer value) {
            lirsCache.put(key, value);
        }

        @Override
        protected Integer generateValue() {
            return random.nextInt();
        }

        @Override
        protected void createCache(int cacheSize) {
            lirsCache = JackRabbitLIRSBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            lirsCache.invalidateAll();
            lirsCache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            lirsCache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(getCacheSize(), (int) lirsCache.size());
        }
    }
}
