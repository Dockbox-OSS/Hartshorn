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

import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.option.Option;

public interface TokenParser extends Context {

    TokenRegistry tokenRegistry();

    TokenParser statementParser(ASTNodeParser<? extends Statement> parser);

    TokenParser expressionParser(ASTNodeParser<? extends Expression> parser);

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
