package desmedt.frederik.cachebenchmarking.generator;

import org.cache2k.benchmark.util.ZipfianPattern;

/**
 * A generator generating random numbers between some lower and upper bound following a zipf-like
 * pattern.
 */
public class ZipfGenerator implements Generator<Integer> {

    public static final String TRACE_TAG = "Zipf";

    private final ZipfianPattern pattern;

    // UPisa trace
    private double UPISA = 0.78;

    public ZipfGenerator(int lowerBound, int upperBound) {
        pattern = new ZipfianPattern((long) lowerBound, (long) upperBound, UPISA);
    }

    @Override
    public Integer next() {
        int next = pattern.next();
        return next;
    }
}
