package org.dockbox.hartshorn.component.condition;

@FunctionalInterface
public interface Condition {
    ConditionResult matches(ConditionContext context);
}
