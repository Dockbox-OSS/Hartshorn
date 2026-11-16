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
