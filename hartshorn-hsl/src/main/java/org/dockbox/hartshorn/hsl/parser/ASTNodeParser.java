/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.hsl.parser;

import java.util.Set;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A parser for a specific type of AST node. This parser is used to parse the tokens of a script into an
 * abstract syntax tree (AST). This will often be a delegate function of the {@link TokenParser}.
 *
 * @param <T> The type of the AST node that this parser can parse.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ASTNodeParser<T extends ASTNode> {

    /**
     * Parses the tokens of a script into an abstract syntax tree (AST) node. This method will return
     * an {@link Option} that contains the parsed AST node, or an empty option if the parser was unable
     * to parse the tokens.
     *
     * @param parser the token parser that is used to parse the tokens.
     * @param validator the token step validator that is used to validate the tokens.
     * @return an {@link Option} that contains the parsed AST node, or an empty option if the parser was unable to parse the tokens.
     * @throws ScriptEvaluationError if an error occurs during the parsing of the tokens.
     */
    Option<? extends T> parse(TokenParser parser, TokenStepValidator validator) throws ScriptEvaluationError;

    /**
     * The types of AST nodes that this parser can produce. This should include all possible subtypes of
     * the AST node that this parser can produce.
     *
     * @return the types of AST nodes that this parser can produce.
     */
    Set<Class<? extends T>> types();
}
