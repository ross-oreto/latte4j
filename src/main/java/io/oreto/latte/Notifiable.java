package io.oreto.latte;

import java.util.List;
import java.util.stream.Collectors;

public interface Notifiable {
    List<Notification> getNotifications();

    default List<Notification> notifications(String group) {
        return getNotifications().stream()
                .filter(it -> it.getGroup().equals(group))
                .collect(Collectors.toList());
    }

    default List<Notification> notifications(Notification.Type type) {
        return getNotifications().stream()
                .filter(it -> it.getType() == type)
                .collect(Collectors.toList());
    }

    default List<Notification> notifications(String group, Notification.Type type) {
        return getNotifications().stream()
                .filter(it -> it.group.equals(group) && it.getType() == type)
                .collect(Collectors.toList());
    }

    default void notify(String message, Notification.Type type) {
        Notification notification = Notification.of(message, type);
        getNotifications().add(notification);
    }

    default Object notify(Notification notification) {
        getNotifications().add(notification);
        return notification;
    }

    default void notify(List<Notification> notifications) {
        getNotifications().addAll(notifications);
        getNotifications();
    }
}
