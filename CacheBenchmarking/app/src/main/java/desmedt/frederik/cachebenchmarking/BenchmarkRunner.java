package desmedt.frederik.cachebenchmarking;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import desmedt.frederik.cachebenchmarking.benchmark.TestBenchmark;

/**
 * Responsible for running all {@link BenchmarkConfiguration}s.
 */
public class BenchmarkRunner {

    private static final String TAG = BenchmarkRunner.class.getSimpleName();

    private TestBenchmark testBenchmark = new TestBenchmark();

    /**
     * ExecutorService executing every benchmark in a serializable fashion. Meaning none of them will
     * run parallel to another benchmark. This is way more reliable than parallel execution as in
     * parallel scenario's different benchmarks are both competing for the same resources.
     */
    private ExecutorService benchmarkRunnerService = Executors.newSingleThreadExecutor();

    public void runBenchmarks() {
        benchmarkRunnerService.submit(new Runnable() {
            @Override
            public void run() {
                final BenchmarkConfiguration.Results timedResults = testBenchmark.runTimed(100, 10000);
                Log.i(TAG, "Timed TestBenchmark average time (in millis): " + timedResults.getAverageTimeMillis());
            }
        });

        benchmarkRunnerService.submit(new Runnable() {
            @Override
            public void run() {
                final BenchmarkConfiguration.Results countedResults = testBenchmark.runMany(0, 1000);
                Log.i(TAG, "Counted TestBenchmark average time (in millis): " + countedResults.getAverageTimeMillis());
            }
        });
    }
}
