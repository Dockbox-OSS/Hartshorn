package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.stereotype.Component;

import lombok.Getter;

@Component(permitProcessing = false, permitProxying = false)
public class NonProcessableType {
    @Getter
    private String nonNullIfProcessed;
}
