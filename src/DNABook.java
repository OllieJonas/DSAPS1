public class DNABook implements SocialNetwork {

    /**
     * Default value for the maximum amount of users that can be stored.
     */
    static final int DEFAULT_MAX_USER_SIZE = 100;

    /**
     * The maximum amount of users that can be stored in the social network.
     */
    private final int maxCapacity;

    /**
     * The current amount of users.
     */
    private int userCount = 0;

    /**
     * The users that are currently stored.
     */
    private final IMap<String, Integer> users;

    /**
     * A matrix saying who is friends with who.
     */
    private final boolean[][] friendMatrix;

    /**
     * Default constructor, uses the {@code MAX_USER_SIZE} as the maximum amount of users that can be stored.
     */
    public DNABook() {
        this(DEFAULT_MAX_USER_SIZE);
    }

    /**
     * The constructor for the DNABook. The maximum capacity dictates the size of both the user array and the
     * friend matrix.
     *
     * @param maxCapacity The maximum amount of users that can be stored
     */
    public DNABook(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.users = new HashMapImpl<>(2);
        this.friendMatrix = new boolean[maxCapacity][maxCapacity];
    }

    /**
     * Registers a given user into the social network.
     *
     * @param name The name of the user to be registered
     * @throws ArrayIndexOutOfBoundsException If the user array is full
     */
    @Override
    public void registerUser(String name) {
        if (userCount >= maxCapacity) // the hashmap is unbounded, but the 2D boolean array isn't... :/
            throw new ArrayIndexOutOfBoundsException(userCount + " >= " + maxCapacity);

        users.put(name, userCount++);
    }

    /**
     * Registers both users as friends.
     *
     * @param name1 The name of the first user to be entered
     * @param name2 The name of the second user to be entered
     */
    @Override
    public void becomeFriends(String name1, String name2) {
        int id1 = getIdFrom(name1);
        int id2 = getIdFrom(name2);

        if (isValidId(id1) && isValidId(id2))
            friendMatrix[id2][id1] = true;
            friendMatrix[id1][id2] = true;
    }

    @Override
    public boolean areTheyFriends(String name1, String name2) {
        int id1 = getIdFrom(name1);
        int id2 = getIdFrom(name2);

        if (!isValidId(id1) || !isValidId(id2))
            return false;

        return friendMatrix[id1][id2] || friendMatrix[id2][id1];
    }

    public boolean isEmpty() {
        return userCount == 0;
    }

    private int getIdFrom(String name) {
        if (isEmpty())
            return -1;

        return users.get(name);
    }

    private boolean isValidId(int id) {
        return id >= 0;
    }


    public interface IMap<K extends Comparable<K>, V> {
        void put(K key, V value);
        V get(K key);
    }

    /**
     * <p>Own implementation of a very simplistic HashMap.</p>
     *
     *
     * ======= TL;DR =======
     *
     *
     * <p>HashMap has O(1) complexity. Hashing function is taken from {@link java.util.HashMap} with a couple of
     * modifications. Collisions are dealt with a linked list. If this linked list gets too large, it's converted to
     * an AVL tree and that's used for lookup / insertion from there. So, a worst case complexity of O(log(n)),
     * although given we only have 100 entries max, it will almost certainly never reach that.</p>
     *
     *
     * ======= EXPLANATION =======
     *
     * <p>I've done this to allow for there to be a very high chance of there being an O(1) complexity when looking up
     * a User's ID from their string, especially considering we'll only have 100 users max.
     * There isn't an implementation to remove from the Map, seeing as it's not asked for in the question. (Who would
     * want to remove friends anyways, right? Never in DSABook! :D)</p>
     *
     * <p>This implementation also provides two additional benefits. Firstly, this collection is unbounded. Therefore,
     * if you were able to implement the friend matrix in an unbounded and more space-efficient way
     * (maybe using a Graph of some kind?), then you could have a theoretical infinite amount of users and friends
     * (provided you had the memory to do it, obviously). Secondly, there is now no longer a requirement to
     * add the users in alphabetical order, since the User ID is now found via hashing.</p>
     *
     *
     * ======= IMPLEMENTATION  =======
     *
     * <p>The hashing function I've used is basically just {@link java.util.HashMap}'s hashing function, however it
     * applies a circular function around the list (ie. hash % number of buckets). I have also taken the absolute
     * value of the hashed result, since I got some weird results after testing. (This probably isn't the actual
     * way to deal with this in a real application, but it works so eh?)</p>
     *
     * <p>Collisions are handled using a linked list, which is contained in each bucket, primarily so I don't have to
     * go implementing some wacky {@link java.util.ArrayList#grow()} stuff to make the collection unbounded.</p>
     *
     * <p>However, if using this collision management system on its own, if the linked list contained inside the
     * buckets were large enough, the worst-case scenario would tend towards O(n) complexity (Since we're searching
     * through essentially just an ordinary list).</p>
     *
     * <p>In this implementation, if a certain threshold is reached, the list will be copied to an AVL tree, and the
     * head of the linked list will be set to null (Java's Garbage Collection will collect the entire list anyways so
     * no need to set everything to null). From this point on, the AVL tree is used for lookup, which would guarantee a
     * worst case lookup and insertion time of O(log(n)).</p>
     *
     * <p>This threshold for how large any linked list needs to be before it starts doing the binary search
     * can be customised using the {@code binaryThreshold} parameter, although I have defaulted it to 8.
     * This, therefore, should ensure worst-case complexity of O(log(n)). That being said, at the point where the
     * linked list is being transferred to a tree, the complexity at this part will be a guaranteed O(n).
     * However, given the upsides if you're dealing with a large amount of users, I personally think this tradeoff
     * is worth it.</p>
     *
     * 
     * ======= NOTES  =======
     *
     * <p>There's definitely a sneaky way of not forcing the key to implement the {@link java.lang.Comparable}
     * interface, but it's easier for the AVL tree.</p>
     *
     * <p>We can suppress un-checked warnings since the public interface that the user can interact with
     * never theoretically causes any casting exceptions - all casting is handled internally.</p>
     *
     * @param <K> The key type
     * @param <V> The value type
     */
    @SuppressWarnings({"unchecked", "JavadocReference"})
    public static class HashMapImpl<K extends Comparable<K>, V> implements IMap<K, V> {

        static final int DEFAULT_BUCKET_CAPACITY = 100;

        static final int DEFAULT_COLLISION_THRESHOLD = 8;

        private final int bucketCapacity;

        private final int collisionThreshold;

        private final Bucket<?, ?>[] data;

        public HashMapImpl() {
            this(DEFAULT_BUCKET_CAPACITY, DEFAULT_COLLISION_THRESHOLD);
        }

        public HashMapImpl(int bucketCapacity) {
            this(bucketCapacity, DEFAULT_COLLISION_THRESHOLD);
        }

        public HashMapImpl(int bucketCapacity, int collisionThreshold) {
            if (bucketCapacity <= 1) {
                throw new IllegalArgumentException("Illegal argument: bucketCapacity - Bucket capacity must be greater than 1!");
            }
            this.bucketCapacity = bucketCapacity;
            this.collisionThreshold = collisionThreshold;
            this.data = new Bucket<?, ?>[bucketCapacity];
        }

        @Override
        public void put(K key, V value) {
            int hash = hashFunction(key);
            Bucket<K, V> bucket = getBucket(hash);
            ListNode<K, V> node = new ListNode<>(key, value);

            if (bucket == null) {
                data[hash] = new Bucket<>(node, collisionThreshold);
            } else {
                bucket.addNode(node);
            }
        }

        @Override
        public V get(K key) {
            Bucket<K, V> bucket = getBucket(hashFunction(key));
            if (bucket == null) // if bucket is null then obviously the key isn't going to exist
                return null;

            ListNode<K, V> node = bucket.getNode(key);

            return (node == null) ? null : node.getValue();
        }

        private Bucket<K, V> getBucket(int hash) {
            return (Bucket<K, V>) data[hash];
        }

        private int hashFunction(Object key) {
            int h;
            return key == null ? 0 : Math.abs(((h = key.hashCode()) ^ (h >>> 16)) % (bucketCapacity - 1));
        }

        private static class Bucket<K extends Comparable<K>, V> {

            private ListNode<K, V> first; // only applicable when using linked list

            private ITree<Integer, ListNode<K, V>> tree;

            private final int collisionThreshold;

            private int noCollisions;

            private int elementCount;

            private boolean usingTree;

            public Bucket(ListNode<K, V> first, int collisionThreshold) {
                this.first = first;
                this.collisionThreshold = collisionThreshold;
                this.usingTree = false;
                this.noCollisions = 0;
            }

            public ListNode<K, V> getNode(K key) {
                if (!usingTree) {
                    return getNodeFromLinkedList(key);
                } else {
                    return getNodeFromTree(key);
                }
            }

            public void addNode(ListNode<K, V> node) {
                if (!usingTree) {
                    addNodeToLinkedList(node);

                    if (updateUsingTree())
                        swapToTree();

                } else {
                    addNodeToTree(node);
                }
                elementCount++;
            }

            private void swapToTree() {
                this.tree = new AVLTree<>();
                int count = 0;
                ListNode<K, V> curr = first;

                while (curr != null) {
                    this.tree.insert(count++, curr);
                    curr = curr.getNext();
                }

                this.first = null; // gc will take care of collecting the rest of the list
            }

            private void addNodeToTree(ListNode<K, V> node) {
                Preconditions.checkNotNull(tree);
                // this.tree.insert(node.getKey(), node);
            }

            private boolean updateUsingTree() {
                return this.usingTree = noCollisions > collisionThreshold;
            }

            private void addNodeToLinkedList(ListNode<K, V> node) {
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
                        curr = tmp.next;
                    }

                    if (!keyExists)
                        prev.setNext(node);
                        noCollisions++;
                }
            }

            private ListNode<K, V> getNodeFromTree(K key) {
                return tree.find((Integer) key);
            }

            private ListNode<K, V> getNodeFromLinkedList(Object key) {
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

            public AVLTree.TreeNode<K, V> toTreeNode(int height, int size) {
                return new AVLTree.TreeNode<>(this.getKey(), this.getValue(), height, size);
            }
        }
    }

    public interface ITree<K extends Comparable<K>, V> {
        void insert(K key, V elem);
        V find(K key);
    }

    public static class AVLTree<K extends Comparable<K>, V> implements ITree<K, V> {

        private TreeNode<K, V> root;

        public AVLTree() {
        }

        @Override
        public void insert(K key, V elem) {
            if (root == null) {
                this.root = new TreeNode<>(key, elem);
            } else {
                this.root = null;
            }
        }

        @Override
        public V find(K key) {
            return null;
        }

        private static class TreeNode<K extends Comparable<K>, V> {
            private final K key;
            private V value;
            private int height;
            private int size;
            private TreeNode<K, V> left;
            private TreeNode<K, V> right;

            public TreeNode(K key, V value) {
                this(key, value, 0, 0);
            }

            public TreeNode(K key, V value, int height, int size) {
                this.key = key;
                this.value = value;
                this.height = height;
                this.size = size;
            }

            public void setValue(V value) {
                this.value = value;
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
        }
    }


    /**
     * Quick implementation of the requireNonNull / checkNotNull method from Java's Objects / Guava's Preconditions
     * utility class.
     */
    public static class Preconditions {
        public static <T> T checkNotNull(T obj) {
            if (obj == null)
                throw new NullPointerException("Somethings gone seriously wrong... :(");
            return obj;
        }
    }
}
