package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Binds(Person.class)
@AllArgsConstructor(onConstructor_ = @Bound)
@Getter
public class PersonImpl implements Person{
    private final String name;
    private final int age;
}
