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
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;

public class StandardTokenParser extends DefaultProvisionContext implements TokenParser {

    private int current = 0;
    private final List<Token> tokens;

    private final Set<ASTNodeParser<? extends Statement>> statementParsers = ConcurrentHashMap.newKeySet();
    private final TokenStepValidator validator;
    private final ExpressionParser expressionParser;

    @Inject
    public StandardTokenParser() {
        this(new ArrayList<>());
    }

    @Inject
    public StandardTokenParser(ExpressionParser parser, TokenStepValidator validator) {
        this(new ArrayList<>(), parser, validator);
    }

    public StandardTokenParser(List<Token> tokens) {
        this.expressionParser = new ComplexExpressionParserAdapter();
        this.validator = new StandardTokenStepValidator(this);
        this.tokens = tokens;
    }

    public StandardTokenParser(List<Token> tokens, ExpressionParser parser, TokenStepValidator validator) {
        this.tokens = tokens;
        this.expressionParser = parser;
        this.validator = validator;
    }

    @Override
    public StandardTokenParser statementParser(ASTNodeParser<? extends Statement> parser) {
        if (parser != null) {
            for(Class<? extends Statement> type : parser.types()) {
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
        List<Statement> statements = new ArrayList<>();
        while (!this.isAtEnd()) {
            statements.add(this.statement());
        }
        return statements;
    }

    @Override
    public boolean match(TokenType... types) {
        return this.find(types) != null;
    }

    @Override
    public Token find(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                Token token = this.peek();
                this.advance();
                return token;
            }
        }
        return null;
    }

    @Override
    public boolean check(TokenType... types) {
        if (this.isAtEnd()) {
            return false;
        }
        for (TokenType type : types) {
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
        return this.peek().type() == TokenType.EOF;
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
    public Token consume(TokenType type, String message) {
        if (this.check(type)) {
            return this.advance();
        }
        if (type != TokenType.SEMICOLON) {
            throw new ScriptEvaluationError(message, Phase.PARSING, this.peek());
        }
        return null;
    }

    @Override
    public Statement statement() {
        for (ASTNodeParser<? extends Statement> parser : this.statementParsers) {
            Option<? extends Statement> statement = parser.parse(this, this.validator)
                    .attempt(ScriptEvaluationError.class)
                    .rethrow();
            if (statement.present()) {
                return statement.get();
            }
        }

        TokenType type = this.peek().type();
        if (type.standaloneStatement()) {
            throw new ScriptEvaluationError("Unsupported standalone statement type: " + type, Phase.PARSING, this.peek());
        }

        return this.expressionStatement();
    }

    @Override
    public ExpressionStatement expressionStatement() {
        Expression expr = this.expression();
        this.validator.expectAfter(TokenType.SEMICOLON, "expression");
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
    public <T extends Statement> Set<ASTNodeParser<T>> compatibleParsers(Class<T> type) {
        return this.compatibleParserStream(type)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T extends Statement> Option<ASTNodeParser<T>> firstCompatibleParser(Class<T> type) {
        return Option.of(this.compatibleParserStream(type).findFirst());
    }

    private <T extends Statement> Stream<ASTNodeParser<T>> compatibleParserStream(Class<T> type) {
        if (Statement.class.isAssignableFrom(type)) {
            return this.compatibleParserStream(this.statementParsers, type);
        }
        return Stream.empty();
    }

    private <T extends ASTNode, N extends ASTNode> Stream<ASTNodeParser<T>> compatibleParserStream(Collection<? extends ASTNodeParser<? extends N>> parsers, Class<T> type) {
        return parsers.stream()
                .filter(parser -> parser.types().contains(type))
                .map(parser -> TypeUtils.adjustWildcards(parser, ASTNodeParser.class));
    }
}
