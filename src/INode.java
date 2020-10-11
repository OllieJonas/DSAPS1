public interface INode<K extends Comparable<K>, V> {
    K getKey();

    V getValue();
}