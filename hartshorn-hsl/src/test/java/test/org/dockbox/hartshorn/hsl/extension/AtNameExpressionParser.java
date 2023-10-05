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

import java.util.Set;

import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.option.Option;

class AtNameExpressionParser implements ASTNodeParser<AtNameExpression> {

    private final AtNameModule atNameModule;

    public AtNameExpressionParser(AtNameModule atNameModule) {
        this.atNameModule = atNameModule;
    }

    @Override
    public Option<? extends AtNameExpression> parse(TokenParser parser, TokenStepValidator validator) {
        if(parser.match(atNameModule.tokenType())) {
            Token identifier = validator.expectAfter(LiteralTokenType.IDENTIFIER, "component lookup");
            return Option.of(new AtNameExpression(parser.previous(), atNameModule, identifier));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends AtNameExpression>> types() {
        return Set.of(AtNameExpression.class);
    }
}
