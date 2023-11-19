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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.util.Customizer;

/**
 * A functional interface for customizing the {@link TokenParser}. This interface is similar to {@link Customizer} but
 * is specific to the {@link TokenParser}. This customizer can be used to configure the {@link TokenParser} with
 * additional {@link org.dockbox.hartshorn.hsl.ast.statement.Statement} {@link ASTNodeParser parsers}.
 *
 * @see TokenParser
 * @see TokenParser#statementParser(ASTNodeParser)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ParserCustomizer extends Customizer<TokenParser> {

    @Override
    default ParserCustomizer compose(Customizer<TokenParser> before) {
        // Override for type, for convenience.
        return parser -> {
            before.configure(parser);
            this.configure(parser);
        };
    }
}
