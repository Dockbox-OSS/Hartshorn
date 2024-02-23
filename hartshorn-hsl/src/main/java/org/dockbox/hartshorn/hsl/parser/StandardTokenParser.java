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
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

/**
 * A parser for the tokens of a script. This parser is used to parse the tokens of a script into an
 * abstract syntax tree (AST). This implementation delegates the parsing of statements and expressions
 * to a set of registered {@link ASTNodeParser parsers}.
 *
 * <p>The primary function of this implementation directly is the tracking of tokens and the current
 * position in the token stream. It also provides a set of methods to parse the tokens into an AST.
 *
 * <p>When parsing a script, the parser will attempt to parse the tokens into a list of statements. As
 * this parser tracks the state directly, it is not thread-safe. It is expected that a new instance is
 * created for each parsing operation.
 *
 * @since 0.5.0
 *
 * @see ASTNodeParser
 *
 * @author Guus Lieben
 */
public class StandardTokenParser extends DefaultProvisionContext implements TokenParser {

    private int current = 0;
    private final List<Token> tokens;

    private final Set<ASTNodeParser<? extends Statement>> statementParsers = ConcurrentHashMap.newKeySet();
    private final Set<ASTNodeParser<? extends Expression>> expressionParsers = ConcurrentHashMap.newKeySet();
    private final TokenStepValidator validator;
    private final ExpressionParser expressionParser;
    private final TokenRegistry tokenRegistry;

    @Inject
    public StandardTokenParser(TokenRegistry tokenRegistry) {
        this(tokenRegistry, new ArrayList<>());
    }

    public StandardTokenParser(TokenRegistry tokenRegistry, List<Token> tokens) {
        this.tokenRegistry = tokenRegistry;
        this.expressionParser = new ComplexExpressionParserAdapter(this::parseModuleExpression);
        this.validator = new StandardTokenStepValidator(this);
        this.tokens = new LinkedList<>(tokens);
    }

    private Expression parseModuleExpression() {
        for(ASTNodeParser<? extends Expression> parser : this.expressionParsers()) {
            Option<? extends Expression> result = parser.parse(this, this.validator);
            if (result.present()) {
                return result.get();
            }
        }
        return null;
    }

    @Override
    public TokenRegistry tokenRegistry() {
        return this.tokenRegistry;
    }

    @Override
    public StandardTokenParser statementParser(ASTNodeParser<? extends Statement> parser) {
        if (parser != null) {
            validateParser(parser, Statement.class);
            this.statementParsers.add(parser);
        }
        return this;
    }

    @Override
    public TokenParser expressionParser(ASTNodeParser<? extends Expression> parser) {
        if (parser != null) {
            validateParser(parser, Expression.class);
            this.expressionParsers.add(parser);
        }
        return this;
    }

    /**
     * Returns the set of statement parsers that are currently registered with this parser.
     *
     * @return the set of statement parsers that are currently registered with this parser
     */
    public Set<ASTNodeParser<? extends Statement>> statementParsers() {
        return statementParsers;
    }

    /**
     * Returns the set of expression parsers that are currently registered with this parser.
     *
     * @return the set of expression parsers that are currently registered with this parser
     */
    public Set<ASTNodeParser<? extends Expression>> expressionParsers() {
        return expressionParsers;
    }

    private static <T extends ASTNode> void validateParser(ASTNodeParser<? extends T> parser, Class<T> expectedType) {
        for(Class<? extends T> type : parser.types()) {
            if (!expectedType.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Parser " + parser.getClass().getName() + " indicated potential yield of type type " + type.getName() + " which is not a child of " + expectedType.getSimpleName());
            }
        }
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
    public Token consume(TokenType type, String message) {
        if (this.check(type)) {
            return this.advance();
        }
        if (type != this.tokenRegistry().statementEnd()) {
            throw new ScriptEvaluationError(message, Phase.PARSING, this.peek());
        }
        return null;
    }

    @Override
    public Statement statement() {
        for (ASTNodeParser<? extends Statement> parser : this.statementParsers) {
            Option<? extends Statement> statement = parser.parse(this, this.validator);
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
        Expression expression = this.expression();
        this.validator.expectAfter(this.tokenRegistry().statementEnd(), "expression");
        return new ExpressionStatement(expression);
    }

    @Override
    public Expression expression() {
        return this.expressionParser.parse(this, this.validator)
                .orElseThrow(() -> new ScriptEvaluationError("Expected expression, but found " + this.peek(), Phase.PARSING, this.peek()));
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
