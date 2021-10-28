package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Context;

import lombok.Getter;

@Getter
public class ContextInjectedType {
    @Context
    private SampleContext context;

    @Context("another")
    private SampleContext anotherContext;
}
