package io.oreto.latte.obj;

import io.oreto.latte.str.Str;

public class Obj {

    public static boolean initialized(Object o) {
        if (o instanceof String)
            return Str.isNotEmpty((String) o);
        else if (o instanceof Iterable)
            return ((Iterable<?>) o).iterator().hasNext();

        return o != null;
    }

    public static boolean notInitialized(Object o) {
        return !initialized(o);
    }
}
