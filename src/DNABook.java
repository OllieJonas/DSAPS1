/**
 * THIS IMPLEMENTATION IS BEING USED IN FINAL SUBMISSION
 */
public class DNABook implements SocialNetwork {

    static final int DEFAULT_MAX_CAPACITY = 100;

    private final int maxCapacity;

    private int size;

    private final String[] users;

    private final boolean[][] friendMatrix;

    public DNABook() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public DNABook(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.users = new String[maxCapacity];
        this.friendMatrix = new boolean[maxCapacity][maxCapacity];
    }

    @Override
    public void registerUser(String name) {
        if (!isFull())
            users[size++] = name;
    }

    @Override
    public void becomeFriends(String name1, String name2) {
        int id1 = getIdFrom(name1);
        int id2 = getIdFrom(name2);

        if (userExists(id1) && userExists(id2)) {
            friendMatrix[id2][id1] = true;
            friendMatrix[id1][id2] = true;
        }
    }

    @Override
    public boolean areTheyFriends(String name1, String name2) {
        int id1 = getIdFrom(name1);
        int id2 = getIdFrom(name2);

        if (!userExists(id1) || !userExists(id2))
            return false;

        return friendMatrix[id2][id1] || friendMatrix[id1][id2];
    }

    public int getIdFrom(String name) {
        return binarySearch(users, name);
    }

    private <T extends Comparable<T>> int binarySearch(T[] arr, T toFind) {
        int lo = 0;
        int hi = size;

        while (lo <= hi) {
            int midIndex = (lo + hi) / 2;
            T mid = arr[midIndex];

            if (mid == null)
                break;

            int comparison = mid.compareTo(toFind);

            if (comparison == 0)
                return midIndex;
            else if (comparison < 0)
                lo = midIndex + 1;
            else
                hi = midIndex - 1;
        }
        return -1;
    }

    private boolean userExists(int id) {
        return id != -1;
    }

    private boolean isFull() {
        return size >= maxCapacity;
    }
}
