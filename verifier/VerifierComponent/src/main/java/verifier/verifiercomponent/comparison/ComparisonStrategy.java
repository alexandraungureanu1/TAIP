package verifier.verifiercomponent.comparison;

public interface ComparisonStrategy<T, U> {
    boolean compare(T obj1, U obj2);
}
