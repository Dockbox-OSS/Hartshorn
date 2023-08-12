package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.stream.Stream;

public interface TypeParameterList extends Iterable<TypeParameterView> {

    Option<TypeParameterView> atIndex(int index);

    int count();

    List<TypeParameterView> asList();

    /**
     * Returns a bi-directional map of type parameters. Depending on whether the type parameter is an input or output
     * parameter, the map will contain different mappings.
     *
     * <p>If the parameter is an input parameter, the map will contain the parameter as the key, and all the types it
     * represents as the values. The represented types will be resolved to their output parameters, similar to
     * {@link TypeParameterView#represents()}. If a type does not represent any outputs, it will not be included in the
     * map.
     *
     * <p>If the parameter is an output parameter, the map will contain the parameter as the key, and the input
     * parameter on the target type as the value. The input parameter will be resolved similar to
     * {@link TypeParameterView#asInputParameter()}.
     *
     * @return a bi-directional map of type parameters
     */
    BiMultiMap<TypeParameterView, TypeParameterView> asMap();

    Stream<TypeParameterView> stream();

}
