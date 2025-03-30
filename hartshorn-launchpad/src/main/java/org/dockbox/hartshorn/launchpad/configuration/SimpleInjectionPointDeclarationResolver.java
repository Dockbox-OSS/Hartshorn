package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class SimpleInjectionPointDeclarationResolver implements InjectionPointDeclarationResolver {

    @Override
    public TypeView<?> resolve(InjectionPoint injectionPoint) {
        return switch(injectionPoint.injectionPoint()) {
            case ExecutableElementView<?> executableElementView -> executableElementView.declaredBy();
            case FieldView<?, ?> fieldView -> fieldView.declaredBy();
            case ParameterView<?> parameterView -> parameterView.declaredBy().declaredBy();
            default -> throw new IllegalStateException("Unexpected value: " + injectionPoint.injectionPoint());
        };
    }
}
