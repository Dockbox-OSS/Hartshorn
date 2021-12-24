package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Context;
import org.dockbox.hartshorn.core.annotations.inject.Required;

import javax.inject.Inject;

import lombok.Getter;

@Getter
public class SetterInjectedComponent {

    private ComponentType component;
    private SampleContext context;

    @Inject
    public void setComponent(@Required final ComponentType component) {
        this.component = component;
    }

    @Inject
    public void setContext(@Context("setter") final SampleContext context) {
        this.context = context;
    }
}
