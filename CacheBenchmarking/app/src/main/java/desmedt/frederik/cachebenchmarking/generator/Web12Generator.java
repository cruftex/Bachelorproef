package desmedt.frederik.cachebenchmarking.generator;

import android.util.Log;

import org.cache2k.benchmark.traces.CacheAccessTraceWeb12;
import org.cache2k.benchmark.util.AccessPattern;
import org.cache2k.benchmark.util.AccessTrace;

/**
 * A generator generating values based on HTTP GET requests of a product detail website.
 */
public class Web12Generator implements Generator<Integer> {

    public static final String TRACE_TAG = "Web12";

    private static final String TAG = Web12Generator.class.getSimpleName();
    private AccessTrace trace = CacheAccessTraceWeb12.getInstance();
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
        return CacheAccessTraceWeb12.getInstance().getHighValue();
    }
}
