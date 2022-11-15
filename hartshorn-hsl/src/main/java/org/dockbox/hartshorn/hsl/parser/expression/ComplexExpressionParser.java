/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.function.TriFunction;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ComplexExpressionParser {

    private final TokenParser parser;
    private final TokenStepValidator validator;

    private static final int MAX_NUM_OF_ARGUMENTS = 8;
    private static final TokenType[] ASSIGNMENT_TOKENS = allTokensMatching(t -> t.assignsWith() != null);

    public ComplexExpressionParser(final TokenParser parser, final TokenStepValidator validator) {
        this.parser = parser;
        this.validator = validator;
    }

    private static TokenType[] allTokensMatching(final Predicate<TokenType> predicate) {
        return Arrays.stream(TokenType.values())
                .filter(predicate)
                .toArray(TokenType[]::new);
    }

    public Expression parse() {
        final Expression expr = this.elvisExp();

        if (this.parser.match(TokenType.EQUAL)) {
            final Token equals = this.parser.previous();
            final Expression value = this.parse();

            if (expr instanceof VariableExpression) {
                final Token name = ((VariableExpression) expr).name();
                return new AssignExpression(name, value);
            }
            else if (expr instanceof final ArrayGetExpression arrayGetExpression) {
                final Token name = arrayGetExpression.name();
                return new ArraySetExpression(name, arrayGetExpression.index(), value);
            }
            else if (expr instanceof final GetExpression get) {
                return new SetExpression(get.object(), get.name(), value);
            }
            throw new ScriptEvaluationError("Invalid assignment target.", Phase.PARSING, equals);
        }
        return expr;
    }

    private Expression elvisExp() {
        final Expression expr = this.ternaryExp();
        if (this.parser.match(TokenType.ELVIS)) {
            final Token elvis = this.parser.previous();
            final Expression rightExp = this.ternaryExp();
            return new ElvisExpression(expr, elvis, rightExp);
        }
        return expr;
    }

    private Expression ternaryExp() {
        final Expression expr = this.bitwise();

        if (this.parser.match(TokenType.QUESTION_MARK)) {
            final Token question = this.parser.previous();
            final Expression firstExp = this.logical();
            final Token colon = this.parser.peek();
            if (this.parser.match(TokenType.COLON)) {
                final Expression secondExp = this.logical();
                return new TernaryExpression(expr, question, firstExp, colon, secondExp);
            }
            throw new ScriptEvaluationError("Expected expression after " + TokenType.COLON.representation(), Phase.PARSING, colon);
        }
        return expr;
    }

    private Expression logicalOrBitwise(final Supplier<Expression> next, final TriFunction<Expression, Token, Expression, Expression> step, final TokenType... whileMatching) {
        Expression expression = next.get();
        while(this.parser.match(whileMatching)) {
            final Token operator = this.parser.previous();
            final Expression right = next.get();
            expression = step.accept(expression, operator, right);
        }
        return expression;
    }

    private Expression bitwise() {
        return this.logicalOrBitwise(this::logical, BitwiseExpression::new,
                TokenType.SHIFT_LEFT,
                TokenType.SHIFT_RIGHT,
                TokenType.LOGICAL_SHIFT_RIGHT,
                TokenType.BITWISE_OR,
                TokenType.BITWISE_AND
        );
    }

    private Expression logical() {
        return this.logicalOrBitwise(this::equality, LogicalExpression::new,
                TokenType.OR,
                TokenType.XOR,
                TokenType.AND
        );
    }

    private Expression equality() {
        return this.logicalOrBitwise(this::range, BinaryExpression::new,
                TokenType.BANG_EQUAL,
                TokenType.EQUAL_EQUAL
        );
    }

    private Expression range() {
        return this.logicalOrBitwise(this::logicalAssign, RangeExpression::new, TokenType.RANGE);
    }

    private Expression logicalAssign() {
        return this.logicalOrBitwise(this::parsePrefixFunctionCall, (left, token, right) -> {
            if (left instanceof VariableExpression variable) {
                return new LogicalAssignExpression(variable.name(), token, right);
            }
            else {
                throw new ScriptEvaluationError("Invalid assignment target.", Phase.PARSING, token);
            }
        }, ASSIGNMENT_TOKENS);
    }

    private Expression parsePrefixFunctionCall() {
        if (this.parser.check(TokenType.IDENTIFIER) && this.hasPrefixFunction(this.parser.peek())) {
            final Token prefixFunctionName = this.parser.advance();
            final Expression right = this.comparison();
            return new PrefixExpression(prefixFunctionName, right);
        }
        return this.comparison();
    }

    private boolean hasPrefixFunction(final Token name) {
        return this.containedInFunctionContext(context -> context.prefixFunctions().contains(name.lexeme()));
    }

    private Expression comparison() {
        Expression expr = this.addition();

        while (this.parser.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            final Token operator = this.parser.previous();
            final Expression right = this.addition();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression addition() {
        Expression expr = this.multiplication();
        while (this.parser.match(TokenType.MINUS, TokenType.PLUS)) {
            final Token operator = this.parser.previous();
            final Expression right = this.multiplication();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression multiplication() {
        Expression expr = this.parseInfixExpressions();

        while (this.parser.match(TokenType.SLASH, TokenType.STAR, TokenType.MODULO)) {
            final Token operator = this.parser.previous();
            final Expression right = this.parseInfixExpressions();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression parseInfixExpressions() {
        Expression expr = this.unary();

        while (this.parser.check(TokenType.IDENTIFIER) && this.hasInfixFunction(this.parser.peek())) {
            final Token operator = this.parser.advance();
            final Expression right = this.unary();
            expr = new InfixExpression(expr, operator, right);
        }
        return expr;
    }

    private boolean hasInfixFunction(final Token name) {
        return this.containedInFunctionContext(context -> context.infixFunctions().contains(name.lexeme()));
    }

    private Expression unary() {
        if (this.parser.match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS_PLUS, TokenType.MINUS_MINUS, TokenType.COMPLEMENT)) {
            final Token operator = this.parser.previous();
            final Expression right = this.unary();
            return new UnaryExpression(operator, right);
        }
        return this.call();
    }

    private Expression call() {
        Expression expr = this.primary();
        while (true) {
            if (this.parser.match(TokenType.LEFT_PAREN)) {
                expr = this.finishCall(expr);
            }
            else if (this.parser.match(TokenType.DOT)) {
                final Token name = this.parser.consume(TokenType.IDENTIFIER, "Expected property name after '.'.");
                expr = new GetExpression(name, expr);
            }
            else if (this.parser.match(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {
                final Token operator = this.parser.previous();
                expr = new PostfixExpression(operator, expr);
            }
            else {
                break;
            }
        }
        return expr;
    }

    private Expression finishCall(final Expression callee) {
        final List<Expression> arguments = new ArrayList<>();
        final Token parenOpen = this.parser.previous();
        // For zero arguments
        if (!this.parser.check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    throw new ScriptEvaluationError("Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " arguments.", Phase.PARSING, this.parser.peek());
                }
                arguments.add(this.parse());
            }
            while (this.parser.match(TokenType.COMMA));
        }
        final Token parenClose = this.validator.expectAfter(TokenType.RIGHT_PAREN, "arguments");
        return new FunctionCallExpression(callee, parenOpen, parenClose, arguments);
    }

    private Expression primary() {
        if (this.parser.match(TokenType.FALSE)) return new LiteralExpression(this.parser.peek(), false);
        if (this.parser.match(TokenType.TRUE)) return new LiteralExpression(this.parser.peek(), true);
        if (this.parser.match(TokenType.NULL)) return new LiteralExpression(this.parser.peek(), null);
        if (this.parser.match(TokenType.THIS)) return new ThisExpression(this.parser.previous());
        if (this.parser.match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR)) return new LiteralExpression(this.parser.peek(), this.parser.previous().literal());
        if (this.parser.match(TokenType.IDENTIFIER)) return this.identifierExpression();
        if (this.parser.match(TokenType.LEFT_PAREN)) return this.groupingExpression();
        if (this.parser.match(TokenType.SUPER)) return this.superExpression();
        if (this.parser.match(TokenType.ARRAY_OPEN)) return this.complexArray();

        throw new ScriptEvaluationError("Expected expression, but found " + this.parser.peek(), Phase.PARSING, this.parser.peek());
    }

    private SuperExpression superExpression() {
        final Token keyword = this.parser.previous();
        this.validator.expectAfter(TokenType.DOT, TokenType.SUPER);
        final Token method = this.validator.expect(TokenType.IDENTIFIER, "super class method name");
        return new SuperExpression(keyword, method);
    }

    private GroupingExpression groupingExpression() {
        final Expression expr = this.parse();
        this.validator.expectAfter(TokenType.RIGHT_PAREN, "expression");
        return new GroupingExpression(expr);
    }

    private Expression identifierExpression() {
        final Token next = this.parser.peek();
        if (next.type() == TokenType.ARRAY_OPEN) {
            final Token name = this.parser.previous();
            this.validator.expect(TokenType.ARRAY_OPEN);
            final Expression index = this.parse();
            this.validator.expect(TokenType.ARRAY_CLOSE);
            return new ArrayGetExpression(name, index);
        }
        return new VariableExpression(this.parser.previous());
    }

    private Expression complexArray() {
        final Token open = this.parser.previous();
        final Expression expr = this.parse();

        if (this.parser.match(TokenType.ARRAY_CLOSE)) {
            final List<Expression> elements = new ArrayList<>();
            elements.add(expr);
            return new ArrayLiteralExpression(open, this.parser.previous(), elements);
        }
        else if (this.parser.match(TokenType.COMMA)) return this.arrayLiteralExpression(open, expr);
        else return this.arrayComprehensionExpression(open, expr);
    }

    private ArrayLiteralExpression arrayLiteralExpression(final Token open, final Expression expr) {
        final List<Expression> elements = new ArrayList<>();
        elements.add(expr);
        do {
            elements.add(this.parse());
        }
        while (this.parser.match(TokenType.COMMA));
        final Token close = this.validator.expectAfter(TokenType.ARRAY_CLOSE, "array");
        return new ArrayLiteralExpression(open, close, elements);
    }

    private ArrayComprehensionExpression arrayComprehensionExpression(final Token open, final Expression expr) {
        final Token forToken = this.validator.expectAfter(TokenType.FOR, "expression");
        final Token name = this.validator.expect(TokenType.IDENTIFIER, "variable name");

        final Token inToken = this.validator.expectAfter(TokenType.IN, "variable name");
        final Expression iterable = this.parse();

        Token ifToken = null;
        Expression condition = null;
        if (this.parser.match(TokenType.IF)) {
            ifToken = this.parser.previous();
            condition = this.parse();
        }

        Token elseToken = null;
        Expression elseExpr = null;
        if (this.parser.match(TokenType.ELSE)) {
            elseToken = this.parser.previous();
            elseExpr = this.parse();
        }

        final Token close = this.validator.expectAfter(TokenType.ARRAY_CLOSE, "array");

        return new ArrayComprehensionExpression(iterable, expr, name, forToken, inToken, open, close, ifToken, condition, elseToken, elseExpr);
    }

    private boolean containedInFunctionContext(final Function<FunctionParserContext, Boolean> rule) {
        final Option<FunctionParserContext> context = this.parser.first(FunctionParserContext.class);
        if (context.absent()) return false;

        return rule.apply(context.get());
    }
}
