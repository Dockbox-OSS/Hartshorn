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

/**
 * A parser for the tokens of a script. This parser is used to parse the tokens of a script into an
 * abstract syntax tree (AST).
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface TokenParser extends Context {

    /**
     * The registry that contains the definition of all available tokens.
     *
     * @return the registry that contains the definition of all available tokens.
     */
    TokenRegistry tokenRegistry();

    /**
     * Adds a statement parser to the runtime, which can be used to parse statements in the
     * script.
     *
     * @param parser the parser to add
     *
     * @see org.dockbox.hartshorn.hsl.parser.TokenParser#statementParser(ASTNodeParser)
     */
    TokenParser statementParser(ASTNodeParser<? extends Statement> parser);

    /**
     * Adds an expression parser to the runtime, which can be used to parse expressions in the
     * script.
     *
     * @param parser the parser to add
     *
     * @see org.dockbox.hartshorn.hsl.parser.TokenParser#expressionParser(ASTNodeParser)
     */
    TokenParser expressionParser(ASTNodeParser<? extends Expression> parser);

    /**
     * Parses the tokens of a script into an abstract syntax tree (AST).
     *
     * @return the abstract syntax tree (AST) that represents the script.
     */
    List<Statement> parse();

    /**
     * Attempts to find and consume a token of the given type. If the token is found, it is consumed
     * and {@code true} is returned. If the token is not found, {@code false} is returned.
     *
     * @param types the types of tokens to find
     * @return {@code true} if the token is found and consumed, {@code false} otherwise
     */
    boolean match(TokenType... types);

    /**
     * Attempts to find a token of the given type. If the token is found, it is returned. If the token
     * is not found, {@code null} is returned.
     *
     * @param types the types of tokens to find
     * @return the token that was found, or {@code null} if no token was found
     */
    Token find(TokenType... types);

    /**
     * Checks if the current token's type is one of the given types. If the current token's type is
     * one of the given types, {@code true} is returned. If the current token's type is not one of the
     * given types, {@code false} is returned. The token is not consumed.
     *
     * @param types the types of tokens to check
     * @return {@code true} if the current token's type is one of the given types, {@code false} otherwise
     */
    boolean check(TokenType... types);

    /**
     * Advances the parser to the next token and returns the token that was consumed.
     *
     * @return the token that was consumed
     */
    Token advance();

    /**
     * Checks if the parser has reached the end of the tokens.
     *
     * @return {@code true} if the parser has reached the end of the tokens, {@code false} otherwise
     */
    boolean isAtEnd();

    /**
     * Returns the current token without consuming it.
     *
     * @return the current token
     */
    Token peek();

    /**
     * Returns the token that came before the current token.
     *
     * @return the token that came before the current token
     */
    Token previous();

    /**
     * Attempts to find and consume a token of the given type. If the token is found, it is consumed
     * and returned. If the token is not found and the given type is not the ending of a statement,
     * an exception is thrown. Statement terminators are allowed to be absent, as they are optional
     * in the HSL syntax. The type of the terminating token is determined by the active {@link
     * TokenRegistry token registry's} {@link TokenRegistry#statementEnd()} method.
     *
     * @param type the type of token to consume
     * @param message the message to include in the exception if the token is not found
     * @return the token that was consumed
     */
    Token consume(TokenType type, String message);

    /**
     * Attempts to parse a statement from the current and following tokens. If a statement is found,
     * it is parsed and returned. If no compatible statement is found, an exception is thrown. This
     * also attempts to parse a {@link ExpressionStatement} if no other statement is found.
     *
     * @return the statement that was parsed
     */
    Statement statement();

    /**
     * Attempts to parse an expression statement from the current and following tokens. If an
     * expression statement is found, it is parsed and returned. If no compatible expression statement
     * is found, an exception is thrown.
     *
     * @return the expression statement that was parsed
     */
    ExpressionStatement expressionStatement();

    /**
     * Attempts to parse an expression from the current and following tokens. If an expression is
     * found, it is parsed and returned. If no compatible expression is found, an exception is thrown.
     *
     * @return the expression that was parsed
     */
    Expression expression();

    /**
     * Looks up all parsers that are compatible with the given type. A parser is compatible with a type
     * if the {@link ASTNodeParser#types() parser's types} contains the given type.
     *
     * @param <T> the type of statement to find compatible parsers for
     * @param type the type of statement to find compatible parsers for
     *
     * @return all parsers that are compatible with the given type
     */
    <T extends Statement> Set<ASTNodeParser<T>> compatibleParsers(Class<T> type);

    /**
     * Looks up the first parser that is compatible with the given type. A parser is compatible with a
     * type if the {@link ASTNodeParser#types() parser's types} contains the given type.
     *
     * @param <T> the type of statement to find a compatible parser for
     * @param type the type of statement to find a compatible parser for
     *
     * @return the first parser that is compatible with the given type, or {@link Option#empty()} if no
     * compatible parser is found
     */
    <T extends Statement> Option<ASTNodeParser<T>> firstCompatibleParser(Class<T> type);
}
