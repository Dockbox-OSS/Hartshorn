/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.MutableExpressionParserChain;
import org.dockbox.hartshorn.hsl.parser.expression.SimpleExpressionParserChain;
import org.dockbox.hartshorn.hsl.parser.statement.StatementParser;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parser for the tokens of a script. This parser is used to parse the tokens of a script into an
 * abstract syntax tree (AST). This implementation delegates the parsing of statements and
 * expressions to a set of registered {@link StatementParser parsers}.
 *
 * <p>The primary function of this implementation directly is the tracking of tokens and the
 * current
 * position in the token stream. It also provides a set of methods to parse the tokens into an AST.
 *
 * <p>When parsing a script, the parser will attempt to parse the tokens into a list of statements.
 * As
 * this parser tracks the state directly, it is not thread-safe. It is expected that a new instance
 * is created for each parsing operation.
 *
 * @see StatementParser
 * 
 * @since 0.4.13
 * 
 * @author Guus Lieben
 */
public class StandardTokenParser extends DefaultFallbackCompatibleContext implements TokenParser {

    private int current = 0;
    private final List<Token> tokens;

    private final Set<StatementParser<? extends Statement>> statementParsers =
        ConcurrentHashMap.newKeySet();
    private final MutableExpressionParserChain expressionParserChain;
    private final TokenStepValidator validator;
    private final TokenRegistry tokenRegistry;

    public StandardTokenParser(TokenRegistry tokenRegistry) {
        this(tokenRegistry, new ArrayList<>());
    }

    public StandardTokenParser(TokenRegistry tokenRegistry, List<Token> tokens) {
        this.tokenRegistry = tokenRegistry;
        this.validator = new StandardTokenStepValidator(this);
        this.tokens = new LinkedList<>(tokens);
        this.expressionParserChain = new SimpleExpressionParserChain();
    }

    @Override
    public TokenRegistry tokenRegistry() {
        return this.tokenRegistry;
    }

    @Override
    public StandardTokenParser statementParser(StatementParser<? extends Statement> parser) {
        if (parser != null) {
            this.statementParsers.add(parser);
        }
        return this;
    }

    @Override
    public TokenParser expressionParser(ExpressionParser parser) {
        if (parser != null) {
            this.expressionParserChain.add(parser);
        }
        return this;
    }

    /**
     * Returns the mutable expression parser chain used by this token parser.
     *
     * @return the mutable expression parser chain
     */
    public MutableExpressionParserChain expressionParserChain() {
        return this.expressionParserChain;
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
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(message)
                .at(this.peek())
                .build();
        }
        return null;
    }

    @Override
    public Statement statement() {
        for (StatementParser<? extends Statement> parser : this.statementParsers) {
            Option<? extends Statement> statement = parser.parse(this, this.validator);
            if (statement.present()) {
                return statement.get();
            }
        }

        TokenType type = this.peek().type();
        if (type.standaloneStatement()) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.UNSUPPORTED_STANDALONE_STATEMENT, type)
                .at(this.peek())
                .build();
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
        Expression expression = this.expressionParserChain.next(this, this.validator);
        if (expression == null) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.EXPECTED_EXPRESSION, this.peek())
                .at(this.peek())
                .build();
        }
        return expression;
    }

    @Override
    public <T extends Statement> Set<StatementParser<T>> compatibleParsers(Class<T> type) {
        return this.compatibleParserStream(type)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T extends Statement> Option<StatementParser<T>> firstCompatibleParser(Class<T> type) {
        return Option.of(this.compatibleParserStream(type).findFirst());
    }

    private <T extends Statement> Stream<StatementParser<T>> compatibleParserStream(Class<T> type) {
        if (Statement.class.isAssignableFrom(type)) {
            return this.compatibleParserStream(this.statementParsers, type);
        }
        return Stream.empty();
    }

    private <T extends Statement, N extends Statement>
    Stream<StatementParser<T>> compatibleParserStream(
        Collection<? extends StatementParser<? extends N>> parsers,
        Class<T> type
    ) {
        return parsers.stream()
            .filter(parser -> parser.types().contains(type))
            .map(parser -> TypeUtils.unchecked(parser, StatementParser.class));
    }
}
