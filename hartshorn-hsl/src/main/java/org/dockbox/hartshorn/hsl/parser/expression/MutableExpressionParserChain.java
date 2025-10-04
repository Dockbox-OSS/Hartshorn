package org.dockbox.hartshorn.hsl.parser.expression;

import java.util.List;

public interface MutableExpressionParserChain extends ExpressionParserChain {

    void add(ExpressionParser parser);

    void addAll(List<ExpressionParser> parsers);

    void insert(int index, ExpressionParser parser);

    void insertAll(int index, List<ExpressionParser> parsers);

    void addBefore(ExpressionParser parser, Class<? extends ExpressionParser> before);

    void addAfter(ExpressionParser parser, Class<? extends ExpressionParser> after);

    List<ExpressionParser> parsers();
}
