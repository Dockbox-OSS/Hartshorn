package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Code customizers run during a specific {@link Phase phase} of a script's execution.
 * During the customization, the customizer can modify the script runtime's behavior,
 * either by modifying the script's AST or by modifying the various executors for each
 * phase.
 */
public interface CodeCustomizer {

    /**
     * The phase of the script's execution during which the customizer should be run.
     * @return the phase of the script's execution during which the customizer should be run.
     */
    Phase phase();

    /**
     * Customize the script runtime's behavior during the given phase.
     * @param context the script context.
     */
    void call(ScriptContext context);

}
