package io.oreto.latte;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IO {
    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding
     * @param s The <code>String</code> to be printed
     */
    public static void print(String s) { System.out.print(s); }

    /**
     * Prints a String and then terminate the line.
     * @param s The <code>String</code> to be printed.
     */
    public static void println(String s) { System.out.println(s); }

    /**
     * Prints a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding
     * @param s The <code>char</code> to be printed
     */
    public static void print(char s) { System.out.print(s); }

    /**
     * Prints a character and then terminate the line.
     * @param s The <code>char</code> to be printed.
     */
    public static void println(char s) { System.out.println(s); }

    /**
     * Prints an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding
     * @param n The <code>Number</code> to be printed
     * @see java.lang.Object#toString()
     */
    public static void print(Number n) { System.out.print(n); }

    /**
     * Prints an Object and then terminate the line.
     * @param n The <code>Number</code> to be printed
     * @see java.lang.Object#toString()
     */
    public static void println(Number n) { System.out.println(n); }

    /**
     * Determine if windows is the current OS
     * @return True if running on Windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    /**
     * Determine if non windows OS is the current OS
     * any non windows OS, all of which will be more unix based.
     * @return True if not running on Windows, false otherwise
     */
    public static boolean isUnix() {
        return !isWindows();
    }

    /**
     * load a classpath resource from the classpath
     * @param classLoader The classloader to use
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional input stream is resource exists, Optional.empty otherwise
     */
    public static Optional<InputStream> loadResource(ClassLoader classLoader, String path, String... resourcePath) {
        InputStream stream = classLoader.getResourceAsStream(Paths.get(path, resourcePath).toString());
        return stream == null ? Optional.empty() : Optional.of(stream);
    }

    /**
     * load a classpath resource from the classpath
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional input stream if the resource exists, Optional.empty otherwise
     */
    public static Optional<InputStream> loadResource(String path, String... resourcePath) {
        return loadResource(IO.class.getClassLoader(), path, resourcePath);
    }

    /**
     * load all lines from a classpath resource from the classpath
     * @param classLoader The classloader to use
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional List of strings if the resource exists, Optional.empty otherwise
     */
    public static Optional<List<String>> resourceLines(ClassLoader classLoader, String path, String... resourcePath) {
        return loadResource(classLoader, path, resourcePath)
                .map(is -> new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.toList()));
    }

    /**
     * load all lines from a classpath resource from the classpath
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional List of strings if the resource exists, Optional.empty otherwise
     */
    public static Optional<List<String>> resourceLines(String path, String... resourcePath) {
        return resourceLines(IO.class.getClassLoader(), path, resourcePath);
    }

    /**
     * load all text from a classpath resource from the classpath
     * @param classLoader The classloader to use
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional string if the resource exists, Optional.empty otherwise
     */
    public static Optional<String> resourceText(ClassLoader classLoader, String path, String... resourcePath) {
        return loadResource(classLoader, path, resourcePath)
                .map(is -> new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n")));
    }

    /**
     * load all text from a classpath resource from the classpath
     * @param path Path to the resource
     * @param resourcePath Any remaining path to the resource
     * @return An Optional string if the resource exists, Optional.empty otherwise
     */
    public static Optional<String> resourceText(String path, String... resourcePath) {
        return resourceText(IO.class.getClassLoader(), path, resourcePath);
    }

    /**
     * load all text from the specified file
     * @param file File to load contents of
     * @return An Optional file contents string if the file exists, Optional.empty otherwise
     */
    public static Optional<String> fileText(File file) {
        try {
            return file.exists()
                    ? Optional.of(String.join("\n", Files.readAllLines(file.toPath())))
                    : Optional.empty();
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }
}
