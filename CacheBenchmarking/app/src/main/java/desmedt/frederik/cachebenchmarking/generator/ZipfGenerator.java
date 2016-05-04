package desmedt.frederik.cachebenchmarking.generator;

import org.apache.commons.math3.distribution.ZipfDistribution;

import java.util.Random;

/**
 * A generator generating random numbers between some lower and upper bound following a zipf-like
 * distribution.
 */
public class ZipfGenerator implements Generator<Integer> {

    private final ZipfDistribution distribution;

    // UPisa trace
    private double UPISA = 0.78;

    public ZipfGenerator(int lowerBound, int upperBound) {
        distribution = new ZipfDistribution(upperBound - lowerBound, UPISA);
    }

    @Override
    public Integer next() {
        int next = distribution.sample();
        return next;
    }
}
