package desmedt.frederik.cachebenchmarking.benchmark;

import android.util.Pair;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * A collection of basic {@link desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration} that
 * implement common code while remaining cache independent.
 */
public class BaseBenchmark {

    public static final String ZIPF_READ_TAG = "ZRead";
    public static final String RANDOM_READ_TAG = "RRead";
    public static final String INSERT_TAG = "Insert";
    public static final String DELETE_TAG = "Delete";
    public static final String UPDATE_TAG = "Update";

    /**
     * Base benchmark configuration used by all static classes in {@link BaseBenchmark}.
     * It contains all common functionalities of its subclasses as well as management of benchmark
     * {@link CacheBenchmarkConfiguration#setup()} and {@link CacheBenchmarkConfiguration#tearDown()}.
     *
     * It expects the cache to be based on {@link Integer} keys.
     *
     * @param <V> The type of values that will be stored in the cache
     */
    public static abstract class BaseBenchmarkConfiguration<V> extends CacheBenchmarkConfiguration<Integer, V> {

        private int cacheSize;

        public BaseBenchmarkConfiguration(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + " (" + cacheSize + ")", lowerBound, upperBound);
            this.cacheSize = cacheSize;
        }

        @Override
        protected void setup() {
            createCache(cacheSize);
        }

        @Override
        protected void tearDown() {
            clearCache();
        }

        /**
         * Generate a random value to be used as a value in a run.
         *
         * @return A random value
         */
        protected abstract V generateValue();

        /**
         * Create the cache that is used for benchmarking of size {@code cacheSize}. This method is
         * called only once before the benchmark run and is not timed.
         *
         * @param cacheSize The maximum amount of entries the cache should have
         */
        protected abstract void createCache(int cacheSize);

        /**
         * Completely clear the cache used for benchmarking. This means cleaning as much as possible
         * and possibly even removing the cache reference for the garbage collector in case there are
         * lots of strong references that are maintained. This method is called only after the complete
         * benchmark run and is not timed.
         */
        protected abstract void clearCache();

        public int getCacheSize() {
            return cacheSize;
        }
    }

    /**
     * A default benchmark configuration for reading a cache. It simulates random cache access by
     * continuously generating random values before each individual run.
     *
     * It expects the cache to be based on {@link Integer} keys.
     *
     * @param <V> The type of the values that will be stored in the cache
     */
    public static abstract class RandomRead<V> extends BaseBenchmarkConfiguration<V> {

        private Generator<Integer> randomGenerator;

        public RandomRead(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + RANDOM_READ_TAG, cacheSize, lowerBound, upperBound);
            randomGenerator = new RandomGenerator(lowerBound, upperBound);
        }

        protected abstract void addToCache(Integer key, V value);

        @Override
        protected void cleanup(Integer key, V value, boolean succeeded) {
            if (!succeeded) {
                addToCache(key, value);
            }
        }

        @Override
        protected Pair<Integer, V> generateInput() {
            return new Pair<>(randomGenerator.next(), generateValue());
        }
    }

    /**
     * A default benchmark configuration for reading a cache. It simulates GET HTTP requests that
     * pass by the cache by continuously generating random values according to a Zipf probability
     * distribution ({@link ZipfGenerator}) before each individual run.
     *
     * It expects the cache to be based on {@link Integer} keys.
     *
     * @param <V> The type of the values that will be stored in the cache
     */
    public static abstract class ZipfRead<V> extends BaseBenchmarkConfiguration<V> {

        private Generator<Integer> randomGenerator;

        public ZipfRead(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + ZIPF_READ_TAG, cacheSize, lowerBound, upperBound);
            randomGenerator = new ZipfGenerator(lowerBound, upperBound);
        }

        protected abstract void addToCache(Integer key, V value);

        protected abstract V generateValue();

        @Override
        protected void cleanup(Integer key, V value, boolean succeeded) {
            if (!succeeded) {
                addToCache(key, value);
            }
        }

        @Override
        protected Pair<Integer, V> generateInput() {
            return new Pair<>(randomGenerator.next(), generateValue());
        }
    }

    /**
     * A default insert benchmark, that is used to monitor the performance of inserting key-value pairs
     * in a cache. The key passed to the run will always be a key of an entry that is not currently
     * stored in the cache. Therefore it will always be a pure insert run and will never be updating
     * an existing entry.
     *
     * It expects the cache to be based on {@link Integer} keys.
     * The keys that are used are completely random.
     *
     * @param <V> The type of the values that will be stored in the cache
     */
    public static abstract class Insert<V> extends BaseBenchmarkConfiguration<V> {

        private Generator<Integer> generator;
        private int nextKey;

        public Insert(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + INSERT_TAG, cacheSize, lowerBound, upperBound);
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        protected abstract void removeElement(Integer key);

        protected abstract V generateValue();

        @Override
        protected void cleanup(Integer key, V value, boolean succeeded) {
            nextKey = generator.next();
            removeElement(key);
        }

        @Override
        protected Pair<Integer, V> generateInput() {
            return new Pair<>(nextKey, generateValue());
        }
    }

    /**
     * A default benchmark configuration for deleting an entry from a cache.
     * The key passed to the run will always be a key of an existing entry in the cache. Therefore
     * the run is never told to try and remove the entry bound to the key that is not in the cache.
     *
     * It expects the cache to be based on {@link Integer} keys.
     * The keys generated are completely random.
     *
     * @param <V> The type of the values that will be stored in the cache
     */
    public static abstract class Delete<V> extends BaseBenchmarkConfiguration<V> {

        private Generator<Integer> generator;
        private int nextKey;
        private int cacheSize;

        public Delete(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + DELETE_TAG, cacheSize, lowerBound, upperBound);
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        public abstract void addToCache(int key, V value);

        protected abstract V generateValue();

        @Override
        protected void setup() {
            super.setup();
            for (int i = 0; i < cacheSize; i++) {
                prepare();
            }
        }

        @Override
        protected void prepare() {
            nextKey = generator.next();
            addToCache(nextKey, generateValue());
        }

        @Override
        protected Pair<Integer, V> generateInput() {
            return new Pair<>(nextKey, generateValue());
        }
    }

    /**
     * A default benchmark configuration for updating entries in a cache.
     * The key passed to the run might be bound to an already existing entry in the cache, yet this
     * is not enforced. Considering that a cache update is almost always used with a
     * "update or add if non existent" semantics.
     *
     * It expects the cache to be based on {@link Integer} keys.
     * The keys generated are completely random.
     *
     * @param <V> The type of the values that will be stored in the cache
     */
    public static abstract class Update<V> extends BaseBenchmarkConfiguration<V> {

        private Generator<Integer> generator;
        private int nextKey;

        public Update(String name, int cacheSize, Integer lowerBound, Integer upperBound) {
            super(name + UPDATE_TAG, cacheSize, lowerBound, upperBound);
            generator = new RandomGenerator(lowerBound, upperBound);
        }

        protected abstract void addToCache(int key, V value);

        protected abstract V generateValue();

        @Override
        protected void prepare() {
            nextKey = generator.next();
            addToCache(nextKey, generateValue());
        }

        @Override
        protected Pair<Integer, V> generateInput() {
            return new Pair<>(nextKey, generateValue());
        }
    }
}
