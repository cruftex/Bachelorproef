package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import javax.xml.transform.Result;

/**
 * A benchmark configuration ran by the {@link BenchmarkRunner}.
 * <p/>
 * Note how this class is immutable, this is to enforce a reliable never-changing configuration
 * that maintains the integrity of the end results. There is no way to retrieve the results of the
 * last run, e.g. a method <code>getResults()</code> as this could be used to cheat, the implementation
 * could trivially modify the last results in its favor. This can not be avoided in any other way
 * due to the intrinsically mutable nature of {@link Results}.
 */
public abstract class BenchmarkConfiguration<E> {

    /**
     * How many times should the configuration log updates in a complete configuration run
     * ({@link BenchmarkConfiguration#runMany(int, int)} or {@link BenchmarkConfiguration#runTimed(long, long)}.
     */
    private static final int CONFIGURATION_RUN_LOG_POINT_COUNT = 5;

    private final String TAG;
    private final String name;

    public BenchmarkConfiguration(String name) {
        this.name = name;
        TAG = BenchmarkConfiguration.class.getSimpleName() + " - " + name;
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
     * @param input The input to the operation, can be null if it is not used in some specific
     *              implementation
     */
    public abstract void run(E input);

    /**
     * Generates a possible input for the next run. If the input to {@link BenchmarkConfiguration#run(Object)}
     * is irrelevant this could return <code>null</code>. Note how this can have an arbitrary
     * probability distribution when picking a value from the input space. In other words, you could
     * always return the same value from the input space (the obvious one being <code>null</code>),
     * or you could have a perfectly random distribution.
     *
     * @return A possible input for the next run
     */
    public abstract E generateInput();

    /**
     * Runs the configuration <code>warmupIterations + runIterations</code> times, using
     * {@link BenchmarkConfiguration#run(Object)} to execute the operation and
     * {@link BenchmarkConfiguration#generateInput()} to generate a random input. These results will
     * then be collected and returned.
     *
     * @param warmupIterations How many iterations the configuration should be run before recording
     *                         the results
     * @param runIterations    How many iterations the configuration should be run and recorded
     * @returns The final result after completing all iterations
     */
    public final Results runMany(int warmupIterations, int runIterations) {
        final Results results = new Results();
        final int logPoint = runIterations / CONFIGURATION_RUN_LOG_POINT_COUNT;

        Log.i(TAG, "Starting warmup");
        for (int i = 0; i < warmupIterations; i++) {
            run(generateInput());
        }

        Log.i(TAG, "Completed warmup, starting run");

        for (int i = 0; i < runIterations; i++) {
            final E input = generateInput();
            long before = System.nanoTime();
            run(input);
            long after = System.nanoTime();
            results.submitRecording(after - before);
            if (i % logPoint == 0 && i != 0) {
                Log.i(TAG, String.format("Reached %d iterations after %d millis", i, results.getTotalTimeMillis()));
            }
        }

        Log.i(TAG, "Completed run");

        return results;
    }

    /**
     * Runs the configuration for <code>millis</code> milliseconds, using
     * {@link BenchmarkConfiguration#run(Object)} to execute the operation and
     * {@link BenchmarkConfiguration#generateInput()} to generate a random input. These results will
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
            final E input = generateInput();
            final long before = System.nanoTime();
            run(input);
            final long after = System.nanoTime();
            accumulatedWarmupTime += after - before;
        }

        Log.i(TAG, "Completed warmup, starting run");

        final Results results = new Results();
        while (results.getTotalTimeMillis() < runMillis) {
            final E input = generateInput();
            final long before = System.nanoTime();
            run(input);
            final long after = System.nanoTime();
            final long runTime = after - before;
            if (results.getTotalTimeMillis() + runTime / 1_000_000 <= runMillis) {
                results.submitRecording(runTime);
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

    public static class Results {

        private long totalTime;
        private int iterations;

        /**
         * Submit one new recording, used to accumulate all recordings to eventually be analyzed.
         *
         * @param time The recorded time, in nanoseconds, of a single recording
         */
        private void submitRecording(long time) {
            totalTime += time;
            iterations += 1;
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
         * {@link Results#submitRecording(long)} is run.
         *
         * @return The total amount of recordings used for this result
         */
        public int getTotalIterations() {
            return iterations;
        }
    }
}
