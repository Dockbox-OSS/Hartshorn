package org.dockbox.hartshorn.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Address {

    private String street;
    private String city;
    @Id
    private int number;

    public Address(final String city, final String street, final int number) {
        this.street = street;
        this.city = city;
        this.number = number;
    }

    public Address() {
    }

    public String street() {
        return this.street;
    }

    public String city() {
        return this.city;
    }

    public int number() {
        return this.number;
    }
}
