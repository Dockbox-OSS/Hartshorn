package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

public class StandardTokenParser implements TokenParser {

    private int current = 0;
    private List<Token> tokens;

    private final Set<ASTNodeParser<? extends Statement>> statementParsers = ConcurrentHashMap.newKeySet();
    private final Set<ASTNodeParser<? extends Expression>> expressionParsers = ConcurrentHashMap.newKeySet();
    private final TokenStepValidator validator;

    @Inject
    public StandardTokenParser(final TokenStepValidator validator) {
        this.validator = validator;
    }

    @Bound
    public StandardTokenParser(final List<Token> tokens) {
        this.tokens = tokens;
        this.validator = new StandardTokenStepValidator(this);
    }

    @Bound
    public StandardTokenParser(final List<Token> tokens, final TokenStepValidator validator) {
        this.tokens = tokens;
        this.validator = validator;
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
        if (this.isAtEnd()) return false;
        for (final TokenType type : types) {
            if (this.peek().type() == type) return true;
        }
        return false;
    }

    @Override
    public Token advance() {
        if (!this.isAtEnd()) this.current++;
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
    public Token consume(final TokenType type, final String message) {
        if (this.check(type))
            return this.advance();
        if (type != TokenType.SEMICOLON) throw new ScriptEvaluationError(message, Phase.PARSING, this.peek());
        return null;
    }

    @Override
    public Statement statement() {
        for (final ASTNodeParser<? extends Statement> parser : this.statementParsers) {
            final Result<? extends Statement> statement = parser.parse(this, this.validator);
            if (statement.present()) return statement.get();
        }
        return this.expressionStatement();
    }

    @Override
    public ExpressionStatement expressionStatement() {
        final Expression expr = this.expression();
        this.validator.expectAfter(TokenType.SEMICOLON, "expression");
        return new ExpressionStatement(expr);
    }

    @Override
    public Expression expression() {
        for (final ASTNodeParser<? extends Expression> parser : this.expressionParsers) {
            final Result<? extends Expression> expression = parser.parse(this, this.validator);
            if (expression.present()) return expression.get();
        }
        throw new ScriptEvaluationError("Expected expression, but found " + this.tokens.get(this.current), Phase.PARSING, this.peek());
    }

    @Override
    public List<Statement> consume() {
        final List<Statement> statements = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            statements.add(this.statement());
        }
        this.validator.expectAfter(TokenType.RIGHT_BRACE, "block");
        return statements;
    }

    @Override
    public <T extends ASTNode> Set<ASTNodeParser<T>> compatibleParsers(final Class<T> type) {
        if (Statement.class.isAssignableFrom(type)) {
            this.statementParsers.stream()
                    .filter(parser -> parser.types().contains(type))
                    .collect(Collectors.toUnmodifiableSet());
        }
        else if (Expression.class.isAssignableFrom(type)) {
            this.expressionParsers.stream()
                    .filter(parser -> parser.types().contains(type))
                    .collect(Collectors.toUnmodifiableSet());
        }
        return Set.of();
    }
}
