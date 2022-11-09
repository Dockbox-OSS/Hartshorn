package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.beans.Bean;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.parser.expression.ArrayExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BitwiseExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ElvisExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.GroupingExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LogicalExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.SuperExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.TernaryExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ThisExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.UnaryExpressionParser;

@Service
@RequiresActivator(UseExpressionValidation.class)
public class HslExpressionBeans {

    public static final String EXPRESSION_BEAN = "expression";

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> arrayExpressionParser() {
        return new ArrayExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> binaryExpressionParser() {
        return new BinaryExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> bitwiseExpressionParser() {
        return new BitwiseExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> elvisExpressionParser() {
        return new ElvisExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<GroupingExpression> groupingExpressionParser() {
        return new GroupingExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> identifierExpressionParser() {
        return new IdentifierExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<LiteralExpression> literalExpressionParser() {
        return new LiteralExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> logicalExpressionParser() {
        return new LogicalExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<SuperExpression> superExpressionParser() {
        return new SuperExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<Expression> ternaryExpressionParser() {
        return new TernaryExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<ThisExpression> thisExpressionParser() {
        return new ThisExpressionParser();
    }

    @Bean(id = EXPRESSION_BEAN)
    public static ExpressionParser<UnaryExpression> unaryExpressionParser() {
        return new UnaryExpressionParser();
    }
}
