public class HashSetImpl<E extends Comparable<E>> implements HashSet<E> {

    private final HashMap<E, Object> map;

    private final Object dummy = new Object();

    public HashSetImpl() {
        this.map = new HashMapImpl<>();
    }

    @Override
    public void add(E elem) {
        map.put(elem, dummy);
    }

    @Override
    public boolean contains(E elem) {
        return map.containsKey(elem);
    }

    @Override
    public int size() {
        return map.size();
    }
}
