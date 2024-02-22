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

package org.dockbox.hartshorn.hsl.customizer;

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.CollectionUtilities;

/**
 * Customizer to simplify the validation of standalone expressions. This customizer is used by the
 * {@link org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime} to wrap the script body in
 * a {@link TestStatement} and inline any required {@link NativeModule}s without the need for a
 * standard library or a module header.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class ExpressionCustomizer extends AbstractCodeCustomizer {

    public static final String VALIDATION_ID = ScriptContext.createSafeRuntimeVariable("validation");

    public ExpressionCustomizer() {
        super(Phase.RESOLVING);
    }

    @Override
    public void call(ScriptContext context) {
        context.runtime().interpreterOptions().enableAssertions(true);
        List<Statement> statements = context.statements();
        this.verifyIsExpression(statements);
        List<Statement> testStatements = this.enhanceTestStatement(statements);
        context.statements(testStatements);
    }

    private void verifyIsExpression(List<Statement> statements) {
        Statement lastStatement = CollectionUtilities.last(statements);
        if (!(lastStatement instanceof ExpressionStatement || lastStatement instanceof ReturnStatement)) {
            throw new ScriptEvaluationError("Expected last statement to be a valid expression or return statement, but found " + lastStatement.getClass().getSimpleName(), Phase.RESOLVING, lastStatement);
        }
    }

    private List<Statement> enhanceTestStatement(List<Statement> statements) {
        Statement lastStatement = CollectionUtilities.last(statements);

        if (!(lastStatement instanceof ReturnStatement)) {
            ExpressionStatement statement = (ExpressionStatement) lastStatement;
            Token returnToken = Token.of(ControlTokenType.RETURN)
                    .lexeme(VALIDATION_ID)
                    .virtual()
                    .build();

            ReturnStatement returnStatement = new ReturnStatement(returnToken, statement.expression());
            statements.set(statements.size() - 1, returnStatement);
        }

        Token testToken = new Token(LiteralTokenType.STRING, VALIDATION_ID, VALIDATION_ID, -1, -1);
        BlockStatement blockStatement = new BlockStatement(testToken, statements);
        TestStatement testStatement = new TestStatement(testToken, blockStatement);

        List<Statement> validationStatements = new ArrayList<>();
        validationStatements.add(testStatement);

        return validationStatements;
    }
}
