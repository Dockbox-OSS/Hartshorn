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

import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

/**
 * Abstract base class for expression parsers that deal with function operators.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractFunctionOperatorExpressionParser implements ExpressionParser {

    /**
     * Checks if the current parsing context is within a function context and applies the given rule
     * to it.
     *
     * @param parser the token parser
     * @param rule the rule to apply to the function context
     *
     * @return true if the rule applies, false otherwise
     */
    protected boolean containedInFunctionContext(
        TokenParser parser,
        Function<FunctionParserContext, Boolean> rule
    ) {
        Option<FunctionParserContext> context = parser.firstContext(FunctionParserContext.class);
        if (context.absent()) {
            return false;
        }

        return rule.apply(context.get());
    }
}
