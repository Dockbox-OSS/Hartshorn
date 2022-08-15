package org.dockbox.hartshorn.hsl.objects.access;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.token.Token;

public class StandardPropertyAccessVerifier implements PropertyAccessVerifier {

    @Override
    public boolean verify(final Token at, final FieldStatement field, final InstanceReference instance, final VariableScope fromScope) {
        if (field.isPublic()) return true;

        if (instance instanceof VirtualInstance virtualInstance) {
            final VariableScope classScope = virtualInstance.type().variableScope();
            VariableScope currentScope = fromScope;
            while (currentScope != null) {
                if (currentScope == classScope) {
                    return true;
                }
                currentScope = currentScope.enclosing();
            }
        }
        return false;
    }
}
