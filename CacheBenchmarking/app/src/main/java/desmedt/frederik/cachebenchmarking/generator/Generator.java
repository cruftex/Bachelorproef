package desmedt.frederik.cachebenchmarking.generator;

/**
 * Something that can generate random values of type {@code E}.
 */
public interface Generator<E> {

    E next();
}
