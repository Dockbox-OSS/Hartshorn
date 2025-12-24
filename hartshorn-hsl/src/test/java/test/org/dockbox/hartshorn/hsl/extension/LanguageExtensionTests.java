/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.hsl.extension;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.RuntimeExtensionCodeCustomizer;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest
@UseExpressionValidation
class LanguageExtensionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void languageExtensionCanInject() {
        ExpressionScript script = ExpressionScript.of(
            this.applicationContext,
            "(@hello == \"hello\") && (@world == \"world\")"
        );

        AtNameModule module = new AtNameModule();
        RuntimeExtensionCodeCustomizer customizer = new RuntimeExtensionCodeCustomizer();
        customizer.expressionModules(module);
        script.runtime().customizer(customizer);

        assertThat(script.valid()).isTrue();
        assertThat(module.resolverAccessed()).isTrue();

        List<Statement> statements = ValidateExpressionRuntime.actualStatements(script);
        assertThat(statements).hasSize(1);

        Statement statement = statements.getFirst();
        ReturnStatement returnStatement = assertThat(statement)
                .asInstanceOf(InstanceOfAssertFactories.type(ReturnStatement.class))
                .actual();
        Expression expression = returnStatement.expression();

        LogicalExpression logicalExpression = assertThat(expression)
                .asInstanceOf(InstanceOfAssertFactories.type(LogicalExpression.class))
                .actual();
        Expression helloExpression = logicalExpression.leftExpression();
        Expression worldExpression = logicalExpression.rightExpression();

        assertExpressionContainsAtName(helloExpression);
        assertExpressionContainsAtName(worldExpression);
    }

    private static void assertExpressionContainsAtName(Expression expression) {
        GroupingExpression groupingExpression = assertThat(expression)
                .asInstanceOf(InstanceOfAssertFactories.type(GroupingExpression.class))
                .actual();
        BinaryExpression binaryExpression = assertThat(groupingExpression.expression())
                .asInstanceOf(InstanceOfAssertFactories.type(BinaryExpression.class))
                .actual();

        Expression leftExpression = binaryExpression.leftExpression();
        Expression rightExpression = binaryExpression.rightExpression();

        assertThat(leftExpression).isInstanceOf(AtNameExpression.class);
        assertThat(rightExpression).isInstanceOf(LiteralExpression.class);
    }
}
