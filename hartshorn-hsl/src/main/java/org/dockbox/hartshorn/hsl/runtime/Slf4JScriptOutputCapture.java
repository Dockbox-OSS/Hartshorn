package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.slf4j.Logger;

public record Slf4JScriptOutputCapture(Logger logger) implements ScriptOutputCapture {

    @Override
    public void print(Object object) {
        String text = HslRuntimeSupport.stringify(object);
        this.logger().info(text);
    }
}
