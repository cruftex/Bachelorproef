package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import desmedt.frederik.cachebenchmarking.benchmark.CustomCacheBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.GuavaBenchmarks;
import desmedt.frederik.cachebenchmarking.benchmark.JackRabbitLIRSCacheBenchmark;
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

        /* Insert benchmarks */

        submitCountedBenchmark(new GuavaBenchmarks.Insert(10, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Insert(10, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("FifoCache", new FIFOCache<Integer, Integer>(10), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("RandomCache", new RandomCache<Integer, Integer>(10), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Insert(100, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Insert(100, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("RandomCache", new RandomCache<Integer, Integer>(100), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Insert(1000, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Insert(1000, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("FifoCache", new FIFOCache<Integer, Integer>(1000), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Insert("RandomCache", new RandomCache<Integer, Integer>(1000), 0, 100));

        /* Random read benchmarks */

        submitCountedBenchmark(new GuavaBenchmarks.RandomRead(10, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(10, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(10), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(10), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.RandomRead(100, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(100, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(100), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.RandomRead(1000, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(1000, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(1000), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(1000), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.RandomRead(100, 0, 10000));
        submitCountedBenchmark(new NativeLruBenchmarks.RandomRead(100, 0, 10000));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 10000));
        submitCountedBenchmark(new CustomCacheBenchmark.RandomRead("RandomCache", new RandomCache<Integer, Integer>(100), 0, 10000));

        /* Update benchmarks */

        submitCountedBenchmark(new GuavaBenchmarks.Update(10, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Update(10, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("FifoCache", new FIFOCache<Integer, Integer>(10), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("RandomCache", new RandomCache<Integer, Integer>(10), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Update(100, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Update(100, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("RandomCache", new RandomCache<Integer, Integer>(100), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Update(1000, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Update(1000, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("FifoCache", new FIFOCache<Integer, Integer>(1000), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Update("RandomCache", new RandomCache<Integer, Integer>(1000), 0, 100));

        /* Delete benchmarks */

        submitCountedBenchmark(new GuavaBenchmarks.Delete(10, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Delete(10, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("FifoCache", new FIFOCache<Integer, Integer>(10), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("RandomCache", new RandomCache<Integer, Integer>(10), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Delete(100, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Delete(100, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("FifoCache", new FIFOCache<Integer, Integer>(100), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("RandomCache", new RandomCache<Integer, Integer>(100), 0, 100));

        submitCountedBenchmark(new GuavaBenchmarks.Delete(1000, 0, 100));
        submitCountedBenchmark(new NativeLruBenchmarks.Delete(1000, 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("FifoCache", new FIFOCache<Integer, Integer>(1000), 0, 100));
        submitCountedBenchmark(new CustomCacheBenchmark.Delete("RandomCache", new RandomCache<Integer, Integer>(1000), 0, 100));

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

        long end = System.currentTimeMillis() + 1_000;
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
