package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

public class ExpressionParserChainView implements ExpressionParserChain {

    private final MutableExpressionParserChain chain;
    private final int index;

    public ExpressionParserChainView(MutableExpressionParserChain chain, int index) {
        this.chain = chain;
        this.index = index;
    }

    @Override
    public Expression next(TokenParser parser, TokenStepValidator validator) {
        if (this.index < chain.parsers().size()) {
            ExpressionParser current = chain.parsers().get(this.index);
            ExpressionParserChain view = new ExpressionParserChainView(chain, this.index + 1);
            return current.parse(parser, validator, view);
        }
        throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.EXPECTED_EXPRESSION, parser.peek())
                .at(parser.peek())
                .build();
    }
}
