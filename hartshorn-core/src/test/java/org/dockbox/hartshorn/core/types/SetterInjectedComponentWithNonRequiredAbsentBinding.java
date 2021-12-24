package org.dockbox.hartshorn.core.types;

import javax.inject.Inject;

import lombok.Getter;

public class SetterInjectedComponentWithNonRequiredAbsentBinding {

    @Getter
    @Inject
    private NotImplemented object;

    @Inject
    // Person is bound, so this will result in a null value
    public void setObject(final NotImplemented object) {
        this.object = object;
    }
}
