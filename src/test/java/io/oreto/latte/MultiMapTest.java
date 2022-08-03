package io.oreto.latte;

import io.oreto.latte.collections.MultiSet;
import io.oreto.latte.map.MultiInt;
import io.oreto.latte.map.MultiString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MultiMapTest {

    @Test
    public void intMapTest() {
        MultiInt<String> multiInt = new MultiInt<>();
        assertEquals(new ArrayList<Object>(){{ add(1); add(2); }}
        , multiInt.put("test", 1).put("test", 2).get("test"));

        multiInt.disallowEmptyValues();
        assertEquals(new ArrayList<Object>(){{ add(1); add(2); }}
                , multiInt.put("test", null).get("test"));

        assertEquals(3, multiInt.sum().get("test").intValue());
    }

    @Test
    public void StringMapTest() {
        MultiString<String> multiString = new MultiString<>();
        assertEquals(new ArrayList<Object>(){{ add("Michael"); add("Ross"); }}
                , multiString.put("test", "Michael").put("test", "Ross").get("test"));

        multiString.disallowEmptyValues();
        assertEquals(new ArrayList<Object>(){{ add("Michael"); add("Ross"); }}
                , multiString.put("test", "").get("test"));

        assertEquals("Michael Ross", multiString.join(" ").get("test"));
    }

    @Test void MultiSetTest() {
        MultiSet<Integer> set = new MultiSet<Integer>().addAll(5, 1, 10, -1, -1);
        assertArrayEquals(new Integer[] { -1, -1, 1, 5, 10 }, set.toArray(new Integer[0]));
        assertEquals(5, set.size());
        assertEquals(2, set.removeAll(1, 5, 10).size());
        assertArrayEquals(new Integer[] { -1, -1 }, set.toArray(new Integer[0]));
        assertArrayEquals(new Integer[] { -1 }, set.remove(-1).toArray(new Integer[0]));
        assertTrue(set.clear().isEmpty());
        assertEquals(0, set.size());

        set.addAll(null, 1, 1, 10, null, 5, null);
        assertArrayEquals(new Integer[] { null, null, null, 1, 1, 5, 10 }, set.toArray(new Integer[0]));
        assertArrayEquals(new Integer[] { null, 1, 5, 10 }, set.removeAll(null, null, 1).toArray(new Integer[0]));
        assertEquals(4, set.size());
    }
}
