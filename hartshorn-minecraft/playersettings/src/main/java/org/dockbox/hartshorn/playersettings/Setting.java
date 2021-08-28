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

package org.dockbox.hartshorn.playersettings;

import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.TypedPersistentDataKey;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.entry.FakeResource;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Setting<T> extends TypedPersistentDataKey<T> {

    private final Function<ApplicationContext, ResourceEntry> resource;
    private final Function<ApplicationContext, ResourceEntry> description;

    private final BiFunction<ApplicationContext, T, ResourceEntry> converter;
    private final Supplier<T> defaultValue;
    private final Function<ApplicationContext, Item> display;
    private final Consumer<PersistentDataHolder> action;

    public Setting(final Class<T> type, final String id, final Function<ApplicationContext, ResourceEntry> name,
                   final Function<ApplicationContext, ResourceEntry> description,
                   final Function<ApplicationContext, TypedOwner> owner,
                   final BiFunction<ApplicationContext, T, ResourceEntry> converter,
                   final Supplier<T> defaultValue,
                   final Function<ApplicationContext, Item> display,
                   final Consumer<PersistentDataHolder> action) {
        super(id, owner, type);
        this.resource = name;
        this.description = description;
        this.converter = converter;
        this.defaultValue = defaultValue;
        this.display = display;
        this.action = action;
    }

    public ResourceEntry resource(final ApplicationContext context) {
        return this.resource.apply(context);
    }

    public ResourceEntry description(final ApplicationContext context) {
        return this.description.apply(context);
    }

    public static <T> SettingBuilder<T> of(final Class<T> type) {
        return new SettingBuilder<>(type);
    }

    protected ResourceEntry convert(final ApplicationContext context, final T value) {
        return this.converter.apply(context, value);
    }

    public Item item(final ApplicationContext context) {
        return this.display.apply(context);
    }

    /**
     * Gets the setting value for the given data holder. If the setting isn't set for the given
     * holder, the default value is used. If the setting isn't set and the default value is absent,
     * {@code null} is returned.
     *
     * @param holder
     *         The data holder to test the setting on
     *
     * @return The set value, default value, or null
     */
    @Nullable
    public T get(final PersistentDataHolder holder) {
        return holder.get(this).orElse(this.defaultValue::get).orNull();
    }

    public Consumer<PersistentDataHolder> action() {
        return this.action;
    }

    public static final class SettingBuilder<T> {

        private String id;
        private Function<ApplicationContext, TypedOwner> owner;
        private Class<T> type;
        private Function<ApplicationContext, ResourceEntry> resource;
        private Function<ApplicationContext, ResourceEntry> description;
        private BiFunction<ApplicationContext, T, ResourceEntry> converter = (ctx, o) -> new FakeResource(String.valueOf(o));
        private Supplier<T> defaultValue;
        private Function<ApplicationContext, Item> display = ctx -> Item.of(ctx, ItemTypes.BARRIER);
        private Consumer<PersistentDataHolder> action = holder -> {};

        private SettingBuilder(final Class<T> type) {
            this.type = type;
        }

        public SettingBuilder<T> owner(final TypedOwner owner) {
            this.owner = ctx -> owner;
            return this;
        }

        public SettingBuilder<T> owner(final Class<?> owner) {
            this.owner = ctx -> ctx.meta().lookup(TypeContext.of(owner));
            return this;
        }

        public SettingBuilder<T> resource(final Function<ApplicationContext, ResourceEntry> resource) {
            this.resource = resource;
            return this;
        }

        public SettingBuilder<T> description(final Function<ApplicationContext, ResourceEntry> description) {
            this.description = description;
            return this;
        }

        public SettingBuilder<T> converter(final BiFunction<ApplicationContext, T, ResourceEntry> converter) {
            this.converter = converter;
            return this;
        }

        public SettingBuilder<T> defaultValue(final Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public SettingBuilder<T> display(final Function<ApplicationContext, Item> display) {
            this.display = display;
            return this;
        }

        public SettingBuilder<T> from(final PersistentDataKey<T> key) {
            return this.id(key.id()).type(key.type());
        }

        public SettingBuilder<T> type(final Class<T> type) {
            this.type = type;
            return this;
        }

        public SettingBuilder<T> id(final String id) {
            this.id = id;
            return this;
        }

        public SettingBuilder<T> action(final Consumer<PersistentDataHolder> action) {
            this.action = action;
            return this;
        }

        public Setting<T> ok() {
            if (this.id == null) throw new IllegalArgumentException("ID should be specified");
            if (this.type == null) throw new IllegalArgumentException("Type should be specified");
            if (this.owner == null) throw new IllegalArgumentException("Owned should be specified");
            if (this.resource == null) throw new IllegalArgumentException("Resource should be specified");
            if (this.description == null) throw new IllegalArgumentException("Description should be specified");
            if (this.defaultValue == null) throw new IllegalArgumentException("Default value should be specified");

            return new Setting<>(
                    this.type,
                    this.id,
                    this.resource,
                    this.description,
                    this.owner,
                    this.converter,
                    this.defaultValue,
                    this.display,
                    this.action);
        }
    }
}
