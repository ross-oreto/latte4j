package io.oreto.latte.obj;

import io.oreto.latte.str.Str;

/**
 * Utility method for generic objects
 */
public class Obj {
    /**
     * Determine if the object holds a value, not empty or null
     * @param o The object to test
     * @return True if initialized, false otherwise
     */
    public static boolean initialized(Object o) {
        if (o instanceof String)
            return Str.isNotEmpty((String) o);
        else if (o instanceof Iterable)
            return ((Iterable<?>) o).iterator().hasNext();
        else if (o instanceof Object[]) {
           return ((Object[]) o).length > 0;
        }

        return o != null;
    }

    /**
     * Determine if the object is not initialized.
     * Negation of the <code>Obj.initialized</code> method.
     * @param o The object to test
     * @return True if initialized, false otherwise
     */
    public static boolean notInitialized(Object o) {
        return !initialized(o);
    }
}
