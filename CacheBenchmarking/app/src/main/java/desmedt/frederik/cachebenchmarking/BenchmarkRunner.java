package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import desmedt.frederik.cachebenchmarking.benchmark.Cache2KClockProBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.CustomBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.GuavaBenchmarks;
import desmedt.frederik.cachebenchmarking.benchmark.JackRabbitLIRSBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.NativeLruBenchmarks;
import desmedt.frederik.cachebenchmarking.cache.RandomCache;
import desmedt.frederik.cachebenchmarking.cache.FIFOCache;

/**
 * Responsible for running all {@link CacheBenchmarkConfiguration}s.
 */
public class BenchmarkRunner {

    private static final String TAG = BenchmarkRunner.class.getSimpleName();

    //    private CacheTestBenchmark testBenchmark = new CacheTestBenchmark();

    private Queue<CacheBenchmarkConfiguration.Results> benchmarkResults = new LinkedList<>();

    /**
     * ExecutorService executing every benchmark in a serializable fashion. Meaning none of them will
     * run parallel to another benchmark. This is way more reliable than parallel execution as in
     * parallel scenario's different benchmarks are both competing for the same resources and locks.
     */
    private ExecutorService benchmarkRunnerService = Executors.newSingleThreadExecutor();

    public void runBenchmarks() {
        /* Zipf read benchmarks */

        for (int i = 0; i < 3; i++) {
            final int cacheSize = (int) Math.pow(5, i);

//            submitCountedBenchmark(new GuavaBenchmarks.ZipfRead(cacheSize, 0, 100), 100, 10000);
//            submitCountedBenchmark(new NativeLruBenchmarks.ZipfRead(cacheSize, 0, 100), 100, 10000);
//            submitCountedBenchmark(new CustomBenchmark.ZipfRead("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 100), 100, 10000);
//            submitCountedBenchmark(new CustomBenchmark.ZipfRead("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 100), 100, 10000);
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.ZipfRead(cacheSize, 0, 100), 100, 10000);
            submitCountedBenchmark(new Cache2KClockProBenchmark.ZipfRead(cacheSize, 0, 100), 100, 10000);
        }

        for (int i = 1; i < 9; i++) {
            final int cacheSize = Math.round(200 * ((float) i / 10));
//            submitCountedBenchmark(new GuavaBenchmarks.ZipfRead(cacheSize, 0, 200), 100, 10000);
//            submitCountedBenchmark(new NativeLruBenchmarks.ZipfRead(cacheSize, 0, 200), 100, 10000);
//            submitCountedBenchmark(new CustomBenchmark.ZipfRead("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 200), 100, 10000);
//            submitCountedBenchmark(new CustomBenchmark.ZipfRead("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 200), 100, 10000);
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.ZipfRead(cacheSize, 0, 200), 100, 10000);
            submitCountedBenchmark(new Cache2KClockProBenchmark.ZipfRead(cacheSize, 0, 200), 100, 10000);
        }
//
//        submitCountedBenchmark(new GuavaBenchmarks.RandomRead(100, 0, 10000));
//        submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(100, 0, 10000));
//        submitCountedBenchmark(new CustomBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 10000));
//        submitCountedBenchmark(new CustomBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(100), 0, 10000));
//        submitCountedBenchmark(new JackRabbitLIRSBenchmark.RandomRead(100, 0, 10000));
        submitCountedBenchmark(new Cache2KClockProBenchmark.RandomRead(100, 0, 10000));
//
//        /* Random read benchmarks */
//
        for (int i = 1; i <= 3; i++) {
            final int cacheSize = (int) Math.pow(5, i);
//
//            submitCountedBenchmark(new GuavaBenchmarks.RandomRead(cacheSize, 0, 100));
//            submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(cacheSize, 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.RandomRead(cacheSize, 0, 100));
            submitCountedBenchmark(new Cache2KClockProBenchmark.RandomRead(cacheSize, 0, 100));
        }
//
//        /* Insert benchmarks */
//
        for (int i = 1; i <= 3; i++) {
            final int cacheSize = (int) Math.pow(5, i);
//
//            submitCountedBenchmark(new GuavaBenchmarks.Insert(cacheSize, 0, 100));
//            submitCountedBenchmark(new NativeLruBenchmarks.Insert(cacheSize, 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Insert("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Insert("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Insert(cacheSize, 0, 100));
            submitCountedBenchmark(new Cache2KClockProBenchmark.Insert(cacheSize, 0, 100));
        }
//
//        /* Update benchmarks */
//
        for (int i = 1; i <= 3; i++) {
            final int cacheSize = (int) Math.pow(5, i);
//
//            submitCountedBenchmark(new GuavaBenchmarks.Update(cacheSize, 0, 100));
//            submitCountedBenchmark(new NativeLruBenchmarks.Update(cacheSize, 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Update("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Update("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Update(cacheSize, 0, 100));
            submitCountedBenchmark(new Cache2KClockProBenchmark.Update(cacheSize, 0, 100));
        }
//
//        /* Delete benchmarks */
//
        for (int i = 1; i <= 3; i++) {
            final int cacheSize = (int) Math.pow(5, i);
//
//            submitCountedBenchmark(new GuavaBenchmarks.Delete(cacheSize, 0, 100));
//            submitCountedBenchmark(new NativeLruBenchmarks.Delete(cacheSize, 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Delete("FifoCache", new FIFOCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new CustomBenchmark.Delete("RandomCache", new RandomCache<Integer, Integer>(cacheSize), 0, 100));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Delete(cacheSize, 0, 100));
            submitCountedBenchmark(new Cache2KClockProBenchmark.Delete(cacheSize, 0, 100));
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
            CacheBenchmarkConfiguration.Results results = benchmarkResults.poll();
            Log.i(TAG + " - " + String.format("%25s", results.getBenchmarkConfigurationName()),
                    String.format("Total (ms): %-10s Average (ns): %-10s Iterations: %-15s Succeeded: %-15s Failed: %-15s",
                            results.getTotalTimeMillis(),
                            results.getAverageTime(),
                            results.getTotalIterations(),
                            results.getSuccesses(),
                            results.getFailures()));
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
                benchmarkResults.add(benchmarkConfiguration.runMany(warmupIterations, runIterations));
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
