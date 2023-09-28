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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.ScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.condition.ExpressionConditionContext;
import org.dockbox.hartshorn.hsl.customizer.CodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.InlineStandardLibraryCustomizer;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.modules.StandardLibrary;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;

import java.util.Map;
import java.util.Set;

/**
 * The default runtime implementation, which follows the evaluation phases and order as
 * defined in {@link Phase}. Each phase can be customized using appropriate
 * {@link CodeCustomizer}s, to modify the in- or output of the current or previous phase.
 *
 * <p>The executor for each phase is obtained from the given {@link ApplicationContext},
 * to allow each executor to be customized through standard DI principles.
 *
 * @author Guus Lieben
 * @since 0.4.12
 * @see ExpressionConditionContext
 */
public class StandardRuntime extends AbstractScriptRuntime {

    public StandardRuntime(final ApplicationContext applicationContext, final ScriptComponentFactory factory) {
        this(applicationContext, factory, Set.of());
    }

    public StandardRuntime(final ApplicationContext applicationContext, final ScriptComponentFactory factory,
                           final Set<ASTNodeParser<? extends Statement>> statementParsers) {
        super(applicationContext, factory, statementParsers);
        this.customizer(new InlineStandardLibraryCustomizer());
    }

    @Override
    protected Map<String, NativeModule> standardLibraries() {
        return StandardLibrary.asModules(this.applicationContext());
    }
}
