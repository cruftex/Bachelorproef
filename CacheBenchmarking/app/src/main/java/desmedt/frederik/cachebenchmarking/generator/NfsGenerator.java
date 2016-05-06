package desmedt.frederik.cachebenchmarking.generator;

import android.util.Log;

import org.cache2k.benchmark.traces.CacheAccessTraceSprite;
import org.cache2k.benchmark.traces.CacheAccessTraceUmassWebSearch1;
import org.cache2k.benchmark.util.AccessPattern;
import org.cache2k.benchmark.util.AccessTrace;

/**
 * A generator generating values based on access requests on the Sprite network file system.
 */
public class NfsGenerator implements Generator<Integer> {

    public static final String TRACE_TAG = "NFS";

    private static final String TAG = NfsGenerator.class.getSimpleName();
    private AccessTrace trace = CacheAccessTraceSprite.getInstance();
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
        return CacheAccessTraceSprite.getInstance().getHighValue();
    }
}
