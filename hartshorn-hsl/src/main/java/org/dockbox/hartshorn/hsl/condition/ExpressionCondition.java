/*
 * Copyright 2019-2024 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Condition which uses the primary {@link ValidateExpressionRuntime} to validate a given expression. The expression
 * is obtained from the {@link org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView} provided through the
 * {@link ConditionContext} for the condition.
 *
 * <p>An expression should follow the standard HSL syntax, and any restrictions introduces by the active
 * {@link ValidateExpressionRuntime}.
 *
 * <p>The runtime is by default always enhanced with the active {@link ApplicationContext}, under the global alias configured
 * in {@link #GLOBAL_APPLICATION_CONTEXT_NAME}. If the {@link ConditionContext} contains a {@link ExpressionConditionContext}
 * which has the {@link ExpressionConditionContext#includeApplicationContext()} set to {@code false}, the application context
 * will not be included in the runtime. Any configured customizers, imports, variables, and modules in the {@link
 * ExpressionConditionContext} are made available to the runtime automatically.
 *
 * <p>Note that if the application context is not explicitly disabled, other variables should not use the {@link
 * #GLOBAL_APPLICATION_CONTEXT_NAME} alias, unless the instance is the same as the active {@link ApplicationContext}. If a
 * conflict arises a warning is logged, but the runtime will proceed as usual.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ExpressionCondition implements Condition {

    private static final Logger LOG = LoggerFactory.getLogger(ExpressionCondition.class);

    /**
     * The global alias for the application context in the expression runtime. This should be the default alias for any
     * application context that is exposed in a HSL runtime, and should not be used for any other variables.
     */
    public static final String GLOBAL_APPLICATION_CONTEXT_NAME = "applicationContext";

    @Override
    public ConditionResult matches(ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresExpression.class)
                .map(condition -> this.calculateResult(context, condition))
                .orElse(ConditionResult.invalidCondition("expression"));
    }

    @NonNull
    private ConditionResult calculateResult(ConditionContext context, RequiresExpression condition) {
        String expression = condition.value();
        ValidateExpressionRuntime runtime = this.createRuntime(context);

        try {
            ScriptContext scriptContext = runtime.interpret(expression);
            boolean result = ValidateExpressionRuntime.valid(scriptContext);
            return ConditionResult.of(result);
        }
        catch (ScriptEvaluationError e) {
            context.applicationContext().handle("Failed to evaluate expression '%s'".formatted(expression), e);
            return ConditionResult.notMatched(e.getMessage());
        }
    }

    /**
     * Creates a new {@link ValidateExpressionRuntime} from the given {@link ConditionContext}. This will customize
     * the runtime based on the presence of a {@link ExpressionConditionContext} and a {@link ProvidedParameterContext}
     * in the given context.
     *
     * @param context the context to create the runtime from
     * @return a new runtime
     */
    protected ValidateExpressionRuntime createRuntime(ConditionContext context) {
        ValidateExpressionRuntime runtime = context.applicationContext().get(ValidateExpressionRuntime.class);
        return this.enhance(runtime, context);
    }

    /**
     * Enhances the given {@link ValidateExpressionRuntime} with the given {@link ConditionContext}. This will customize
     * the runtime based on the presence of a {@link ExpressionConditionContext} and a {@link ProvidedParameterContext}
     * in the given context.
     *
     * <p>To allow customizers to work on parameters and imports, the context is first checked for a {@link ProvidedParameterContext}
     * and the parameters are loaded into the runtime. Then, the context is checked for a {@link ExpressionConditionContext}
     * and the customizers, imports, global variables, and external modules are loaded into the runtime.
     *
     * <p>If no {@link ExpressionConditionContext} is present, the runtime is enhanced with the active {@link ApplicationContext}
     * from the given context. If the context contains a {@link ExpressionConditionContext}, the application context will only
     * be included if the context's {@link ExpressionConditionContext#includeApplicationContext()} returns {@code true}. If the
     * application context is exposed, it will always be exposed under the global alias configured in {@link #GLOBAL_APPLICATION_CONTEXT_NAME}.
     *
     * @param runtime the runtime to enhance
     * @param context the context to enhance the runtime with
     * @return the enhanced runtime
     */
    protected ValidateExpressionRuntime enhance(ValidateExpressionRuntime runtime, ConditionContext context) {
        // Load parameters first, so they can be overwritten by the customizers and imports.
        context.firstContext(ProvidedParameterContext.class).peek(parameterContext -> {
            parameterContext.arguments().forEach((parameter, value) -> runtime.global(parameter.name(), value));
        });

        context.firstContext(ExpressionConditionContext.class).peek(expressionContext -> {

            runtime.customizers(expressionContext.customizers());
            runtime.imports(expressionContext.imports());
            runtime.global(expressionContext.globalVariables());
            runtime.modules(expressionContext.externalModules());

            if (expressionContext.includeApplicationContext()) {
                this.enhanceWithApplicationContext(runtime, context.applicationContext());
            }
        }).onEmpty(() -> {
            this.enhanceWithApplicationContext(runtime, context.applicationContext());
        });
        return runtime;
    }

    private void enhanceWithApplicationContext(ScriptRuntime runtime, ApplicationContext applicationContext) {
        if (runtime.globalVariables().containsKey(GLOBAL_APPLICATION_CONTEXT_NAME)) {
            if (runtime.globalVariables().get(GLOBAL_APPLICATION_CONTEXT_NAME) != applicationContext) {
                LOG.warn("Runtime contains mismatched application context reference");
            }
            // Ignore if the global applicationContext is equal to our active context
        }
        else {
            runtime.global(GLOBAL_APPLICATION_CONTEXT_NAME, applicationContext);
        }
    }
}
