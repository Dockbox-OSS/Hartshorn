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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of an expression parser chain. Maintains a list of expression parsers and
 * delegates parsing to them in order.
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
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
        int index = this.find(before);
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
        int index = this.find(after) + 1;
        this.parsers.add(index, parser);
    }

    @Override
    public List<ExpressionParser> parsers() {
        return List.copyOf(this.parsers);
    }
}
