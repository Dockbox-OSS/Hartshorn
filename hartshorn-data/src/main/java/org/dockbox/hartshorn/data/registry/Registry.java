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

package org.dockbox.hartshorn.data.registry;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Registry<V> extends HashMap<String, RegistryColumn<V>> {

    public Registry() {}

    public Registry(final Map<RegistryIdentifier, RegistryColumn<V>> data) {
        for (final Entry<RegistryIdentifier, RegistryColumn<V>> entry : data.entrySet()) {
            this.put(entry.getKey().key(), entry.getValue());
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
    public Registry<V> addRegistry(@NotNull final Map<RegistryIdentifier, RegistryColumn<V>> other) {
        other.forEach(this::add);
        return this;
    }

    public Registry<V> add(final RegistryIdentifier columnID, final RegistryColumn<V> column) {
        if (this.containsKey(columnID.key())) {
            this.get(columnID.key()).addAll(column);
        }
        else {
            this.addColumn(columnID, column);
        }
        return this;
    }

    public Registry<V> addColumn(final RegistryIdentifier columnID, final RegistryColumn<V> column) {
        this.put(columnID.key(), column);
        return this;
    }

    public Registry<V> addRegistry(@NotNull final Registry<V> other) {
        // Iterate over entries instead of using putAll to avoid overwriting existing
        // column values.
        for (final Entry<String, RegistryColumn<V>> column : other.entrySet()) {
            this.add(new RegistryIdentifierImpl(column.getKey()), column.getValue());
        }
        return this;
    }

    /**
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to remove from the Registry if
     *         contained.
     *
     * @return Itself.
     */
    public Registry<V> removeColumns(@NotNull final RegistryIdentifier... columnIDs) {
        for (final RegistryIdentifier columnID : columnIDs) {
            this.remove(columnID);
        }
        return this;
    }

    public RegistryColumn<V> remove(final RegistryIdentifier key) {
        return super.remove(key.key());
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
    public final RegistryColumn<V> getColumnOrCreate(final RegistryIdentifier identifier, final V... defaultValues) {
        if (!this.containsColumns(identifier)) {
            this.addColumn(identifier, defaultValues);
        }
        return this.matchingColumns(identifier);
    }

    /**
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to check if contained in the
     *         Registry.
     *
     * @return True if all of the {@link RegistryIdentifier}s are contained, otherwise false.
     */
    public boolean containsColumns(final RegistryIdentifier... columnIDs) {
        for (final RegistryIdentifier columnID : columnIDs) {
            if (!this.containsKey(columnID.key())) return false;
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
    public final Registry<V> addColumn(final RegistryIdentifier columnID, final V... values) {
        return this.addColumn(columnID, Arrays.asList(values));
    }

    /**
     * Gets all the matching columns in the Registry if contained.
     *
     * @param columnIDs
     *         A varargs of {@link RegistryIdentifier}s to return from the Registry if
     *         contained.
     *
     * @return All data from the matching columns combined into a single {@link RegistryColumn}. If no
     *         matches are found, an empty {@link RegistryColumn} will be returned.
     */
    public RegistryColumn<V> matchingColumns(final RegistryIdentifier... columnIDs) {
        final RegistryColumn<V> result = new RegistryColumn<>();
        for (final RegistryIdentifier columnID : columnIDs) {
            if (this.containsKey(columnID.key())) {
                result.addAll(this.get(columnID.key()));
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
    public Registry<V> addColumn(final RegistryIdentifier columnID, final Collection<V> values) {
        this.put(columnID.key(), new RegistryColumn<>(values));
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
    public Registry<V> removeColumnsIf(final Predicate<RegistryIdentifier> filter) {
        final Registry<V> registry = new Registry<>();

        for (final String columnID : this.keySet()) {
            final RegistryIdentifier identifier = new RegistryIdentifierImpl(columnID);
            if (!filter.test(identifier)) {
                registry.addColumn(identifier, this.get(columnID));
            }
        }
        return registry;
    }

    /** @return All the data in the Registry combined into a single {@link RegistryColumn} */
    public RegistryColumn<V> data() {
        final RegistryColumn<V> result = new RegistryColumn<>();
        for (final RegistryColumn<V> columnData : this.values()) {
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
    public Registry<V> removeColumnsIf(final BiPredicate<RegistryIdentifier, RegistryColumn<? super V>> biFilter) {
        final Registry<V> registry = new Registry<>();

        this.forEach((columnID, column) -> {
            final RegistryIdentifier identifier = new RegistryIdentifierImpl(columnID);
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
    public Registry<V> removeValuesIf(final Predicate<? super V> filter) {
        final Registry<V> registry = new Registry<>();

        for (final String columnID : this.keySet()) {
            final RegistryColumn<V> column = new RegistryColumn<>(this.get(columnID));
            column.removeValueIf(filter);
            registry.addColumn(new RegistryIdentifierImpl(columnID), column);
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
    public Registry<V> removeValuesIf(final BiPredicate<RegistryIdentifier, ? super V> biFilter) {
        final Registry<V> registry = new Registry<>();

        this.forEach((columnID, column) -> column.forEach(v -> {
            final RegistryIdentifier identifier = new RegistryIdentifierImpl(columnID);
            if (!biFilter.test(identifier, v)) {
                registry.add(identifier, v);
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
    public final Registry<V> add(final RegistryIdentifier columnID, final V... values) {
        return this.add(columnID, Arrays.asList(values));
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
    public Registry<V> add(final RegistryIdentifier columnID, final Collection<V> values) {
        if (this.containsKey(columnID.key())) {
            this.get(columnID.key()).addAll(values);
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
        final StringBuilder builder = new StringBuilder();
        this.buildHierarchy(builder, 0);

        return builder.toString();
    }

    /**
     * Builds the registry hierarchy.
     *
     * @param builder
     *         The {@link StringBuilder} being used to build the registry hierarchy
     * @param indents
     *         The depth of the registry (Caused by nested registries)
     */
    private void buildHierarchy(final StringBuilder builder, final int indents) {
        this.forEach((identifier, column) -> {
            builder.append("\t".repeat(Math.max(0, indents)));
            builder.append("- ").append(identifier).append("\n");

            column.forEach(value -> {
                builder.append("\t".repeat(Math.max(0, indents)));
                if (value instanceof Registry)
                    ((Registry<?>) value).buildHierarchy(builder, indents + 1);
                else builder.append("| ").append(value).append("\n");
            });
        });
    }

    public RegistryColumn<V> get(final RegistryIdentifier identifier) {
        return super.get(identifier.key());
    }

    public boolean containsKey(final RegistryIdentifier key) {
        return super.containsKey(key.key());
    }

    public RegistryColumn<V> put(final RegistryIdentifier key, final RegistryColumn<V> value) {
        return super.put(key.key(), value);
    }

    public RegistryColumn<V> getOrDefault(final RegistryIdentifier key, final RegistryColumn<V> defaultValue) {
        return super.getOrDefault(key.key(), defaultValue);
    }

    public RegistryColumn<V> putIfAbsent(final RegistryIdentifier key, final RegistryColumn<V> value) {
        return super.putIfAbsent(key.key(), value);
    }

    public boolean remove(final RegistryIdentifier key, final Object value) {
        return super.remove(key.key(), value);
    }

    public boolean replace(final RegistryIdentifier key, final RegistryColumn<V> oldValue, final RegistryColumn<V> newValue) {
        return super.replace(key.key(), oldValue, newValue);
    }

    public RegistryColumn<V> replace(final RegistryIdentifier key, final RegistryColumn<V> value) {
        return super.replace(key.key(), value);
    }

    public RegistryColumn<V> computeIfAbsent(final RegistryIdentifier key, final Function<? super String, ? extends RegistryColumn<V>> mappingFunction) {
        return super.computeIfAbsent(key.key(), mappingFunction);
    }

    public RegistryColumn<V> computeIfPresent(final RegistryIdentifier key, final BiFunction<? super String, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.computeIfPresent(key.key(), remappingFunction);
    }

    public RegistryColumn<V> compute(final RegistryIdentifier key, final BiFunction<? super String, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.compute(key.key(), remappingFunction);
    }

    public RegistryColumn<V> merge(final RegistryIdentifier key, final RegistryColumn<V> value, final BiFunction<? super RegistryColumn<V>, ? super RegistryColumn<V>, ? extends RegistryColumn<V>> remappingFunction) {
        return super.merge(key.key(), value, remappingFunction);
    }
}
