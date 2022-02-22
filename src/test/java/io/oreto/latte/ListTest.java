package io.oreto.latte;

import io.oreto.latte.collections.Lists;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListTest {
    @Test
    public void order() {
        List<Entity> entities = Lists.of(
                new Entity(1L, 2, "ross", "oreto")
                , new Entity(10L, 0, "ross", "test")
                , new Entity(3L, 0, "ross", "michael")
                , new Entity(6L, 1, "alpha", "beta")
                , new Entity(12L, 2, "foo", "bar")
                , new Entity(11L, 1, "foo", "fighters")
        );

        assertEquals(6L, (long) Lists.orderBy(entities, Lists.of("name,asc")).get(0).getId());
        assertEquals(10L, (long) Lists.orderBy(entities, Lists.of("name,desc", "f1,desc")).get(0).getId());
        assertEquals(3L, (long) Lists.orderBy(entities, Lists.of("name,desc", "f1")).get(0).getId());

        Lists.orderBy(entities, Lists.of("name", "i,desc", "f1"));
        List<Entity> ordered = Lists.orderBy(entities, Lists.of("name", "i,desc", "f1"));
        assertArrayEquals(new Long[]{ 6L, 12L, 11L, 1L, 3L, 10L }, ordered.stream().map(Entity::getId).toArray(Long[]::new));
    }

    @Test
    public void subList() {
        assertEquals(Lists.of(
                Lists.of("a")
                , Lists.of("a", "b")
                , Lists.of("a", "b", "c")
                , Lists.of("a", "b", "c", "d")
        ), Lists.subLists(Lists.of("a", "b", "c", "d")));
    }

    @Test
    public void collate() {
        assertEquals(Lists.of(
                Lists.of("a", "b", "c")
                , Lists.of("b", "c", "d")
                , Lists.of("c", "d")
                , Lists.of("d")
        ), Lists.collate(Lists.of("a", "b", "c", "d"), 3,  1));

        assertEquals(Lists.of(
                Lists.of("a", "b", "c")
                , Lists.of("b", "c", "d")
        ), Lists.collate(Lists.of("a", "b", "c", "d"), 3,  false));

        Lists.collate(new String[]{}, 1, 3, true);
    }

    public static class Entity {
        private Long id;
        private Integer i;
        private String name;
        private String f1;

        public Entity(Long id, Integer i, String name, String f1) {
            this.id = id;
            this.i = i;
            this.name = name;
            this.f1 = f1;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public Integer getI() {
            return i;
        }
        public void setI(Integer i) {
            this.i = i;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getF1() {
            return f1;
        }
        public void setF1(String f1) {
            this.f1 = f1;
        }
    }
}
