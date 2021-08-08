package io.oreto.latte.web;

import io.oreto.latte.web.constants.C;

import java.text.MessageFormat;
import java.util.*;

public class I18n {
    public static final String LANG_ACCEPT_HEADER = "Accept-Language";

    /**
     * Convert the string to Locale object
     * @param s The language string
     * @return A Locale object
     */
    public static Locale toLocale(String s) {
        String[] codes = s.split("-");
        return codes.length > 1 ? new Locale(codes[0], codes[1].toUpperCase()) : new Locale(codes[0]);
    }

    /**
     * Parse the locale from request parameters and or header values.
     * @param params Map of query/path parameters.
     * @param headers Map of request headers.
     * @return The locale based on the request.
     */
    public static Locale parseLocale(Map<String, String> params, Map<String, String> headers) {
        if (Objects.nonNull(params) && params.containsKey(C.lang))
            return toLocale(params.get(C.lang));
        else if (Objects.nonNull(headers) && headers.containsKey(LANG_ACCEPT_HEADER))
            return toLocale(headers.get(LANG_ACCEPT_HEADER));
        return Locale.getDefault();
    }

    public static I18n of(ResourceBundle resourceBundle) {
        return new I18n(resourceBundle);
    }
    public static I18n of(String baseName, Locale locale) {
        return new I18n(baseName, locale);
    }
    public static I18n of(Locale locale) {
        return new I18n(locale);
    }
    public static I18n of(String baseName, String language) {
        return new I18n(baseName, language);
    }
    public static I18n of(String language) {
        return new I18n(language);
    }

    private final ResourceBundle resourceBundle;

    protected I18n(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    protected I18n(String baseName, Locale locale) {
        this(ResourceBundle.getBundle(baseName, locale));
    }
    protected I18n(Locale locale) {
        this(C.messages, locale);
    }
    protected I18n(String baseName, String language) {
       this(baseName, toLocale(language));
    }
    protected I18n(String language) {
        this(toLocale(language));
    }

    /**
     * Translate the specified key using any args present.
     * @param key The resource bundle key.
     * @param args Arguments needed to resolve the translation message.
     * @return The translated string or empty if not found.
     */
    public Optional<String> t(String key, Object... args) {
        if (resourceBundle.containsKey(key)) {
            String property = resourceBundle.getString(key);
            return Optional.of(args == null
                    ? property
                    : MessageFormat.format(property, args));
        } else {
           return Optional.empty();
        }
    }

    /**
     * Same as t(key, args), with the additional support of argument replacement
     * given the message properties:
     * save=submit
     * test=click on the {0} button
     * t("test", ":save") will result in 'click on the save button';
     * @param key The resource bundle key.
     * @param args Arguments needed to resolve the translation message.
     * @return The translated string or empty if not found.
     */
    public Optional<String> t2(String key, Object... args) {
        return t(key, Arrays.stream(args).map(it -> Objects.nonNull(it)
                && it instanceof String && ((String) it).startsWith(":") ? t(key).orElse(key) : it));
    }
}
