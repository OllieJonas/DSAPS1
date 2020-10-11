/**
 *
 * <p>I do understand that this implementation is absolutely 100% overkill, however to be honest I really enjoyed
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

    public boolean isBounded() {
        return maxCapacity != -1;
    }

    public boolean isFull() {
        return size() >= maxCapacity;
    }

    public int size() {
        return users.size();
    }
}