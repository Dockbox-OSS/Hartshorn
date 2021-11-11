package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParameterLoaderContext extends DefaultContext implements ContextCarrier {
    private final MethodContext<?, ?> method;
    private final TypeContext<?> type;
    private final Object instance;
    private final ApplicationContext applicationContext;
}
