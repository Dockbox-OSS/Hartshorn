package org.dockbox.hartshorn.hsl.objects.access;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.token.Token;

public interface PropertyAccessVerifier {
    boolean verify(Token at, FieldStatement field, InstanceReference instance, VariableScope fromScope);
}
