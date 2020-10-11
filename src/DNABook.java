/**
 *<p>Implementation of the SocialNetwork interface.</p>
 *
 *
 * ======= IMPLEMENTATION =======
 *
 * <p>To implement this, I decided to implement my own version of a {@link java.util.HashMap} and
 * {@link java.util.HashSet}. I have included more specific comments as to how I've gone about implementing these in
 * the respective classes. I've used a HashMap where the key is the user's name, and the value is a Set of their
 * friends. Then, in order to register, make friends with and check whether 2 users are friends is just as simple as
 * manipulating the data structures and performing some null checks.</p>
 *
 *
 * ======= EXPLANATION =======
 *
 * <p>By doing it in this way, it provides 4 major benefits over binary search.</p>
 *
 * <p>Firstly, the time to execute is decreased - the implementation of DSABook now runs at O(1) complexity.</p>
 *
 * <p>Secondly, the space complexity is also reduced. With a 2D Boolean array {@code boolean[][]}, the space
 * complexity would be a guaranteed O(n^2), since you'd have to fill every single entry with some amount of
 * information. With this implementation, there is no need to store any information until the user becomes friends
 * with someone else. This does mean that the storage if every single user was friends with everyone would reach O(n^2),
 * however obviously this is very unlikely.</p>
 *
 * <p>Thirdly, you no longer need to register users into the network in alphabetical order - they can be registered in
 * any order you wish.</p>
 *
 * <p>Finally, this list is now unbounded - provided you have the memory, you could theoretically have an infinite
 * number of friends. I have added an artificial cap for the sake of keeping to the 100 user limit set out in the
 * question, but it is by no means a requirement of the system.</p>
 *
 * <p>The main disadvantage of using a system like this comes from the way I've implemented the HashMap. In order
 * to guarantee keeping within the O(log(n)) requirements, I've opted to use an AVL tree to resolve collisions.
 * Obviously, this is far from ideal, given how much memory this will take up, however there isn't a memory usage
 * limitation put into the question, so I opted for this route instead.</p>
 *
 *
 * ======= NOTES =======
 *
 * <p>I do understand that this implementation is absolutely 100% overkill. However, to be honest, I really enjoyed
 * challenging myself with this! I feel like I've learned a lot about how Java implements a HashMap and HashSet, how to
 * implement my own as well as learn more about self-balancing trees.</p>
 */
public class DNABook implements SocialNetwork {

    static final int DEFAULT_MAX_CAPACITY = 100;

    /**
     * The users that are currently stored.
     */
    private final HashMap<String, HashSet<String>> users;

    private final int maxCapacity;

    public DNABook() {
        this(-1);
    }

    @SuppressWarnings("unused")
    public DNABook(boolean bounded) {
        this(bounded ? -1 : DEFAULT_MAX_CAPACITY);
    }

    /**
     * The constructor for the DNABook.
     */
    public DNABook(int maxCapacity) {
        this.users = new HashMapImpl<>();
        this.maxCapacity = maxCapacity;
    }

    /**
     * Registers a given user into the social network.
     *
     * @param name The name of the user to be registered
     * @throws ArrayIndexOutOfBoundsException If the user array is full
     */
    @Override
    public void registerUser(String name) {
        if (!isFull() || !isBounded())
            users.put(name, new HashSetImpl<>());
    }

    /**
     * Registers both users as friends.
     *
     * @param name1 The name of the first user to be entered
     * @param name2 The name of the second user to be entered
     */
    @Override
    public void becomeFriends(String name1, String name2) {
        HashSet<String> friends1 = users.get(name1);
        HashSet<String> friends2 = users.get(name2);

        if (friends1 != null && friends2 != null) {
            friends1.add(name2);
            friends2.add(name1);
        }
    }

    @Override
    public boolean areTheyFriends(String name1, String name2) {
        HashSet<String> friends1 = users.get(name1);
        HashSet<String> friends2 = users.get(name2);

        return (friends1 != null && friends1.contains(name2)) || (friends2 != null && friends2.contains(name1));
    }

    private boolean isBounded() {
        return maxCapacity != -1;
    }

    private boolean isFull() {
        return size() >= maxCapacity;
    }

    public int size() {
        return users.size();
    }

    /*
     * ============================ INTERFACES ============================
     */
    public interface HashMap<K extends Comparable<K>, V> {
        void put(K key, V value);

        V get(K key);

        boolean containsKey(K key);

        int size();
    }

    public interface AVLTree<K extends Comparable<K>, V> {
        void put(K key, V elem);

        V get(K key);
    }

    public interface HashSet<E extends Comparable<E>> {
        void add(E elem);

        boolean contains(E elem);
    }

    /*
     * ============================ IMPLEMENTATIONS ============================
     */

    /**
     * <p>Own implementation of a very simplistic HashMap.</p>
     *
     *
     * ======= TL;DR =======
     *
     *
     * <p>HashMap has O(1) complexity. Hashing function is taken from {@link java.util.HashMap} with a couple of
     * modifications. Collisions are resolved using an AVT Tree because although each node takes up more memory than a
     * linked list, the lookup / insertion time will always be O(log(n)), which is what is asked for in the question.</p>
     *
     *
     * ======= EXPLANATION =======
     *
     * <p>I've done this to allow for there to be a very high chance of there being an O(1) complexity when looking up
     * a User's ID from their string, especially considering we'll only have 100 users max.
     * There isn't an implementation to remove from the Map, seeing as it's not asked for in the question. (Who would
     * want to remove friends anyways, right? Never in DSABook! :D)</p>
     *
     * <p>This implementation also provides two additional benefits. Firstly, this collection is unbounded. With this,
     * you could have a theoretical infinite amount of users and friends (provided you had the memory to do it,
     * obviously). Secondly, there is now no longer a requirement to add the users in alphabetical order, since the
     * User ID is now found via hashing.</p>
     *
     *
     * ======= IMPLEMENTATION  =======
     *
     * <p>The hashing function I've used is basically just {@link java.util.HashMap}'s hashing function, however it
     * applies a circular function around the list (ie. hash % number of buckets). I have also taken the absolute
     * value of the hashed result, since I got some weird results after testing. (This probably isn't the actual
     * way to deal with this in a real application, but it works so eh?)</p>
     *
     * <p>Collisions are resolved using an AVT tree. Although I do understand it's far more conventional to use a
     * linked list, using this method of implementation would take O(n) complexity for lookup / insertion, which is
     * prohibited in the question. Although an AVT tree will take up more memory (the nodes for a tree will take up
     * considerably more memory than a linked list node), given the constraints being for time complexity, using a
     * tree makes more sense in this case.<p>
     *
     * <p>One of the main disadvantages of using an AVT tree over a linked list however, is that there is a requirement
     * that the keys are comparable in some way - hence the {@code <K extends Comparable<K>>} bounded type parameter
     * given in the key. However, given that we're using Strings as our key in our implementation, this won't really
     * affect us.</p>
     *
     *<p>I am also aware that {@link java.util.HashMap} uses a combination of a LinkedList and a red-black tree
     * to resolve their collisions to save memory. However, I opted against this for two reasons. The first is stated
     * above, where it would require a O(n) lookup time. Secondly, converting I found converting a linked list to an
     * AVT tree would also take O(n) time to complete, which again isn't worth it considering we will only have 100
     * users.</p>
     *
     *
     * ======= NOTES  =======
     *
     * <p>There's definitely a sneaky way of not forcing the key to implement the {@link java.lang.Comparable}
     * interface. However, it's easier for the AVL tree and I can't be bothered to figure it out.</p>
     *
     * <p>We can suppress any un-checked warnings since the public interface that the user can interact with
     * never theoretically causes any casting exceptions - all casting is handled internally.</p>
     *
     * @param <K> The key type
     * @param <V> The value type
     */
    public static class HashMapImpl<K extends Comparable<K>, V> implements HashMap<K, V> {

        static final int DEFAULT_BUCKET_CAPACITY = 30;

        private final int bucketCapacity;

        private int elementCount;

        private final Bucket<?, ?>[] data;

        public HashMapImpl() {
            this(DEFAULT_BUCKET_CAPACITY);
        }

        public HashMapImpl(int bucketCapacity) {
            if (bucketCapacity < 1) {
                throw new IllegalArgumentException("Illegal argument: bucketCapacity - bucketCapacity < 1!");
            }

            this.bucketCapacity = bucketCapacity;
            this.data = new Bucket<?, ?>[bucketCapacity];
        }

        @Override
        public void put(K key, V value) {
            int hash = hashFunction(key);
            Bucket<K, V> bucket = getBucket(hash);

            if (bucket == null) {
                bucket = new Bucket<>();
            }

            bucket.put(key, value);

            data[hash] = bucket;
            elementCount++;
        }

        @Override
        public V get(K key) {
            Bucket<K, V> bucket = getBucket(hashFunction(key));
            return bucket == null ? null : bucket.get(key);
        }

        @Override
        public boolean containsKey(K key) {
            Bucket<K, V> bucket = getBucket(hashFunction(key));
            return bucket != null && bucket.get(key) != null;
        }

        @Override
        public int size() {
            return elementCount;
        }

        @SuppressWarnings("unchecked")
        private Bucket<K, V> getBucket(int hash) {
            return (Bucket<K, V>) data[hash];
        }

        private int hashFunction(Object key) {
            int h;
            return key == null ? 0 : Math.abs(((h = key.hashCode()) ^ (h >>> 16)) % bucketCapacity);
        }

        private static class Bucket<K extends Comparable<K>, V> {

            /**
             * Initial key / value combination - prevents the creation of a tree unnecessarily if there's only going to
             * be one thing in the bucket - which is the most likely scenario.
             */
            private K key;
            private V val;

            private AVLTree<K, V> tree;

            public Bucket() {
            }

            public V get(K key) {
                if (this.key != null) {

                    if (this.key == key)
                        return val;
                    else if (this.tree != null)
                        return tree.get(key);

                }
                return null;
            }

            private void put(K key, V val) {
                if (this.key != null) {
                    this.tree = new AVLTreeImpl<>(); // default first collection to be used
                    tree.put(key, val);
                } else {
                    this.key = key;
                    this.val = val;
                }
            }
        }

        /**
         * Basic implementation of an AVL self-balancing tree.
         *
         * @param <K> The type of the key (must implement Comparable interface)
         * @param <V> The type of the value
         */
        public static class AVLTreeImpl<K extends Comparable<K>, V> implements AVLTree<K, V> {

            private TreeNode<K, V> root;

            public AVLTreeImpl() {
            }

            @Override
            public void put(K key, V elem) {
                this.root = this.root == null ? new TreeNode<>(key, elem) : putNode(root, key, elem);
            }

            @Override
            public V get(K key) {
                assertNotNull(key);
                TreeNode<K, V> node = getNode(key);
                return node == null ? null : node.getValue();
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

            private static class TreeNode<K extends Comparable<K>, V> {
                private final K key;
                private V value;
                private int height;
                private TreeNode<K, V> left;
                private TreeNode<K, V> right;

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
    }

    public static class HashSetImpl<E extends Comparable<E>> implements HashSet<E> {

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
    }

    public static void assertNotNull(Object obj) {
        if (obj == null)
            throw new NullPointerException("Somethings gone seriously wrong... :(");
    }
}