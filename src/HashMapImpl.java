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
 * <p>However, if using this collision resolution system on its own, if the linked list contained inside the
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
public class HashMapImpl<K extends Comparable<K>, V> implements IMap<K, V> {

    static final int DEFAULT_BUCKET_CAPACITY = 30;

    static final int DEFAULT_TREE_COLLISION_THRESHOLD = 8;

    private final int bucketCapacity;

    private int elementCount;

    private final int treeCollisionThreshold;

    private final Bucket<?, ?>[] data;

    public HashMapImpl() {
        this(DEFAULT_BUCKET_CAPACITY, DEFAULT_TREE_COLLISION_THRESHOLD);
    }

    public HashMapImpl(int bucketCapacity, int collisionThreshold) {
        if (bucketCapacity <= 1) {
            throw new IllegalArgumentException("Illegal argument: bucketCapacity - Bucket capacity must be greater than 1!");
        }
        this.bucketCapacity = bucketCapacity;
        this.treeCollisionThreshold = collisionThreshold;
        this.data = new Bucket<?, ?>[bucketCapacity];
    }

    @Override
    public void put(K key, V value) {
        int hash = hashFunction(key);
        Bucket<K, V> bucket = getBucket(hash);

        if (bucket == null) {
            bucket = new Bucket<>(treeCollisionThreshold);
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

    private Bucket<K, V> getBucket(int hash) {
        return (Bucket<K, V>) data[hash];
    }

    private int hashFunction(Object key) {
        int h;
        return key == null ? 0 : Math.abs(((h = key.hashCode()) ^ (h >>> 16)) % (bucketCapacity - 1));
    }

    private static class Bucket<K extends Comparable<K>, V> {

        private CollisionResolutionStructure<K, V> collection;

        private final int collisionThreshold;

        private boolean usingTree;

        public Bucket(int collisionThreshold) {
            this.collisionThreshold = collisionThreshold;
            this.collection = new LinkedList<>(); // default first collection to be used
        }

        public V get(K key) {
            return collection.get(key);
        }

        private void put(K key, V val) {
            collection.put(key, val);

            if (shouldUseTree() && !usingTree)
                swapToTree();
        }

        private void swapToTree() {
            this.usingTree = true;
            this.collection = Util.cast(collection, LinkedList.class).toTree(); // asserts no wacky stuff going on

        }

        private boolean shouldUseTree() {
            return collection.size() >= collisionThreshold;
        }
    }
}