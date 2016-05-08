package desmedt.frederik.cachebenchmarking.generator;

import java.util.Random;

/**
 * A generator generating random numbers between some lower and upper bound.
 */
public class RandomGenerator implements Generator<Integer> {

    public static final String TRACE_TAG = "Random";

    private Random random = new Random();

    private int lower;
    private int upper;

    public RandomGenerator(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public Integer next() {
        return lower + random.nextInt(upper);
    }
}
