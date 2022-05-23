package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.data.annotations.ConfigurationObject;

@ConfigurationObject(prefix = "user")
public class SampleConfigurationObject {

    private String name;
    private int age;

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }
}
