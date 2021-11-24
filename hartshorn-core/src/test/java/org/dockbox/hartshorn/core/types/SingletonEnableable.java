package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.Enableable;

import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class SingletonEnableable implements Enableable {

    @Getter
    private int enabled = 0;

    @Override
    public void enable() {
        this.enabled++;
    }
}
