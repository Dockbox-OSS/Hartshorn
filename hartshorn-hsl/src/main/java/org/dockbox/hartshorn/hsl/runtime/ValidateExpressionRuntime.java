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

package org.dockbox.hartshorn.hsl.runtime;

import java.util.List;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.ParserCustomizer;
import org.dockbox.hartshorn.hsl.ScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.customizer.ExpressionCustomizer;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A customized runtime specifically targeted at evaluating single expressions. This makes use of
 * the {@link StandardRuntime} to evaluate the script, and customizes the input using the {@link
 * ExpressionCustomizer}.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ValidateExpressionRuntime extends StandardRuntime {

    public ValidateExpressionRuntime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory
    ) {
        this(applicationContext, factory, parser -> {});
    }

    public ValidateExpressionRuntime(
        ApplicationContext applicationContext,
        ScriptComponentFactory factory,
        ParserCustomizer parserCustomizer
    ) {
        super(applicationContext, factory, parserCustomizer);
        this.customizer(new ExpressionCustomizer());
    }

    /**
     * Looks up the validation result in the given {@link ResultCollector}. If there is no validation result
     * present, {@code false} is returned.
     *
     * @param collector The result collector in which the validation result may be stored.
     * @return The validation result, or {@code false} if it does not exist.
     */
    public static boolean valid(ResultCollector collector) {
        Option<Boolean> result = collector.result(ExpressionCustomizer.VALIDATION_ID, Boolean.class);
        return result.test(Boolean::booleanValue);
    }

    /**
     * Resolves all executable statements within the script. As expression scripts are typically wrapped in a
     * {@link TestStatement test statement}, the {@link ExpressionScript#scriptContext() script context} would
     * usually only return that single statement. This method returns the actual statements within the test
     * statement's body.
     *
     * @param expressionScript The expression script to resolve the statements for.
     * @return The actual statements within the script.
     */
    public static List<Statement> actualStatements(ExpressionScript expressionScript) {
        return actualStatements(expressionScript, true);
    }

    /**
     * Resolves all executable statements within the script. As expression scripts are typically wrapped in a
     * {@link TestStatement test statement}, the {@link ExpressionScript#scriptContext() script context} would
     * usually only return that single statement. This method returns the actual statements within the test
     * statement's body.
     *
     * <p>When {@code excludeModuleStatements} is {@code true}, module statements are excluded from the result.
     * This can be useful when the script is to be executed in a context which implicitly imports modules, and
     * the module statements are not directly relevant to the caller.
     *
     * @param expressionScript The expression script to resolve the statements for.
     * @param excludeModuleStatements Whether to exclude module statements from the result.
     * @return The actual statements within the script.
     */
    public static List<Statement> actualStatements(ExpressionScript expressionScript, boolean excludeModuleStatements) {
        List<Statement> statements = expressionScript.scriptContext().statements();
        List<TestStatement> testStatements = statements.stream()
                .filter(statement -> statement instanceof TestStatement)
                .map(TestStatement.class::cast)
                .toList();
        if (testStatements.size() == 1) {
            BlockStatement body = testStatements.getFirst().body();
            return body.statements().stream()
                    .filter(statement -> !(excludeModuleStatements && statement instanceof ModuleStatement))
                    .toList();
        }
        else {
            throw new IllegalArgumentException("Expected exactly one test statement, but found " + testStatements.size());
        }
    }
}
