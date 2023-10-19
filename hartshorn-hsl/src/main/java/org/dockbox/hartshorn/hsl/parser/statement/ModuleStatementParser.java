/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class ModuleStatementParser implements ASTNodeParser<ModuleStatement> {

    @Override
    public Option<ModuleStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.match(TokenType.IMPORT)) {
            Token name = validator.expect(TokenType.IDENTIFIER, "module name");
            validator.expectAfter(TokenType.SEMICOLON, TokenType.IMPORT);
            return Option.of(new ModuleStatement(name));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends ModuleStatement>> types() {
        return Set.of(ModuleStatement.class);
    }
}
