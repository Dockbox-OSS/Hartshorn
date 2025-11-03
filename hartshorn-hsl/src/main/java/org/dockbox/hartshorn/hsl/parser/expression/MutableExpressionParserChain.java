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
 * A mutable chain of expression parsers, allowing for dynamic modification of the parser sequence.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface MutableExpressionParserChain extends ExpressionParserChain {

    /**
     * Adds an expression parser to the end of the chain.
     *
     * @param parser the expression parser to add
     */
    void add(ExpressionParser parser);

    /**
     * Adds multiple expression parsers to the end of the chain.
     *
     * @param parsers the list of expression parsers to add
     */
    void addAll(List<ExpressionParser> parsers);

    /**
     * Inserts an expression parser at the specified index in the chain.
     *
     * @param index the index at which to insert the parser
     * @param parser the expression parser to insert
     */
    void insert(int index, ExpressionParser parser);

    /**
     * Inserts multiple expression parsers starting at the specified index in the chain.
     *
     * @param index the index at which to start inserting the parsers
     * @param parsers the list of expression parsers to insert
     */
    void insertAll(int index, List<ExpressionParser> parsers);

    /**
     * Adds an expression parser before the specified parser class in the chain. If the specified class
     * is not found, the parser is added to the end of the chain.
     *
     * @param parser the expression parser to add
     * @param before the class of the expression parser before which to add the new parser
     */
    void addBefore(ExpressionParser parser, Class<? extends ExpressionParser> before);

    /**
     * Adds an expression parser after the specified parser class in the chain. If the specified class
     * is not found, the parser is added to the end of the chain.
     *
     * @param parser the expression parser to add
     * @param after the class of the expression parser after which to add the new parser
     */
    void addAfter(ExpressionParser parser, Class<? extends ExpressionParser> after);

    /**
     * Retrieves the current list of expression parsers in the chain.
     *
     * @return the list of expression parsers
     */
    List<ExpressionParser> parsers();
}
