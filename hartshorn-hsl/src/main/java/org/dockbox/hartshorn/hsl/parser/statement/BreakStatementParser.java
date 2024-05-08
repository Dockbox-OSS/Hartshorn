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

import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * A parser for the 'break' statement, using the {@link ControlTokenType#BREAK} token type.
 *
 * @see BreakStatement
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class BreakStatementParser extends AbstractControlStatementParser<BreakStatement> {

    @Override
    protected TokenType keyword() {
        return ControlTokenType.BREAK;
    }

    @Override
    protected BreakStatement create(Token keyword) {
        return new BreakStatement(keyword);
    }

    @Override
    public Set<Class<? extends BreakStatement>> types() {
        return Set.of(BreakStatement.class);
    }
}
