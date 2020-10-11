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
        this.users = new HashMapImpl<>();
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
}