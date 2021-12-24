package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Required;

import javax.inject.Inject;

import lombok.Getter;

public class SetterInjectedComponent {

    @Getter
    @Inject
    private ComponentType component;

    @Inject
    public void setComponent(@Required final ComponentType component) {
        this.component = component;
    }
}
