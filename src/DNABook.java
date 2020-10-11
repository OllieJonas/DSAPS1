public class DNABook implements SocialNetwork {

    /**
     * The users that are currently stored.
     */
    private final IMap<String, ISet<String>> users;

    /**
     * The constructor for the DNABook. The maximum capacity dictates the size of both the user array and the
     * friend matrix.
     */
    public DNABook() {
        this.users = new HashMapImpl<>();
    }

    /**
     * Registers a given user into the social network.
     *
     * @param name The name of the user to be registered
     * @throws ArrayIndexOutOfBoundsException If the user array is full
     */
    @Override
    public void registerUser(String name) {
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
        users.get(name1).add(name2);
        users.get(name2).add(name1);
    }

    @Override
    public boolean areTheyFriends(String name1, String name2) {
        return users.get(name1).contains(name2) || users.get(name2).contains(name1);
    }
}