package org.dockbox.hartshorn.application;

import java.util.List;

import org.dockbox.hartshorn.context.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationBuildContext extends DefaultContext {

    private final Class<?> mainClass;
    private final List<String> arguments;

    private final Logger logger;

    public ApplicationBuildContext(Class<?> mainClass, List<String> arguments) {
        this.mainClass = mainClass;
        this.arguments = arguments;

        this.logger = LoggerFactory.getLogger(mainClass);
    }

    public Class<?> mainClass() {
        return mainClass;
    }

    public List<String> arguments() {
        return arguments;
    }

    public Logger logger() {
        return logger;
    }
}
