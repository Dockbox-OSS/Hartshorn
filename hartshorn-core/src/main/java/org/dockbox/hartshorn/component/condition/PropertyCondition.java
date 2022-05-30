package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.util.Result;

public class PropertyCondition implements Condition {

    @Override
    public ConditionResult matches(final ConditionContext context) {
        return context.annotatedElementContext().annotation(RequiresProperty.class).map(condition -> {
            final String name = condition.name();
            final Result<String> result = context.applicationContext().property(name);
            if (result.absent()) {
                if (condition.matchIfMissing()) {
                    return ConditionResult.matched();
                }
                return ConditionResult.notFound("property", name);
            }

            final String value = result.get();
            if (condition.matchIfMissing()) {
                return ConditionResult.found("property", name, value);
            }

            if (condition.withValue().isEmpty()) {
                return ConditionResult.matched();
            }
            return condition.withValue().equals(value) ? ConditionResult.matched() : ConditionResult.notEqual("property", condition.withValue(), value);
        }).orElse(() -> ConditionResult.invalidCondition("property")).get();
    }
}
