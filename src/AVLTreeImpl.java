
public class AVLTreeImpl<K extends Comparable<K>, V> implements AVLTree<K, V> {

    private TreeNode<K, V> root;

    private int size;

    public AVLTreeImpl() {
    }

    @Override
    public void put(K key, V elem) {
        this.root = this.root == null ? new TreeNode<>(key, elem) : putNode(root, key, elem);
        size++;
    }

    @Override
    public V get(K key) {
        Util.assertNotNull(key);
        TreeNode<K, V> node;
        return (node = getNode(key)) == null ? null : node.getValue();
    }

    @Override
    public int size() {
        return size;
    }

    private TreeNode<K, V> getNode(K key) {
        return getNode(root, key);
    }

    private TreeNode<K, V> putNode(TreeNode<K, V> node, K key, V val) {
        if (node == null) return new TreeNode<>(key, val); // recursively go through until we find an empty node
        int comparison = key.compareTo(node.getKey());

        if (comparison < 0) {  // if key already exists, update the value
            node.setLeft(putNode(node.getLeft(), key, val));
        } else if (comparison > 0) {
            node.setRight(putNode(node.getRight(), key, val));
        } else {
            node.setValue(val);
            return node;
        }

        node.setHeight(1 + Math.max(node.getLeftHeight(), node.getRightHeight()));
        return balance(node);
    }

    private TreeNode<K, V> balance(TreeNode<K, V> node) {
        int balanceFactor = node.getBalanceFactor();

        if (balanceFactor < -1) {
            TreeNode<K, V> right = node.getRight();

            if (right != null && right.getBalanceFactor() > 0) {
                node.setRight(rotateRight(right));
            }
            node = rotateLeft(node);

        } else if (balanceFactor > 1) {
            TreeNode<K, V> left = node.getLeft();
            if (left != null && left.getBalanceFactor() < 0) {
                node.setLeft(rotateLeft(left));
            }
            node = rotateRight(node);
        }
        return node;
    }

    private TreeNode<K, V> rotateLeft(TreeNode<K, V> node1) {
        TreeNode<K, V> node2 = node1.right;

        node1.right = node2.left;
        node2.left = node1;

        node1.setHeight(1 + Math.max(node1.getLeftHeight(), node1.getRightHeight()));
        node2.setHeight(1 + Math.max(node2.getLeftHeight(), node2.getRightHeight()));
        return node2;
    }

    private TreeNode<K, V> rotateRight(TreeNode<K, V> node1) {
        TreeNode<K, V> node2 = node1.left;

        node1.left = node2.right;
        node2.right = node1;

        node1.setHeight(1 + Math.max(node1.getLeftHeight(), node1.getRightHeight()));
        node2.setHeight(1 + Math.max(node2.getLeftHeight(), node2.getRightHeight()));
        return node2;
    }
    private TreeNode<K, V> getNode(TreeNode<K, V> node, K key) {
        if (node == null) // couldn't find the node
            return null;

        int comparison = key.compareTo(node.getKey());

        if (comparison == 0) // if the key matches the key we're looking for, then great!
            return node;
        else if (comparison < 0)  // we inserted all nodes with smaller keys than curr on the left, so its somewhere there.
            return getNode(node.getLeft(), key);
        else
            return getNode(node.getRight(), key);  // same logic for right
    }

    static class TreeNode<K extends Comparable<K>, V> {
        private final K key;
        private V value;
        private int height;
        private TreeNode<K, V> left, right;

        public TreeNode(K key, V value) {
            this(key, value, 0);
        }

        public TreeNode(K key, V value, int height) {
            this.key = key;
            this.value = value;
            this.height = height;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int getHeight() {
            return height;
        }

        public int getLeftHeight() {
            if (left == null) return -1;
            return left.getHeight();
        }

        public int getRightHeight() {
            if (right == null) return -1;
            return right.getHeight();
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setLeft(TreeNode<K, V> left) {
            this.left = left;
        }

        public void setRight(TreeNode<K, V> right) {
            this.right = right;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public TreeNode<K, V> getLeft() {
            return left;
        }

        public TreeNode<K, V> getRight() {
            return right;
        }

        public int getBalanceFactor() {
            return (left == null ? -1 : left.getHeight()) - (right == null ? -1 : right.getHeight());
        }
    }
}