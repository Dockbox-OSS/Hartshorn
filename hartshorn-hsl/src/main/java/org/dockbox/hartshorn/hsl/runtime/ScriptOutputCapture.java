package org.dockbox.hartshorn.hsl.runtime;

/**
 * Capture object for any output generated during a script's execution.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ScriptOutputCapture {

    /**
     * Prints the given object to the capture object.
     *
     * @param object the object to print.
     */
    void print(Object object);
}
