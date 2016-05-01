package desmedt.frederik.cachebenchmarking.benchmark;

import android.support.v4.util.LruCache;
import android.util.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.generator.RandomNumberGenerator;

/**
 * Contains a collection of native LRU benchmarks as inner classes.
 */
public class NativeLruBenchmarks {

    public static class Update extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final LruCache cache;
        private final RandomNumberGenerator generator;

        public Update(int cacheSize, int lowerBound, int upperBound) {
            super("NativeLruUpdate (" + cacheSize + ")", lowerBound, upperBound);
            cache = new LruCache(cacheSize);
            generator = new RandomNumberGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), generator.next());
        }
    }

    public static class RandomRead extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final LruCache cache;
        private final Random random = new Random();
        private final RandomNumberGenerator generator;

        public RandomRead(int cacheSize, int lowerBound, int upperBound) {
            super("NativeLruRRead (" + cacheSize + ")", lowerBound, upperBound);
            cache = new LruCache(cacheSize);
            generator = new RandomNumberGenerator(lowerBound, upperBound);

            for (int i = 0; i < upperBound || i < cacheSize; i++) {
                cache.put(i + lowerBound, random.nextInt());
            }
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.get(key) != null;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), generator.next());
        }
    }

    public static class Delete extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final LruCache cache;
        private final Random random = new Random();
        private final RandomNumberGenerator generator;
        private int nextKey;

        public Delete(int cacheSize, int lowerBound, int upperBound) {
            super("NativeLruDelete (" + cacheSize + ")", lowerBound, upperBound);
            cache = new LruCache(cacheSize);
            generator = new RandomNumberGenerator(lowerBound, upperBound);

            for (int i = 0; i < upperBound - lowerBound; i++) {
                cache.put(i + lowerBound, random.nextInt());
            }
        }

        @Override
        protected void prepare() {
            nextKey = generator.next();
            cache.put(nextKey, random.nextInt());
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.remove(nextKey);
            return true;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), 0);
        }
    }

    public static class Insert extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final LruCache cache;
        private final Random random = new Random();

        private int nextKey = 0;

        public Insert(int cacheSize, int lowerBound, int upperBound) {
            super("NativeLruInsert (" + cacheSize + ")", lowerBound, upperBound);
            cache = new LruCache(cacheSize);
        }

        @Override
        protected void cleanup() {
            nextKey = getLowerKeyBound() + random.nextInt(getUpperKeyBound());
            cache.remove(nextKey);
        }

        @Override
        public boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        public Pair<Integer, Integer> generateInput() {
            return new Pair<>(nextKey, random.nextInt());
        }
    }
}
