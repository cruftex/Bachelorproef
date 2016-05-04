package desmedt.frederik.cachebenchmarking.benchmark;

import com.google.common.cache.Cache;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.cache.CacheLIRS;

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
    }

    public static class ZipfRead extends BaseBenchmark.ZipfRead<Integer> {

        private Random random = new Random();
        private Cache<Integer, Integer> lirsCache;
        private int cacheSize;

        public ZipfRead(int cacheSize, Integer lowerBound, Integer upperBound) {
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
            Integer result = lirsCache.getIfPresent(key);
            boolean present = result != null;
            return present;
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Insert(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Delete(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Cache<Integer, Integer> lirsCache;
        private Random random = new Random();

        public Update(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
    }
}
