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

package org.dockbox.hartshorn.hsl.extension;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.hsl.customizer.AbstractCodeCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.MutableScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.MutableTokenRegistry;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;

public class RuntimeExtensionCodeCustomizer extends AbstractCodeCustomizer {

    private final Set<StatementModule<?>> statementModules = ConcurrentHashMap.newKeySet();
    private final Set<ExpressionModule<?>> expressionModules = ConcurrentHashMap.newKeySet();

    public RuntimeExtensionCodeCustomizer() {
        super(Phase.TOKENIZING);
    }

    @Override
    public void call(ScriptContext context) {
        customizeContext(context);
    }

    private void customizeContext(ScriptContext context) {
        if (context.runtime() instanceof MutableScriptRuntime mutableScriptRuntime) {
            this.statementModules.forEach(module -> mutableScriptRuntime.statementParser(module.parser()));
            this.expressionModules.forEach(module -> mutableScriptRuntime.expressionParser(module.parser()));
        }

        TokenRegistry tokenRegistry = context.lexer().tokenRegistry();
        if (tokenRegistry instanceof MutableTokenRegistry mutableTokenRegistry) {
            this.statementModules.forEach(module -> mutableTokenRegistry.addTokens(module.tokenType()));
            this.expressionModules.forEach(module -> mutableTokenRegistry.addTokens(module.tokenType()));
        }
    }

    public void statementModules(StatementModule<?>... modules) {
        this.statementModules.addAll(Set.of(modules));
    }

    public void expressionModules(ExpressionModule<?>... modules) {
        this.expressionModules.addAll(Set.of(modules));
    }
}
