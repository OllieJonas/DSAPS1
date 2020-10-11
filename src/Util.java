/**
 * Quick implementation of the requireNonNull / checkNotNull method from Java's Objects / Guava's Preconditions
 * utility class.
 */
public class Util {

    public static <T> T assertNotNull(T obj) {
        if (obj == null)
            throw new NullPointerException("Somethings gone seriously wrong... :(");
        return obj;
    }

    public static <T> T checkCanCast(Object obj, Class<T> clazz) {
        assertNotNull(obj);

        if (!obj.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("Cannot cast " + obj.toString() + " to " + clazz.getSimpleName() + "!");
        }

        return clazz.cast(obj);
    }
}