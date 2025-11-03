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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * An immutable view of a mutable expression parser chain. This class is used to provide
 * a view of the current state of the parser chain to the expression parsers, without
 * allowing them to modify the underlying chain.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ExpressionParserChainView implements ExpressionParserChain {

    private final MutableExpressionParserChain chain;
    private final int index;

    public ExpressionParserChainView(MutableExpressionParserChain chain, int index) {
        this.chain = chain;
        this.index = index;
    }

    @Override
    public Expression next(TokenParser parser, TokenStepValidator validator) {
        if (this.index < this.chain.parsers().size()) {
            ExpressionParser current = this.chain.parsers().get(this.index);
            ExpressionParserChain view = new ExpressionParserChainView(this.chain, this.index + 1);
            return current.parse(parser, validator, view);
        }
        throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.EXPECTED_EXPRESSION, parser.peek())
                .at(parser.peek())
                .build();
    }
}
