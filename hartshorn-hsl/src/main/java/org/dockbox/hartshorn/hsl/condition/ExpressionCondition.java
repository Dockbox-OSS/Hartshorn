package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.condition.Condition;
import org.dockbox.hartshorn.component.condition.ConditionContext;
import org.dockbox.hartshorn.component.condition.ConditionResult;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;

public class ExpressionCondition implements Condition {

    public static final String GLOBAL_APPLICATION_CONTEXT_NAME = "applicationContext";

    @Override
    public ConditionResult matches(final ConditionContext context) {
        return context.annotatedElementContext().annotation(RequiresExpression.class).map(condition -> {
            final String expression = condition.value();
            final ValidateExpressionRuntime runtime = this.createRuntime(context);
            final ScriptContext scriptContext = runtime.run(expression);

            if (!scriptContext.errors().isEmpty()) {
                return ConditionResult.notMatched(scriptContext.errors().get(0));
            }

            if (!scriptContext.runtimeErrors().isEmpty()) {
                return ConditionResult.notMatched(scriptContext.runtimeErrors().get(0));
            }

            final boolean result = ValidateExpressionRuntime.valid(scriptContext);
            return ConditionResult.of(result);
        }).or(ConditionResult.invalidCondition("expression"));
    }

    protected ValidateExpressionRuntime createRuntime(final ConditionContext context) {
        final ValidateExpressionRuntime runtime = context.applicationContext().get(ValidateExpressionRuntime.class);
        return this.enhance(runtime, context);
    }

    protected ValidateExpressionRuntime enhance(final ValidateExpressionRuntime runtime, final ConditionContext context) {
        context.first(ExpressionConditionContext.class).present(expressionContext -> {

            runtime.customizers(expressionContext.customizers());
            runtime.imports(expressionContext.imports());
            runtime.global(expressionContext.globalVariables());
            runtime.modules(expressionContext.externalModules());

            if (expressionContext.includeApplicationContext()) {
                this.enhanceWithApplicationContext(runtime, context.applicationContext());
            }
        });
        return runtime;
    }

    private void enhanceWithApplicationContext(final ValidateExpressionRuntime runtime, final ApplicationContext applicationContext) {
        if (runtime.globalVariables().containsKey("applicationContext")) {
            if (runtime.globalVariables().get("applicationContext") != applicationContext) applicationContext.log().warn("Runtime contains mismatched application context reference");
            // Ignore if the global applicationContext is equal to our active context
        }
        else {
            runtime.global("applicationContext", applicationContext);
        }
    }
}
