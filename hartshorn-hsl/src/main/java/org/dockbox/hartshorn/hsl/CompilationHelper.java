/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.compiler.ScriptCompiler;
import org.dockbox.hartshorn.hsl.compiler.StandardScriptCompiler;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;

public final class CompilationHelper {

    public static CompiledScript compile(ExecutableScript script) {
        ScriptContext scriptContext = script.resolve();
        ScriptCompiler compiler = null;
        if (script.applicationContext() != null) {
            compiler = script.applicationContext().get(ScriptCompiler.class);
        }
        if (compiler == null) {
            compiler = new StandardScriptCompiler();
        }
        var compiledDelegate = compiler.compile(scriptContext, scriptContext.statements());
        scriptContext.compiledScript(compiledDelegate);
        return compiledDelegate;
    }
}
