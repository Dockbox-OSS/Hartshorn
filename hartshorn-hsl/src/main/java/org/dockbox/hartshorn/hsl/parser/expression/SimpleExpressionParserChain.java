package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;

import java.util.ArrayList;
import java.util.List;

public class SimpleExpressionParserChain implements MutableExpressionParserChain {

    private final List<ExpressionParser> parsers = new ArrayList<>();

    @Override
    public Expression next(TokenParser parser, TokenStepValidator validator) {
        ExpressionParserChainView view = new ExpressionParserChainView(this, 0);
        return view.next(parser, validator);
    }

    @Override
    public void add(ExpressionParser parser) {
        this.parsers.add(parser);
    }

    @Override
    public void addAll(List<ExpressionParser> parsers) {
        this.parsers.addAll(parsers);
    }

    @Override
    public void insert(int index, ExpressionParser parser) {
        this.parsers.add(index, parser);
    }

    @Override
    public void insertAll(int index, List<ExpressionParser> parsers) {
        this.parsers.addAll(index, parsers);
    }

    @Override
    public void addBefore(ExpressionParser parser, Class<? extends ExpressionParser> before) {
        int index = find(before);
        this.parsers.add(index, parser);
    }

    private int find(Class<? extends ExpressionParser> before) {
        int index = this.parsers.size();
        for (int i = 0; i < this.parsers.size(); i++) {
            if (before.isInstance(this.parsers.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void addAfter(ExpressionParser parser, Class<? extends ExpressionParser> after) {
        int index = find(after) + 1;
        this.parsers.add(index, parser);
    }

    @Override
    public List<ExpressionParser> parsers() {
        return List.copyOf(this.parsers);
    }
}
