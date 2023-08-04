package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public abstract class AbstractReflectionTypeParametersIntrospector implements TypeParametersIntrospector {

    private List<TypeParameterView> outputParameters;

    protected abstract TypeView<?> type();

    @Override
    public Option<TypeParameterView> atIndex(final int index) {
        final List<TypeParameterView> parameters = this.all();
        if (parameters.size() > index) return Option.of(parameters.get(index));
        return Option.empty();
    }

    @Override
    public List<TypeParameterView> resolveFor(final Class<?> fromParentType) {
        // TODO: Complex resolving, first needs .all() to be implemented
        return null;
    }

    @Override
    public int count() {
        return this.all().size();
    }

    @Override
    public Option<TypeView<?>> at(final int index) {
        return this.atIndex(index).flatMap(TypeParameterView::upperBound);
    }

    @Override
    public List<TypeParameterView> all() {
        return CollectionUtilities.mergeList(this.allInput(), this.allOutput());
    }

    @Override
    public List<TypeParameterView> allOutput() {
        if (this.outputParameters == null) {
            final TypeView<?> genericSuperClass = this.type().genericSuperClass();
            final List<TypeParameterView> superInput = genericSuperClass.typeParameters().allInput();
            final List<TypeParameterView> interfacesInput = this.type().genericInterfaces().stream()
                    .flatMap(genericInterface -> genericInterface.typeParameters().allInput().stream())
                    .toList();

            this.outputParameters = CollectionUtilities.mergeList(superInput, interfacesInput);
        }
        return this.outputParameters;
    }
}
