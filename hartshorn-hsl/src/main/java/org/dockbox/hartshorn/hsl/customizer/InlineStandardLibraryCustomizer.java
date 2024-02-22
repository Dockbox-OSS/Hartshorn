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

package org.dockbox.hartshorn.hsl.customizer;

import java.util.List;
import java.util.Map;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class InlineStandardLibraryCustomizer extends AbstractCodeCustomizer {

    public InlineStandardLibraryCustomizer() {
        super(Phase.RESOLVING);
    }

    @Override
    public void call(ScriptContext context) {
        List<Statement> enhancedStatements = this.enhanceModuleStatements(context);
        context.statements(enhancedStatements);
    }

    private List<Statement> enhanceModuleStatements(ScriptContext context) {
        List<Statement> statements = context.statements();
        Map<String, NativeModule> modules = context.interpreter().state().externalModules();
        TokenType identifier = context.tokenRegistry().literals().identifier();
        for (String module : modules.keySet()) {
            Token moduleToken = Token.of(identifier, module)
                    .virtual()
                    .build();
            ModuleStatement moduleStatement = new ModuleStatement(moduleToken);
            statements.addFirst(moduleStatement);
        }
        return statements;
    }
}
