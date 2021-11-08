package org.dockbox.hartshorn.core.parameterloaders;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;

import java.util.Set;

public class RuleBasedParameterLoaderImpl extends RuleBasedParameterLoader<ParameterLoaderContext> {
    @Override
    public Set<ParameterLoaderRule<ParameterLoaderContext>> rules() {
        return super.rules();
    }
}
