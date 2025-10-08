package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ExpressionParser {

    Expression parse(TokenParser parser, TokenStepValidator validator, ExpressionParserChain chain);
}
