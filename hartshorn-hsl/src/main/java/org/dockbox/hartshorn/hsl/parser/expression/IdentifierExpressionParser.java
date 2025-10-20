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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

/**
 * Parser for identifiers. Handles the parsing of variable names and array access expressions.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class IdentifierExpressionParser implements ExpressionParser {
    @Override
    public Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain) {
        if (parser.match(parser.tokenRegistry().literals().identifier())) {
            Token next = parser.peek();
            TokenTypePair array = parser.tokenRegistry().tokenPairs().array();
            if (next.type() == array.open()) {
                Token name = parser.previous();
                validator.expect(array.open());
                Expression index = parser.expression();
                validator.expect(array.close());
                return new ArrayGetExpression(name, index);
            }
            return new VariableExpression(parser.previous());
        }
        return chain.next(parser, validator);
    }

}
