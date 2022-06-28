package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionCustomizer extends AbstractCodeCustomizer {

    public static final String VALIDATION_ID = "$__validation__$";

    @Override
    public List<Statement> resolving(final List<Statement> statements, final Resolver resolver, final Map<String, NativeModule> modules) {
        this.verifyIsExpression(statements);
        final List<Statement> testStatements = this.enhanceTestStatement(statements);
        return this.enhanceModuleStatements(testStatements, modules);
    }

    private void verifyIsExpression(final List<Statement> statements) {
        if (statements.size() != 1) {
            throw new IllegalArgumentException("Expected only one statement, but found " + statements.size());
        }

        final Statement statement = statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            throw new IllegalArgumentException("Expected statement to be a valid expression, but found " + statement);
        }
    }

    private List<Statement> enhanceTestStatement(final List<Statement> statements) {
        final ExpressionStatement statement = (ExpressionStatement) statements.get(0);

        final Token returnToken = new Token(TokenType.RETURN, VALIDATION_ID, -1);
        final ReturnStatement returnStatement = new ReturnStatement(returnToken, statement.expression());

        final Token testToken = new Token(TokenType.STRING, VALIDATION_ID, VALIDATION_ID, -1);
        final TestStatement testStatement = new TestStatement(testToken, statements, returnStatement);

        final List<Statement> validationStatements = new ArrayList<>();
        validationStatements.add(testStatement);

        return validationStatements;
    }

    private List<Statement> enhanceModuleStatements(final List<Statement> statements, final Map<String, NativeModule> modules) {
        for (final String module : modules.keySet()) {
            final Token moduleToken = new Token(TokenType.IDENTIFIER, module, -1);
            final ModuleStatement moduleStatement = new ModuleStatement(moduleToken);
            statements.add(0, moduleStatement);
        }
        return statements;
    }
}
