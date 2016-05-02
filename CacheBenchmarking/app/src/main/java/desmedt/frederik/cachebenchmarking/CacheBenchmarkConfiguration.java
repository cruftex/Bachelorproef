package desmedt.frederik.cachebenchmarking;

import android.util.Log;
import android.util.Pair;

/**
 * A benchmark configuration ran by the {@link BenchmarkRunner}.
 * <p/>
 * Note how this class is immutable, this is to enforce a reliable never-changing configuration
 * that maintains the integrity of the end results. There is no way to retrieve the results of the
 * last run, e.g. a method <code>getResults()</code> as this could be used to cheat, the implementation
 * could trivially modify the last results in its favor. This can not be avoided in any other way
 * due to the intrinsically mutable nature of {@link Results}. Every cache benchmark configuration
 * handles keys and values, where the key is {@link Comparable}.
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
     * Optional intermediary step performed before each single run. Here the benchmark configuration has
     * the chance of performing dependent operations that are required in the run, yet should not be
     * recorded. Therefore this method is not recorded/timed.
     */
    protected void prepare() {
    }

    /**
     * Optional intermediary step performed after each single run. Here the benchmark configuration has the
     * chance of cleaning everything up to maintain reliable runs. This method is not recorded/timed.
     * @param key The key of the last run
     * @param value The value of the last run
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
    public final Results runMany(long warmupIterations, long runIterations) {
        final Results results = new Results(name);
        final long logPoint = runIterations / CONFIGURATION_RUN_LOG_POINT_COUNT;

        Log.i(TAG, "Starting warmup");
        for (int i = 0; i < warmupIterations; i++) {
            randomRunAndRecord();
        }

        Log.i(TAG, "Completed warmup, starting run");

        for (int i = 0; i < runIterations; i++) {
            results.submitRecording(randomRunAndRecord());
            if (i % logPoint == 0 && i != 0) {
                Log.i(TAG, String.format("Reached %d iterations after %d millis", i, results.getTotalTimeMillis()));
            }
        }

        Log.i(TAG, "Completed run");

        return results;
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
    public final Results runTimed(long warmupMillis, long runMillis) {
        long nextLogPoint = runMillis / CONFIGURATION_RUN_LOG_POINT_COUNT;

        Log.i(TAG, "Starting warmup");
        long accumulatedWarmupTime = 0;
        while (accumulatedWarmupTime < warmupMillis) {
            accumulatedWarmupTime += randomRunAndRecord().getTime();
        }

        Log.i(TAG, "Completed warmup, starting run");

        final Results results = new Results(name);
        while (results.getTotalTimeMillis() < runMillis) {
            Recording recording = randomRunAndRecord();
            if (results.getTotalTimeMillis() + recording.getTime() / 1_000_000 <= runMillis) {
                results.submitRecording(recording);
            } else {
                // Stop the loop as the recording does not fit in the specified run time
                // Repeating the run until there is a recording that fits in the specified run time
                // is both indeterministic and unfair.
                break;
            }

            if (results.getTotalTimeMillis() >= nextLogPoint) {
                Log.i(TAG, String.format("Reached %d iterations after %d millis", results.getTotalIterations(),
                        results.getTotalTimeMillis()));
                nextLogPoint = nextLogPoint + runMillis / CONFIGURATION_RUN_LOG_POINT_COUNT;
            }
        }

        Log.i(TAG, "Completed run");

        return results;
    }

    private Recording randomRunAndRecord() {
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
        return new Recording(succeeded, after - before);
    }

    public static class Results {

        private long totalTime;
        private int iterations;
        private String benchmarkConfigurationName;

        private int successes;
        private int failures;

        public Results(String benchmarkConfigurationName) {
            this.benchmarkConfigurationName = benchmarkConfigurationName;
        }

        /**
         * Submit one new recording, used to accumulate all recordings to eventually be analyzed.
         *
         * @param recording A recording with the recorded time, in nanoseconds, and whether
         *                  there is a successes
         */
        private void submitRecording(Recording recording) {
            totalTime += recording.getTime();
            iterations += 1;

            if (recording.isSuccess()) {
                successes += 1;
            } else {
                failures += 1;
            }
        }

        /**
         * @return The accumulated time of all recordings in nanoseconds.
         */
        public long getTotalTime() {
            return totalTime;
        }

        /**
         * @return The accumulated time of all recordings in milliseconds.
         */
        public long getTotalTimeMillis() {
            return totalTime / 1_000_000;
        }

        /**
         * Calculates the average time of a single recording based on the total amount of iterations
         * and the total time.
         *
         * @return The average time, in nanoseconds, considering all accumulated recordings
         */
        public double getAverageTime() {
            return iterations == 0 ? 0 : totalTime / iterations;
        }

        /**
         * Calculates the average time of a single recording based on the total amount of iterations
         * and the total time.
         *
         * @return The average time, in milliseconds, considering all accumulated recordings
         */
        public double getAverageTimeMillis() {
            return iterations == 0 ? 0 : totalTime / iterations / 1_000_000;
        }

        /**
         * Returns the total amount of recordings, which is essentially the amount of times
         * {@link Results#submitRecording(Recording)} is run.
         *
         * @return The total amount of recordings used for this result
         */
        public int getTotalIterations() {
            return iterations;
        }

        public String getBenchmarkConfigurationName() {
            return benchmarkConfigurationName;
        }

        public int getSuccesses() {
            return successes;
        }

        public int getFailures() {
            return failures;
        }
    }

    private static class Recording {

        private long time;
        private boolean success;

        public Recording(boolean success, long time) {
            this.success = success;
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
