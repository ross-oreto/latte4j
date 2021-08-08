package io.oreto.latte.web.http;

public enum HttpVerb {
    GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE, ANY, WS;

    public static HttpVerb from(String verb) {
        try {
            return valueOf(verb.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unsupported HTTP verb: " + verb);
        }
    }
}
