package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.stream.Stream;

public interface TypeParameterList extends Iterable<TypeParameterView> {

    Option<TypeParameterView> atIndex(int index);

    int count();

    List<TypeParameterView> asList();

    Stream<TypeParameterView> stream();

}
