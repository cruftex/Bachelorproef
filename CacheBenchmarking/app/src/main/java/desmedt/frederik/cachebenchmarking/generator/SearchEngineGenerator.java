package desmedt.frederik.cachebenchmarking.generator;

import android.util.Log;

import org.cache2k.benchmark.traces.CacheAccessTraceSprite;
import org.cache2k.benchmark.traces.CacheAccessTraceUmassWebSearch1;
import org.cache2k.benchmark.traces.CacheAccessTraceWeb12;
import org.cache2k.benchmark.util.AccessPattern;
import org.cache2k.benchmark.util.AccessTrace;

/**
 * A generator generating values based on search requests of an unnamed popular search engine.
 */
public class SearchEngineGenerator implements Generator<Integer> {

    public static final String TRACE_TAG = "SearchEngine";

    private static final String TAG = SearchEngineGenerator.class.getSimpleName();
    private AccessTrace trace = CacheAccessTraceUmassWebSearch1.getInstance();
    private AccessPattern pattern = new LoopingAccessPattern(trace);

    @Override
    public Integer next() {
        int next = Integer.MIN_VALUE;

        while (next < 0) {
            try {
                next = pattern.next();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't generate next value in trace", e);
                return null;
            }
        }

        return next;
    }

    public static int getLowerBound() {
        return 0;
    }

    public static int getUpperBound() {
        // Call getInstance() instead of static field to not cause a strong reference
        return CacheAccessTraceUmassWebSearch1.getInstance().getHighValue();
    }
}
