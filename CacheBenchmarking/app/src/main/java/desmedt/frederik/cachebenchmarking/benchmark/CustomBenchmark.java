package desmedt.frederik.cachebenchmarking.benchmark;

import android.util.Pair;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.benchmark.BaseBenchmark.Read;
import desmedt.frederik.cachebenchmarking.cache.Cache;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * A collection of cache benchmark configurations for every custom cache implemented.
 */
public class CustomBenchmark {

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Generator<Cache<Integer, Integer>> cacheGenerator;

        public Insert(String name, double cachedRatio, Integer lowerBound, Integer upperBound, Generator<Cache<Integer, Integer>> generator) {
            super(name, cachedRatio, lowerBound, upperBound);
            this.cacheGenerator = generator;
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
            cache = cacheGenerator.next();
        }

        @Override
        protected void clearCache() {
            cache.removeAll();
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

    public static class Read extends BaseBenchmark.Read<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Generator<Cache<Integer, Integer>> cacheGenerator;

        private int succeses = 0;
        private int failures = 0;

        public Read(String name, String traceTag, Generator<Integer> traceGenerator, double cachedRatio, Integer lowerBound, Integer upperBound, Generator<Cache<Integer, Integer>> generator) {
            super(name, traceTag, traceGenerator, cachedRatio, lowerBound, upperBound);
            this.cacheGenerator = generator;
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
            cache = cacheGenerator.next();
        }

        @Override
        protected void clearCache() {
            cache.removeAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.get(key) != null;
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            super.cleanup(key, value, succeeded);
            if (succeeded) {
                succeses++;
            } else {
                failures++;
            }
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read(succeses, failures, cache.maxSize(), cache.size());
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Generator<Cache<Integer, Integer>> cacheGenerator;

        public Update(String name, double cachedRatio, Integer lowerBound, Integer upperBound, Generator<Cache<Integer, Integer>> generator) {
            super(name, cachedRatio, lowerBound, upperBound);
            this.cacheGenerator = generator;
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
            cache = cacheGenerator.next();
        }

        @Override
        protected void clearCache() {
            cache.removeAll();
            cache = null;
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.UPDATE, cache.maxSize(), cache.size());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Generator<Cache<Integer, Integer>> cacheGenerator;

        public Delete(String name, double cachedRatio, Integer lowerBound, Integer upperBound, Generator<Cache<Integer, Integer>> generator) {
            super(name, cachedRatio, lowerBound, upperBound);
            this.cacheGenerator = generator;
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
            cache = cacheGenerator.next();
        }

        @Override
        protected void clearCache() {
            cache.removeAll();
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
}
