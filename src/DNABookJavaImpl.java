import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * NOT USING IN FINAL SUBMISSION
 *
 * An implementation of the same problem using Java's implementation of {@link java.util.HashMap} to compare speed of
 * their HashMap to my own - it didn't go well :(
 */
public class DNABookJavaImpl implements SocialNetwork {

    static final int DEFAULT_MAX_CAPACITY = 100;

    private final Map<String, Set<String>> users;

    private final int maxCapacity;


    public DNABookJavaImpl() {
        this(-1);
    }

    @SuppressWarnings("unused")
    public DNABookJavaImpl(boolean bounded) {
        this(bounded ? -1 : DEFAULT_MAX_CAPACITY);
    }

    public DNABookJavaImpl(int maxCapacity) {
        this.users = new HashMap<>();
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
            users.put(name, new HashSet<>());
    }

    /**
     * Registers both users as friends.
     *
     * @param name1 The name of the first user to be entered
     * @param name2 The name of the second user to be entered
     */
    @Override
    public void becomeFriends(String name1, String name2) {
        Set<String> friends1 = users.get(name1);
        Set<String> friends2 = users.get(name2);

        if (friends1 != null && friends2 != null) {
            friends1.add(name2);
            friends2.add(name1);
        }
    }

    @Override
    public boolean areTheyFriends(String name1, String name2) {
        Set<String> friends1 = users.get(name1);
        Set<String> friends2 = users.get(name2);

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
}
