package org.dockbox.hartshorn.core.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BackingImplementationContext<P> extends DefaultContext {
    private final P implementation;
}
