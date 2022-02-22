package io.oreto.latte.str;

import io.oreto.latte.num.IntRange;
import io.oreto.latte.num.Num;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.oreto.latte.str.Word.reservedWords;

public class Str implements CharSequence, java.io.Serializable, Comparable<CharSequence> {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String DASH = "-";
    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String UNDER_SCORE = "_";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String EQUALS = "=";
    protected static final List<CharSequence> emptyList = new ArrayList<>();

    /**
     * Static variables for characters
     */
    public static class Chars {
        public static final char NEGATIVE = '-';
        public static final char POSITIVE = '+';
        public static final char DECIMAL = '.';
        public static final char SPACE = ' ';
        public static final char DASH = '-';
        public static final char SLASH = '/';
        public static final char UNDER_SCORE = '_';
        public static final char ZERO = '0';
        public static final char EQUALS = '=';
    }

    /**
     * Compares two {@code CharSequence} instances lexicographically. Returns a
     * negative value, zero, or a positive value if the first sequence is lexicographically
     * less than, equal to, or greater than the second, respectively.
     *
     * <p>
     * The lexicographical ordering of {@code CharSequence} is defined as follows.
     * Consider a {@code CharSequence} <i>cs</i> of length <i>len</i> to be a
     * sequence of char values, <i>cs[0]</i> to <i>cs[len-1]</i>. Suppose <i>k</i>
     * is the lowest index at which the corresponding char values from each sequence
     * differ. The lexicographic ordering of the sequences is determined by a numeric
     * comparison of the char values <i>cs1[k]</i> with <i>cs2[k]</i>. If there is
     * no such index <i>k</i>, the shorter sequence is considered lexicographically
     * less than the other. If the sequences have the same length, the sequences are
     * considered lexicographically equal.
     *
     *
     * @param cs1 the first {@code CharSequence}
     * @param cs2 the second {@code CharSequence}
     *
     * @return  the value {@code 0} if the two {@code CharSequence} are equal;
     *          a negative integer if the first {@code CharSequence}
     *          is lexicographically less than the second; or a
     *          positive integer if the first {@code CharSequence} is
     *          lexicographically greater than the second.
     */
    @SuppressWarnings("unchecked")
    public static int compare(CharSequence cs1, CharSequence cs2) {
        if (Objects.requireNonNull(cs1) == Objects.requireNonNull(cs2)) {
            return 0;
        }

        if (cs1.getClass() == cs2.getClass() && cs1 instanceof Comparable) {
            return ((Comparable<Object>) cs1).compareTo(cs2);
        }

        for (int i = 0, len = Math.min(cs1.length(), cs2.length()); i < len; i++) {
            char a = cs1.charAt(i);
            char b = cs2.charAt(i);
            if (a != b) {
                return a - b;
            }
        }

        return cs1.length() - cs2.length();
    }

    /**
     * Determine if a given string is numeric.
     * This is better than relying on something like Integer.parseInt
     * to throw an Exception which has to be part of the normal method behavior.
     * Also better than a regular expression which is more difficult to maintain.
     * @param s A string.
     * @param type the type of number, natural, whole, integer, rational.
     * @return Return true if the value is numeric, false otherwise.
     */
    public static boolean isNumber(CharSequence s, Num.Type type) {
        // do some initial sanity checking to catch easy issues quickly and with very little processing
        // also this makes the state tracking easier below
        if (s == null)
            return false;

        int length = s.length();
        if (isBlank(s)
                || (length == 1 && !Character.isDigit(s.charAt(0)))
                ||  s.charAt(length - 1) == Chars.DECIMAL)
            return false;

        boolean dotted = false;
        int[] arr = s.chars().toArray();

        int len = arr.length;
        for (int i = 0; i < len; i++) {
            char c = (char) arr[i];

            switch (c) {
                // +/- can only be in the start position
                case Chars.NEGATIVE:
                    if (i > 0) return false;
                    if (type == Num.Type.natural || type == Num.Type.whole) return false;
                    break;
                case Chars.POSITIVE:
                    if (i > 0) return false;
                    break;
                case Chars.DECIMAL:
                    // only one decimal place allowed
                    if (dotted || type != Num.Type.rational) return false;
                    dotted = true;
                    break;
                default:
                    // this better be a digit
                    if (!Character.isDigit(c))
                        return false;
                    break;
            }
        }
        // make sure natural number type isn't assigned a 0
        return type != Num.Type.natural || !s.chars().allMatch(it -> (char) it == Chars.ZERO);
    }

    /**
     * Determine if the string is null or empty
     * @param s The string to test
     * @return True if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * Determine if the string is not empty. Negation of <code>Str.isEmpty(s)</code>
     * @param s The string to test
     * @return True if the string is not empty, false otherwise
     */
    public static boolean isNotEmpty(final CharSequence s) {
        return !isEmpty(s);
    }

    /**
     * Determine if the string is blank
     * @param s The string to test
     * @return True if the string is blank (something other than whitespace)
     */
    public static boolean isBlank(CharSequence s) {
       return isEmpty(s) || s.chars().allMatch(Character::isWhitespace);
    }

    /**
     * Determine if the string is a number
     * @param s The string to test
     * @return True if the string is a valid number, false otherwise
     */
    public static boolean isNumber(CharSequence s) {
        return isNumber(s, Num.Type.rational);
    }

    /**
     * Determine if the string is an integer
     * @param s The string to test
     * @return True if the string is a valid integer, false otherwise
     */
    public static boolean isInteger(CharSequence s) {
        return isNumber(s, Num.Type.integer);
    }

    /**
     * Determine if the string is a byte
     * @param s The string to test
     * @return True if the string is a valid byte, false otherwise
     */
    public static boolean isByte(CharSequence s) {
        if (isInteger(s)) {
            int i = Integer.parseInt(s.toString());
            return i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE;
        } else {
         return false;
        }
    }

    /**
     * Determine if the string is a boolean
     * @param s The string to test
     * @return True if the string is a valid boolean, false otherwise
     */
    public static boolean isBoolean(CharSequence s) {
        return s != null && (s.equals(TRUE) || s.equals(FALSE));
    }

    /**
     * Determine if the char c is alphanumeric
     * @param c The character to test
     * @return True if the character is alphanumeric, false otherwise
     */
    public static boolean isAlphaNumeric(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    /**
     * Determine if the string is a valid email
     * A valid email address consists of an email prefix and an email domain, both in acceptable formats.
     * The prefix appears to the left of the @ symbol.
     * The domain appears to the right of the @ symbol.
     * Allowed characters: letters (a-z), numbers, underscores, periods, and dashes.
     * An underscore, period, or dash must be followed by one or more letter or number.
     * @param s The string to test
     * @return True if the string is a valid email, false otherwise
     */
    public static boolean isEmail(CharSequence s) {
        if (s == null)
            return false;
        String str = s.toString().trim();
        s = str;
        int len = s.length();
        // can't start or end with [_.-@]
        if (len == 0 || !isAlphaNumeric(s.charAt(0)) || !isAlphaNumeric(s.charAt(len - 1)))
            return false;
        // make sure final domain is at least two characters long
        int i = str.lastIndexOf('.');
        if (i < 0)
            return false; // must have one dot at least
        if (len - i <= 2)
            return false;

        boolean at = false;      // encountered @ symbol
        boolean alpha = true;    // [_.-@] expect an alpha numeric char after
        boolean dotted = false;  // dot in domain
        for (i = 0; i < len; i++) {
            char c = s.charAt(i);
            boolean alphaNumeric = isAlphaNumeric(c);
            if (alpha) {
                if (alphaNumeric) {
                    alpha = false;
                } else
                    return false;
            } else if (c == '@') {
                if (at)
                    return false; // can't have two @ symbols
                at = true;
                alpha = true;
            } else {
                if (c == '.') {
                    alpha = true;
                    if (at)
                        dotted = true;
                } else if (c == '_') {
                    if (at) // underscores are not permitted in domain names
                        return false;
                    else
                        alpha = true;
                }
                else if (c == '-') {
                    alpha = true;
                } else if (!alphaNumeric)
                    return false;
            }
        }
        return dotted;
    }

    /**
     * Convert string to an optional boolean
     * @param s The string to convert
     * @return Optional boolean if the string is a valid boolean, Optional.empty otherwise
     */
    public static Optional<Boolean> toBoolean(CharSequence s) {
        try {
            return isBoolean(s) ? Optional.of(Boolean.parseBoolean(s.toString())) : Optional.empty();
        } catch (Exception ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional byte
     * @param s The string to convert
     * @return Optional byte if the string is a valid byte, Optional.empty otherwise
     */
    public static Optional<Byte> toByte(CharSequence s) {
        try {
            return isByte(s) ? Optional.of(Byte.parseByte(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional short
     * @param s The string to convert
     * @return Optional short if the string is a valid short, Optional.empty otherwise
     */
    public static Optional<Short> toShort(CharSequence s) {
        try {
            return isInteger(s) ? Optional.of(Short.parseShort(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional integer
     * @param s The string to convert
     * @return Optional integer if the string is a valid integer, Optional.empty otherwise
     */
    public static Optional<Integer> toInteger(CharSequence s) {
        try {
            return isInteger(s) ? Optional.of(Integer.parseInt(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional long
     * @param s The string to convert
     * @return Optional long if the string is a valid long, Optional.empty otherwise
     */
    public static Optional<Long> toLong(CharSequence s) {
        try {
            return isNumber(s) ? Optional.of(Long.parseLong(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional float
     * @param s The string to convert
     * @return Optional float if the string is a valid float, Optional.empty otherwise
     */
    public static Optional<Float> toFloat(CharSequence s) {
        try {
            return isNumber(s) ? Optional.of(Float.parseFloat(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to an optional double
     * @param s The string to convert
     * @return Optional double if the string is a valid double, Optional.empty otherwise
     */
    public static Optional<Double> toDouble(CharSequence s) {
        try {
            return isNumber(s) ? Optional.of(Double.parseDouble(s.toString())) : Optional.empty();
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }

    /**
     * Convert string to a character array
     * @param s The string to convert
     * @return An array of type character <tt>char[]</tt>
     */
    public static char[] toArray(CharSequence s) {
        int len = s.length();
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = s.charAt(i);
        }
        return arr;
    }

    /**
     * Convert string to a list of characters
     * @param s The string to convert
     * @return A list of characters <tt>List<Character></tt>
     */
    public static List<Character> toList(CharSequence s) {
        int len = s.length();
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(s.charAt(i));
        }
        return list;
    }

    /**
     * Get the first character from the string if not empty
     * @param s The string
     * @return Optional character if at least one character is in the string, <code>Optional.empty()</code> otherwise
     */
    public static Optional<Character> head(CharSequence s) {
        return isEmpty(s) ? Optional.empty() : Optional.of(s.charAt(0));
    }

    /**
     * Get the last character from the string if not empty
     * @param s The string
     * @return Optional character if at least one character is in the string, <code>Optional.empty()</code> otherwise
     */
    public static Optional<Character> tail(CharSequence s) {
        return isEmpty(s) ? Optional.empty() : Optional.of(s.charAt(s.length() - 1));
    }

    /**
     * Find and replace all search strings with a replacement string
     * @param s The string to replace
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @param max The maximum amount of replacements to make in the string
     * @return The resulting string after replacement
     */
    public static String findAndReplace(CharSequence s, CharSequence search, CharSequence replacement, int max) {
        return Str.of(s).findAndReplace(search, replacement, max).toString();
    }

    /**
     * Find and replace all search strings with a replacement string
     * @param s The string to replace
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @return The resulting string after replacement
     */
    public static String findAndReplace(CharSequence s, CharSequence search, CharSequence replacement) {
        return Str.of(s).findAndReplace(search, replacement).toString();
    }

    /**
     * Replace all regex matches with a replacement string
     * @param s The string to replace
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @param max The maximum amount of replacements to make in the string
     * @return The resulting string after replacement
     */
    public static String replace(CharSequence s, CharSequence regex, CharSequence replacement, int max) {
        return Str.of(s).replace(regex, replacement, max).toString();
    }

    /**
     * Replace all regex matches with a replacement string
     * @param s The string to replace
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @return The resulting string after replacement
     */
    public static String replace(CharSequence s, CharSequence regex, CharSequence replacement) {
        return Str.of(s).replace(regex, replacement).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @param to The ending index
     * @param policy Inclusion policy: INCLUDE, INCLUDE_EXCLUDE, EXCLUDE_INCLUDE, EXCLUDE
     * @param failOnOutOfBounds If true throw an exception if the starting/ending indices are out of bounds, otherwise ignore
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from, int to, Slice policy, boolean failOnOutOfBounds) {
        return Str.of(s).slice(from, to, policy, failOnOutOfBounds).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @param to The ending index
     * @param policy Inclusion policy: INCLUDE, INCLUDE_EXCLUDE, EXCLUDE_INCLUDE, EXCLUDE
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from, int to, Slice policy) {
        return Str.of(s).slice(from, to, policy).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @param to The ending index
     * @param failOnOutOfBounds If true throw an exception if the starting/ending indices are out of bounds, otherwise ignore
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from, int to, boolean failOnOutOfBounds) {
        return Str.of(s).slice(from, to, failOnOutOfBounds).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @param to The ending index
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from, int to) {
        return Str.of(s).slice(from, to).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @param failOnOutOfBounds If true throw an exception if the starting/ending indices are out of bounds, otherwise ignore
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from, boolean failOnOutOfBounds) {
        return Str.of(s).slice(from, failOnOutOfBounds).toString();
    }

    /**
     * Slices the string into a new substring
     * @param s The string to slice
     * @param from The beginning index
     * @return The resulting string slice
     */
    public static String slice(CharSequence s, int from) {
        return Str.of(s).slice(from).toString();
    }

    /**
     * Trim characters at the beginning of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String ltrim(CharSequence s, CharSequence trim) {
        return Str.of(s).ltrim(trim).toString();
    }

    /**
     * Trim characters at the end of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String rtrim(CharSequence s, CharSequence trim) {
        return Str.of(s).rtrim(trim).toString();
    }

    /**
     * Trim characters at the beginning and end of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String trim(CharSequence s, CharSequence trim) {
        return Str.of(s).trim(trim).toString();
    }

    /**
     * Trim characters at the beginning of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String ltrim(CharSequence s, char trim) {
        return Str.of(s).ltrim(trim).toString();
    }

    /**
     * Trim characters at the end of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String rtrim(CharSequence s, char trim) {
        return Str.of(s).rtrim(trim).toString();
    }

    /**
     * Trim characters at the beginning and end of the string
     * @param s The string to trim
     * @param trim The characters to trim
     * @return The resulting trimmed string
     */
    public static String trim(CharSequence s, char trim) {
        return Str.of(s).trim(trim).toString();
    }

    static private Map<Integer, List<CharSequence>> groupBySizes(CharSequence[] search) {
        Map<Integer, List<CharSequence>> strings = new WeakHashMap<>();
        Arrays.stream(search).forEach(it -> {
            if (strings.containsKey(it.length()))
                strings.get(it.length()).add(it);
            else
                strings.put(it.length(), new ArrayList<CharSequence>() {{ add(it); }});
        });
        return strings;
    }

    /**
     * Remove all whitespace from a string
     * @param s The string to remove from
     * @return The resulting string with no whitespace
     */
    public static String removeWhiteSpace(CharSequence s) {
        return Str.of(s).removeWhiteSpace().toString();
    }

    // --------------------------------- WORDS ---------------------------------
    static final char[] delimiters = new char[] { Chars.SPACE, Chars.DASH, Chars.DECIMAL, Chars.UNDER_SCORE};
    static boolean isDelimiter(char c) { return Arrays.binarySearch(delimiters, c) > -1; }

    /**
     * Quote the given string using double quotes
     * @param s The string to put in quotes
     * @return The resulting quoted string
     */
    public static String quote(CharSequence s) {
        return Str.of(s).quote().toString();
    }

    /**
     * Quote the given string using single quotes
     * @param s The string to put in quotes
     * @return The resulting quoted string
     */
    public static String singleQuote(CharSequence s) {
        return Str.of(s).singleQuote().toString();
    }

    /**
     * Capitalize the given string
     * @param s The string to capitalize
     * @return The resulting capitalized string
     */
    public static String capitalize(CharSequence s) {
        return Str.of(s).capitalize().toString();
    }

    /**
     * UnCapitalize the given string
     * @param s The string to unCapitalize
     * @return The resulting unCapitalized string
     */
    public static String unCapitalize(CharSequence s) {
        return Str.of(s).unCapitalize().toString();
    }

    /**
     * Convert the given string to a variable name
     * Removes reserved words and other illegal characters from the string
     * @param s The string to convert
     * @return The resulting string as a variable name
     */
    public static String toVariableName(CharSequence s) {
        return Str.of(s).toVariableName().toString();
    }

    /**
     * Convert the given string into camel casing
     * @param s The string to camel case
     * @return The resulting string in camel case
     */
    public static String toCamel(CharSequence s) {
        return Str.of(s).toCamel().toString();
    }

    /**
     * Convert the given string into pascal casing
     * @param s The string to pascal case
     * @return The resulting string in pascal case
     */
    public static String toPascal(CharSequence s) {
        return Str.of(s).toPascal().toString();
    }

    /**
     * Convert the given string into kebab casing
     * @param s The string to kebab case
     * @return The resulting string in kebab case
     */
    public static String toKebab(CharSequence s) {
        return Str.of(s).toKebab().toString();
    }

    /**
     * Convert the given string into snake casing
     * @param s The string to snake case
     * @return The resulting string in snake case
     */
    public static String toSnake(CharSequence s) {
        return Str.of(s).toSnake().toString();
    }

    /**
     * Convert the given string into title casing
     * @param s The string to title case
     * @return The resulting string in title case
     */
    public static String toTitleCase(CharSequence s) {
        return Str.of(s).toTitleCase().toString();
    }

    /**
     * Separate the given string into spaced words
     * @param s The string to separate
     * @return The resulting string separated
     */
    public static String toWords(CharSequence s) {
        return Str.of(s).toWords().toString();
    }

    /**
     * Convert the given string separated by forward slash '/' character
     * @param s The string to separate
     * @return The resulting string separated with '/'
     */
    public static String toUri(CharSequence s) {
        return Str.of(s).toUri().toString();
    }

    /**
     * Make the given string all upper case
     * @param s The string to make upper case
     * @return The result string in upper case
     */
    public static String toUpper(CharSequence s) {
        return Str.of(s).toUpper().toString();
    }

    /**
     * Make the given string all lower case
     * @param s The string to make lower case
     * @return The result string in lower case
     */
    public static String toLower(CharSequence s) {
        return Str.of(s).toLower().toString();
    }

    /**
     * Sort the characters according to the direction
     * @param s The string characters to sort
     * @param direction Sort direction ascending or descending order
     * @return The resulting sorted string
     */
    public static String sort(CharSequence s, Sort direction) {
        return Str.of(s).sort(direction).toString();
    }

    /**
     * Sort the characters according to the direction
     * @param s The string characters to sort
     * @return The resulting sorted string
     */
    public static String sort(CharSequence s) {
        return Str.of(s).sort().toString();
    }

    /**
     * Reverse the characters in the given string
     * @param s The string characters to reverse
     * @return The resulting string reversed
     */
    public static String reverse(CharSequence s) {
        return Str.of(s).reverse().toString();
    }

    // --------------------------------- static constructors ---------------------------------

    /**
     * New Str object initialized with any specified strings
     * @param s Any number of strings
     * @return The new Str object
     */
    public static Str of(CharSequence... s) {
        return new Str(s);
    }

    /**
     * Crate new Str object initialized with given character
     * @param c The character to add to this string
     * @return The new Str object
     */
    public static Str of(char c) {
        return new Str(c);
    }

    /**
     * Create new Str object with the given capacity
     * @param capacity The capacity of the Str object
     * @return The new Str object
     */
    public static Str of(int capacity) {
        return new Str(capacity);
    }

    /**
     * Create a new empty Str object
     * @return The new Str object
     */
    public static Str empty() {
        return new Str(EMPTY);
    }

    /**
     * Create a new Str object initialized with a random string
     * @param size The size of the random string
     * @return The new Str object
     */
    public static Str random(int size) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(Str::empty, (a, b) -> a.sb.appendCodePoint(b), Str::add);
    }

    /**
     * Create a new Str object initialized with a random string
     * @return The new Str object
     */
    public static Str random() {
        return random(new Random().nextInt(1000));
    }

    /**
     * Create a new salt encryption using the given length
     * @param length The length of the salt
     * @return New Optional salt string if the length is valid, Optional.empty otherwise
     */
    public static Optional<String> salt(final int length) {
        if (length < 1) {
            System.err.println("error in salt: length must be > 0");
            return Optional.empty();
        }

        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);

        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }

    /**
     * Hash the given password with the specified salt string
     * @param password The password to hash
     * @param salt The salt to use in the hashing algorithm
     * @return New Optional hash string if the hashing is successful, Optional.empty otherwise
     */
    public static Optional<String> hash(String password, String salt) {
        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, 10001, 512);
        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in key hashing");
        }
        spec.clearPassword();
        return Optional.empty();
    }

    /**
     * Defines how to slice a string
     * INCLUDE = include the beginning and end index
     * INCLUDE_EXCLUDE = include the beginning index, exclude the end index
     * EXCLUDE_INCLUDE = exclude the beginning index, include the end index
     * EXCLUDE = exclude the beginning and end index
     */
    public enum Slice {
        INCLUDE, INCLUDE_EXCLUDE, EXCLUDE_INCLUDE, EXCLUDE
    }

    /**
     * Defines the direction to sort
     * ASC = ascending order
     * DESC = descending order
     */
    public enum Sort {
        ASC, DESC
    }

    // --------------------------------- END STATIC ---------------------------------

    private final StringBuilder sb;

    private Str(CharSequence... charSequences) {
        this.sb = new StringBuilder();
        add(charSequences);
    }

    private Str(char...chars) {
        this.sb = new StringBuilder();
        for(char c : chars)
            sb.append(c);
    }

    private Str(int capacity) {
        this.sb = new StringBuilder(capacity);
    }

    /**
     * Returns the length (character count).
     *
     * @return  the length of the sequence of characters currently
     *          represented by this object
     */
    @Override
    public int length() {
        return sb.length();
    }

    /**
     * Returns the {@code char} value in this sequence at the specified index.
     * The first {@code char} value is at index {@code 0}, the next at index
     * {@code 1}, and so on, as in array indexing.
     * <p>
     * The index argument must be greater than or equal to
     * {@code 0}, and less than the length of this sequence.
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the desired {@code char} value.
     * @return     the {@code char} value at the specified index.
     * @throws     IndexOutOfBoundsException  if {@code index} is
     *             negative or greater than or equal to {@code length()}.
     */
    @Override
    public char charAt(int index) {
        return sb.charAt(index);
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     *
     * <p> An invocation of this method of the form
     *
     * <pre>{@code
     * sb.subSequence(begin,&nbsp;end)}</pre>
     *
     * behaves in exactly the same way as the invocation
     *
     * <pre>{@code
     * sb.substring(begin,&nbsp;end)}</pre>
     *
     * This method is provided so that this class can
     * implement the {@link CharSequence} interface.
     *
     * @param      start   the start index, inclusive.
     * @param      end     the end index, exclusive.
     * @return     the specified subsequence.
     *
     * @throws  IndexOutOfBoundsException
     *          if {@code start} or {@code end} are negative,
     *          if {@code end} is greater than {@code length()},
     *          or if {@code start} is greater than {@code end}
     * @spec JSR-51
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return sb.subSequence(start, end);
    }

    /**
     * @return A new String representing this Str object
     */
    @Override
    public String toString() {
        return sb.toString();
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * @return A new String representing this Str object substituting any format parameters with the supplied arguments
     */
    public String fmt(Object...args) {
        return String.format(toString(), args);
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CharSequence && this.compareTo((CharSequence) obj) == 0;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(CharSequence o) {
        return Str.compare(this, o);
    }

    /**
     * Convert the Str to a byte array
     * @return The byte array representing this Str object
     */
    public byte[] getBytes() {
        char[] chars = toArray();
        ByteBuffer bb = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
        byte[] b = new byte[bb.remaining()];
        bb.get(b);
        return b;
    }

    /**
     * Convenience method to pass to any Str consumers
     * @param consumer The Str consumer
     * @return This Str object
     */
    public Str andThen(Consumer<Str> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index
     * @param s The string to search for
     * @param from the index from which to start the search.
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> indexOf(CharSequence s, int from) {
        int i = sb.indexOf(s.toString(), from);
        return i > -1 ? Optional.of(i) : Optional.empty();
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index
     * @param s The string to search for
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> indexOf(CharSequence s) {
        return indexOf(s, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index
     * @param c The character to search for
     * @param from the index from which to start the search.
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> indexOf(char c, int from) {
        return indexOf(String.valueOf(c), from);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index
     * @param c The character to search for
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> indexOf(char c) {
        return indexOf(String.valueOf(c), 0);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring
     * @param s The string to search for
     * @param from the index from which to start the search.
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> lastIndexOf(CharSequence s, int from) {
        int i = sb.lastIndexOf(s.toString(), from);
        return i > -1 ? Optional.of(i) : Optional.empty();
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring
     * @param s The string to search for
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> lastIndexOf(CharSequence s) {
        return lastIndexOf(s, 0);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring
     * @param c The character to search for
     * @param from the index from which to start the search.
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> lastIndexOf(char c, int from) {
        return lastIndexOf(String.valueOf(c), from);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring
     * @param c The character to search for
     * @return An Optional index >= 0 if the string is found, Optional.empty otherwise
     */
    public Optional<Integer> lastIndexOf(char c) {
        return lastIndexOf(String.valueOf(c), 0);
    }

    /**
     * An equals method which takes a String/CharSequence to avoid cast warnings.
     * @param s The string to compare.
     * @return True if the strings are equal
     */
    public boolean eq(CharSequence s) {
        return this.equals(s);
    }

    /**
     * Case insensitive equals.
     * @param s The string to compare.
     * @return True if the strings are equal regardless of case.
     */
    public boolean ieq(CharSequence s) {
        return toString().equalsIgnoreCase(s.toString());
    }

    /**
     * Determine if this Str object contains the specified search string
     * @param s The string to search for
     * @return True if the Str object contains the search string
     */
    public boolean contains(CharSequence s) {
        return sb.indexOf(s.toString()) > -1;
    }

    /**
     * Determine if this Str object contains any of the specified search strings
     * @param s The strings to search for
     * @return True if the Str object contains any of the search strings
     */
    public boolean containsAny(CharSequence[] s) {
        for (CharSequence c : s) {
            if (eq(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if this Str object contains all the specified search strings
     * @param s The strings to search for
     * @return True if the Str object contains all the search strings
     */
    public boolean containsAll(CharSequence[] s) {
        for (CharSequence c : s) {
            if (!eq(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if this Str object starts with the specified string
     * @param s The string to search for
     * @return True if the Str object starts with the specified string
     */
    public boolean startsWith(CharSequence s) {
        return subSequence(0, Math.min(s.length(), length())).equals(s);
    }

    /**
     * Determine if this Str object starts with the specified string
     * @param s The string to search for
     * @return True if the Str object starts with the specified string
     */
    public boolean endsWith(CharSequence s) {
        return subSequence(Math.max(length() - s.length(), 0), length()).equals(s);
    }

    /**
     * Determine if this Str object matches the regex string
     * @param regex The regex to match with
     * @return True if the regex matches this Str object
     */
    public boolean matches(CharSequence regex) {
        return toString().matches(regex.toString());
    }

    /**
     * Find and replace the search string with a replacement string
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @param max The maximum amount of replacements to make in the string
     * @return The Str object.
     */
    public Str findAndReplace(CharSequence search, CharSequence replacement, int max) {
        if (isNotEmpty(search)) {
            String r = replacement.toString();
            int searchLength = search.length();
            int replacementLength = replacement.length();

            int count = 0;
            if (max < 0) {
                max = Math.abs(max);

                for (int i = length(); count < max && i >= searchLength; i--) {
                    int from = i - searchLength;
                    if (charAt(from) == search.charAt(0) && subSequence(from, i).equals(search)) {
                        sb.replace(from, i, r);
                        count++;
                    }
                }
            } else {
                String s = search.toString();
                int i = sb.indexOf(s);
                while (count < max && i > -1) {
                    sb.replace(i, i + searchLength, r);
                    i = sb.indexOf(s, i + replacementLength);
                    count++;
                }
            }
        }
        return this;
    }

    /**
     * Find and replace the search string with a replacement string
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplace(CharSequence search, CharSequence replacement) {
        return findAndReplace(search, replacement, length());
    }

    /**
     * Find and replace the first search string found with a replacement string
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplaceFirst(CharSequence search, CharSequence replacement) {
        return findAndReplace(search, replacement, 1);
    }

    /**
     * Find and replace the last search string found with a replacement string
     * @param search The string to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplaceLast(CharSequence search, CharSequence replacement) {
        return findAndReplace(search, replacement, -1);
    }

    /**
     * Find and replace all search strings with a replacement string
     * @param search The strings to search for
     * @param replacement The string to replace the search string with
     * @param max The maximum amount of replacements to make in the string
     * @return The Str object.
     */
    public Str findAndReplace(CharSequence[] search, CharSequence replacement, int max) {
        if (search.length == 1) { // if only one search term call the appropriate method
            return findAndReplace(search[0], replacement, max);
        }

        Map<Integer, List<CharSequence>> strings = groupBySizes(search);

        List<Integer> sizes = strings.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .filter(it -> it > 0 && it <= length())
                .collect(Collectors.toList());

        if (!sizes.isEmpty()) {
            int minLength = sizes.get(sizes.size() - 1);

            String r = replacement.toString();
            int replacementLength = replacement.length();

            int count = 0;
            // search from the tail
            if (max < 0) {
                max = Math.abs(max);
                for (int i = length(); count < max && i >= minLength; i--) {
                    for (int size : sizes) {
                        int from = i - size;
                        if (from >= 0 && strings.get(size).contains(subSequence(from, i))) {
                            sb.replace(from, i, r);
                            count++;
                        }
                    }
                }
            } else { // search from the head
                for (int i = 0; count < max && i <= length() - minLength; i++) {
                    for (int size : sizes) {
                        int to = i + size;
                        if (to <= length() && strings.get(size).contains(subSequence(i, to))) {
                            sb.replace(i, to, r);
                            i += replacementLength - 1;
                            count++;
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * Find and replace the search strings with a replacement string
     * @param search The strings to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplace(CharSequence[] search, CharSequence replacement) {
        return findAndReplace(search, replacement, length());
    }

    /**
     * Find and replace the first instance of each search string fround with a replacement string
     * @param search The strings to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplaceFirst(CharSequence[] search, CharSequence replacement) {
        return findAndReplace(search, replacement, 1);
    }

    /**
     * Find and replace the last instance of each search string fround with a replacement string
     * @param search The strings to search for
     * @param replacement The string to replace the search string with
     * @return The Str object.
     */
    public Str findAndReplaceLast(CharSequence[] search, CharSequence replacement) {
        return findAndReplace(search, replacement, -1);
    }

    /**
     * Replace all regex matches with a replacement string
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @param max The maximum amount of replacements to make in the string
     * @return This Str object
     */
    public Str replace(CharSequence regex, CharSequence replacement, int max) {
        Matcher matcher = Pattern.compile(regex.toString()).matcher(toString());
        boolean found = matcher.find();
        int count = 0;
        if (found && count < max) {
            String r = replacement.toString();
            int replacementLength = replacement.length();
            int offset = 0;
            while(found) {
                int from = matcher.start();
                int to = matcher.end();
                sb.replace(from + offset, to + offset, r);
                offset += replacementLength - (to - from);
                count++;
                found = count < max && matcher.find();
            }
        }
        return this;
    }

    /**
     * Replace all regex matches with a replacement string
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @return This Str object
     */
    public Str replace(CharSequence regex, CharSequence replacement) {
        return replace(regex, replacement, length());
    }

    /**
     * Replace the first string which matches the given regex with a replacement string
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @return This Str object
     */
    public Str replaceFirst(CharSequence regex, CharSequence replacement) {
        return replace(regex, replacement, 1);
    }

    /**
     * Replace the last string which matches the given regex with a replacement string
     * @param regex The regex to match with
     * @param replacement The string to replace the search string with
     * @return This Str object
     */
    public Str replaceLast(CharSequence regex, CharSequence replacement) {
        Matcher matcher = Pattern.compile(regex.toString()).matcher(this);
        IntRange range = IntRange.empty();
        while(matcher.find()) {
            if (range.isEmpty() || matcher.start() > range.from()){
                range.from(matcher.start());
                range.to(matcher.end());
            }
        }
        if (range.isPresent()) {
            sb.replace(range.from(), range.to(), replacement.toString());
        }
        return this;
    }

    /**
     * Delete the search string from this Str object
     * @param search The string to delete
     * @return This Str object
     */
    public Str delete(CharSequence search) {
        return findAndReplace(search, EMPTY);
    }

    /**
     * Delete the search strings from this Str object
     * @param search The strings to delete
     * @return This Str object
     */
    public Str delete(CharSequence... search) {
        return findAndReplace(search, EMPTY);
    }

    /**
     * Delete the search strings from this Str object
     * @param search The strings to delete
     * @param max The maximum number of strings to delete
     * @return This Str object
     */
    public Str delete(CharSequence[] search, int max) {
        return findAndReplace(search, EMPTY, max);
    }

    /**
     * Delete the first search string from this Str object
     * @param search The string to delete
     * @return This Str object
     */
    public Str deleteFirst(CharSequence search) {
        return findAndReplace(search, EMPTY, 1);
    }

    /**
     * Delete the first found instance of each search string from this Str object
     * @param search The strings to delete
     * @return This Str object
     */
    public Str deleteFirst(CharSequence... search) {
        return findAndReplace(search, EMPTY, 1);
    }

    /**
     * Delete the last search string from this Str object
     * @param search The string to delete
     * @return This Str object
     */
    public Str deleteLast(CharSequence search) {
        return findAndReplace(search, EMPTY, -1);
    }

    /**
     * Delete the last found instance of each search string from this Str object
     * @param search The strings to delete
     * @return This Str object
     */
    public Str deleteLast(CharSequence... search) {
        return findAndReplace(search, EMPTY, -1);
    }

    /**
     * Delete all characters from the Str object
     * @return This Str object
     */
    public Str delete() {
        sb.setLength(0);
        return this;
    }

    /**
     * Remove all whitespace from a string
     * @return This Str object
     */
    public Str removeWhiteSpace() {
        trim();
        int len = length();
        for (int i = 0; i < len; i++) {
            if (Character.isWhitespace(charAt(i))) {
                sb.deleteCharAt(i);
                i--;
                len--;
            }
        }
        return this;
    }

    /**
     * Delete all characters from the Str object then add the specified string to this Str object
     * @return This Str object
     */
    public Str setTo(CharSequence s) {
        return delete().add(s);
    }

    /**
     * Determine if the string is null or empty
     * @return True if the string is null or empty, false otherwise
     */
    public boolean isEmpty() {
        return isEmpty(this);
    }

    /**
     * Determine if the string is not empty. Negation of <code>Str.isEmpty(s)</code>
     * @return True if the string is not empty, false otherwise
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Determine if the string is blank
     * @return True if the string is not empty, false otherwise
     */
    public boolean isBlank() {
        return isBlank(this);
    }

    /**
     * Add all the specified strings to this Str object
     * @param charSequences The strings to add
     * @return The Str object
     */
    public Str add(CharSequence... charSequences) {
        for(CharSequence cs : charSequences)
            sb.append(cs);
        return this;
    }

    /**
     * Preface this Str object with all the specified strings
     * @param charSequences The strings to preface
     * @return The Str object
     */
    public Str preface(CharSequence... charSequences) {
        for(CharSequence cs : charSequences)
            sb.insert(0, cs);
        return this;
    }

    /**
     * Add all the specified characters to this Str object
     * @param chars The characters to add
     * @return The Str object
     */
    public Str add(char... chars) {
        for(char c : chars)
            sb.append(c);
        return this;
    }

    /**
     * Preface this Str object with all the specified characters
     * @param chars The characters to preface
     * @return The Str object
     */
    public Str preface(char... chars) {
        for(char c : chars)
            sb.insert(0, c);
        return this;
    }

    /**
     * Add all the specified numbers to this Str object
     * @param numbers The characters to add
     * @return The Str object
     */
    public Str add(Number... numbers) {
        for(Number n : numbers)
            sb.append(n);
        return this;
    }

    /**
     * Preface this Str object with all the specified numbers
     * @param numbers The characters to preface
     * @return The Str object
     */
    public Str preface(Number... numbers) {
        for(Number n : numbers)
            sb.insert(0, n);
        return this;
    }

    /**
     * Quote the given string using double quotes
     * @return The Str object
     */
    public Str quote() {
        return preface('"').add('"');
    }

    /**
     * Quote the given string using single quotes
     * @return The Str object
     */
    public Str singleQuote() {
        return preface("'").add("'");
    }

    /**
     * Repeat the specified string len number of times
     * @param s The string to repeat
     * @param len How many times to repeat the string. If len is negative, the string will be prepended
     * @return The Str object
     */
    public Str repeat(CharSequence s, int len) {
        if (len > 0)
            sb.append(String.join(EMPTY, Collections.nCopies(len, s)));
        else if (len < 0)
            sb.insert(0, String.join(EMPTY, Collections.nCopies(Math.abs(len), s)));

        return this;
    }

    /**
     * Repeat the specified string len number of times
     * @param c The character to repeat
     * @param len How many times to repeat the string. If len is negative, the string will be prepended
     * @return The Str object
     */
    public Str repeat(char c, int len) {
        return repeat(String.valueOf(c), len);
    }

    /**
     * Rotate the string i spaces by dropping the last characters and prepending the string with the dropped characters
     * @param i The number of characters to rotate
     * @return The Str object
     */
    public Str rotateRight(int i) {
        int length = length();
        i = i > length ? i % length : i;
        return i == length ? this : preface(sb.subSequence(length() - i, length())).drop(i);
    }

    /**
     * Rotate the string i spaces by dropping the beginning characters and appending the string with the dropped characters
     * @param i The number of characters to rotate
     * @return The Str object
     */
    public Str rotateLeft(int i) {
        int length = length();
        i = i > length ? i % length : i;
        return i == length ? this : add(sb.subSequence(0, i)).skip(i);
    }

    /**
     * Add any number of spaces to the string
     * @param spaces The number of spaces to add
     * @return The Str object
     */
    public Str space(int spaces) {
        return repeat(Chars.SPACE, spaces);
    }

    /**
     * Add a space to the string
     * @return The Str object
     */
    public Str space() {
        return space(1);
    }

    /**
     * Add any number of tabs to the string
     * @param tabs The number of tabs to add
     * @return The Str object
     */
    public Str tab(int tabs) {
        return repeat(Chars.SPACE, tabs * 4);
    }

    /**
     * Add a tab to the string
     * @return The Str object
     */
    public Str tab() {
        return space(4);
    }

    /**
     * Add any number of lines to the string
     * @param lines The number of line separators to add
     * @return The Str object
     */
    public Str br(int lines) {
        return repeat(System.lineSeparator(), lines);
    }

    /**
     * Add a line separator to the string
     * @return The Str object
     */
    public Str br() {
        return br(1);
    }

    /**
     * Add any number of dots to the string
     * @param dots The number of spaces to add
     * @return The Str object
     */
    public Str dot(int dots) {
        return repeat(Chars.DECIMAL, dots);
    }

    /**
     * Add a dot to the string
     * @return The Str object
     */
    public Str dot() {
        return dot(1);
    }

    /**
     * Add any number of file separators to the string
     * @param separators The number of separator characters to add
     * @return The Str object
     */
    public Str sep(int separators) {
        return repeat(File.separatorChar, separators);
    }

    /**
     * Add a file separator to the string
     * @return The Str object
     */
    public Str sep() {
        return sep(1);
    }

    /**
     * Add a file path to the string
     * @param s The string path to add
     * @return The Str object
     */
    public Str path(CharSequence...s) {
        return add(String.join(File.separator, s));
    }

    /**
     * Deletes i number of characters from the beginning of the string
     * @param i number of characters to skip
     * @return The Str object
     */
    public Str skip(int i) {
        sb.delete(0, i);
        return this;
    }

    /**
     * Delete all characters from the string which are not taken
     * @param i The number of characters to take
     * @return The Str object
     */
    public Str take(int i) {
        sb.delete(i, length());
        return this;
    }

    /**
     * Delete the characters from the end of the string
     * @param i The number of characters to drop
     * @return The Str object
     */
    public Str drop(int i) {
        sb.delete(length() - i, length());
        return this;
    }

    /**
     * Flexible substring method which supports multiple inclusion policies and negative indexes.
     * INCLUDE from and to are included in the result
     * INCLUDE_EXCLUDE from is included to is excluded
     * EXCLUDE_INCLUDE from is excluded to is included
     * EXCLUDE from and to are excluded
     * NOTE: from greater than to is always undefined and return will result is an empty string or an exception.
     * @param from The start index inclusive or exclusive. If negative the index will count backwards from the tail.
     * @param to The final index inclusive or exclusive. If negative the index will count backwards from the tail.
     * @param policy Determines which indexes should be included or excluded
     * @param failOnOutOfBounds Throws StringIndexOutOfBounds Exception if true and indexes are violated.
     *                          If false out of bounds indexes are moved to the ends of the valid range.
     * @return A self referencing Str to support a fluent api.
     */
    public Str slice(int from, int to, Slice policy, boolean failOnOutOfBounds) {
        int len = length();

        from = from >= 0 ? from : len + from;
        to = to >= 0 ? to : len + to;

        if (failOnOutOfBounds && from > to) {
            throw new StringIndexOutOfBoundsException(String.format("begin %s, end %s, length %s", from, to, len));
        }

        switch (policy) {
            case INCLUDE:
                to++;
                break;
            case INCLUDE_EXCLUDE:
                break;
            case EXCLUDE_INCLUDE:
                if (from != to) from++;
                to++;
                break;
            case EXCLUDE:
                from++;
                break;
        }
        if (failOnOutOfBounds) {
            if (!IntRange.From(0).to(len).allIn(from, to))
                throw new StringIndexOutOfBoundsException(String.format("begin %s, end %s, length %s", from, to, len));
        }
        if (from > to || to <= 0 || from >= len) {
            return delete();
        } else {
            return skip(Math.max(Math.min(from, len), 0))
                    .take(Math.min(to - from, length()));
        }
    }

    /**
     * Slices the string into a new substring
     * @param from The beginning index
     * @param to The ending index
     * @param failOnOutOfBounds If true throw an exception if the starting/ending indices are out of bounds, otherwise ignore
     * @return The Str object
     */
    public Str slice(int from, int to, boolean failOnOutOfBounds) {
        return slice(from, to, Slice.INCLUDE, failOnOutOfBounds);
    }


    /**
     * Slices the string into a new substring
     * @param from The beginning index
     * @param to The ending index
     * @param policy Inclusion policy: INCLUDE, INCLUDE_EXCLUDE, EXCLUDE_INCLUDE, EXCLUDE
     * @return The Str object
     */
    public Str slice(int from, int to, Slice policy) {
        return slice(from, to, policy, false);
    }

    /**
     * Slices the string into a new substring
     * @param from The beginning index
     * @param to The ending index
     * @return The Str object
     */
    public Str slice(int from, int to) {
        return slice(from, to, Slice.INCLUDE, false);
    }

    /**
     * Slices the string into a new substring
     * @param from The beginning index
     * @param failOnOutOfBounds If true throw an exception if the starting/ending indices are out of bounds, otherwise ignore
     * @return The Str object
     */
    public Str slice(int from, boolean failOnOutOfBounds) {
        return slice(from, length(), Slice.INCLUDE_EXCLUDE, failOnOutOfBounds);
    }

    /**
     * Slices the string into a new substring
     * @param from The beginning index
     * @return The Str object
     */
    public Str slice(int from) {
        return slice(from, length(), Slice.INCLUDE_EXCLUDE, false);
    }

    /**
     * Trim s [-1 == left, 1 == right, 0 == left and right]
     * @param lr Indicates which direction to trim -1 for left, 0 for left and right, and 1 for right.
     * @param s The string to trim.
     * @return A self referencing Str to support a fluent api.
     */
    protected Str lrtrim(int lr, CharSequence... s) {
        Map<Integer, List<CharSequence>> strings = groupBySizes(s);

        List<Integer> sizes = strings.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .filter(it -> it > 0 && it <= length())
                .collect(Collectors.toList());

        int i = 0;
        boolean trim;

        // left trim or both
        if (lr == 0 || lr == -1) {
            int len = length();
            do {
                trim = false;
                for (int size : sizes) {
                    int to = i + size;
                    if (to < len && strings.getOrDefault(size, emptyList).contains(subSequence(i, to)) ) {
                        i = to;
                        trim = true;
                        break;
                    }
                }
            } while(trim);

            if (i > 0) sb.delete(0, i);
        }

        // right trim or both
        if (lr == 0 || lr == 1) {
            int len = length();
            i = len;
            do {
                trim = false;
                for (int size : sizes) {
                    int from = i - size;
                    if (from >= 0 && strings.getOrDefault(size, emptyList).contains(subSequence(from, i))) {
                        i = from;
                        trim = true;
                        break;
                    }
                }
            } while(trim);

            if (i < len) sb.delete(i, len);
        }
        return this;
    }

    /**
     * Trim characters at the beginning of the string
     * @param s The strings to trim
     * @return The Str object
     */
    public Str ltrim(CharSequence[] s) {
        if (s.length == 0) {
            ltrim();
        } else {
            lrtrim(-1, s);
        }
        return this;
    }

    /**
     * Trim characters at the end of the string
     * @param s The strings to trim
     * @return The Str object
     */
    public Str rtrim(CharSequence[] s) {
        if (s.length == 0) {
            rtrim();
        } else {
            lrtrim(1, s);
        }
        return this;
    }

    /**
     * Trim characters at the beginning and end of the string
     * @param s The string to trim
     * @return The Str object
     */
    public Str trim(CharSequence[] s) {
        return s.length == 0 ? ltrim().rtrim() : lrtrim(0, s);
    }

    /**
     * Trim characters at the beginning of the string
     * @param s The string to trim
     * @return The Str object
     */
    public Str ltrim(CharSequence s) {
        return lrtrim(-1, s);
    }

    /**
     * Trim characters at the end of the string
     * @param s The string to trim
     * @return The Str object
     */
    public Str rtrim(CharSequence s) {
        return lrtrim(1, s);
    }

    /**
     * Trim characters at the beginning and end of the string
     * @param s The string to trim
     * @return The Str object
     */
    public Str trim(CharSequence s) {
        return lrtrim(0, s);
    }

    /**
     * Trim all whitespace from the beginning of a string
     * @return The Str object
     */
    public Str ltrim() {
        int length = length();
        if (length > 0 && Character.isWhitespace(charAt(0))) {
            int i = 1;
            for (; i < length; i++) {
                if (!Character.isWhitespace(charAt(i))) {
                    break;
                }
            }
            sb.delete(0, i);
        }
        return this;
    }

    /**
     * Trim all whitespace from the end of a string
     * @return The Str object
     */
    public Str rtrim() {
        int length = length();
        int last = length - 1;
        if (length > 0 && Character.isWhitespace(charAt(last))) {
            int i = last - 1;
            for (; i >= 0; i--) {
                if (!Character.isWhitespace(charAt(i))) {
                    break;
                }
            }
            sb.delete(i + 1, length);
        }
        return this;
    }

    /**
     * Trim all whitespace from the beginning and end of a string
     * @return The Str object
     */
    public Str trim() {
        return ltrim().rtrim();
    }

    /**
     * Trim characters at the beginning of the string
     * @param c The character to trim
     * @return The Str object
     */
    public Str ltrim(char c) {
        int length = length();
        if (length > 0 && charAt(0) == c) {
            int i = 1;
            for (; i < length; i++) {
                if (charAt(i) != c) {
                    break;
                }
            }
            sb.delete(0, i);
        }
        return this;
    }

    /**
     * Trim characters at the end of the string
     * @param c The character to trim
     * @return The Str object
     */
    public Str rtrim(char c) {
        int length = length();
        int last = length - 1;
        if (length > 0 && charAt(last) == c) {
            int i = last - 1;
            for (; i >= 0; i--) {
                if (charAt(i) != c) {
                    break;
                }
            }
            sb.delete(i + 1, length);
        }
        return this;
    }

    /**
     * Trim characters from the beginning and end of a string
     * @return The Str object
     */
    public Str trim(char c) {
        return ltrim(c).rtrim(c);
    }

    /**
     * Get the first character from the string if not empty
     * @return Optional character if at least one character is in the string, <code>Optional.empty()</code> otherwise
     */
    public Optional<Character> head() {
        return head(this);
    }

    /**
     * Get the last character from the string if not empty
     * @return Optional character if at least one character is in the string, <code>Optional.empty()</code> otherwise
     */
    public Optional<Character> tail() {
        return tail(this);
    }

    /**
     * Reverse the characters in the given string
     * @return The Str object
     */
    public Str reverse() {
        sb.reverse();
        return this;
    }

    /**
     * Sort the characters according to the direction
     * @param direction Sort direction ascending or descending order
     * @return The Str Object
     */
    public Str sort(Sort direction) {
        CharSequence sorted =
                chars()
                        .mapToObj(i -> (char) i)
                        .map(String::valueOf)
                        .sorted(direction == Sort.ASC ? Comparator.naturalOrder() : Comparator.reverseOrder())
                        .collect(Collectors.joining(EMPTY));
        return delete().add(sorted);
    }

    /**
     * Sort the characters in ascending order
     * @return The Str Object
     */
    public Str sort() {
        return sort(Sort.ASC);
    }

    /**
     * Capitalize the string
     * @return The Str Object
     */
    public Str capitalize() {
        if (isNotEmpty()) {
            char c = charAt(0);
            int i = 1;
            int len = length();
            while(!Character.isAlphabetic(c) && i < len) {
                c = charAt(i);
                i++;
            }

            if (Character.isLowerCase(c)) {
                sb.setCharAt(i - 1, Character.toUpperCase(c));
            }
        }
        return this;
    }

    /**
     * UnCapitalize the string
     * @return The Str Object
     */
    public Str unCapitalize() {
        if (isNotEmpty()) {
            char c = charAt(0);
            int i = 1;
            int len = length();
            while(!Character.isAlphabetic(c) && i < len) {
                c = charAt(i);
                i++;
            }

            if (Character.isUpperCase(c)) {
                sb.setCharAt(i - 1, Character.toLowerCase(c));
            }
        }
        return this;
    }

    /**
     * Convert the string to a variable name
     * Removes reserved words and other illegal characters from the string
     * @return The Str Object
     */
    public Str toVariableName() {
        Optional<Character> c = head();
        if (c.isPresent()) {
            if (Character.isDigit(c.get()))
                preface(Chars.UNDER_SCORE);

            replace("[^0-9_$\\w]", UNDER_SCORE);
            for(String word : reservedWords) {
                if (eq(word)) {
                    preface(Chars.UNDER_SCORE);
                    break;
                }
            }
        }
        return this;
    }

    /**
     * Convert the given string into camel casing
     * @param pascal Use pascal case simply by capitalizing the camel cased result
     * @return The Str object
     */
    protected Str toCamel(boolean pascal) {
        boolean dotted = false;

        for (int i = 0; i < length(); i++) {
            char c = charAt(i);
            if (isDelimiter(c)) {
                sb.deleteCharAt(i);
                // update the loop b/c we are looping and modifying
                // generally don't do this, however this is really efficient string manipulation
                i--;
                dotted = true;
            } else if(dotted) {
                sb.setCharAt(i, Character.toUpperCase(c));
                dotted = false;
            }
        }
        return pascal ? capitalize() : unCapitalize();
    }

    /**
     * Convert the string into camel casing
     * @return The Str object
     */
    public Str toCamel() {
        return toCamel(false);
    }

    /**
     * Convert the string into pascal casing
     * @return The Str object
     */
    public Str toPascal() {
        return toCamel(true);
    }

    /**
     * Convert the string into a delimited string
     * @param delimiter The delimiter to use
     * @param lowerCase If true make the string all lower case
     * @return The Str object
     */
    protected Str toDelimitedName(char delimiter, boolean lowerCase) {
        boolean dotted = false;

        for (int i = 0; i < length(); i++) {
            char c = charAt(i);
            if (isDelimiter(c)) {
                if (dotted || i == 0 || i >= length() - 1) {
                    sb.deleteCharAt(i);
                    // update the loop b/c we are looping and modifying
                    // generally don't do this, however this is really efficient string manipulation
                    i--;
                } else {
                    sb.setCharAt(i, delimiter);
                }
                dotted = true;
            } else {
                if (Character.isUpperCase(c)) {
                    if (lowerCase) {
                        sb.setCharAt(i, Character.toLowerCase(c));
                    }
                    if (!dotted && i > 0 && i <= length() - 1) {
                        sb.insert(i, delimiter);
                        i++;
                    }
                } else if (!lowerCase && dotted) {
                    sb.setCharAt(i, Character.toUpperCase(c));
                }
                dotted = false;
            }
        }
        return rtrim(delimiter);
    }

    /**
     * Convert the given string into kebab casing
     * @return The Str object
     */
    public Str toKebab() {
        return toDelimitedName(Chars.DASH, true);
    }

    /**
     * Convert the given string into snake casing
     * @return The Str object
     */
    public Str toSnake() {
        return toDelimitedName(Chars.UNDER_SCORE, true);
    }

    /**
     * Convert the given string into title casing
     * @return The Str object
     */
    public Str toTitleCase() {
        return toDelimitedName(Chars.SPACE, false).capitalize();
    }

    /**
     * Separate the given string into spaced words
     * @return The Str object
     */
    public Str toWords() {
        return toDelimitedName(Chars.SPACE, true);
    }

    /**
     * Convert the given string separated by forward slash '/' character
     * @return The Str object
     */
    public Str toUri() {
        return toDelimitedName(Chars.SLASH, true);
    }

    /**
     * Make the given string all upper case
     * @return The Str object
     */
    public Str toUpper() {
        for (int i = 0; i < length(); i++) {
            char c = charAt(i);
            if (Character.isLowerCase(c))
                sb.setCharAt(i, Character.toUpperCase(c));
        }
        return this;
    }

    /**
     * Make the given string all lower case
     * @return The Str object
     */
    public Str toLower() {
        for (int i = 0; i < length(); i++) {
            char c = charAt(i);
            if (Character.isUpperCase(c))
                sb.setCharAt(i, Character.toLowerCase(c));
        }
        return this;
    }

    /**
     * Determine if the string is a number
     * @param type The type of the number
     * @return True if the string is a valid number, false otherwise
     */
    public boolean isNum(Num.Type type) {
        return isNumber(this, type);
    }

    /**
     * Determine if the string is a number
     * @return True if the string is a valid number, false otherwise
     */
    public boolean isNum() {
        return isNumber(this);
    }

    /**
     * Determine if the string is an integer
     * @return True if the string is a valid integer, false otherwise
     */
    public boolean isInt() {
        return isInteger(this);
    }

    /**
     * Determine if the string is a boolean
     * @return True if the string is a valid boolean, false otherwise
     */
    public boolean isBoolean() {
        return isBoolean(this);
    }

    /**
     * Determine if the string is a byte
     * @return True if the string is a valid byte, false otherwise
     */
    public boolean isByte() {
        return isByte(this);
    }

    /**
     * Convert string to an optional boolean
     * @return Optional boolean if the string is a valid boolean, Optional.empty otherwise
     */
    public Optional<Boolean> toBoolean() {
        return toBoolean(this);
    }

    /**
     * Convert string to an optional byte
     * @return Optional byte if the string is a valid byte, Optional.empty otherwise
     */
    public Optional<Byte> toByte() {
        return toByte(this);
    }

    /**
     * Convert string to an optional short
     * @return Optional short if the string is a valid short, Optional.empty otherwise
     */
    public Optional<Short> toShort() {
        return toShort(this);
    }

    /**
     * Convert string to an optional integer
     * @return Optional integer if the string is a valid integer, Optional.empty otherwise
     */
    public Optional<Integer> toInteger() {
        return toInteger(this);
    }

    /**
     * Convert string to an optional long
     * @return Optional long if the string is a valid long, Optional.empty otherwise
     */
    public Optional<Long> toLong() {
        return toLong(this);
    }

    /**
     * Convert string to an optional float
     * @return Optional float if the string is a valid float, Optional.empty otherwise
     */
    public Optional<Float> toFloat() {
        return toFloat(this);
    }

    /**
     * Convert string to an optional double
     * @return Optional double if the string is a valid double, Optional.empty otherwise
     */
    public Optional<Double> toDouble() {
        return toDouble(this);
    }

    /**
     * Convert string to a character array
     * @return An array of type character <tt>char[]</tt>
     */
    public char[] toArray() {
        return toArray(this);
    }

    /**
     * Convert string to a list of characters
     * @return A list of characters <tt>List<Character></tt>
     */
    public List<Character> toList() {
        return toList(this);
    }
}
