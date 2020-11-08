package org.dockbox.selene.core.impl.objects.registry;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

public class Registry<V extends Serializable> implements Serializable
{
    private HashMap<RegistryIdentifier, TResult<V>> data = new HashMap<>();

    /**
     * Adds a column of data to the Registry. <B>Note</B> this will override an existing column if they share the same {@code RegistryIdentifier}
     *
     * @param columnID The {@code RegistryIdentifier} for which this data added under.
     * @param values A safe varargs of type {@link V} to be added.
     * @return Itself.
     */
    @SafeVarargs
    public final Registry<V> addColumn(RegistryIdentifier columnID, V... values) {
        addColumn(columnID, Arrays.asList(values));
        return this;
    }

    /**
     * Adds a column of data to the Registry. <B>Note</B> this will override an existing column if they share the same {@code RegistryIdentifier}
     *
     * @param columnID The {@code RegistryIdentifier} for which this data added under.
     * @param values A collection of type {@link V} or its children to be added.
     * @return Itself.
     */
    public Registry<V> addColumn(RegistryIdentifier columnID, Collection<? extends V> values) {
        data.put(columnID, new TResult<>(values));
        return this;
    }
    /**
     * Adds data to the registry. If the columnID does not exist, it creates a new column, otherwise it
     * adds the data to the existing column.
     *
     * @param columnID The {@code RegistryIdentifier} for which this data will be added to.
     * @param values A safe varargs of type {@link V} to be added.
     * @return Itself.
     */
    @SafeVarargs
    public final Registry<V> addData(RegistryIdentifier columnID, V... values) {
        addData(columnID, Arrays.asList(values));
        return this;
    }

    /**
     * Adds data to the registry. If the columnID does not exist, it creates a new column, otherwise it
     * adds the data to the existing column.
     *
     * @param columnID The {@code RegistryIdentifier} for which this data will be added to.
     * @param values A collection of type {@link V} or its children to be added.
     * @return Itself.
     */
    public Registry<V> addData(RegistryIdentifier columnID, Collection<? extends V> values) {
        if (data.containsKey(columnID)) {
            data.get(columnID).addAll(values);
        }
        else {
            addColumn(columnID, values);
        }
        return this;
    }

    /**
     * Adds another registry to this one. If the added registry contains the same {@code RegistryIdentifier}s, then that
     * data will be added to the existing columns.
     *
     * @param otherRegistry The other registry to add to this one.
     * @return Itself.
     */
    public Registry<V> addRegistry(@NotNull Registry<? extends V> otherRegistry) {
        for (RegistryIdentifier columnID : otherRegistry.data.keySet()) {
            addData(columnID, otherRegistry.data.get(columnID));
        }
        return this;
    }

    /**
     * @param columnIDs A varargs of {@code RegistryIdentifier}s to remove from the registry if contained.
     * @return Itself.
     */
    public Registry<V> removeColumns(@NotNull RegistryIdentifier... columnIDs) {
        for (RegistryIdentifier columnID : columnIDs) {
            data.remove(columnID);
        }
        return this;
    }

    /**
     * @param columnID The {@code RegistryIdentifier} to check if contained in the registry.
     * @return True if {@code RegistryIdentifier} is contained, otherwise false.
     */
    public boolean containsColumn(RegistryIdentifier columnID) {
        return data.containsKey(columnID);
    }

    /**
     * @param columnIDs A varargs of {@code RegistryIdentifier}s to check if contained in the registry.
     * @return True if all of the {@code RegistryIdentifier}s are contained, otherwise false.
     */
    public boolean containsColumns(RegistryIdentifier... columnIDs) {
        for (RegistryIdentifier columnID : columnIDs) {
            if (!data.containsKey(columnID)) return false;
        }
        return true;
    }

    /**
     * @return True if this registry is empty, otherwise false.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Gets all the matching columns in the registry if contained.
     *
     * @param columnIDs A varargs of {@code RegistryIdentifier}s to return from the registry if contained.
     * @return
     * All the matching columns data combined into a single {@code TResult<V>}. If no matches are found, an empty
     * {@code TResult<V>} will be returned.
     */
    public TResult<V> getMatching(RegistryIdentifier... columnIDs) {
        TResult<V> result = new TResult<>();
        for (RegistryIdentifier columnID : columnIDs) {
            if (data.containsKey(columnID)) {
                result.addAll(data.get(columnID));
            }
        }
        return result;
    }

    /**
     * @return All the data in the registry combined into a single {@code TResult<V>}
     */
    public TResult<V> getAllData() {
        TResult<V> result = new TResult<>();
        for (TResult<V> columnData : data.values()) {
            result.addAll(columnData);
        }
        return result;
    }

    /**
     * Filter the registry by its columns. Note this creates a new Registry and doesn't modify itself.
     *
     * @param predicate
     * The predicate accepts a {@code RegistryIdentifier} and returns true to keep that column, false to remove it.
     * The columns which pass the filter are stored in a <b>new</b> Registry.
     * @return The new Registry containing the filtered columns.
     */
    public Registry<V> filterColumns(Predicate<RegistryIdentifier> predicate) {
        Registry<V> registry = new Registry<>();

        for (RegistryIdentifier columnID : data.keySet()) {
            if (predicate.test(columnID)) {
                registry.addColumn(columnID, data.get(columnID));
            }
        }
        return registry;
    }

    /**
     * Filter the registry by its values. Note this creates a new Registry and doesn't modify itself.
     *
     * @param predicate
     * The predicate accepts a value of type {@link V} or its parents and returns true to keep that value, false to remove it.
     * The values which pass the filter are stored in a <b>new</b> Registry. If no values in a particular column pass the
     * filter, it is still added to the new Registry, it will simply contain no values.
     * @return The new Registry containing the filtered values.
     */
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
