package Data;

import java.util.ArrayList;
import java.util.List;

public class Repo<T> {
    /**
     * Class representing the type of the elements.
     */
    protected Class<T> elementType;

    /**
     * A genetic list for holding the data.
     */
    protected List<T> elements;

    protected Repo(Class<T> elementType) {
        this.elementType = elementType;
        elements = new ArrayList<>();
    }

    /**
     * Returns the size of the {@link #elements} List.
     * @return size
     */
    public int size() {
        return elements.size();
    }

    /**
     * Adds a member to the {@link #elements} List.
     * @param element - element
     */
    public void add(T element) {
        elements.add(element);
    }

    /**
     * Deletes all values from the {@link #elements} List.
     */
    public void clear() {
        elements.clear();
    }

}
