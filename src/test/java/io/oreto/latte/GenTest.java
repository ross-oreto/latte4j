package io.oreto.latte;

import io.oreto.latte.collections.Lists;
import io.oreto.latte.constants.Generator;
import io.oreto.latte.obj.Reflect;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenTest {
    @Test
    public void generateConstants() throws ClassNotFoundException, IOException, IllegalAccessException {
        Path classesDir = Paths.get("target", "test-classes", "io", "oreto", "latte");
        Path output = Paths.get(classesDir.toString(), "C.java");
        Generator.main(Lists.array("-Doverwrite=true", "-Doutput=" + output, "-Dpackage=io.oreto.latte"));
        assertTrue(Files.exists(output));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertEquals(0, compiler.run(null, null, null,
                output.toString()));

        Path classFile = Paths.get(classesDir.toString(), "C.class");
        assertTrue(Files.exists(classFile));
        Files.delete(output);

        ClassLoader classLoader = Generator.class.getClassLoader();
        Class<?> cls = classLoader.loadClass("io.oreto.latte.C");
        Optional<Field> field = Reflect.getField(cls, "one");

        assertTrue(field.isPresent());
        assertEquals("one", field.get().get(null));

        field = Reflect.getField(cls, "BILBO_BAGGINS_SCREAM");
        assertTrue(field.isPresent());
        assertEquals("BILBO BAGGINS", field.get().get(null));

        field = Reflect.getField(cls, "T1");
        assertTrue(field.isPresent());
        assertEquals("This is a test", field.get().get(null));
    }
}
