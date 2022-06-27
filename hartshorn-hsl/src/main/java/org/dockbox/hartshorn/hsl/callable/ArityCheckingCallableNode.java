package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

public abstract class ArityCheckingCallableNode implements VerifiableCallableNode {

    public abstract int arity();

    @Override
    public void verify(final Token at, final List<Object> arguments) {
        if (arguments.size() != this.arity()) {
            throw new RuntimeError(at, "Expected " +
                    this.arity() + " arguments but got " +
                    arguments.size() + ".");
        }
    }
}
