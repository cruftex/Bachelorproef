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
        double exponent = 0.7d + Math.random() * 0.3;
        distribution = new ZipfDistribution(upperBound - lowerBound, exponent);
    }

    @Override
    public Integer next() {
        return distribution.sample();
    }
}
