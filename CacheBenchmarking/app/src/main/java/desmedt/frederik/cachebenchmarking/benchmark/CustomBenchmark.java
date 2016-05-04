package desmedt.frederik.cachebenchmarking.benchmark;

import android.util.Pair;

import java.util.Random;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.cache.Cache;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * A collection of cache benchmark configurations for every custom cache implemented.
 */
public class CustomBenchmark {

    public static class Insert extends CacheBenchmarkConfiguration<Integer, Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final RandomGenerator generator;
        private int nextKey;

        public Insert(String name, Cache<Integer, Integer> cache, int lowerBound, int upperBound) {
            super(name + "Insert (" + cache.maxSize() + ")", lowerBound, upperBound);
            this.cache = cache;
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            nextKey = generator.next();
            cache.remove(nextKey);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.put(key, value);
            return true;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(nextKey, random.nextInt());
        }
    }

    public static class RandomRead extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final RandomGenerator generator;

        public RandomRead(String name, Cache<Integer, Integer> cache, int lowerBound, int upperBound) {
            super(name + "RRead (" + cache.maxSize() + ")", lowerBound, upperBound);
            this.cache = cache;
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.get(key) != null;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), generator.next());
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            if (!succeeded) {
                cache.put(key, value);
            }
        }
    }

    public static class ZipfRead extends CacheBenchmarkConfiguration<Integer, Integer> {

        private final Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Generator<Integer> generator;

        public ZipfRead(String name, Cache<Integer, Integer> cache, int lowerBound, int upperBound) {
            super(name + "ZRead (" + cache.maxSize() + ")", lowerBound, upperBound);
            this.cache = cache;
            generator = new ZipfGenerator(lowerBound, upperBound);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            return cache.get(key) != null;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(generator.next(), generator.next());
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            if (!succeeded) {
                cache.put(key, value);
            }
        }
    }

    public static class Update extends CacheBenchmarkConfiguration<Integer, Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final RandomGenerator generator;

        public Update(String name, Cache<Integer, Integer> cache, int lowerBound, int upperBound) {
            super(name + "Update (" + cache.maxSize() + ")", lowerBound, upperBound);
            this.cache = cache;
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

    public static class Delete extends CacheBenchmarkConfiguration<Integer, Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final RandomGenerator generator;
        private int lastKey;

        public Delete(String name, Cache<Integer, Integer> cache, int lowerBound, int upperBound) {
            super(name + "Delete (" + cache.maxSize() + ")", lowerBound, upperBound);
            this.cache = cache;
            generator = new RandomGenerator(lowerBound, upperBound);
            for (int i = 0; i < upperBound - lowerBound; i++) {
                cache.put(i + lowerBound, random.nextInt());
            }
        }

        @Override
        protected void prepare() {
            lastKey = generator.next();
            cache.put(lastKey, 0);
        }

        @Override
        protected boolean run(Integer key, Integer value) {
            cache.remove(key);
            return true;
        }

        @Override
        protected Pair<Integer, Integer> generateInput() {
            return new Pair<>(lastKey, random.nextInt());
        }

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            cache.put(lastKey, 0);
        }
    }
}
