package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.context.DefaultContext;

public final class ComponentRequestContext extends DefaultContext {

    private final InjectionPoint injectionPoint;

    private ComponentRequestContext(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
    }

    public static ComponentRequestContext createForInjectionPoint(InjectionPoint injectionPoint) {
        return new ComponentRequestContext(injectionPoint);
    }

    public static ComponentRequestContext createForComponent() {
        return new ComponentRequestContext(null);
    }

    public InjectionPoint injectionPoint() {
        return this.injectionPoint;
    }

    public boolean isForInjectionPoint() {
        return this.injectionPoint != null;
    }
}
