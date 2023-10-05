package test.org.dockbox.hartshorn.hsl.extension;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExpressionScript;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.RuntimeExtensionCodeCustomizer;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest
@UseExpressionValidation
public class LanguageExtensionTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void name() {
        ExpressionScript script = ExpressionScript.of(this.applicationContext, "@hello == \"hello\" && @world == \"world\"");

        AtNameModule module = new AtNameModule();
        RuntimeExtensionCodeCustomizer customizer = new RuntimeExtensionCodeCustomizer();
        customizer.expressionModules(module);
        script.runtime().customizer(customizer);

        Assertions.assertTrue(script.valid());

        List<Statement> statements = ValidateExpressionRuntime.actualStatements(script);
        Assertions.assertEquals(1, statements.size());

        Statement statement = statements.get(0);
        Assertions.assertTrue(statement instanceof ReturnStatement);

        ReturnStatement returnStatement = (ReturnStatement) statement;
        Expression expression = returnStatement.expression();

        Assertions.assertTrue(expression instanceof LogicalExpression);
        LogicalExpression logicalExpression = (LogicalExpression) expression;

        Expression helloExpression = logicalExpression.leftExpression();
        Expression worldExpression = logicalExpression.rightExpression();

        assertExpressionContainsAtName(helloExpression);
        assertExpressionContainsAtName(worldExpression);
    }

    private static void assertExpressionContainsAtName(Expression expression) {
        Assertions.assertTrue(expression instanceof BinaryExpression);
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        Expression leftExpression = binaryExpression.leftExpression();
        Expression rightExpression = binaryExpression.rightExpression();

        Assertions.assertTrue(leftExpression instanceof AtNameExpression);
        Assertions.assertTrue(rightExpression instanceof LiteralExpression);
    }

}
