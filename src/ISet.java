public interface ISet<E extends Comparable<E>> {
    boolean add(E elem);

    boolean contains(E elem);

    int size();

}
