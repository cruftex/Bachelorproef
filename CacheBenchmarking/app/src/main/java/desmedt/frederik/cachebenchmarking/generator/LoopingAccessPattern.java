package desmedt.frederik.cachebenchmarking.generator;

import org.cache2k.benchmark.util.AccessPattern;
import org.cache2k.benchmark.util.AccessTrace;

/**
 * An {@link AccessPattern} looping over a given eternal {@link org.cache2k.benchmark.util.AccessTrace},
 * essentially making it eternal.
 */
public class LoopingAccessPattern extends AccessPattern {

    private AccessTrace trace;
    private int index = 0;
    private int[] traceArray;

    /**
     * @param trace The trace that should be looped
     */
    public LoopingAccessPattern(AccessTrace trace) {
        this.trace = trace;
        traceArray = trace.getTrace();
    }

    @Override
    public boolean isEternal() {
        return true;
    }

    @Override
    public boolean hasNext() throws Exception {
        return true;
    }

    @Override
    public int next() throws Exception {
        int result = traceArray[index];
        index = (index + 1) % traceArray.length;
        return result;
    }
}
