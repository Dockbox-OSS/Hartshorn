package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.data.annotations.ConfigurationObject;

@ConfigurationObject(prefix = "user")
public class SampleSetterConfigurationObject {

    private String name;
    private int age;

    public String name() {
        return name;
    }

    // Classic bean-style setter
    public void setName(String name) {
        this.name = name + "!";
    }

    public int age() {
        return age;
    }

    // Fluent-style setter, private
    private SampleSetterConfigurationObject setAge(int age) {
        this.age = age + 10;
        return this;
    }
}
