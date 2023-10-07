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

package org.dockbox.hartshorn.hsl.parser.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.function.TriFunction;
import org.dockbox.hartshorn.util.option.Option;

public class ComplexExpressionParser {

    private final TokenParser parser;
    private final TokenStepValidator validator;
    private final TokenType[] assignmentTokens;
    private final Supplier<? extends Expression> fallbackExpression;

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    public ComplexExpressionParser(final TokenParser parser, final TokenStepValidator validator, final Supplier<? extends Expression> fallbackExpression) {
        this.parser = parser;
        this.validator = validator;
        this.assignmentTokens = parser.tokenRegistry()
                .tokenTypes(token -> token.assignsWith() != null)
                .toArray(TokenType[]::new);
        this.fallbackExpression = fallbackExpression;
    }

    public Expression parse() {
        final Expression expression = this.elvisExp();

        if (this.parser.match(BaseTokenType.EQUAL)) {
            final Token equals = this.parser.previous();
            final Expression value = this.parser.expression();

            if (expression instanceof VariableExpression variableExpression) {
                final Token name = variableExpression.name();
                return new AssignExpression(name, value);
            }
            else if (expression instanceof final ArrayGetExpression arrayGetExpression) {
                final Token name = arrayGetExpression.name();
                return new ArraySetExpression(name, arrayGetExpression.index(), value);
            }
            else if (expression instanceof final GetExpression getExpression) {
                return new SetExpression(getExpression.object(), getExpression.name(), value);
            }
            throw new ScriptEvaluationError("Invalid assignment target.", Phase.PARSING, equals);
        }
        return expression;
    }

    private Expression elvisExp() {
        final Expression expression = this.ternaryExp();
        if (this.parser.match(ConditionTokenType.ELVIS)) {
            final Token elvis = this.parser.previous();
            final Expression rightExp = this.ternaryExp();
            return new ElvisExpression(expression, elvis, rightExp);
        }
        return expression;
    }

    private Expression ternaryExp() {
        final Expression expression = this.bitwise();

        if (this.parser.match(BaseTokenType.QUESTION_MARK)) {
            final Token question = this.parser.previous();
            final Expression firstExp = this.logical();
            final Token colon = this.parser.peek();
            if (this.parser.match(BaseTokenType.COLON)) {
                final Expression secondExp = this.logical();
                return new TernaryExpression(expression, question, firstExp, colon, secondExp);
            }
            throw new ScriptEvaluationError("Expected expression after " + BaseTokenType.COLON.representation(), Phase.PARSING, colon);
        }
        return expression;
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
                BitwiseTokenType.SHIFT_LEFT,
                BitwiseTokenType.SHIFT_RIGHT,
                BitwiseTokenType.LOGICAL_SHIFT_RIGHT,
                BitwiseTokenType.BITWISE_OR,
                BitwiseTokenType.BITWISE_AND
        );
    }

    private Expression logical() {
        return this.logicalOrBitwise(this::equality, LogicalExpression::new,
                BitwiseTokenType.XOR,
                ConditionTokenType.OR,
                ConditionTokenType.AND
        );
    }

    private Expression equality() {
        return this.logicalOrBitwise(this::range, BinaryExpression::new,
                ConditionTokenType.BANG_EQUAL,
                ConditionTokenType.EQUAL_EQUAL
        );
    }

    private Expression range() {
        return this.logicalOrBitwise(this::logicalAssign, RangeExpression::new, LoopTokenType.RANGE);
    }

    private Expression logicalAssign() {
        return this.logicalOrBitwise(this::parsePrefixFunctionCall, (left, token, right) -> {
            if (left instanceof VariableExpression variable) {
                return new LogicalAssignExpression(variable.name(), token, right);
            }
            else {
                throw new ScriptEvaluationError("Invalid assignment target.", Phase.PARSING, token);
            }
        }, this.assignmentTokens);
    }

    private Expression parsePrefixFunctionCall() {
        if (this.parser.check(LiteralTokenType.IDENTIFIER) && this.hasPrefixFunction(this.parser.peek())) {
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
        Expression expression = this.addition();

        while (this.parser.match(ConditionTokenType.GREATER, ConditionTokenType.GREATER_EQUAL, ConditionTokenType.LESS, ConditionTokenType.LESS_EQUAL)) {
            final Token operator = this.parser.previous();
            final Expression right = this.addition();
            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression addition() {
        Expression expression = this.multiplication();
        while (this.parser.match(ArithmeticTokenType.MINUS, ArithmeticTokenType.PLUS)) {
            final Token operator = this.parser.previous();
            final Expression right = this.multiplication();
            expression = new BinaryExpression(expression, operator, right);
        }
        return expression;
    }

    private Expression multiplication() {
        Expression expression = this.parseInfixExpressions();

        while (this.parser.match(ArithmeticTokenType.SLASH, ArithmeticTokenType.STAR, ArithmeticTokenType.MODULO)) {
            final Token operator = this.parser.previous();
            final Expression right = this.parseInfixExpressions();
            expression = new BinaryExpression(expression, operator, right);
        }
        return expression;
    }

    private Expression parseInfixExpressions() {
        Expression expression = this.unary();

        while (this.parser.check(LiteralTokenType.IDENTIFIER) && this.hasInfixFunction(this.parser.peek())) {
            final Token operator = this.parser.advance();
            final Expression right = this.unary();
            expression = new InfixExpression(expression, operator, right);
        }
        return expression;
    }

    private boolean hasInfixFunction(final Token name) {
        return this.containedInFunctionContext(context -> context.infixFunctions().contains(name.lexeme()));
    }

    private Expression unary() {
        if (this.parser.match(BaseTokenType.BANG, ArithmeticTokenType.MINUS, ArithmeticTokenType.PLUS_PLUS, ArithmeticTokenType.MINUS_MINUS, BitwiseTokenType.COMPLEMENT)) {
            final Token operator = this.parser.previous();
            final Expression right = this.unary();
            return new UnaryExpression(operator, right);
        }
        return this.call();
    }

    private Expression call() {
        Expression expression = this.primary();
        if (expression != null) {
            while(true) {
                if(this.parser.match(this.parser.tokenRegistry().tokenPairs().parameters().open())) {
                    expression = this.finishCall(expression);
                }
                else if(this.parser.match(BaseTokenType.DOT)) {
                    final Token name = this.parser.consume(LiteralTokenType.IDENTIFIER, "Expected property name after '.'.");
                    expression = new GetExpression(name, expression);
                }
                else if(this.parser.match(ArithmeticTokenType.PLUS_PLUS, ArithmeticTokenType.MINUS_MINUS)) {
                    final Token operator = this.parser.previous();
                    expression = new PostfixExpression(operator, expression);
                }
                else {
                    break;
                }
            }
        }
        return expression;
    }

    private Expression finishCall(final Expression callee) {
        final List<Expression> arguments = new ArrayList<>();
        final Token parenOpen = this.parser.previous();
        // For zero arguments
        if (!this.parser.check(this.parser.tokenRegistry().tokenPairs().parameters().close())) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    throw new ScriptEvaluationError("Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " arguments.", Phase.PARSING, this.parser.peek());
                }
                arguments.add(this.parser.expression());
            }
            while (this.parser.match(BaseTokenType.COMMA));
        }
        final Token parenClose = this.validator.expectAfter(this.parser.tokenRegistry().tokenPairs().parameters().close(), "arguments");
        return new FunctionCallExpression(callee, parenOpen, parenClose, arguments);
    }

    private Expression primary() {
        if (this.parser.match(LiteralTokenType.FALSE)) {
            return new LiteralExpression(this.parser.peek(), false);
        }
        if (this.parser.match(LiteralTokenType.TRUE)) {
            return new LiteralExpression(this.parser.peek(), true);
        }
        if (this.parser.match(LiteralTokenType.NULL)) {
            return new LiteralExpression(this.parser.peek(), null);
        }
        if (this.parser.match(ObjectTokenType.THIS)) {
            return new ThisExpression(this.parser.previous());
        }
        if (this.parser.match(LiteralTokenType.NUMBER, LiteralTokenType.STRING, LiteralTokenType.CHAR)) {
            return new LiteralExpression(this.parser.peek(), this.parser.previous().literal());
        }
        if (this.parser.match(LiteralTokenType.IDENTIFIER)) {
            return this.identifierExpression();
        }
        if (this.parser.match(this.parser.tokenRegistry().tokenPairs().parameters().open())) {
            return this.groupingExpression();
        }
        if (this.parser.match(ObjectTokenType.SUPER)) {
            return this.superExpression();
        }
        if (this.parser.match(this.parser.tokenRegistry().tokenPairs().array().open())) {
            return this.complexArray();
        }

        return fallbackExpression.get();
    }

    private SuperExpression superExpression() {
        final Token keyword = this.parser.previous();
        this.validator.expectAfter(BaseTokenType.DOT, ObjectTokenType.SUPER);
        final Token method = this.validator.expect(LiteralTokenType.IDENTIFIER, "super class method name");
        return new SuperExpression(keyword, method);
    }

    private GroupingExpression groupingExpression() {
        final Expression expression = this.parser.expression();
        this.validator.expectAfter(this.parser.tokenRegistry().tokenPairs().parameters().close(), "expression");
        return new GroupingExpression(expression);
    }

    private Expression identifierExpression() {
        final Token next = this.parser.peek();
        TokenTypePair array = this.parser.tokenRegistry().tokenPairs().array();
        if (next.type() == array.open()) {
            final Token name = this.parser.previous();
            this.validator.expect(array.open());
            final Expression index = this.parser.expression();
            this.validator.expect(array.close());
            return new ArrayGetExpression(name, index);
        }
        return new VariableExpression(this.parser.previous());
    }

    private Expression complexArray() {
        final Token open = this.parser.previous();
        final Expression expression = this.parser.expression();

        if (this.parser.match(this.parser.tokenRegistry().tokenPairs().array().close())) {
            final List<Expression> elements = new ArrayList<>();
            elements.add(expression);
            return new ArrayLiteralExpression(open, this.parser.previous(), elements);
        }
        else if (this.parser.match(BaseTokenType.COMMA)) {
            return this.arrayLiteralExpression(open, expression);
        }
        else {
            return this.arrayComprehensionExpression(open, expression);
        }
    }

    private ArrayLiteralExpression arrayLiteralExpression(final Token open, final Expression expression) {
        final List<Expression> elements = new ArrayList<>();
        elements.add(expression);
        do {
            elements.add(this.parser.expression());
        }
        while (this.parser.match(BaseTokenType.COMMA));
        final Token close = this.validator.expectAfter(this.parser.tokenRegistry().tokenPairs().array().close(), "array");
        return new ArrayLiteralExpression(open, close, elements);
    }

    private ArrayComprehensionExpression arrayComprehensionExpression(final Token open, final Expression expression) {
        final Token forToken = this.validator.expectAfter(LoopTokenType.FOR, "expression");
        final Token name = this.validator.expect(LiteralTokenType.IDENTIFIER, "variable name");

        final Token inToken = this.validator.expectAfter(LoopTokenType.IN, "variable name");
        final Expression iterable = this.parser.expression();

        Token ifToken = null;
        Expression condition = null;
        if (this.parser.match(ControlTokenType.IF)) {
            ifToken = this.parser.previous();
            condition = this.parser.expression();
        }

        Token elseToken = null;
        Expression elseExpression = null;
        if (this.parser.match(ControlTokenType.ELSE)) {
            elseToken = this.parser.previous();
            elseExpression = this.parser.expression();
        }

        final Token close = this.validator.expectAfter(this.parser.tokenRegistry().tokenPairs().array().close(), "array");

        return new ArrayComprehensionExpression(iterable, expression, name, forToken, inToken, open, close, ifToken, condition, elseToken, elseExpression);
    }

    private boolean containedInFunctionContext(final Function<FunctionParserContext, Boolean> rule) {
        final Option<FunctionParserContext> context = this.parser.first(FunctionParserContext.class);
        if (context.absent()) {
            return false;
        }

        return rule.apply(context.get());
    }
}
