package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Required;

import javax.inject.Inject;

import lombok.Getter;

public class SetterInjectedComponentWithAbsentBinding {

    @Getter
    @Inject
    private NotImplemented object;

    @Inject
    public void setObject(@Required final NotImplemented object) {
        this.object = object;
    }
}
