package desmedt.frederik.cachebenchmarking.benchmark;

import desmedt.frederik.cachebenchmarking.BenchmarkConfiguration;

/**
 * A test benchmark to quickly check the effectiveness of the {@link BenchmarkConfiguration}
 * implementation.
 */
public class TestBenchmark extends BenchmarkConfiguration {

    public TestBenchmark() {
        super("TestBenchmark");
    }

    @Override
    public void run(Object input) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public Object generateInput() {
        return null;
    }
}
