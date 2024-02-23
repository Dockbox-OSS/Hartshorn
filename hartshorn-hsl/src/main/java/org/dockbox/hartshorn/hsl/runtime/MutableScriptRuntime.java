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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.condition.ConditionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.util.Customizer;

/**
 * A mutable version of {@link ScriptRuntime} that allows for additional customization of the
 * runtime. This interface is often a requirement for {@link CodeCustomizer}s that need to
 * customize the runtime.
 *
 * <p>Note that {@link ScriptRuntime} itself is already partially customizable through the
 * implementation of {@link ConditionContext}, which allows for the customization of the
 * runtime context.
 *
 * @since 0.5.0
 *
 * @see ScriptRuntime
 *
 * @author Guus Lieben
 */
public interface MutableScriptRuntime extends ScriptRuntime {

    /**
     * Adds an expression parser to the runtime, which can be used to parse expressions in the
     * script.
     *
     * @param parser the parser to add
     *
     * @see org.dockbox.hartshorn.hsl.parser.TokenParser#expressionParser(ASTNodeParser)
     */
    void expressionParser(ASTNodeParser<? extends Expression> parser);

    /**
     * Adds a statement parser to the runtime, which can be used to parse statements in the
     * script.
     *
     * @param parser the parser to add
     *
     * @see org.dockbox.hartshorn.hsl.parser.TokenParser#statementParser(ASTNodeParser)
     */
    void statementParser(ASTNodeParser<? extends Statement> parser);

    /**
     * Adds a customizer to the runtime, which can be used to customize the script runtime
     * context before phase execution.
     *
     * @param customizer the customizer to add
     */
    void scriptContextCustomizer(Customizer<ScriptContext> customizer);
}
