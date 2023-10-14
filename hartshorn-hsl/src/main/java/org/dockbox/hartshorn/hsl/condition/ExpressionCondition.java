/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.condition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.condition.Condition;
import org.dockbox.hartshorn.component.condition.ConditionContext;
import org.dockbox.hartshorn.component.condition.ConditionResult;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;

/**
 * Condition which uses the primary {@link ValidateExpressionRuntime} to validate a given expression. The expression
 * is obtained from the {@link org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView} provided through the
 * {@link ConditionContext} for the condition.
 *
 * <p>An expression should follow the standard HSL syntax, and any restrictions introduces by the active
 * {@link ValidateExpressionRuntime}.
 *
 * <p>The runtime is always enhanced with the active {@link ApplicationContext}, under the global alias configured
 * in {@link #GLOBAL_APPLICATION_CONTEXT_NAME}. If the {@link ConditionContext} contains a {@link ExpressionConditionContext}
 * all configured customizers, imports, variables, and modules are made available to the runtime automatically. Other
 * variables should not use the {@link #GLOBAL_APPLICATION_CONTEXT_NAME} alias, unless the instance is the same as the
 * active {@link ApplicationContext}. If a conflict arises a warning is logged, but the runtime will proceed as usual.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class ExpressionCondition implements Condition {

    public static final String GLOBAL_APPLICATION_CONTEXT_NAME = "applicationContext";

    @Override
    public ConditionResult matches(final ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresExpression.class)
                .map(condition -> this.calculateResult(context, condition))
                .orElse(ConditionResult.invalidCondition("expression"));
    }

    @NonNull
    private ConditionResult calculateResult(ConditionContext context, RequiresExpression condition) {
        final String expression = condition.value();
        final ValidateExpressionRuntime runtime = this.createRuntime(context);

        try {
            final ScriptContext scriptContext = runtime.run(expression);
            final boolean result = ValidateExpressionRuntime.valid(scriptContext);
            return ConditionResult.of(result);
        }
        catch (final ScriptEvaluationError e) {
            context.applicationContext().handle("Failed to evaluate expression '%s'".formatted(expression), e);
            return ConditionResult.notMatched(e.getMessage());
        }
    }

    protected ValidateExpressionRuntime createRuntime(final ConditionContext context) {
        final ValidateExpressionRuntime runtime = context.applicationContext().get(ValidateExpressionRuntime.class);
        return this.enhance(runtime, context);
    }

    protected ValidateExpressionRuntime enhance(final ValidateExpressionRuntime runtime, final ConditionContext context) {
        // Load parameters first, so they can be overwritten by the customizers and imports.
        context.first(ProvidedParameterContext.class).peek(parameterContext -> {
            parameterContext.arguments().forEach((parameter, value) -> runtime.global(parameter.name(), value));
        });

        context.first(ExpressionConditionContext.class).peek(expressionContext -> {

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

    private void enhanceWithApplicationContext(final ScriptRuntime runtime, final ApplicationContext applicationContext) {
        if (runtime.globalVariables().containsKey(GLOBAL_APPLICATION_CONTEXT_NAME)) {
            if (runtime.globalVariables().get(GLOBAL_APPLICATION_CONTEXT_NAME) != applicationContext) applicationContext.log().warn("Runtime contains mismatched application context reference");
            // Ignore if the global applicationContext is equal to our active context
        }
        else {
            runtime.global(GLOBAL_APPLICATION_CONTEXT_NAME, applicationContext);
        }
    }
}
