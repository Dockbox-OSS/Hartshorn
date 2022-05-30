package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;

public class ConditionContext extends DefaultApplicationAwareContext {

    private final AnnotatedElementContext<?> annotatedElementContext;
    private final RequiresCondition condition;

    public ConditionContext(final ApplicationContext applicationContext, final AnnotatedElementContext<?> annotatedElementContext, final RequiresCondition condition) {
        super(applicationContext);
        this.annotatedElementContext = annotatedElementContext;
        this.condition = condition;
    }

    public AnnotatedElementContext<?> annotatedElementContext() {
        return this.annotatedElementContext;
    }

    public RequiresCondition condition() {
        return this.condition;
    }
}
