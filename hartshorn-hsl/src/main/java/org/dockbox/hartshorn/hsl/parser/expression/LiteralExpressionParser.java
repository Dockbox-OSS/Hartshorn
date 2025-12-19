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

package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Parser for literal expressions, such as boolean values, null, numbers, strings, characters, the
 * <code>this</code> keyword, and the <code>super</code> keyword.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LiteralExpressionParser implements ExpressionParser {
    @Override
    public Expression parse(
        TokenParser parser,
        TokenStepValidator validator,
        ExpressionParserChain chain
    ) {
        if (parser.match(LiteralTokenType.FALSE)) {
            return new LiteralExpression(parser.peek(), false);
        }
        if (parser.match(LiteralTokenType.TRUE)) {
            return new LiteralExpression(parser.peek(), true);
        }
        if (parser.match(LiteralTokenType.NULL)) {
            return new LiteralExpression(parser.peek(), null);
        }
        if (parser.match(ObjectTokenType.THIS)) {
            return new ThisExpression(parser.previous());
        }
        if (parser.match(LiteralTokenType.NUMBER, LiteralTokenType.STRING, LiteralTokenType.CHAR)) {
            return new LiteralExpression(parser.peek(), parser.previous().literal());
        }
        if (parser.match(ObjectTokenType.SUPER)) {
            return this.superExpression(parser, validator);
        }
        return chain.next(parser, validator);
    }

    private SuperExpression superExpression(TokenParser parser, TokenStepValidator validator) {
        Token keyword = parser.previous();
        validator.expectAfter(BaseTokenType.DOT, ObjectTokenType.SUPER);
        TokenType identifier = parser.tokenRegistry().literals().identifier();
        Token method = validator.expect(identifier, "super class method name");
        return new SuperExpression(keyword, method);
    }
}
