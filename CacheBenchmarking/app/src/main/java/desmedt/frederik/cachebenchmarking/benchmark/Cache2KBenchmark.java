package desmedt.frederik.cachebenchmarking.benchmark;

import org.cache2k.Cache;
import org.cache2k.CacheBuilder;
import org.cache2k.impl.ArcCache;
import org.cache2k.impl.BaseCache;
import org.cache2k.impl.ClockCache;
import org.cache2k.impl.RandomCache;

import java.util.Random;
import java.util.UUID;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;
import desmedt.frederik.cachebenchmarking.generator.Generator;

/**
 * Contains a collection of Clock benchmarks (by Cache2K) as inner classes.
 */
public class Cache2KBenchmark {

    public static final Class<ClockCache> CLOCK_CACHE = ClockCache.class;
    public static final Class<ArcCache> ARC_CACHE = ArcCache.class;
    public static final Class<RandomCache> RANDOM_CACHE = RandomCache.class;

    private static Cache<Integer, Integer> createCache(Class<? extends BaseCache> cacheImplementation, int maxSize) {
        return CacheBuilder.newCache(Integer.class, Integer.class).name(UUID.randomUUID().toString())
                .eternal(true)
                .maxSize(maxSize)
                .keepDataAfterExpired(false)
                .implementation(cacheImplementation)
                .build();
    }

    public static class Read extends BaseBenchmark.Read<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Class<? extends BaseCache> cacheClass;

        // Certain Cache2K cache implementations automatically generate statistics, yet the base
        // cache does not, therefore record them here so that the implementation is irrelevant.
        private int successes;
        private int failures;

        public Read(Class<? extends BaseCache> cacheClass, String traceTag, Generator<Integer> traceGenerator, double cachedRatio, Integer lowerBound, Integer upperBound) {
            super(cacheClass.getSimpleName(), traceTag, traceGenerator, cachedRatio, lowerBound, upperBound);
            this.cacheClass = cacheClass;
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
            cache = Cache2KBenchmark.createCache(cacheClass, cacheSize);
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

        @Override
        protected void cleanup(Integer key, Integer value, boolean succeeded) {
            super.cleanup(key, value, succeeded);
            if (succeeded) {
                successes++;
            } else {
                failures++;
            }
        }

        @Override
        protected CacheStats generateStats() {
            return CacheStats.read(successes, failures, getCacheSize(), cache.getTotalEntryCount());
        }
    }

    public static class Insert extends BaseBenchmark.Insert<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Class<? extends BaseCache> cacheClass;

        public Insert(Class<? extends BaseCache> cacheClass, double cachedRatio, Integer lowerBound, Integer upperBound) {
            super(cacheClass.getSimpleName(), cachedRatio, lowerBound, upperBound);
            this.cacheClass = cacheClass;
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
            cache = Cache2KBenchmark.createCache(cacheClass, cacheSize);
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

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.INSERT, getCacheSize(), cache.getTotalEntryCount());
        }
    }

    public static class Update extends BaseBenchmark.Update<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Class<? extends BaseCache> cacheClass;


        public Update(Class<? extends BaseCache> cacheClass, double cachedRatio, Integer lowerBound, Integer upperBound) {
            super(cacheClass.getSimpleName(), cachedRatio, lowerBound, upperBound);
            this.cacheClass = cacheClass;
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
            cache = Cache2KBenchmark.createCache(cacheClass, cacheSize);
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

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.UPDATE, getCacheSize(), cache.getTotalEntryCount());
        }
    }

    public static class Delete extends BaseBenchmark.Delete<Integer> {

        private Cache<Integer, Integer> cache;
        private final Random random = new Random();
        private final Class<? extends BaseCache> cacheClass;


        public Delete(Class<? extends BaseCache> cacheClass, double cachedRatio, Integer lowerBound, Integer upperBound) {
            super(cacheClass.getSimpleName(), cachedRatio, lowerBound, upperBound);
            this.cacheClass = cacheClass;
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
            cache = Cache2KBenchmark.createCache(cacheClass, cacheSize);
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

        @Override
        protected CacheStats generateStats() {
            return CacheStats.nonRead(StatType.DELETE, getCacheSize(), cache.getTotalEntryCount());
        }
    }
}
