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

package org.dockbox.hartshorn.hsl.parser.statement;

import java.util.Set;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.util.option.Option;

public abstract class AbstractBodyStatementParser<T extends ASTNode> implements ASTNodeParser<T> {

    protected BlockStatement blockStatement(String afterStatement, ASTNode at, TokenParser parser, TokenStepValidator validator) {
        Set<ASTNodeParser<BlockStatement>> parsers = parser.compatibleParsers(BlockStatement.class);
        if (parsers.isEmpty()) {
            throw new ScriptEvaluationError("No BlockStatement parsers found", Phase.PARSING, at);
        }

        for (ASTNodeParser<BlockStatement> nodeParser : parsers) {
            Option<? extends BlockStatement> statement = nodeParser.parse(parser, validator);
            if (statement.present()) {
                return statement.get();
            }
        }

        throw new ScriptEvaluationError("Expected block after " + afterStatement, Phase.PARSING, parser.peek());
    }
}
