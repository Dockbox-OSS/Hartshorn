package org.dockbox.hartshorn.properties;

public sealed interface Property permits ValueProperty, ListProperty, ObjectProperty {

    String name();
}
