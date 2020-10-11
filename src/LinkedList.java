public class LinkedList<K extends Comparable<K>, V> implements CollisionManagementStructure<K, V> {

    private ListNode<K, V> first;

    private int size;

    @Override
    public void put(K key, V value) {
        if (first == null)
            this.first = new ListNode<>(key, value);
        else
            addNode(new ListNode<>(key, value));
        size++;
    }

    @Override
    public V get(K key) {
        ListNode<K, V> node;
        return (node = getNode(key)) == null ? null : node.getValue();
    }

    @Override
    public int size() {
        return size;
    }

    public AVLTree<K, V> toTree() {
        System.out.println("Swapping to tree...");
        AVLTree<K, V> tree = new AVLTree<>();
        ListNode<K, V> curr = first;

        while (curr != null) {
            tree.put(curr.getKey(), curr.getValue());
            curr = curr.getNext();
        }

        return tree;
    }

    private void addNode(ListNode<K, V> node) {
        ListNode<K, V> curr = first;
        ListNode<K, V> prev = null;
        boolean keyExists = false;

        if (first == null) {
            this.first = node;
        } else {
            while (curr != null && !keyExists) {
                if (curr.getKey() == node.getKey()) {  // prevents duplicate key value pairs
                    V currVal = curr.getValue();
                    V nodeVal = node.getValue();

                    if (currVal != nodeVal)
                        curr.setValue(node.getValue());
                    keyExists = true;
                }

                ListNode<K, V> tmp = curr;
                prev = curr;
                curr = tmp.getNext();
            }

            if (!keyExists)
                prev.setNext(node);
        }
    }


    private ListNode<K, V> getNode(Object key) {
        ListNode<K, V> curr = first;
        if (curr != null) {
            K nodeKey = curr.getKey();
            if (nodeKey != null && nodeKey == key)
                return curr;
            else {
                while (curr.getNext() != null) {
                    ListNode<K, V> tmp = curr;
                    curr = tmp.getNext();
                    nodeKey = curr.getKey();

                    if (nodeKey == key)
                        return curr;
                }
            }
        }
        return null;
    }

    private static class ListNode<K extends Comparable<K>, V> {
        private final K key;
        private V value;
        private ListNode<K, V> next;

        public ListNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public ListNode<K, V> getNext() {
            return next;
        }

        public void setNext(ListNode<K, V> next) {
            this.next = next;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
