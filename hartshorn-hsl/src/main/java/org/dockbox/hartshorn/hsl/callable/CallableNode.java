package org.dockbox.hartshorn.hsl.callable;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ApplicationException;

import java.util.List;

/**
 * Represents a node that can be called, producing a result. This is the base class for all types
 * of executable nodes, including functions and constructors.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface CallableNode {

    /**
     * Executes the node, producing a result. The arguments may or may not be used, depending on
     * the type of node.
     *
     * @param at The token at which the node is being executed. This is used for error reporting.
     * @param interpreter The interpreter that is executing the node.
     * @param arguments The arguments that are passed to the node.
     * @return The result of the node.
     * @throws ApplicationException If an error occurs while executing the node.
     */
    Object call(Token at, Interpreter interpreter, List<Object> arguments) throws ApplicationException;
}
