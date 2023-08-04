package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionClassTypeParametersIntrospector extends AbstractReflectionTypeParametersIntrospector {

    private final TypeView<?> type;

    private List<TypeParameterView> inputParameters;

    public ReflectionClassTypeParametersIntrospector(final TypeView<?> type) {
        this.type = type;
    }

    @Override
    public List<TypeView<?>> from(final Class<?> fromInterface) {
        // TODO: Complex resolving, first needs .all() to be implemented
        return List.of();
    }

    @Override
    public List<TypeParameterView> allInput() {
        if (this.inputParameters == null) {
            this.inputParameters = Arrays.stream(this.type.type().getTypeParameters())
                    .map(parameter -> new ReflectionTypeParameterView(parameter, this.type))
                    .collect(Collectors.toList());
        }
        return this.inputParameters;
    }

    @Override
    protected TypeView<?> type() {
        return this.type;
    }
}
