package desmedt.frederik.cachebenchmarking.benchmark;

import android.util.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * Contains a collection of Guava benchmarks as inner classes.
 */
public class GuavaBenchmarks {

    public static class RandomRead extends CacheBenchmarkConfiguration<Integer, Object> {

        private final Cache<Integer, Object> cache;
        private final RandomGenerator generator;

        public RandomRead(int cacheSize, int lowerBound, int upperBound) {
            super("GuavaRRead (" + cacheSize + ")", lowerBound, upperBound);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheSize)
                    .build();

            generator = new RandomGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Object value) {
            return cache.getIfPresent(key) != null;
        }

        @Override
        protected Pair<Integer, Object> generateInput() {
            return new Pair<>(generator.next(), new Object());
        }

        @Override
        protected void cleanup(Integer key, Object value, boolean succeeded) {
            if (!succeeded) {
                cache.put(key, value);
            }
        }
    }

    public static class ZipfRead extends CacheBenchmarkConfiguration<Integer, Object> {

        private final Cache<Integer, Object> cache;
        private final Generator<Integer> generator;

        public ZipfRead(int cacheSize, int lowerBound, int upperBound) {
            super("GuavaZRead (" + cacheSize + ")", lowerBound, upperBound);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheSize)
                    .build();

            generator = new ZipfGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Object value) {
            return cache.getIfPresent(key) != null;
        }

        @Override
        protected Pair<Integer, Object> generateInput() {
            return new Pair<>(generator.next(), new Object());
        }

        @Override
        protected void cleanup(Integer key, Object value, boolean succeeded) {
            if (!succeeded) {
                cache.put(key, value);
            }
        }
    }

    public static class Insert extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final Random random = new Random();
        private final Cache<Integer, Integer> cache;

        private int nextKey = 0;

        public Insert(int cacheSize, int lowerBound, int upperBound) {
            super("GuavaInsert (" + cacheSize + ")", lowerBound, upperBound);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheSize)
                    .build();
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            nextKey = getLowerKeyBound() + random.nextInt(getUpperKeyBound());
            cache.invalidate(nextKey);
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

    public static class Delete extends CacheBenchmarkConfiguration<Integer, Object> {

        private final Cache<Integer, Integer> cache;
        private final RandomGenerator generator;
        private int lastKey = 0;

        public Delete(int cacheSize, int lowerBound, int upperBound) {
            super("GuavaDelete (" + cacheSize + ")", lowerBound, upperBound);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheSize)
                    .build();

            generator = new RandomGenerator(lowerBound, upperBound);

            for (int i = 0; i < upperBound - lowerBound; i++) {
                cache.put(i + lowerBound, generator.next());
            }
        }

        @Override
        protected void prepare() {
            lastKey = generator.next();
            cache.put(lastKey, 0);
        }

        @Override
        protected boolean run(Integer key, Object value) {
            cache.invalidate(key);
            return true;
        }

        @Override
        protected Pair<Integer, Object> generateInput() {
            return new Pair<>(lastKey, null);
        }

        @Override
        protected void cleanup(Integer key, Object value, boolean succeeded) {
            cache.put(lastKey, 0);
        }
    }

    public static class Update extends CacheBenchmarkConfiguration<Integer, Integer> {

        private Random random = new Random();
        private final Cache<Integer, Integer> cache;
        private final RandomGenerator generator;

        public Update(int cacheSize, int lowerBound, int upperBound) {
            super("GuavaUpdate (" + cacheSize + ")", lowerBound, upperBound);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(cacheSize)
                    .build();
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), random.nextInt());
        }
    }
}
