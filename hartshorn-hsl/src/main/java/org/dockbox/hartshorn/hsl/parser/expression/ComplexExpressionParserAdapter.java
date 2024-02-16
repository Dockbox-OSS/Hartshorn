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

package org.dockbox.hartshorn.hsl.parser.expression;

import java.util.Set;
import java.util.function.Supplier;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO #748: Rewrite docs
 *
 * <p>This parser is a wrapper around the {@link ComplexExpressionParser} and is used to parse
 * the tokens into a syntax tree. The {@link ComplexExpressionParser} is a recursive descent
 * parser which is able to parse complex expressions.
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public class ComplexExpressionParserAdapter implements ExpressionParser {

    private final Supplier<? extends Expression> fallbackExpression;

    public ComplexExpressionParserAdapter(Supplier<? extends Expression> fallbackExpression) {
        this.fallbackExpression = fallbackExpression;
    }

    @Override
    public Option<? extends Expression> parse(TokenParser parser, TokenStepValidator validator) {
        ComplexExpressionParser expressionParser = new ComplexExpressionParser(parser, validator, fallbackExpression);
        return Option.of(expressionParser.parse());
    }

    @Override
    public Set<Class<? extends Expression>> types() {
        return Set.of(Expression.class);
    }
}
