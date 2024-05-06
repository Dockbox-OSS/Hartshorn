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

package org.dockbox.hartshorn.hsl.interpreter;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface Interpreter {

    Object evaluate(Expression expression);

    void execute(Statement statement);

    void execute(BlockStatement blockStatement, VariableScope localVariableScope);

    void execute(List<Statement> statementList, VariableScope localVariableScope);

    Object lookUpVariable(Token name, Expression expression);

    void resolve(Expression expression, int depth);

    VariableScope visitingScope();

    VariableScope global();

    void withNextScope(Runnable runnable);

    void enterScope(VariableScope scope);

    Integer distance(Expression expression);

    ApplicationContext applicationContext();

    ExecutionOptions executionOptions();

    Interpreter executionOptions(ExecutionOptions options);

    InterpreterState state();

    TokenRegistry tokenRegistry();

    ResultCollector resultCollector();

    void interpret(List<Statement> statements);

    void restore();
}
