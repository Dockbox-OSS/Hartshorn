package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A simple implementation of the {@link InjectionPointDeclarationResolver} that resolves the declaration type
 * of an injection point based on its specific type (executable element, field, or parameter).
 *
 * @since 0.7.0
 * @author Guus Lieben
 */
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
