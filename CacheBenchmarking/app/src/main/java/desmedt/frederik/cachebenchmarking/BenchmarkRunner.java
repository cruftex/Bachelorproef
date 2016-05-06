package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import desmedt.frederik.cachebenchmarking.benchmark.BaseBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.Cache2KBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.CustomBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.GuavaBenchmarks;
import desmedt.frederik.cachebenchmarking.benchmark.JackRabbitLIRSBenchmark;
import desmedt.frederik.cachebenchmarking.benchmark.NativeLruBenchmarks;
import desmedt.frederik.cachebenchmarking.cache.Cache;
import desmedt.frederik.cachebenchmarking.cache.FIFOCache;
import desmedt.frederik.cachebenchmarking.cache.RandomCache;
import desmedt.frederik.cachebenchmarking.generator.Generator;
import desmedt.frederik.cachebenchmarking.generator.NfsGenerator;
import desmedt.frederik.cachebenchmarking.generator.RandomGenerator;
import desmedt.frederik.cachebenchmarking.generator.SearchEngineGenerator;
import desmedt.frederik.cachebenchmarking.generator.Web12Generator;
import desmedt.frederik.cachebenchmarking.generator.ZipfGenerator;

/**
 * Responsible for running all {@link CacheBenchmarkConfiguration}s.
 */
public class BenchmarkRunner {

    private static final String TAG = BenchmarkRunner.class.getSimpleName();

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

        NfsGenerator nfsGenerator = new NfsGenerator();
        for (int i = 1; i <= 10; i++) {
            submitCountedReadBenchmarks(NfsGenerator.getLowerBound(), NfsGenerator.getUpperBound(), (double) i / 100, NfsGenerator.TRACE_TAG, nfsGenerator, 1000, NfsGenerator.getUpperBound() * 50);
        }
        nfsGenerator = null; // remove strong reference

//        SearchEngineGenerator searchEngineGenerator = new SearchEngineGenerator();
//        for (int i = 1; i <= 10; i++) {
//            submitCountedReadBenchmarks(SearchEngineGenerator.getLowerBound(), SearchEngineGenerator.getUpperBound(), (double) i / 1000, SearchEngineGenerator.TRACE_TAG, searchEngineGenerator, 1000, 10_000);
//        }
//        searchEngineGenerator = null; // remove strong reference
//
//        Web12Generator web12Generator = new Web12Generator();
//        for (int i = 1; i <= 20; i++) {
//            submitCountedReadBenchmarks(0, Web12Generator.getUpperBound(), (double) i / 100, Web12Generator.TRACE_TAG, web12Generator, 1000, 100_000);
//        }
//        web12Generator = null;
//
//        ZipfGenerator zipfGenerator = new ZipfGenerator(0, 500);
//        for (int i = 1; i <= 10; i++) {
//            submitCountedReadBenchmarks(0, 500, (double) i / 10, ZipfGenerator.TRACE_TAG, zipfGenerator, 1000, 100_000);
//        }
//        zipfGenerator = null;
//
//        RandomGenerator randomGenerator = new RandomGenerator(0, 500);
//        for (int i = 1; i <= 10; i++) {
//            submitCountedReadBenchmarks(0, 500, (double) i / 10, RandomGenerator.TRACE_TAG, randomGenerator, 1000, 100_000);
//        }
//        randomGenerator = null;

        /* Insert benchmarks */

//        for (int i = 1; i <= 10; i++) {
//            final int upperBound = 500;
//            double cacheRatio = i / 10;
//            final int cacheSize = Math.round(upperBound * ((float) i / 10));
//
//            submitCountedBenchmark(new GuavaBenchmarks.Insert(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new NativeLruBenchmarks.Insert(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new CustomBenchmark.Insert("FifoCache", cacheRatio, 0, upperBound, generateFifoCache(cacheSize)));
//            submitCountedBenchmark(new CustomBenchmark.Insert("RandomCache", cacheRatio, 0, upperBound, generateRandomCache(cacheSize)));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Insert(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Insert(Cache2KBenchmark.CLOCK_CACHE, cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Insert(Cache2KBenchmark.ARC_CACHE, cacheRatio, 0, upperBound));
//        }
//
//        /* Update benchmarks */
//
//        for (int i = 1; i <= 10; i++) {
//            final int upperBound = 500;
//            double cacheRatio = i / 10;
//
//            final int cacheSize = Math.round(upperBound * ((float) i / 10));
//
//            submitCountedBenchmark(new GuavaBenchmarks.Update(cacheSize, 0, upperBound));
//            submitCountedBenchmark(new NativeLruBenchmarks.Update(cacheSize, 0, upperBound));
//            submitCountedBenchmark(new CustomBenchmark.Update("FifoCache", cacheSize, 0, upperBound, generateFifoCache(cacheSize)));
//            submitCountedBenchmark(new CustomBenchmark.Update("RandomCache", cacheSize, 0, upperBound, generateRandomCache(cacheSize)));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Update(cacheSize, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Update(Cache2KBenchmark.CLOCK_CACHE, cacheSize, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Update(Cache2KBenchmark.ARC_CACHE, cacheSize, 0, upperBound));
//        }
//
//        /* Delete benchmarks */
//
//        for (int i = 1; i <= 10; i++) {
//            final int upperBound = 500;
//            double cacheRatio = i / 10;
//            final int cacheSize = Math.round(upperBound * ((float) i / 10));
//
//            submitCountedBenchmark(new GuavaBenchmarks.Delete(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new NativeLruBenchmarks.Delete(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new CustomBenchmark.Delete("FifoCache", cacheRatio, 0, upperBound, generateFifoCache(cacheSize)));
//            submitCountedBenchmark(new CustomBenchmark.Delete("RandomCache", cacheRatio, 0, upperBound, generateRandomCache(cacheSize)));
//            submitCountedBenchmark(new JackRabbitLIRSBenchmark.Delete(cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Delete(Cache2KBenchmark.CLOCK_CACHE, cacheRatio, 0, upperBound));
//            submitCountedBenchmark(new Cache2KBenchmark.Delete(Cache2KBenchmark.ARC_CACHE, cacheRatio, 0, upperBound));
//        }

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
                Log.i(TAG, benchmarkConfiguration.getStats().toString());
                resetEnvironment();
            }
        });
    }

    private void submitCountedReadBenchmarks(int lowerBound, int upperBound, double cachedRatio, String traceTag, Generator<Integer> generator, int warmupIterations, int runIterations) {
        final int cacheSize = (int) Math.round((upperBound - lowerBound) * cachedRatio);
        submitCountedBenchmark(new GuavaBenchmarks.Read(traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
        submitCountedBenchmark(new NativeLruBenchmarks.Read(traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
//        submitCountedBenchmark(new CustomBenchmark.Read(FIFOCache.CACHE_TAG, traceTag, generator, cachedRatio, lowerBound, upperBound, generateFifoCache(cacheSize)), warmupIterations, runIterations);
        submitCountedBenchmark(new Cache2KBenchmark.Read(Cache2KBenchmark.RANDOM_CACHE, traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
        submitCountedBenchmark(new JackRabbitLIRSBenchmark.Read(traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
        submitCountedBenchmark(new Cache2KBenchmark.Read(Cache2KBenchmark.CLOCK_CACHE, traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
        submitCountedBenchmark(new Cache2KBenchmark.Read(Cache2KBenchmark.ARC_CACHE, traceTag, generator, cachedRatio, lowerBound, upperBound), warmupIterations, runIterations);
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
