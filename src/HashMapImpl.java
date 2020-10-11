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
 * you could have a theoretical infinite amount of users and friends (provided you had the memory to do it, obviously).
 * Secondly, there is now no longer a requirement to add the users in alphabetical order,since the User ID is
 * now found via hashing.</p>
 *
 *
 * ======= IMPLEMENTATION  =======
 *
 * <p>The hashing function I've used is basically just {@link java.util.HashMap}'s hashing function, however it
 * applies a circular function around the list (ie. hash % number of buckets). I have also taken the absolute
 * value of the hashed result, since I got some weird results after testing. (This probably isn't the actual
 * way to deal with this in a real application, but it works so eh?)</p>
 *
 * <p>Collisions are resolved using an AVT tree. Although I do understand it's far more conventional to use a linked
 * list, using this method of implementation would take O(n) complexity for lookup / insertion, which is prohibited
 * in the question. Although an AVT tree will take up more memory (the nodes for a tree will take up considerably
 * more memory than a linked list node), given the constraints being for time complexity, using a tree makes
 * more sense in this case.<p>
 *
 * <p>One of the main disadvantages of using an AVT tree over a linked list however, is that there is a requirement
 * that the keys are comparable in some way - hence the {@code <K extends Comparable<K>>} bounded type parameter given in
 * the key. However, given that we're using Strings as our key in our implementation, this won't really affect us.</p>
 *
 *<p>I am also aware that {@link java.util.HashMap} uses a combination of a LinkedList and a red-black tree
 * to resolve their collisions to save memory. However, I opted against this for two reasons. The first is stated above,
 * where it would require a O(n) lookup time. Secondly, converting I found converting a linked list to an AVT tree
 * would also take O(n) time to complete, which again isn't worth it considering we will only have 100 users.</p>
 *
 *
 * ======= NOTES  =======
 *
 * <p>There's definitely a sneaky way of not forcing the key to implement the {@link java.lang.Comparable}
 * interface. However, it's easier for the AVL tree and I can't be bothered to figure it out.</p>
 *
 * <p>We can suppress un-checked warnings since the public interface that the user can interact with
 * never theoretically causes any casting exceptions - all casting is handled internally.</p>
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class HashMapImpl<K extends Comparable<K>, V> implements HashMap<K, V> {

    static final int DEFAULT_BUCKET_CAPACITY = 30;

    private final int bucketCapacity;

    private int elementCount;

    private final Bucket<?, ?>[] data;

    public HashMapImpl() {
        this(DEFAULT_BUCKET_CAPACITY);
    }

    public HashMapImpl(int bucketCapacity) {
        if (bucketCapacity <= 1) {
            throw new IllegalArgumentException("Illegal argument: bucketCapacity - Bucket capacity must be greater than 1!");
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
        return key == null ? 0 : Math.abs(((h = key.hashCode()) ^ (h >>> 16)) % (bucketCapacity - 1));
    }

    private static class Bucket<K extends Comparable<K>, V> {

        private final AVLTree<K, V> tree;

        public Bucket() {
            this.tree = new AVLTreeImpl<>(); // default first collection to be used
        }

        public V get(K key) {
            return tree.get(key);
        }

        private void put(K key, V val) {
            tree.put(key, val);
        }
    }
}