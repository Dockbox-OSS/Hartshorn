package org.dockbox.hartshorn.launchpad.test;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.IntrospectedConditionContext;
import org.dockbox.hartshorn.inject.condition.IntrospectionCondition;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * A condition that matches when the application context contains a binding for
 * {@link ApplicationTestManager}. This component is only ever present when an application is
 * bootstrapped through the Hartshorn Test Suite, and is the preferred way to verify whether the
 * application is being executed in a test environment.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class TestApplicationCondition implements IntrospectionCondition {

    @Override
    public ConditionResult matches(IntrospectedConditionContext context) {
        HierarchicalBinder binder = context.application().defaultBinder();
        BindingHierarchy<ApplicationTestManager> hierarchy = binder.hierarchy(
                ComponentKey.of(ApplicationTestManager.class)
        );
        if (hierarchy.size() != 0) {
            return ConditionResult.matched();
        }
        else {
            return ConditionResult.notFound("test binding", ApplicationContext.class.getName());
        }
    }
}
