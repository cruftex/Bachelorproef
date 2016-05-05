package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import desmedt.frederik.cachebenchmarking.benchmark.Cache2KBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.CustomBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.GuavaBenchmarks;
import desmedt.frederik.cachebenchmarking.benchmark.JackRabbitLIRSBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.NativeLruBenchmarks;
import desmedt.frederik.cachebenchmarking.cache.Cache;
import desmedt.frederik.cachebenchmarking.cache.RandomCache;
import desmedt.frederik.cachebenchmarking.generator.Generator;

/**
 * Responsible for running all {@link CacheBenchmarkConfiguration}s.
 */
public class BenchmarkRunner {

    private static final String TAG = BenchmarkRunner.class.getSimpleName();

    //    private CacheTestBenchmark testBenchmark = new CacheTestBenchmark();

    private Queue<CacheBenchmarkConfiguration.CacheStats> benchmarkResults = new LinkedList<>();

    /**
     * ExecutorService executing every benchmark in a serializable fashion. Meaning none of them will
     * run parallel to another benchmark. This is way more reliable than parallel execution as in
     * parallel scenario's different benchmarks are both competing for the same resources and locks.
     */
    private ExecutorService benchmarkRunnerService = Executors.newSingleThreadExecutor();

    private Generator<Cache<Integer, Integer>> generateRandomCache(final int cacheSize) {
        return new Generator<Cache<Integer, Integer>>() {
            @Override
            public Cache<Integer, Integer> next() {
                return new RandomCache<>(cacheSize);
            }
        };
    }

    private Generator<Cache<Integer, Integer>> generateFifoCache(final int cacheSize) {
        return new Generator<Cache<Integer, Integer>>() {
            @Override
            public Cache<Integer, Integer> next() {
                return new RandomCache<>(cacheSize);
            }
        };
    }

    public void runBenchmarks() {

        /* Zipf read benchmarks */

        for (int i = 1; i <= 10; i++) {
            final int zipfUpperBound = 500;
            final int cacheSize = Math.round(zipfUpperBound * ((float) i / 10));
            submitCountedBenchmark(new GuavaBenchmarks.ZipfRead(cacheSize, 0, zipfUpperBound), 100, 10000);
            submitCountedBenchmark(new NativeLruBenchmarks.ZipfRead(cacheSize, 0, zipfUpperBound), 100, 10000);
            submitCountedBenchmark(new CustomBenchmark.ZipfRead("FifoCache", cacheSize, 0, zipfUpperBound, generateFifoCache(cacheSize)), 100, 10000);
            submitCountedBenchmark(new CustomBenchmark.ZipfRead("RandomCache", cacheSize, 0, zipfUpperBound, generateRandomCache(cacheSize)), 100, 10000);
            submitCountedBenchmark(new JackRabbitLIRSBenchmark.ZipfRead(cacheSize, 0, zipfUpperBound), 100, 10000);
            submitCountedBenchmark(new Cache2KBenchmark.ZipfRead(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, zipfUpperBound), 100, 10000);
            submitCountedBenchmark(new Cache2KBenchmark.ZipfRead(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, zipfUpperBound), 100, 10000);
        }


        /* Random read benchmarks */

        for (int i = 1; i <= 10; i++) {
            final int randomUpperBound = 500;
            final int cacheSize = Math.round(randomUpperBound * ((float) i / 10));

            submitCountedBenchmark(new GuavaBenchmarks.RandomRead(cacheSize, 0, randomUpperBound));
            submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(cacheSize, 0, randomUpperBound));
            submitCountedBenchmark(new CustomBenchmark.RandomRead("FifoCache", cacheSize, 0, randomUpperBound, generateFifoCache(cacheSize)));
            submitCountedBenchmark(new CustomBenchmark.RandomRead("RandomCache", cacheSize, 0, randomUpperBound, generateRandomCache(cacheSize)));
            submitCountedBenchmark(new JackRabbitLIRSBenchmark.RandomRead(cacheSize, 0, randomUpperBound));
            submitCountedBenchmark(new Cache2KBenchmark.RandomRead(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, randomUpperBound));
            submitCountedBenchmark(new Cache2KBenchmark.RandomRead(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, randomUpperBound));
        }

        /* Insert benchmarks */

        for (int i = 1; i <= 10; i++) {
            final int upperBound = 500;
            final int cacheSize = Math.round(upperBound * ((float) i / 10));

            submitCountedBenchmark(new GuavaBenchmarks.Insert(cacheSize, 0, upperBound));
            submitCountedBenchmark(new NativeLruBenchmarks.Insert(cacheSize, 0, upperBound));
            submitCountedBenchmark(new CustomBenchmark.Insert("FifoCache", cacheSize, 0, upperBound, generateFifoCache(cacheSize)));
            submitCountedBenchmark(new CustomBenchmark.Insert("RandomCache", cacheSize, 0, upperBound, generateRandomCache(cacheSize)));
            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Insert(cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Insert(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Insert(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, upperBound));
        }

        /* Update benchmarks */

        for (int i = 1; i <= 10; i++) {
            final int upperBound = 500;
            final int cacheSize = Math.round(upperBound * ((float) i / 10));

            submitCountedBenchmark(new GuavaBenchmarks.Update(cacheSize, 0, upperBound));
            submitCountedBenchmark(new NativeLruBenchmarks.Update(cacheSize, 0, upperBound));
            submitCountedBenchmark(new CustomBenchmark.Update("FifoCache", cacheSize, 0, upperBound, generateFifoCache(cacheSize)));
            submitCountedBenchmark(new CustomBenchmark.Update("RandomCache", cacheSize, 0, upperBound, generateRandomCache(cacheSize)));
            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Update(cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Update(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Update(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, upperBound));
        }

        /* Delete benchmarks */

        for (int i = 1; i <= 10; i++) {
            final int upperBound = 500;
            final int cacheSize = Math.round(upperBound * ((float) i / 10));

            submitCountedBenchmark(new GuavaBenchmarks.Delete(cacheSize, 0, upperBound));
            submitCountedBenchmark(new NativeLruBenchmarks.Delete(cacheSize, 0, upperBound));
            submitCountedBenchmark(new CustomBenchmark.Delete("FifoCache", cacheSize, 0, upperBound, generateFifoCache(cacheSize)));
            submitCountedBenchmark(new CustomBenchmark.Delete("RandomCache", cacheSize, 0, upperBound, generateRandomCache(cacheSize)));
            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Delete(cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Delete(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, upperBound));
            submitCountedBenchmark(new Cache2KBenchmark.Delete(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, upperBound));
        }

        benchmarkRunnerService.submit(new Runnable() {
            @Override
            public void run() {
                logBenchmarkResults();
            }
        });

        benchmarkRunnerService.shutdown();
    }

    public void resetEnvironment() {
        gc();
    }

    public void logBenchmarkResults() {
        while (!benchmarkResults.isEmpty()) {
            CacheBenchmarkConfiguration.CacheStats stats = benchmarkResults.poll();
            Log.i(TAG, stats.toString());
        }
    }

    public ExecutorService getBenchmarkRunnerService() {
        return benchmarkRunnerService;
    }

    private void submitCountedBenchmark(final CacheBenchmarkConfiguration benchmarkConfiguration) {
        submitCountedBenchmark(benchmarkConfiguration, 100, 1_000_000);
    }

    private void submitCountedBenchmark(final CacheBenchmarkConfiguration benchmarkConfiguration, final long warmupIterations, final long runIterations) {
        benchmarkRunnerService.submit(new Runnable() {

            @Override
            public void run() {
                benchmarkConfiguration.runMany(warmupIterations, runIterations);
                benchmarkResults.add(benchmarkConfiguration.getStats());
                resetEnvironment();
            }
        });
    }

    /**
     * Force the garbage collection to run, rather than suggesting it. This will make sure that every
     * benchmark will be run in a "fresh" memory environment, without the garbage collector kicking in
     * clearing objects of previous benchmarks during recording.
     */
    private void gc() {
        Log.i(TAG, "Running garbage collector");
        Object obj = new Object();
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference ref = new PhantomReference<>(obj, queue);
        obj = null;

        long end = System.currentTimeMillis() + 2_000;
        while (!ref.isEnqueued()) {
            System.gc();
            if (System.currentTimeMillis() > end) {
                Log.i(TAG, "No garbage collection needed!");
                return;
            }
        }

        Log.i(TAG, "Memory is garbage collected");
    }
}
