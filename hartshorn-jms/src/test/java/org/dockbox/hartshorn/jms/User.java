package org.dockbox.hartshorn.jms;

public class User {
    private String name;
    private int age;

    public User(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public User() {
    }

    public String name() {
        return this.name;
    }

    public int age() {
        return this.age;
    }
}
