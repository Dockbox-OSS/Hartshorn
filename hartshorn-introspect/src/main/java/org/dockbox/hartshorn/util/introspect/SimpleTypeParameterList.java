package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.collections.ArrayListHashBiMultiMap;
import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SimpleTypeParameterList implements TypeParameterList {

    private final List<? extends TypeParameterView> typeParameters;
    private BiMultiMap<TypeParameterView, TypeParameterView> representationMap;

    public SimpleTypeParameterList(final List<? extends TypeParameterView> typeParameters) {
        this.typeParameters = typeParameters;
    }

    @Override
    public Option<TypeParameterView> atIndex(final int index) {
        if (this.typeParameters.size() <= index) {
            return Option.empty();
        }
        return Option.of(this.typeParameters.get(index));
    }

    @Override
    public int count() {
        return this.typeParameters.size();
    }

    @Override
    public List<TypeParameterView> asList() {
        return List.copyOf(this.typeParameters);
    }

    @Override
    public BiMultiMap<TypeParameterView, TypeParameterView> asMap() {
        if (this.representationMap == null) {
            final BiMultiMap<TypeParameterView, TypeParameterView> map = new ArrayListHashBiMultiMap<>();
            for (final TypeParameterView typeParameter : this.typeParameters) {
                if (typeParameter.isInputParameter()) {
                    map.putAll(typeParameter, typeParameter.represents());
                }
                else {
                    map.put(typeParameter, typeParameter.asInputParameter());
                }
            }
            this.representationMap = map;
        }
        return this.representationMap;
    }

    @Override
    public Stream<TypeParameterView> stream() {
        return this.asList().stream();
    }

    @Override
    public Iterator<TypeParameterView> iterator() {
        return this.asList().iterator();
    }
}
