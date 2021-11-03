package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ParameterLoader<C extends ParameterLoaderContext> {
    public abstract List<Object> loadArguments(C context, Object... args);
}
