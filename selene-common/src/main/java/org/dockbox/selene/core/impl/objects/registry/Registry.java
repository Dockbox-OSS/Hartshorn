package org.dockbox.selene.core.impl.objects.registry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

public class Registry<V extends Serializable> implements Serializable
{
    private HashMap<RegistryIdentifier, TResult<V>> data = new HashMap<>();

    @SafeVarargs
    public final Registry<V> addColumn(RegistryIdentifier columnID, V... values) {
        addColumn(columnID, Arrays.asList(values));
        return this;
    }

    public Registry<V> addColumn(RegistryIdentifier columnID, Collection<? extends V> values) {
        data.put(columnID, new TResult<>(values));
        return this;
    }

    @SafeVarargs
    public final Registry<V> addData(RegistryIdentifier columnID, V... values) {
        addData(columnID, Arrays.asList(values));
        return this;
    }

    public Registry<V> addData(RegistryIdentifier columnID, Collection<? extends V> values) {
        if (data.containsKey(columnID)) {
            data.get(columnID).addAll(values);
        }
        else {
            addColumn(columnID, values);
        }
        return this;
    }

    public TResult<V> getMatching(RegistryIdentifier... columns) {
        TResult<V> result = new TResult<>();
        for (RegistryIdentifier columnID : columns) {
            if (data.containsKey(columnID)) {
                result.addAll(data.get(columnID));
            }
        }
        return result;
    }

    public TResult<V> getAllData() {
        TResult<V> result = new TResult<>();
        for (TResult<V> columnData : data.values()) {
            result.addAll(columnData);
        }
        return result;
    }

    public Registry<V> filterColumns(Predicate<RegistryIdentifier> predicate) {
        Registry<V> registry = new Registry<>();

        for (RegistryIdentifier columnID : data.keySet()) {
            if (predicate.test(columnID)) {
                registry.addColumn(columnID, data.get(columnID));
            }
        }
        return registry;
    }

    public Registry<V> filterValues(Predicate<? super V> predicate) {
        Registry<V> registry = new Registry<>();

        for (RegistryIdentifier columnID : data.keySet()) {
            TResult<V> column = new TResult<>(data.get(columnID));
            column.filter(predicate);
            registry.addColumn(columnID, column);
        }
        return registry;
    }
}
