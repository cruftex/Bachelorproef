package desmedt.frederik.cachebenchmarking.benchmark;

import android.util.Pair;

import desmedt.frederik.cachebenchmarking.CacheBenchmarkConfiguration;

/**
 * A test benchmark to quickly check the effectiveness of the {@link CacheBenchmarkConfiguration}
 * implementation.
 */
public class CacheTestBenchmark extends CacheBenchmarkConfiguration {

    public CacheTestBenchmark() {
        super("CacheTestBenchmark", null, null);
    }

    @Override
    public boolean run(Comparable key, Object value) {
        try {
            Thread.sleep(1);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public Pair generateInput() {
        return null;
    }

    @Override
    protected CacheStats generateStats() {
        return CacheStats.nonRead(0, 0);
    }
}
