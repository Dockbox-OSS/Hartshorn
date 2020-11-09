package org.dockbox.selene.core.impl.objects.registry;

import org.dockbox.selene.core.objects.optional.Exceptional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegistryColumn<T> extends ArrayList<T>
{
    public RegistryColumn() {
        super();
    }

    public RegistryColumn(Collection<? extends T> values) {
        super(values);
    }

    /**
     * Filters the RegistryColumn based on the provided filter.
     *
     * @param filter
     * The filter accepts a value of type {@link T} or its parents and returns true to keep that value,
     * false to remove it.
     * @return Itself
     */
    public RegistryColumn<T> filter(Predicate<? super T> filter) {
        super.removeIf(filter);
        return this;
    }

    /**
     * Maps this registryColumn to another type.
     *
     * @param mapper
     * The mapper accepts a value of type {@link T} or its parents and returns a value of type {@link K}.
     * @param <K> The type of the new RegistryColumn.
     * @return A new RegistryColumn which contains the mapped values of the previous RegistryColumn.
     */
    public <K> RegistryColumn<K> mapTo(Function<? super T, K> mapper) {
        RegistryColumn<K> result = new RegistryColumn<>();

        for (T value : this) {
            result.add(mapper.apply(value));
        }
        return result;
    }

    /**
     * Maps this registryColumn to a collection and then adds all the collections into a new single RegistryColumn
     *
     * @param mapper
     * The mapper accepts a value of type {@link T} or its parents and returns a collection of type {@link K}.
     * @param <K> The type of the new RegistryColumn.
     * @return A new RegistryColumn which contains all the values of the collections.
     */
    public <K> RegistryColumn<K> mapToSingleList(Function<? super T, ? extends Collection<K>> mapper) {
        RegistryColumn<K> result = new RegistryColumn<>();

        for (T value : this) {
            result.addAll(mapper.apply(value));
        }
        return result;
    }

    /**
     * Finds the first value which matches the provided predicate.
     *
     * @param predicate
     * The predicate takes in a value of type {@link T} or its parents and returns true if that value is a match,
     * otherwise it returns false.
     * @return An Exceptional containing the value of the first match, if one is found.
     */
    public Exceptional<T> firstMatch(Predicate<? super T> predicate) {
        for (T value : this) {
            if (predicate.test(value)) return Exceptional.of(value);
        }
        return Exceptional.empty();
    }
}

