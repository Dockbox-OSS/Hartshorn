package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.Configurer;

import java.util.function.Consumer;

public abstract class ApplicationConfigurer extends DefaultContext implements Configurer {

    // TODO: Determine how we want to pass this context for the application setup
    public static final ContextKey<ApplicationSetupContext> CONTEXT = ContextKey.builder(ApplicationSetupContext.class)
            .name("application-setup-context")
            .fallback(ApplicationSetupContext::new)
            .build();

    protected ApplicationSetupContext context() {
        return this.first(CONTEXT).orElseThrow(() -> new IllegalStateException("Application setup context not available"));
    }

    protected <T, R extends T> Consumer<R> bind(Class<T> type) {
        return instance -> this.context().cache().put(ComponentKey.of(type), instance);
    }
}
