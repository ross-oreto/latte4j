package io.oreto.latte.pojos;

import java.util.*;

public class Person {
    private Long id;
    private String name;
    private final List<String> nickNames;
    private final List<Order> orders;
    private Address address;

    public Person() {
        orders = new ArrayList<>();
        nickNames = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders.clear();
        addOrder(orders);
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getNickNames() {
        return nickNames;
    }
    public void setNickNames(List<String> nickNames) {
        this.nickNames.clear();
        addNickName(nickNames);
    }

    public Person withId(Long id) {
        this.id = id;
        return this;
    }
    public Person withName(String name) {
        this.name = name;
        return this;
    }
    public Person withOrders(List<Order> orders) {
        setOrders(orders);
        return this;
    }
    public Person withAddress(Address address) {
        setAddress(address);
        return this;
    }
    public Person withNickNames(List<String> nickNames) {
        setNickNames(nickNames);
        return this;
    }

    public Person addNickName(String... nickNames) {
        this.nickNames.addAll(Arrays.asList(nickNames));
        return this;
    }
    public Person addNickName(Collection<String> nickNames) {
        this.nickNames.addAll(nickNames);
        return this;
    }

    public Person addOrder(Order... orders) {
        for(Order order : orders) {
            order.setPerson(this);
            this.orders.add(order);
        }
        return this;
    }
    public Person addOrder(Collection<Order> orders) {
        return addOrder(orders.toArray(new Order[0]));
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Person)) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(person.getId(), getId())
                && Objects.equals(person.getName(), getName());
    }
}
