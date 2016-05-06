package desmedt.frederik.cachebenchmarking;

import android.util.Log;
import android.util.Pair;

/**
 * A benchmark configuration ran by the {@link BenchmarkRunner}.
 * <p/>
 * Note how this class is immutable, this is to enforce a reliable never-changing configuration
 * that maintains the integrity of the end results.
 * After the benchmark is run {@link CacheStats} are generated that can be retrieved.
 *
 * Every cache benchmark configuration handles keys and values, where the key is {@link Comparable}.
 */
public abstract class CacheBenchmarkConfiguration<K extends Comparable, V> {

    /**
     * How many times should the configuration log updates in a complete configuration run
     * ({@link CacheBenchmarkConfiguration#runMany(long, long)} or {@link CacheBenchmarkConfiguration#runTimed(long, long)}.
     */
    private static final int CONFIGURATION_RUN_LOG_POINT_COUNT = 5;

    public final String TAG;
    private final String name;

    private K lowerKeyBound;
    private K upperKeyBound;

    private CacheStats stats;
    private long totalTimeNanos;

    public CacheBenchmarkConfiguration(String name, K lowerBound, K upperBound) {
        this.name = name;
        TAG = CacheBenchmarkConfiguration.class.getSimpleName() + " - " + name;
        this.lowerKeyBound = lowerBound;
        this.upperKeyBound = upperBound;
    }

    public K getLowerKeyBound() {
        return lowerKeyBound;
    }

    public K getUpperKeyBound() {
        return upperKeyBound;
    }

    public String getName() {
        return name;
    }

    /**
     * Run the operation the benchmark is supposed to evaluate exactly once. The behaviour,
     * such as the speed, of this method is recorded and used to generate the final result of the
     * configuration. Therefore it is essential that this is as high performance as it can be, e.g.
     * you shouldn't log non-essential information or have an entire try-catch block unless if this
     * is absolutely necessary.
     *
     * @param key   The key used in the operation, can be null if it is not used in some specific
     *              implementation. The key will always be within the lower and upper bounds.
     * @param value The value used in the operation, can be null if it is not used in some specific
     *              implementation.
     * @return true if the run succeeded, say by a cache success, false if the run did not succeed,
     * say by a cache miss
     */
    protected abstract boolean run(K key, V value);

    /**
     * Generates a possible input for the next run. If the input to {@link CacheBenchmarkConfiguration#run(Comparable, Object)}
     * is irrelevant this could return <code>null</code>. Note how this can have an arbitrary
     * probability distribution when picking a value from the input space. In other words, you could
     * always return the same value from the input space (the obvious one being <code>null</code>),
     * or you could have a perfectly random distribution. This method is not recorded/timed.
     *
     * @return A key-value pair used as a possible input for a single run
     */
    protected abstract Pair<K, V> generateInput();

    /**
     * Generates statistics regarding the cache that is being benchmarked. Based on the type of benchmark
     * some of the properties of the returned stats are allowed to be null.
     * <p/>
     * This method should not be used when trying to get an overview of benchmark statistics,
     * {@link CacheBenchmarkConfiguration#getStats()} should be used instead.
     *
     * @return Statistics relating to the cache
     * @see CacheStats
     */
    protected abstract CacheStats generateStats();

    /**
     * Optional step performed after initialization and before the benchmark run.
     * Here the benchmark configuration has the chance of performing operations that should be run a
     * single time before the benchmark is started, like initializing the cache.
     * This method is not recorded/timed.
     */
    protected void setup() {
    }

    /**
     * Optional step performed after the complete benchmark run with
     * {@link CacheBenchmarkConfiguration#runMany(long, long)} and
     * {@link CacheBenchmarkConfiguration#runTimed(long, long)}. Here the benchmark configuration has
     * the chance of performing operations that should be run a single time after the benchmark run,
     * like purging the cache. This method is not recorded/timed.
     */
    protected void tearDown() {
    }

    /**
     * Optional intermediary step performed before each single run. Here the benchmark configuration has
     * the chance of performing dependent operations that are required in the run, yet should not be
     * recorded. Therefore this method is not recorded/timed.
     */
    protected void prepare() {
    }

    /**
     * Optional intermediary step performed after each single run. Here the benchmark configuration has the
     * chance of cleaning everything up to maintain reliable runs. This method is not recorded/timed.
     *
     * @param key       The key of the last run
     * @param value     The value of the last run
     * @param succeeded Whether the last run succeeded or not
     */
    protected void cleanup(K key, V value, boolean succeeded) {
    }

    /**
     * Generates a legal key-value pair to be used as an input for a single run by using
     * {@link CacheBenchmarkConfiguration#generateInput()} and then checking the lower and upper bounds.
     *
     * @return A legal input key-value pair
     */
    private Pair<K, V> generateLegalInput() {
        final Pair<K, V> input = generateInput();

        if (input != null && (input.first.compareTo(lowerKeyBound) < 0 || input.first.compareTo(upperKeyBound) > 0)) {
            throw new IllegalArgumentException("Generated an input " + input.toString() + " that is either lower or higher than the lower or upper bound!");
        }

        return input;
    }

    /**
     * Runs the configuration <code>warmupIterations + runIterations</code> times, using
     * {@link CacheBenchmarkConfiguration#run(Comparable, Object)} to execute the operation and
     * {@link CacheBenchmarkConfiguration#generateInput()} to generate a random input. These results will
     * then be collected and returned.
     *
     * @param warmupIterations How many iterations the configuration should be run before recording
     *                         the results
     * @param runIterations    How many iterations the configuration should be run and recorded
     * @returns The final result after completing all iterations
     */
    public final void runMany(long warmupIterations, long runIterations) {
        setup();
        final long logPoint = runIterations / CONFIGURATION_RUN_LOG_POINT_COUNT;

        Log.i(TAG, "Starting warmup");
        for (int i = 0; i < warmupIterations; i++) {
            runAndRecord();
        }

        Log.i(TAG, "Completed warmup, starting run");

        for (int i = 0; i < runIterations; i++) {
            runAndRecord();
            if (i % logPoint == 0 && i != 0) {
                Log.i(TAG, String.format("Reached %d iterations after %d millis", i, totalTimeNanos / 1_000_000));
            }
        }

        stats = generateStats();
        stats.benchmarkName = getName();
        stats.averageRunTime = totalTimeNanos / runIterations;
        tearDown();
        Log.i(TAG, "Completed run");
    }

    /**
     * Runs the configuration for <code>millis</code> milliseconds, using
     * {@link CacheBenchmarkConfiguration#run(Comparable, Object)} to execute the operation and
     * {@link CacheBenchmarkConfiguration#generateInput()} to generate a random input. These results will
     * then be collected and returned.
     *
     * @param warmupMillis How long the warmup run should be in milliseconds
     * @param runMillis    How long the actual recorded run should be in milliseconds
     * @returns The final result after completing all iterations fitting in <code>runMillis</code> milliseconds
     */
    public final void runTimed(long warmupMillis, long runMillis) {
        setup();
        long nextLogPoint = runMillis / CONFIGURATION_RUN_LOG_POINT_COUNT;

        Log.i(TAG, "Starting warmup");
        while (totalTimeNanos < warmupMillis) {
            runAndRecord();
        }

        Log.i(TAG, "Completed warmup, starting run");

        totalTimeNanos = 0;
        int totalIterations = 0;
        while (totalTimeNanos < runMillis) {
            totalIterations++;
            runAndRecord();
            if (totalTimeNanos / 1_000_000 >= runMillis) {
                // Stop the loop as the recording passed the specified run time
                // Repeating the run until there is a recording that fits in the specified run time
                // is both indeterministic and unfair.
                break;
            }

            if (totalTimeNanos >= nextLogPoint) {
                Log.i(TAG, String.format("Reached %d iterations after %d millis", totalIterations,
                        totalTimeNanos / 1_000_000));
                nextLogPoint = nextLogPoint + runMillis / CONFIGURATION_RUN_LOG_POINT_COUNT;
            }
        }

        stats = generateStats();
        stats.benchmarkName = getName();
        stats.averageRunTime = totalTimeNanos / totalIterations;
        tearDown();
        Log.i(TAG, "Completed run");
    }

    private void runAndRecord() {
        prepare();
        final Pair<K, V> input = generateLegalInput();
        long before = 0;
        long after = 0;
        boolean succeeded;

        if (input == null) {
            before = System.nanoTime();
            succeeded = run(null, null);
            after = System.nanoTime();
        } else {
            before = System.nanoTime();
            succeeded = run(input.first, input.second);
            after = System.nanoTime();
        }

        cleanup(input.first, input.second, succeeded);
        totalTimeNanos += after - before;
    }

    /**
     * Get stats of the benchmark configuration, consisting of a combination of statistics generated
     * by the base {@link CacheBenchmarkConfiguration} and statistics generated by the cache itself.
     * <p/>
     * When the benchmark configuration is has not been run or is not yet finished, this will return
     * null.
     *
     * @return Reliable cache statistics
     */
    public CacheStats getStats() {
        return stats;
    }

    /**
     * Represents statistics of the cache used in the cache benchmark. Several statistics might be null
     * based on the use case.
     */
    public static class CacheStats {

        private Integer successCount;
        private Integer failureCount;
        private Integer maxCacheSize;
        private Integer cacheEntryCount;

        private String benchmarkName;
        private double averageRunTime;

        private CacheStats() {
        }

        /**
         * A Simple Factory used for creating {@link CacheStats} of a cache benchmark where reading
         * a cache is recorded.
         *
         * @param successCount    The amount of successful reads that have occurred in the current benchmark
         *                        configuration
         * @param failureCount    The amount of failed reads that have occurred in the current benchmark
         *                        configuration
         * @param cacheSize       The cache size of the cache used in the benchmark configuration (in entries), with
         *                        a dynamically sized cache this is the maximum amount of entries
         * @param cacheEntryCount The amount of cache entries in the cache used in the benchmark configuration
         * @return A {@link CacheStats} object containing the specified data
         */
        public static CacheStats read(int successCount, int failureCount, int cacheSize, int cacheEntryCount) {
            CacheStats metrics = new CacheStats();
            metrics.successCount = successCount;
            metrics.failureCount = failureCount;
            metrics.maxCacheSize = cacheSize;
            metrics.cacheEntryCount = cacheEntryCount;
            return metrics;
        }

        /**
         * A Simple Factory used for creating {@link CacheStats} of a cache benchmark where
         * inserting, updating or deleting a cache is recorded.
         *
         * @param maxCacheSize    The cache size of the cache used in the benchmark configuration (in entries), with
         *                        a dynamically sized cache this is the maximum amount of entries
         * @param cacheEntryCount The amount of cache entries in the cache used in the benchmark configuration
         * @return A {@link CacheStats} object containing the specified data
         */
        public static CacheStats nonRead(int maxCacheSize, int cacheEntryCount) {
            CacheStats metrics = new CacheStats();
            metrics.maxCacheSize = maxCacheSize;
            metrics.cacheEntryCount = cacheEntryCount;
            return metrics;
        }

        /**
         * @return The amount of successful reads that have occurred in the current benchmark
         * configuration. Null if the benchmark configuration is not a reading benchmark.
         */
        public Integer getSuccessCount() {
            return successCount;
        }

        /**
         * @return The amount of failed reads that have occurred in the current benchmark
         * configuration. Null if the benchmark configuration is not a reading benchmark.
         */
        public Integer getFailureCount() {
            return failureCount;
        }

        /**
         * @return The cache size of the cache used in the benchmark configuration (in entries), with
         * a dynamically sized cache this is the maximum amount of entries.
         */
        public Integer getMaxCacheSize() {
            return maxCacheSize;
        }

        /**
         * @return The amount of cache entries in the cache used in the benchmark configuration.
         * Null if this does not make sense in the current benchmark configuration, e.g. a delete
         * benchmark that should always have 0 cache entries after each run.
         */
        public Integer getCacheEntryCount() {
            return cacheEntryCount;
        }

        /**
         * Sets the benchmark name, should only be set by the base {@link CacheBenchmarkConfiguration}.
         *
         * @param benchmarkName The benchmark name
         */
        private void setBenchmarkName(String benchmarkName) {
            this.benchmarkName = benchmarkName;
        }

        /**
         * Sets the average runtime, should only be set by the base {@link CacheBenchmarkConfiguration}.
         *
         * @param averageRunTime The average runtime in nanoseconds
         */
        private void setAverageRunTime(double averageRunTime) {
            this.averageRunTime = averageRunTime;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(String.format("%-25s ", benchmarkName));
            final boolean readBenchmark = successCount != null && failureCount != null;

            builder.append(String.format("Cache size: %-5d ", maxCacheSize));

            if (readBenchmark) {
                builder.append(String.format("Hit ratio: %-5.3f%%     ", (double) successCount / (successCount + failureCount) * 100));
            }

            builder.append(String.format("Average (ns): %-7.1f     ", averageRunTime));

            if (successCount != null) {
                builder.append(String.format("Successes: %-8d     ", successCount));
            }

            if (failureCount != null) {
                builder.append(String.format("Failures: %-8d     ", failureCount));
            }

            if (cacheEntryCount != null) {
                builder.append(String.format("Cache entries: %-5d", cacheEntryCount));
            }

            return builder.toString();
        }
    }
}
