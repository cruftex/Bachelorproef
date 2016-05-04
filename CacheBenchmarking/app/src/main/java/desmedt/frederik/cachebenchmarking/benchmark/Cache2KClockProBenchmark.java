package desmedt.frederik.cachebenchmarking.benchmark;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheBuilder;
import org.cache2k.core.IntegrityState;

import java.util.Random;
import java.util.UUID;

/**
 * Contains a collection of ClockPro benchmarks (by Cache2K) as inner classes.
 */
public class Cache2KClockProBenchmark {

    public static final String CACHE_TAG = "ClockPro";

    private static Cache<Integer, Integer> createCache(int maxSize) {
        return Cache2kBuilder.of(Integer.class, Integer.class)
                .name(UUID.randomUUID().toString())
                .eternal(true)
                .entryCapacity(maxSize)
                .build();
    }

    public static class RandomRead extends BaseBenchmark.RandomRead<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public RandomRead(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
            cache = Cache2KClockProBenchmark.createCache(cacheSize);
            cache = null;
        }

        @Override
        protected void clearCache() {
            cache.close();
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.peek(key) != null;
        }
    }

    public static class ZipfRead extends BaseBenchmark.ZipfRead<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public ZipfRead(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
            cache = Cache2KClockProBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.close();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.peek(key) != null;
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

        public Insert(int cacheSize, Integer lowerBound, Integer upperBound) {
            super(CACHE_TAG, cacheSize, lowerBound, upperBound);
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
            cache = Cache2KClockProBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.close();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Cache<Integer, Integer> cache;
        private Random random = new Random();

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
            cache = Cache2KClockProBenchmark.createCache(cacheSize);
            cache = null;
        }

        @Override
        protected void clearCache() {
            cache.close();
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
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
            cache = Cache2KClockProBenchmark.createCache(cacheSize);
        }

        @Override
        protected void clearCache() {
            cache.close();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.remove(key);
            return true;
        }
    }
}
