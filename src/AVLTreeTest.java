public class AVLTreeTest {

    public static void main(String[] args) {
        AVLTreeImpl<String, Integer> tree = new AVLTreeImpl<>();

        tree.put("David", 25);
        tree.put("Chris", 20);
        tree.put("Indigo", 45);
        tree.put("George", 40);
        tree.put("Katie", 55);
        tree.put("Alex", 10);
        tree.put("Eva", 30);
        tree.put("Fred", 35);
        tree.put("John", 50);
        tree.put("Bertie", 15);

        System.out.println(tree.get("George"));
        System.out.println(tree.get("Katie"));

        System.out.println(tree.size());
    }
}
