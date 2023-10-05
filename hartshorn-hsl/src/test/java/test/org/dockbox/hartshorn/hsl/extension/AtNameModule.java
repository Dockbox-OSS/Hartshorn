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

package test.org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.extension.ExpressionModule;
import org.dockbox.hartshorn.hsl.token.SimpleTokenCharacter;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class AtNameModule implements ExpressionModule<AtNameExpression> {

    public static final TokenType TOKEN_TYPE = SimpleTokenType.builder()
            .characters(SimpleTokenCharacter.of('@', true))
            .tokenName("at")
            .build();

    @Override
    public TokenType tokenType() {
        return TOKEN_TYPE;
    }

    @Override
    public ASTNodeParser<AtNameExpression> parser() {
        return new AtNameExpressionParser(this);
    }

    @Override
    public ASTNodeInterpreter<Object, AtNameExpression> interpreter() {
        return (node, adapter) -> node.identifier().lexeme();
    }
}
