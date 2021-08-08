package io.oreto.latte;

public class Notification {
    public static Notification of(String message, Type type, String group) {
        return new Notification(message, type, group);
    }
    public static Notification of(String message, Type type) {
        return new Notification(message, type, "");
    }
    public static Notification of(String message, String group) {
        return of(message, Type.info, group);
    }
    public static Notification of(String message) {
        return of(message, "");
    }

    protected String message;
    protected Type type;
    protected String group;

    protected Notification(String message, Type type, String group) {
        this.message = message;
        this.type = type;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public Notification withName(String name) {
        this.group = name;
        return this;
    }
    public Notification withMessage(String message) {
        this.message = message;
        return this;
    }
    public Notification withType(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return message;
    }

    public enum Type {
        info, success, warning, error, tip, description
    }
}
