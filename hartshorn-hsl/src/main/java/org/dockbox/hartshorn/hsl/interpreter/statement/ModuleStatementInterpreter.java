package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.modules.HslLibrary;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;

public class ModuleStatementInterpreter implements ASTNodeInterpreter<Void, ModuleStatement> {
    @Override
    public Void interpret(final ModuleStatement node, final InterpreterAdapter adapter) {
        final String moduleName = node.name().lexeme();
        final NativeModule module = adapter.interpreter().state().externalModules().get(moduleName);
        for (final NativeFunctionStatement supportedFunction : module.supportedFunctions(node.name())) {
            final HslLibrary library = new HslLibrary(supportedFunction, moduleName, module);

            if (adapter.global().contains(supportedFunction.name().lexeme()) && !adapter.interpreter().executionOptions().permitAmbiguousExternalFunctions()) {
                throw new ScriptEvaluationError("Module '" + moduleName + "' contains ambiguous function '" + supportedFunction.name().lexeme() + "' which is already defined in the global scope.", Phase.INTERPRETING, supportedFunction.name());
            }

            adapter.global().define(supportedFunction.name().lexeme(), library);
        }
        return null;
    }
}
