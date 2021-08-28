package org.dockbox.hartshorn.events.parents;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.Context;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.events.EventBus;
import org.jetbrains.annotations.NotNull;

public abstract class ContextCarrierEvent extends DefaultContext implements Event {

    private ApplicationContext context;

    @Override
    public ApplicationContext applicationContext() {
        if (this.context == null) {
            throw new IllegalStateException("Event instance has not been enhanced with application context");
        }
        return this.context;
    }

    @Override
    public <C extends Context> void add(final C context) {
        if (context instanceof ApplicationContext applicationContext) {
            this.with(applicationContext);
        }
        super.add(context);
    }

    @Override
    public ContextCarrierEvent with(final ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Cannot enhance with non-existent application context");
        }
        this.context = context;
        return this;
    }

    @Override
    public @NotNull Event post() {
        this.applicationContext().get(EventBus.class).post(this);
        return this;
    }
}
