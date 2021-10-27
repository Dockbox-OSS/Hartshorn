package org.dockbox.hartshorn.di.types;

import org.dockbox.hartshorn.di.annotations.inject.Context;

import lombok.Getter;

@Getter
public class ContextInjectedType {
    @Context
    private SampleContext context;

    @Context("another")
    private SampleContext anotherContext;
}
