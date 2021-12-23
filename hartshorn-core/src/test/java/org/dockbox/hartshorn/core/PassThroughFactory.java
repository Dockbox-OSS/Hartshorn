package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.types.Person;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

@Service
public interface PassThroughFactory {
    @Factory
    Person create(String name, int age);

    default Person create(final String name) {
        return this.create(name, -1);
    }
}
