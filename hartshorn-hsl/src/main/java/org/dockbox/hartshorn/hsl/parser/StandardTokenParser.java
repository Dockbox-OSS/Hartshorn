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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.expression.ComplexExpressionParserAdapter;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

public class StandardTokenParser extends DefaultProvisionContext implements TokenParser {

    private int current = 0;
    private final List<Token> tokens;

    private final Set<ASTNodeParser<? extends Statement>> statementParsers = ConcurrentHashMap.newKeySet();
    private final TokenStepValidator validator;
    private final ExpressionParser expressionParser;
    private final TokenRegistry tokenRegistry;

    @Inject
    public StandardTokenParser(TokenRegistry tokenRegistry) {
        this(tokenRegistry, new ArrayList<>());
    }

    public StandardTokenParser(TokenRegistry tokenRegistry, List<Token> tokens) {
        this.tokenRegistry = tokenRegistry;
        this.expressionParser = new ComplexExpressionParserAdapter();
        this.validator = new StandardTokenStepValidator(this);
        this.tokens = new LinkedList<>(tokens);
    }

    @Override
    public TokenRegistry tokenSet() {
        return this.tokenRegistry;
    }

    @Override
    public StandardTokenParser statementParser(final ASTNodeParser<? extends Statement> parser) {
        if (parser != null) {
            for(final Class<? extends Statement> type : parser.types()) {
                if (!Statement.class.isAssignableFrom(type)) {
                    throw new IllegalArgumentException("Parser " + parser.getClass().getName() + " indicated potential yield of type type " + type.getName() + " which is not a child of Statement");
                }
            }
            this.statementParsers.add(parser);
        }
        return this;
    }

    @Override
    public List<Statement> parse() {
        final List<Statement> statements = new ArrayList<>();
        while (!this.isAtEnd()) {
            statements.add(this.statement());
        }
        return statements;
    }

    @Override
    public boolean match(final TokenType... types) {
        return this.find(types) != null;
    }

    @Override
    public Token find(final TokenType... types) {
        for (final TokenType type : types) {
            if (this.check(type)) {
                final Token token = this.peek();
                this.advance();
                return token;
            }
        }
        return null;
    }

    @Override
    public boolean check(final TokenType... types) {
        if (this.isAtEnd()) {
            return false;
        }
        for (final TokenType type : types) {
            if (this.peek().type() == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Token advance() {
        if (!this.isAtEnd()) {
            this.current++;
        }
        return this.previous();
    }

    @Override
    public boolean isAtEnd() {
        return this.peek().type() == LiteralTokenType.EOF;
    }

    @Override
    public Token peek() {
        return this.tokens.get(this.current);
    }

    @Override
    public Token previous() {
        return this.tokens.get(this.current - 1);
    }

    @Override
    public Token consume(final TokenType type, final String message) {
        if (this.check(type)) {
            return this.advance();
        }
        if (type != BaseTokenType.SEMICOLON) {
            throw new ScriptEvaluationError(message, Phase.PARSING, this.peek());
        }
        return null;
    }

    @Override
    public Statement statement() {
        for (final ASTNodeParser<? extends Statement> parser : this.statementParsers) {
            final Option<? extends Statement> statement = parser.parse(this, this.validator)
                    .attempt(ScriptEvaluationError.class)
                    .rethrow();
            if (statement.present()) {
                return statement.get();
            }
        }

        final TokenType type = this.peek().type();
        if (type.standaloneStatement()) {
            throw new ScriptEvaluationError("Unsupported standalone statement type: " + type, Phase.PARSING, this.peek());
        }

        return this.expressionStatement();
    }

    @Override
    public ExpressionStatement expressionStatement() {
        final Expression expr = this.expression();
        this.validator.expectAfter(BaseTokenType.SEMICOLON, "expression");
        return new ExpressionStatement(expr);
    }

    @Override
    public Expression expression() {
        return this.expressionParser.parse(this, this.validator)
                .attempt(ScriptEvaluationError.class)
                .rethrow()
                .orElseThrow(() -> new ScriptEvaluationError("Unsupported expression type", Phase.PARSING, this.peek()));
    }

    @Override
    public <T extends Statement> Set<ASTNodeParser<T>> compatibleParsers(final Class<T> type) {
        return this.compatibleParserStream(type)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T extends Statement> Option<ASTNodeParser<T>> firstCompatibleParser(final Class<T> type) {
        return Option.of(this.compatibleParserStream(type).findFirst());
    }

    private <T extends Statement> Stream<ASTNodeParser<T>> compatibleParserStream(final Class<T> type) {
        if (Statement.class.isAssignableFrom(type)) {
            return this.compatibleParserStream(this.statementParsers, type);
        }
        return Stream.empty();
    }

    private <T extends ASTNode, N extends ASTNode> Stream<ASTNodeParser<T>> compatibleParserStream(final Collection<? extends ASTNodeParser<? extends N>> parsers, final Class<T> type) {
        return parsers.stream()
                .filter(parser -> parser.types().contains(type))
                .map(parser -> TypeUtils.adjustWildcards(parser, ASTNodeParser.class));
    }
}
