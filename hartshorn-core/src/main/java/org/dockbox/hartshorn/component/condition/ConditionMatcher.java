package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import javax.inject.Inject;

@Component
public class ConditionMatcher {

    @Inject
    private ApplicationContext applicationContext;

    public boolean match(final AnnotatedElementContext annotatedElementContext) {
        final Result<RequiresCondition> annotation = annotatedElementContext.annotation(RequiresCondition.class);
        if (annotation.absent()) {
            return true;
        }

        final RequiresCondition condition = annotation.get();
        final Class<? extends Condition> conditionClass = condition.value();

        final Condition conditionInstance = this.applicationContext.get(conditionClass);
        return this.match(annotatedElementContext, conditionInstance, condition);
    }

    public boolean match(final AnnotatedElementContext<?> element, final Condition condition, final RequiresCondition requiresCondition) {
        final ConditionContext context = new ConditionContext(this.applicationContext, element, requiresCondition);
        final ConditionResult result = condition.matches(context);
        if (!result.matches() && requiresCondition.failOnNoMatch()) {
            throw new IllegalStateException("Condition failed (" + TypeContext.of(condition).name() + "): " + result.message());
        }
        return result.matches();
    }

}
