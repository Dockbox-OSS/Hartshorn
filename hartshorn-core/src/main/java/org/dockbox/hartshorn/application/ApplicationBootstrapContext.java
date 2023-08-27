package org.dockbox.hartshorn.application;

import java.util.List;

public class ApplicationBootstrapContext extends ApplicationBuildContext {

    private final boolean includeBasePackages;

    public ApplicationBootstrapContext(Class<?> mainClass, List<String> arguments, boolean includeBasePackages) {
        super(mainClass, arguments);
        this.includeBasePackages = includeBasePackages;
    }

    public boolean includeBasePackages() {
        return includeBasePackages;
    }
}
