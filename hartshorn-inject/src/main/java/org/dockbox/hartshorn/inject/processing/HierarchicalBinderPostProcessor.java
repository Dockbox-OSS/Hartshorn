package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.scope.Scope;

/**
 * A post processor for hierarchical binders. This can be used to add additional functionality to a binder, or to modify
 * the binder in some way. The post processor will be called for each binder that is created, and will be called
 * in the order of the specified {@link HierarchicalBinderPostProcessor#priority()} value.
 *
 * <p>Explicit component instantiation is not recommended in this processor, as this processor may be called at any time
 * in the application lifecycle, including during the initialization of the application.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HierarchicalBinderPostProcessor {

    void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder);

    int priority();
}
