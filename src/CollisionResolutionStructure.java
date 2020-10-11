public interface CollisionResolutionStructure<K extends Comparable<K>, V> {
    void put(K key, V elem);

    V get(K key);

    int size();

    void destroy();
}
