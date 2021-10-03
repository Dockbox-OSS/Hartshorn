package org.dockbox.hartshorn.demo.caching.domain;

import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.di.context.ApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple bean type which can carry {@link org.dockbox.hartshorn.api.keys.Key keys}, as it implements
 * {@link KeyHolder}. {@link org.dockbox.hartshorn.api.keys.Key Keys} are not persistent, and depend on
 * a managed data store.
 */
@Getter
@AllArgsConstructor
public class User implements KeyHolder<User> {

    // For the KeyHolder
    private final ApplicationContext applicationContext;
    private final String name;
    private final int age;
}
