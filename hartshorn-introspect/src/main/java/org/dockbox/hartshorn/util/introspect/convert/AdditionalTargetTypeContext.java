package org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class AdditionalTargetTypeContext<T> extends DefaultContext {

    private final Class<T> targetType;
    private final ParameterizableType parameterizableType;

    public AdditionalTargetTypeContext(TypeView<T> typeView) {
        this.targetType = typeView.type();
        this.parameterizableType = ParameterizableType.create(typeView);
    }

    public AdditionalTargetTypeContext(Class<T> targetType, ParameterizableType parameterizableType) {
        this.targetType = targetType;
        this.parameterizableType = parameterizableType;
    }

    public Class<T> targetType() {
        return this.targetType;
    }

    public ParameterizableType parameterizableType() {
        return this.parameterizableType;
    }
}
