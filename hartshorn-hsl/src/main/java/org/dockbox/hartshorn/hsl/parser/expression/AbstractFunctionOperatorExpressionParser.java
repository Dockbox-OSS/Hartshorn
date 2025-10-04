package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

public abstract class AbstractFunctionOperatorExpressionParser implements ExpressionParser {

    protected boolean containedInFunctionContext(TokenParser parser, Function<FunctionParserContext, Boolean> rule) {
        Option<FunctionParserContext> context = parser.firstContext(FunctionParserContext.class);
        if (context.absent()) {
            return false;
        }

        return rule.apply(context.get());
    }
}
