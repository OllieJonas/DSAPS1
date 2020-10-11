public interface HashSet<E extends Comparable<E>> {
    void add(E elem);

    boolean contains(E elem);

    int size();
}
