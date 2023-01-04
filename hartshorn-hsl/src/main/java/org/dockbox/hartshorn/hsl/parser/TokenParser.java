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

package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;

public interface TokenParser extends Context {

    TokenParser statementParser(ASTNodeParser<? extends Statement> parser);

    List<Statement> parse();

    boolean match(TokenType... types);

    Token find(TokenType... types);

    boolean check(TokenType... types);

    Token advance();

    boolean isAtEnd();

    Token peek();

    Token previous();

    Token consume(TokenType type, String message);

    Statement statement();

    ExpressionStatement expressionStatement();

    Expression expression();

    <T extends Statement> Set<ASTNodeParser<T>> compatibleParsers(Class<T> type);

    <T extends Statement> Option<ASTNodeParser<T>> firstCompatibleParser(Class<T> type);
}
