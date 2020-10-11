/**
 * Quick implementation of 2 quick utility methods in the style of Java's Objects / Guava's Preconditions
 * utility classes.
 */
public class Util {

    public static void assertNotNull(Object obj) {
        if (obj == null)
            throw new NullPointerException("Somethings gone seriously wrong... :(");
    }

    public static <T> T cast(Object obj, Class<T> clazz) {
        assertNotNull(obj);

        if (!obj.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("Cannot cast " + obj.toString() + " to " + clazz.getSimpleName() + "!");
        }

        return clazz.cast(obj);
    }
}