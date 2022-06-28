package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;

public interface CallableNode {

    Object call(Token at, Interpreter interpreter, List<Object> arguments) throws ApplicationException;
}
