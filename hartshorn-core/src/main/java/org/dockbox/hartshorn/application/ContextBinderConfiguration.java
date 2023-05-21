package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.binding.Binder;

public interface ContextBinderConfiguration<C extends Context> {

    void configureBindings(C context, Binder binder);
}
