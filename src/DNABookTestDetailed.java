import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * NOT PART OF FINAL SUBMISSION - JUST MY OWN TESTS TO CHECK IF MY HASHMAP WAS ACTUALLY FASTER
 *
 * Executable class to test the DNABook abstract data type.
 */
public class DNABookTestDetailed {

    static final int DEFAULT_SIZE = 100;

    static final int DEFAULT_SUBSET_SIZE = 10;

    private final String implementationName;

    // has to be a java list, not just a normal array so I can shuffle and sort the list using Collections library
    private final List<String> generatedNames;

    private final int size;

    private final int subsetSize;

    /**
     * Allows for dynamic creation of SocialNetwork objects. Similar to
     * https://medium.com/@mhd.durrah/factory-pattern-the-dynamic-way-with-java-8-3ca5ab48a9cf in concept, but obviously
     * not used for factories. Saves me from doing some dodgy reflection to make new instances of the classes or from
     * using a proper factory.
     */
    private final Supplier<? extends SocialNetwork> networkSupplier;

    public DNABookTestDetailed(String name, Supplier<? extends SocialNetwork> networkSupplier) {
        this(name, networkSupplier, null, DEFAULT_SUBSET_SIZE);
    }

    public DNABookTestDetailed(String name, Supplier<? extends SocialNetwork> networkSupplier, List<String> names, int subsetSize) {
        this.implementationName = name;
        this.generatedNames = lazyInitNames(names, DEFAULT_SIZE);
        this.size = generatedNames.size();
        this.subsetSize = subsetSize;
        this.networkSupplier = networkSupplier;
    }

    public static void main(String[] args) {
        DNABookTestDetailed binarySearch = new DNABookTestDetailed("Binary Search", () -> new DNABook(DEFAULT_SIZE));
        DNABookTestDetailed ownHashImpl = new DNABookTestDetailed("Own HashMap", DNABookHashImpl::new);
        DNABookTestDetailed javaImpl = new DNABookTestDetailed("Java HashMap", DNABookJavaImpl::new);

        // binarySearch.runTests();
        binarySearch.runTests();
        ownHashImpl.runTests();
        javaImpl.runTests();
    }

    public void runTests() {
        header(implementationName);

        SocialNetwork accuracyNetwork = newNetwork();
        accuracyTests(accuracyNetwork);
        System.out.println("Accuracy Tests: All correct!"); // if not correct, it will throw an exception
        System.out.println();

        SocialNetwork stressNetwork = newNetwork();
        System.out.println("Stress Tests (Entry Size: " + DEFAULT_SIZE + ", Subset Size: " + DEFAULT_SUBSET_SIZE + "):");
        stressTests(stressNetwork);
    }

    private void header(String head) {
        String border = "============================";
        System.out.println();
        System.out.println(border + " " + head + " " + border);
    }

    private void accuracyTests(SocialNetwork network) {
        BiFunction<String, String, Boolean> areTheyFriends = network::areTheyFriends; // debatable to use a BiPredicate here?

        network.registerUser("Alex");
        network.registerUser("Bea");
        network.registerUser("Chris");
        network.registerUser("Daniel");
        network.registerUser("Ed");

        assertCorrect(areTheyFriends, "Alex", "Bea", false);
        assertCorrect(areTheyFriends, "Daniel", "Bea", false);
        assertCorrect(areTheyFriends, "Bea", "Chris", false);

        network.becomeFriends("Alex", "Chris");
        network.becomeFriends("Bea", "Daniel");

        assertCorrect(areTheyFriends, "Alex", "Chris", true);
        assertCorrect(areTheyFriends, "Chris", "Alex", true);
        assertCorrect(areTheyFriends, "Bea", "Daniel", true);
    }

    private void stressTests(SocialNetwork network) {
        registerUserTests(network);
        becomeFriendsTests(network);
        areTheyFriendsTests(network);
    }

    private void registerUserTests(SocialNetwork network) {
        long start = System.nanoTime();
        generatedNames.forEach(network::registerUser);
        long end = System.nanoTime();
        System.out.println("Registering Users took " + (end - start) / size + " nanoseconds!");
    }

    private void becomeFriendsTests(SocialNetwork network) {
        List<String> randomSubSet = randomSubSet();
        Random random = new Random();

        long start = System.nanoTime();

        for (int i = 0; i < subsetSize; i++) {
            String name1 = randomSubSet.get(random.nextInt(subsetSize));
            String name2 = randomSubSet.get(random.nextInt(subsetSize));
            network.becomeFriends(name1, name2);
        }

        long end = System.nanoTime();

        System.out.println("Become Friends took " + (end - start) / subsetSize + " nanoseconds!");
    }

    private void areTheyFriendsTests(SocialNetwork network) {
        List<String> randomSubSet = randomSubSet();
        Random random = new Random();

        long start = System.nanoTime();

        for (int i = 0; i < subsetSize; i++) {
            String name1 = randomSubSet.get(random.nextInt(subsetSize));
            String name2 = randomSubSet.get(random.nextInt(subsetSize));
            network.areTheyFriends(name1, name2);
        }

        long end = System.nanoTime();

        System.out.println("Are They Friends took " + (end - start) / subsetSize + " nanoseconds!");
    }

    /**
     * Lazily generates random names if list of names given is null or empty...
     *
     * Maybe not needed here since I'm using the same names each time (could be static?)
     * but thinking it's probably for the best for the hypothetical concept of future proofing.
     */
    private List<String> lazyInitNames(List<String> names, int size) {
        if (names == null || names.isEmpty())
            names = new ArrayList<>(buildNames(size));

        return names;
    }

    private List<String> buildNames(int size) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < size - 1; i++) {
            names.add(randomStr());
        }
        Collections.sort(names); // sort for binary search
        return names;
    }

    private static String randomStr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) { // length of the random string.
            int index = (int) (random.nextFloat() * chars.length());
            builder.append(chars.charAt(index));
        }
        return builder.toString();
    }

    private List<String> randomSubSet() {
        Collections.shuffle(generatedNames);
        return generatedNames.subList(0, subsetSize);
    }

    private SocialNetwork newNetwork() {
        return networkSupplier.get();
    }

    private <S, U, V> void assertCorrect(BiFunction<S, U, V> function, S s, U u, V expected) {
        V actual = function.apply(s, u);
        if (actual != expected)
            throw new IncorrectAnswerException(implementationName, expected, actual);
    }

    private static class IncorrectAnswerException extends RuntimeException {
        public IncorrectAnswerException(String name, Object expected, Object actual) {
            super("Result not correct for " + name + "! Expected: " + expected + " Actual: " + actual);
        }

    }
}
