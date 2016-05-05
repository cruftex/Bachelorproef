package desmedt.frederik.cachebenchmarking.generator;

/**
 * Something that can generate values of type {@code E}. This interface is used with arbitrary
 * semantics, be it as a random instance generator or a one-time single instance generator.
 */
public interface Generator<E> {

    E next();
}
