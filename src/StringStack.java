/**
 * A stack abstract data type that contains Strings.
 */
public class StringStack {

    /**
     * <p>The total amount of elements that can be added to the stack.</p>
     *
     * <p>NOTE: This collection is bounded - meaning that once this limit is reached,
     * the {@link #push(String)} method will return false.</p>
     */
    private final int maxCapacity;

    /**
     * The total number of elements currently stored in the stack.
     */
    private int elementCount;

    /**
     * The elements stored in this stack.
     */
    private final String[] elementData;

    /**
     * Constructor for creating a new StringStack with a certain capacity.
     *
     * @param maxCapacity the maximum number of strings the stack can hold
     */
    public StringStack(int maxCapacity) {
        if (maxCapacity < 0)
            throw new IllegalArgumentException("Illegal max capacity: " + maxCapacity);

        this.maxCapacity = maxCapacity;
        this.elementData = new String[maxCapacity];
    }

    /**
     * Puts the given String on top of the stack (if there is enough space).
     *
     * @param s the String to add to the top of the stack
     * @return false if there was not enough space in the stack to add the string - otherwise true
     *
     */
    public boolean push(String s) {
        if (elementCount >= maxCapacity)
            return false;

        elementData[elementCount++] = s;
        return true;
    }

    /**
     * Removes the String on top of the stack from the stack and returns it.
     *
     * @return the String on top of the stack, or null if the stack is empty.
     */
    public String pop() {
        String obj = peek();

        if (obj != null)
            removeElementAt(elementCount - 1);

        return obj;
    }

    /**
     * Returns the number of Strings in the stack.
     *
     * @return the number of Strings in the stack
     */
    public int count() {
        return elementCount;
    }

    /**
     * <p>Look at the object at the top of the stack without actually removing it.</p>
     *
     * <p>NOTE: Returns null if there is nothing currently in the stack.</p>
     *
     * @return The object at the top of the stack. If nothing in the stack, returns null
     */
    public String peek() {
        if (isEmpty())
            return null;

        return elementData[elementCount - 1];
    }

    /**
     * Removes an element from the elementData at the given index.
     *
     * @param index The index which to remove the element
     * @throws ArrayIndexOutOfBoundsException If the index specified is not between 0 and the max capacity of the stack
     */
    public void removeElementAt(int index) {
        if (0 > index || maxCapacity <= index)
            throw new ArrayIndexOutOfBoundsException(index + " >= "+ maxCapacity);

        elementData[index] = null;
        elementCount--;
    }

    public boolean isEmpty() {
        return elementCount == 0;
    }
}
