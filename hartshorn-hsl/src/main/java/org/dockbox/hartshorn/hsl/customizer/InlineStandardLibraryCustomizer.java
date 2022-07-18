package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.List;
import java.util.Map;

public class InlineStandardLibraryCustomizer extends AbstractCodeCustomizer {

    public InlineStandardLibraryCustomizer() {
        super(Phase.RESOLVING);
    }

    @Override
    public void call(final ScriptContext context) {
        final List<Statement> enhancedStatements = this.enhanceModuleStatements(context.statements(), context.interpreter().externalModules());
        context.statements(enhancedStatements);
    }

    private List<Statement> enhanceModuleStatements(final List<Statement> statements, final Map<String, NativeModule> modules) {
        for (final String module : modules.keySet()) {
            final Token moduleToken = new Token(TokenType.IDENTIFIER, module, -1, -1);
            final ModuleStatement moduleStatement = new ModuleStatement(moduleToken);
            statements.add(0, moduleStatement);
        }
        return statements;
    }
}
