package org.dockbox.hartshorn.component.condition;

public class ClassCondition implements Condition {

    @Override
    public ConditionResult matches(final ConditionContext context) {
        return context.annotatedElementContext().annotation(RequiresClass.class).map(condition -> {
            try {
                Class.forName(condition.name());
                return ConditionResult.matched();
            }
            catch (final ClassNotFoundException e) {
                return ConditionResult.notFound("Class", condition.name());
            }
        }).or(ConditionResult.invalidCondition("class"));
    }
}
