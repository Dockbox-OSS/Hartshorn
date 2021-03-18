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

package org.dockbox.selene.api.objects.keys;

import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.keys.data.DoublePersistentDataKey;
import org.dockbox.selene.api.objects.keys.data.IntegerPersistentDataKey;
import org.dockbox.selene.api.objects.keys.data.StringPersistentDataKey;
import org.dockbox.selene.api.objects.keys.data.TypedPersistentDataKey;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.tasks.CheckedFunction;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Keys {

    private static final List<Class<?>> nbtSupportedTypes =
            SeleneUtils.asList(
                    boolean.class,
                    byte.class,
                    short.class,
                    int.class,
                    long.class,
                    float.class,
                    double.class,
                    byte[].class,
                    int[].class,
                    long[].class,
                    String.class,
                    List.class,
                    Map.class);

    private Keys() {}

    /**
     * Looks up a {@link InjectorProperty} based on a given {@code key}. If a property with that key
     * is present, and matches the expected type it is returned wrapped in a {@link Exceptional}. If
     * no property is present, or the type of the object does not match the expected type, a empty
     * {@link Exceptional} is returned.
     *
     * @param <T>
     *         The expected type of the property
     * @param key
     *         The key of the property to look up
     * @param expectedType
     *         The {@link Class} of the expected property type
     * @param properties
     *         The properties to perform a lookup on
     *
     * @return The property value, wrapped in a {@link Exceptional}
     */
    public static <T> Exceptional<T> getPropertyValue(
            @NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        InjectorProperty<T> property = Keys.getProperty(key, expectedType, properties);
        // As the object is provided by a supplier this cannot currently be simplified to #ofNullable
        if (null != property) {
            return Exceptional.of(property::getObject);
        }
        return Exceptional.empty();
    }

    /**
     * Gets property.
     *
     * @param <T>
     *         the type parameter
     * @param key
     *         the key
     * @param expectedType
     *         the expected type
     * @param properties
     *         the properties
     *
     * @return the property
     */
    @Nullable
    public static <T> InjectorProperty<T> getProperty(
            @NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        List<InjectorProperty<T>> matchingProperties =
                Keys.getProperties(key, expectedType, properties);
        if (matchingProperties.isEmpty()) return null;
        else return matchingProperties.get(0);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<InjectorProperty<T>> getProperties(
            @NonNls String key, Class<T> expectedType, InjectorProperty<?>... properties) {
        List<InjectorProperty<T>> matchingProperties = SeleneUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (property.getKey().equals(key)
                    && null != property.getObject()
                    && Reflect.isAssignableFrom(expectedType, property.getObject().getClass())) {
                matchingProperties.add((InjectorProperty<T>) property);
            }
        }
        return matchingProperties;
    }

    @SuppressWarnings("unchecked")
    public static <T extends InjectorProperty<?>> List<T> getAllPropertiesOf(
            Class<T> type, InjectorProperty<?>... properties) {
        List<T> matches = SeleneUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (null != property && type.isAssignableFrom(property.getClass())) matches.add((T) property);
        }
        return matches;
    }

    /**
     * Gets sub properties.
     *
     * @param <T>
     *         the type parameter
     * @param propertyFilter
     *         the property filter
     * @param properties
     *         the properties
     *
     * @return the sub properties
     */
    @SuppressWarnings("unchecked")
    public static <T extends InjectorProperty<?>> List<T> getSubProperties(
            Class<T> propertyFilter, InjectorProperty<?>... properties) {
        List<T> values = SeleneUtils.emptyList();
        for (InjectorProperty<?> property : properties) {
            if (Reflect.isAssignableFrom(propertyFilter, property.getClass())) values.add((T) property);
        }
        return values;
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param owningClass
     *         the owning class
     *
     * @return the persistent data key
     */
    public static <T> PersistentDataKey<T> persistentKeyOf(
            Class<T> type, String name, Class<?> owningClass) {
        return Keys.persistentKeyOf(type, name, Reflect.getModule(owningClass));
    }

    /**
     * Persistent key of persistent data key.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param name
     *         the name
     * @param module
     *         the module
     *
     * @return the persistent data key
     */
    @SuppressWarnings("unchecked")
    public static <T> PersistentDataKey<T> persistentKeyOf(
            Class<T> type, String name, Module module) {
        if (!Keys.isNbtSupportedType(type))
            throw new UncheckedSeleneException(
                    "Unsupported data type for persistent key: " + type.getCanonicalName());

        if (type.equals(String.class))
            return (PersistentDataKey<T>) StringPersistentDataKey.of(name, module);
        else if (Reflect.isAssignableFrom(Integer.class, type)) {
            return (PersistentDataKey<T>) IntegerPersistentDataKey.of(name, module);
        }
        else if (Reflect.isAssignableFrom(Double.class, type)) {
            return (PersistentDataKey<T>) DoublePersistentDataKey.of(name, module);
        }

        return new TypedPersistentDataKey<>(
                name, Keys.convertToModuleIdString(name, module), module, type);
    }

    /**
     * Is nbt supported type boolean.
     *
     * @param type
     *         the type
     *
     * @return the boolean
     */
    public static boolean isNbtSupportedType(Class<?> type) {
        boolean supportedType = false;
        for (Class<?> nbtSupportedType : nbtSupportedTypes) {
            if (nbtSupportedType.isAssignableFrom(type)) {
                supportedType = true;
                break;
            }
        }
        return supportedType;
    }

    /**
     * Convert to module id.
     *
     * @param name
     *         the name
     * @param module
     *         the module
     *
     * @return the string
     */
    public static String convertToModuleIdString(String name, Module module) {
        name = name.toLowerCase(Locale.ROOT).replaceAll("[ .]", "").replaceAll("-", "_");
        return module.id() + ':' + name;
    }

    /**
     * Checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> checkedDynamicKeyOf(
            BiFunction<K, A, TransactionResult> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(setter, getter) {
        };
    }

    /**
     * Unsafe dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> unsafeDynamicKeyOf(
            BiConsumer<K, A> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(
                Keys.transactSetter(setter), k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> transactSetter(BiConsumer<K, A> setter) {
        return Keys.transactSetter(setter, TransactionResult.success());
    }

    /**
     * Transact setter bi function.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param defaultResult
     *         the default result
     *
     * @return the bi function
     */
    public static <K, A> BiFunction<K, A, TransactionResult> transactSetter(
            BiConsumer<K, A> setter, TransactionResult defaultResult) {
        return (t, u) -> {
            setter.accept(t, u);
            return defaultResult;
        };
    }

    /**
     * Unsafe checked dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> unsafeCheckedDynamicKeyOf(
            BiFunction<K, A, TransactionResult> setter, CheckedFunction<K, A> getter) {
        return new Key<K, A>(setter, k -> Exceptional.of(() -> getter.apply(k))) {
        };
    }

    /**
     * Dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> dynamicKeyOf(
            BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(Keys.transactSetter(setter), getter, remover) {
        };
    }

    /**
     * Checked dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> checkedDynamicKeyOf(
            BiFunction<K, A, TransactionResult> setter,
            Function<K, Exceptional<A>> getter,
            Consumer<K> remover) {
        return new RemovableKey<K, A>(setter, getter, remover) {
        };
    }

    /**
     * Unsafe dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> unsafeDynamicKeyOf(
            BiConsumer<K, A> setter, CheckedFunction<K, A> getter, Consumer<K> remover) {
        return new RemovableKey<K, A>(
                Keys.transactSetter(setter), k -> Exceptional.of(() -> getter.apply(k)), remover) {
        };
    }

    /**
     * Unsafe checked dynamic key of removable key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     * @param remover
     *         the remover
     *
     * @return the removable key
     */
    public static <K, A> RemovableKey<K, A> unsafeCheckedDynamicKeyOf(
            BiFunction<K, A, TransactionResult> setter,
            CheckedFunction<K, A> getter,
            Consumer<K> remover) {
        return new RemovableKey<K, A>(setter, k -> Exceptional.of(() -> getter.apply(k)), remover) {
        };
    }

    public static <K, A> Key<K, A> getterKey(Function<K, Exceptional<A>> getter) {
        return dynamicKeyOf((k, a) -> {
            throw new UnsupportedOperationException("Cannot set the value of this key");
        }, getter);
    }

    /**
     * Dynamic key of key.
     *
     * @param <K>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param setter
     *         the setter
     * @param getter
     *         the getter
     *
     * @return the key
     */
    public static <K, A> Key<K, A> dynamicKeyOf(
            BiConsumer<K, A> setter, Function<K, Exceptional<A>> getter) {
        return new Key<K, A>(Keys.transactSetter(setter), getter) {
        };
    }

    public static <K, A> Key<K, A> setterKey(BiConsumer<K, A> setter) {
        return dynamicKeyOf(setter, in -> Exceptional.empty());
    }
}
