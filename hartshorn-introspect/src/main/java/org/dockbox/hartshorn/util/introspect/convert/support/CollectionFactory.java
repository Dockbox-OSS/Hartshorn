package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionFactory {

    private final Introspector introspector;

    public CollectionFactory(final Introspector introspector) {
        this.introspector = introspector;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <O extends Collection> O createCollection(final Class<O> targetType, final Class<?> elementType, final int length) {
        if (targetType.isInterface()) {
            if (targetType == Collection.class) {
                return (O) new ArrayList<>(length);
            }
            else if (targetType == List.class) {
                return (O) new ArrayList<>(length);
            }
            else if (targetType == Set.class) {
                return (O) new HashSet<>(length);
            }
            else if (targetType == SortedSet.class) {
                return (O) new TreeSet<>();
            }
            else if (targetType == Queue.class) {
                return (O) new LinkedList<>();
            }
            else if (EnumSet.class.isAssignableFrom(targetType)) {
                return (O) EnumSet.noneOf((Class<Enum>) elementType);
            }
            else {
                throw new IllegalArgumentException("Unsupported Collection interface: " + targetType.getName());
            }
        }
        else {
            final Option<ConstructorView<O>> defaultConstructor = this.introspector.introspect(targetType).constructors().defaultConstructor();
            if (defaultConstructor.present()) {
                return (O) defaultConstructor.get().create();
            }
            else {
                throw new IllegalArgumentException("Unsupported Collection implementation: " + targetType.getName());
            }
        }
    }
}
