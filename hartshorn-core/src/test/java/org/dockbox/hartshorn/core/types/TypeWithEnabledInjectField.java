package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.annotations.inject.Enable;

import javax.inject.Inject;

import lombok.Getter;

@Component
public class TypeWithEnabledInjectField {

    @Inject
    @Enable
    @Getter
    private SingletonEnableable singletonEnableable;

}
