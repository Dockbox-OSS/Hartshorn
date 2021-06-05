/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.persistence.registry;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
@Entity("registry")
public class Registry<V> extends HashMap<String, RegistryColumn<V>> {

    public Registry() {}

    public Registry(Map<RegistryIdentifier, RegistryColumn<V>> data) {
        for (Entry<RegistryIdentifier, RegistryColumn<V>> entry : data.entrySet()) {
            this.put(entry.getKey().getKey(), entry.getValue());
        }
    }

    /**
     * Adds another Registry to this one. If the added Registry contains the same {@link
     * RegistryIdentifier}s, then that data will be added to the existing columns.
     *
     * @param other
     *         The other Registry to add to this one.
     *
     * @return Itself.
     */
    public Registry<V> addRegistry(@NotNull Map<RegistryIdentifier, RegistryColumn<V>> other) {
        other.forEach(this::addData);
        return this;
    }

    public Registry<V> addRegistry(@NotNull Registry<V> other) {
        // Iterate over entries instead of using putAll to avoid overwriting existing
        // column values.
        for (Entry<String, RegistryColumn<V>> column : other.entrySet()) {
            this.addData(new SimpleIdentifier(column.getKey()), column.getValue());
        }
        return this;
    }

    public Registry<V> addData(RegistryIdentifier columnID, RegistryColumn<V> column) {
        if (this.containsKey(columnID.getKey())) {
            this.get(columnID.getKey()).addAll(column);
        }
        else {
            this.addColumn(columnID, column);
        }
        return this;
    }

    public Registry<V> addColumn(RegistryIdentifier columnID, RegistryColumn<V> column) {
        this.put(columnID.getKey(), column);
        return this;
    }

    /**
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to remove from the Registry if
     *         contained.
     *
     * @return Itself.
     */
    public Registry<V> removeColumns(@NotNull RegistryIdentifier... columnIDs) {
        for (RegistryIdentifier columnID : columnIDs) {
            this.remove(columnID);
        }
        return this;
    }

    /**
     * Returns the column if it exists, or creates a new column with the provided default values.
     *
     * @param identifier
     *         The {@link RegistryIdentifier} of the {@link RegistryColumn} to retrieve
     * @param defaultValues
     *         A varargs of {@code values} to add create the column with, if it doesn't
     *         exist
     *
     * @return The {@link RegistryColumn}
     */
    @SafeVarargs
    public final RegistryColumn<V> getColumnOrCreate(RegistryIdentifier identifier, V... defaultValues) {
        if (!this.containsColumns(identifier)) {
            this.addColumn(identifier, defaultValues);
        }
        return this.getMatchingColumns(identifier);
    }

    /**
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to check if contained in the
     *         Registry.
     *
     * @return True if all of the {@link RegistryIdentifier}s are contained, otherwise false.
     */
    public boolean containsColumns(RegistryIdentifier... columnIDs) {
        for (RegistryIdentifier columnID : columnIDs) {
            if (!this.containsKey(columnID.getKey())) return false;
        }
        return true;
    }

    /**
     * Adds a column of data to the Registry. <B>Note</B> this will override an existing column if
     * they share the same {@link RegistryIdentifier}
     *
     * @param columnID
     *         The {@link RegistryIdentifier} for which to add this data added under.
     * @param values
     *         A safe varargs of type {@code V} to be added.
     *
     * @return Itself.
     */
    @SafeVarargs
    public final Registry<V> addColumn(RegistryIdentifier columnID, V... values) {
        return this.addColumn(columnID, Arrays.asList(values));
    }

    /**
     * Gets all the matching columns in the Registry if contained.
     *
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to return from the Registry if
     *         contained.
     *
     * @return All the matching columns data combined into a single {@link RegistryColumn}. If no
     *         matches are found, an empty {@link RegistryColumn} will be returned.
     */
    public RegistryColumn<V> getMatchingColumns(RegistryIdentifier... columnIDs) {
        RegistryColumn<V> result = new RegistryColumn<>();
        for (RegistryIdentifier columnID : columnIDs) {
            if (this.containsKey(columnID.getKey())) {
                result.addAll(this.get(columnID.getKey()));
            }
        }
        return result;
    }

    /**
     * Adds a column of data to the Registry. <B>Note</B> this will override an existing column if
     * they share the same {@link RegistryIdentifier}
     *
     * @param columnID
     *         The {@link RegistryIdentifier} for which to add this data added under.
     * @param values
     *         A collection of type {@code V} or its children to be added.
     *
     * @return Itself.
     */
    public Registry<V> addColumn(RegistryIdentifier columnID, Collection<V> values) {
        this.put(columnID.getKey(), new RegistryColumn<>(values));
        return this;
    }

    /**
     * Filter the Registry by its columns. Note this creates a new Registry and doesn't modify itself.
     *
     * @param filter
     *         The filter accepts a {@link RegistryIdentifier} and returns true to remove that
     *         column, false to keep it. The columns which pass the filter are stored in a <b>new</b>
     *         Registry.
     *
     * @return The new Registry containing the filtered columns.
     */
    public Registry<V> removeColumnsIf(Predicate<RegistryIdentifier> filter) {
        Registry<V> registry = new Registry<>();

        for (String columnID : this.keySet()) {
            final RegistryIdentifier identifier = new SimpleIdentifier(columnID);
            if (!filter.test(identifier)) {
                registry.addColumn(identifier, this.get(columnID));
            }
        }
        return registry;
    }

    /** @return All the data in the Registry combined into a single {@link RegistryColumn} */
    @JsonIgnore
    public RegistryColumn<V> getAllData() {
        RegistryColumn<V> result = new RegistryColumn<>();
        for (RegistryColumn<V> columnData : this.values()) {
            result.addAll(columnData);
        }
        return result;
    }

    /**
     * Filter the Registry by its columns. Note this creates a new Registry and doesn't modify itself.
     *
     * @param biFilter
     *         The biFilter accepts a {@link RegistryIdentifier}, along with its {@link
     *         RegistryColumn} and returns true to remove that column, false to keep it. The columns which
     *         pass the filter are stored in a <b>new</b> Registry.
     *
     * @return The new Registry containing the filtered columns.
     */
    public Registry<V> removeColumnsIf(BiPredicate<RegistryIdentifier, RegistryColumn<? super V>> biFilter) {
        Registry<V> registry = new Registry<>();

        this.forEach((columnID, column) -> {
            final RegistryIdentifier identifier = new SimpleIdentifier(columnID);
            if (!biFilter.test(identifier, column)) {
                registry.addColumn(identifier, column);
            }
        });
        return registry;
    }

    /**
     * Filter the Registry by its values. Note this creates a new Registry and doesn't modify itself.
     *
     * @param filter
     *         The filter accepts a value of type {@code V} or its parents and returns true to
     *         remove that column, false to keep it. The values which pass the filter are stored in a
     *         <b>new</b> Registry. If no values in a particular column pass the filter, it is still added
     *         to the new Registry, it will simply contain no values.
     *
     * @return The new Registry containing the filtered values.
     */
    public Registry<V> removeValuesIf(Predicate<? super V> filter) {
        Registry<V> registry = new Registry<>();

        for (String columnID : this.keySet()) {
            RegistryColumn<V> column = new RegistryColumn<>(this.get(columnID));
            column.removeValueIf(filter);
            registry.addColumn(new SimpleIdentifier(columnID), column);
        }
        return registry;
    }

    /**
     * Filter the Registry by its values. Note this creates a new Registry and doesn't modify itself.
     *
     * @param biFilter
     *         The biFilter accepts a {@link RegistryIdentifier} (The columnID of the value),
     *         along with a value of type {@code V} or its parents and returns true to remove that column,
     *         false to keep it. The values which pass the filter are stored in a <b>new</b> Registry. If
     *         no values in a particular column pass the filter, it is still added to the new Registry, it
     *         will simply contain no values.
     *
     * @return The new Registry containing the filtered values.
     */
    public Registry<V> removeValuesIf(BiPredicate<RegistryIdentifier, ? super V> biFilter) {
        Registry<V> registry = new Registry<>();

        this.forEach((columnID, column) -> column.forEach(v -> {
            final RegistryIdentifier identifier = new SimpleIdentifier(columnID);
            if (!biFilter.test(identifier, v)) {
                registry.addData(identifier, v);
            }
        }));
        return registry;
    }

    /**
     * Adds data to the Registry. If the columnID does not exist, it creates a new column, otherwise
     * it adds the data to the existing column.
     *
     * @param columnID
     *         The {@link RegistryIdentifier} for which this data will be added to.
     * @param values
     *         A safe varargs of type {@code V} to be added.
     *
     * @return Itself.
     */
    @SafeVarargs
    public final Registry<V> addData(RegistryIdentifier columnID, V... values) {
        return this.addData(columnID, Arrays.asList(values));
    }

    /**
     * Adds data to the Registry. If the columnID does not exist, it creates a new column, otherwise
     * it adds the data to the existing column.
     *
     * @param columnID
     *         The {@link RegistryIdentifier} for which this data will be added to.
     * @param values
     *         A collection of type {@code V} or its children to be added.
     *
     * @return Itself.
     */
    public Registry<V> addData(RegistryIdentifier columnID, Collection<V> values) {
        if (this.containsKey(columnID.getKey())) {
            this.get(columnID.getKey()).addAll(values);
        }
        else {
            this.addColumn(columnID, values);
        }
        return this;
    }

    /**
     * @return The registry in an easy to view manner, which displays the relationship between {@link
     *         RegistryIdentifier}s and the values in the columns.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.buildhierarchy(builder, 0);

        return builder.toString();
    }

    /**
     * Builds the registry hierarchy.
     *
     * @param builder
     *         The {@link StringBuilder} being used to build the reistry heirarchy
     * @param indents
     *         The depth of the registry (Caused by nested registries)
     */
    private void buildhierarchy(StringBuilder builder, int indents) {
        this.forEach((identifier, column) -> {
            for (int i = 0; i < indents; i++) builder.append("\t");
            builder.append("- ").append(identifier).append("\n");

            column.forEach(value -> {
                for (int i = 0; i < indents; i++) builder.append("\t");
                if (value instanceof Registry)
                    ((Registry<?>) value).buildhierarchy(builder, indents + 1);
                else builder.append("| ").append(value).append("\n");
            });
        });
    }

    public RegistryColumn<V> get(RegistryIdentifier identifier) {
        return super.get(identifier.getKey());
    }

    public boolean containsKey(RegistryIdentifier key) {
        return super.containsKey(key.getKey());
    }

    public RegistryColumn<V> put(RegistryIdentifier key, RegistryColumn<V> value) {
        return super.put(key.getKey(), value);
    }

    public RegistryColumn<V> remove(RegistryIdentifier key) {
        return super.remove(key.getKey());
    }

    public RegistryColumn<V> getOrDefault(RegistryIdentifier key, RegistryColumn<V> defaultValue) {
        return super.getOrDefault(key.getKey(), defaultValue);
    }

    public RegistryColumn<V> putIfAbsent(RegistryIdentifier key, RegistryColumn<V> value) {
        return super.putIfAbsent(key.getKey(), value);
    }

    public boolean remove(RegistryIdentifier key, Object value) {
        return super.remove(key.getKey(), value);
    }

    public boolean replace(RegistryIdentifier key, RegistryColumn<V> oldValue, RegistryColumn<V> newValue) {
        return super.replace(key.getKey(), oldValue, newValue);
    }

    public RegistryColumn<V> replace(RegistryIdentifier key, RegistryColumn<V> value) {
        return super.replace(key.getKey(), value);
    }

    public RegistryColumn<V> computeIfAbsent(RegistryIdentifier key, Function<? super String, ? extends RegistryColumn<V>> mappingFunction) {
        return super.computeIfAbsent(key.getKey(), mappingFunction);
    }

    public RegistryColumn<V> computeIfPresent(RegistryIdentifier key, BiFunction<? super String, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.computeIfPresent(key.getKey(), remappingFunction);
    }

    public RegistryColumn<V> compute(RegistryIdentifier key, BiFunction<? super String, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.compute(key.getKey(), remappingFunction);
    }

    public RegistryColumn<V> merge(RegistryIdentifier key, RegistryColumn<V> value, BiFunction<? super RegistryColumn<V>, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.merge(key.getKey(), value, remappingFunction);
    }
}