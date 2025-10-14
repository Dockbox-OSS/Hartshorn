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

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface MutableExpressionParserChain extends ExpressionParserChain {

    void add(ExpressionParser parser);

    void addAll(List<ExpressionParser> parsers);

    void insert(int index, ExpressionParser parser);

    void insertAll(int index, List<ExpressionParser> parsers);

    void addBefore(ExpressionParser parser, Class<? extends ExpressionParser> before);

    void addAfter(ExpressionParser parser, Class<? extends ExpressionParser> after);

    List<ExpressionParser> parsers();
}
