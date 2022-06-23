package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;

public interface HslCallable {
    void verify(Token at, List<Object> arguments);

    Object call(Interpreter interpreter, List<Object> arguments) throws ApplicationException;
}
