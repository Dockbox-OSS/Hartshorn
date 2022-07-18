package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.condition.ConditionContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;

public interface ScriptRuntime extends ConditionContext {

    ScriptContext run(String source);

    ScriptContext run(String source, Phase until);

    ScriptContext run(ScriptContext context, Phase only);

}
