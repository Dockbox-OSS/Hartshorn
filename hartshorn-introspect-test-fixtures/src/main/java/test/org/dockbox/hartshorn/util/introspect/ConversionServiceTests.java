/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterCache;
import org.dockbox.hartshorn.util.introspect.convert.ConverterRegistry;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverters;
import org.dockbox.hartshorn.util.introspect.convert.NullAccess;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.util.introspect.support.basic.TestEnumType;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * Tests for the {@link ConversionService} interface, driven by configurable {@link Introspector}
 * implementations.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class ConversionServiceTests {

    protected abstract Introspector introspector();

    protected ConversionService conversionService() {
        return new StandardConversionService(this.introspector()).withDefaults();
    }

    @Test
    void primitiveWrapperFromEmptyString() {
        ConversionService conversionService = this.conversionService();
        Integer empty = conversionService.convert("", Integer.class);
        assertThat(empty)
                .isZero();
    }

    @Test
    void numberFromString() {
        ConversionService conversionService = this.conversionService();
        Integer valid = conversionService.convert("12", Integer.class);
        assertThat(valid)
                .isNotNull()
                .isEqualTo(12);
    }

    @Test
    void primitiveFromString() {
        ConversionService conversionService = this.conversionService();
        int primitive = conversionService.convert("12", int.class);
        assertThat(primitive).isEqualTo(12);
    }

    @Test
    void enumFromString() {
        ConversionService conversionService = this.conversionService();
        TestEnumType a = conversionService.convert("A", TestEnumType.class);
        assertThat(a)
                .isNotNull()
                .isSameAs(TestEnumType.A);

        TestEnumType d = conversionService.convert("D", TestEnumType.class);
        assertThat(d).isNull();
    }

    @Test
    void optionFromObject() {
        ConversionService conversionService = this.conversionService();
        Option<?> option = conversionService.convert("test", Option.class);
        assertThat(option).isNotNull();
        assertThat(option.present()).isTrue();
        assertThat(option.get()).isEqualTo("test");
    }

    @Test
    void objectFromOption() {
        ConversionService conversionService = this.conversionService();
        String converted = conversionService.convert(Option.of("test"), String.class);
        assertThat(converted)
                .isNotNull()
                .isEqualTo("test");
    }

    @Test
    void optionDefaultValue() {
        ConversionService conversionService = this.conversionService();
        Option<?> optionFromNull = conversionService.convert(null, Option.class);
        assertThat(optionFromNull).isNotNull();
        assertThat(optionFromNull.present()).isFalse();
    }

    @Test
    void stringDefaultValue() {
        ConversionService conversionService = this.conversionService();
        String stringFromNull = conversionService.convert(null, String.class);
        assertThat(stringFromNull).isNotNull();
        assertThat(stringFromNull).isEmpty();
    }

    @Test
    void primitiveDefaultValue() {
        ConversionService conversionService = this.conversionService();
        int intFromNull = conversionService.convert(null, int.class);
        assertThat(intFromNull).isZero();
    }

    @Test
    void primitiveWrapperDefaultValue() {
        ConversionService conversionService = this.conversionService();
        Integer integerFromNull = conversionService.convert(null, Integer.class);
        assertThat(integerFromNull)
                .isZero();
    }

    @Test
    void concreteSetDefaultValue() {
        ConversionService conversionService = this.conversionService();
        Set<?> hashSet = conversionService.convert(null, TreeSet.class);
        assertThat(hashSet)
                .isInstanceOf(TreeSet.class)
                .isEmpty();
    }

    @Test
    void setDefaultValue() {
        ConversionService conversionService = this.conversionService();
        Set<?> set = conversionService.convert(null, Set.class);
        assertThat(set).isNotNull();
        assertThat(set).isEmpty();
    }

    @Test
    void listDefaultValue() {
        ConversionService conversionService = this.conversionService();
        List<?> list = conversionService.convert(null, List.class);
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    void concreteListDefaultValue() {
        ConversionService conversionService = this.conversionService();
        List<?> arrayList = conversionService.convert(null, CopyOnWriteArrayList.class);
        assertThat(arrayList)
                .isInstanceOf(CopyOnWriteArrayList.class)
                .isEmpty();
    }

    @Test
    void collectionFromObject() {
        ConversionService conversionService = this.conversionService();
        List<?> linkedList = conversionService.convert("test", LinkedList.class);
        assertThat(linkedList)
                .isNotEmpty()
                .first()
                .isEqualTo("test");
    }

    @Test
    void collectionFromCollection() {
        ConversionService conversionService = this.conversionService();
        Set<?> setFromList = conversionService.convert(List.of(1, 2, 3), Set.class);
        assertThat(setFromList)
                .isNotEmpty()
                .hasSize(3);
    }

    @Test
    void collectionFromOption() {
        ConversionService conversionService = this.conversionService();
        List<?> listFromOption = conversionService.convert(Option.of("123"), List.class);
        assertThat(listFromOption)
                .isNotEmpty()
                .hasSize(1);
        assertThat(listFromOption.get(0)).isEqualTo("123");
    }

    @Test
    void arrayFromCollection() {
        ConversionService conversionService = this.conversionService();
        String[] array = conversionService.convert(List.of("1", "2", "3"), String[].class);
        assertThat(array).isNotNull();
        assertThat(array.length).isEqualTo(3);
        assertThat(array[0]).isEqualTo("1");
        assertThat(array[1]).isEqualTo("2");
        assertThat(array[2]).isEqualTo("3");
    }

    @Test
    void arrayFromObject() {
        ConversionService conversionService = this.conversionService();
        String[] array = conversionService.convert("test", String[].class);
        assertThat(array).isNotNull();
        assertThat(array.length).isOne();
        assertThat(array[0]).isEqualTo("test");
    }

    @Test
    void implicitlyTypedConverterIsAdaptedCorrectly() {
        this.testConverterTypeIsAdaptedCorrectly(
            registry -> registry.addConverter(new SimpleConverter()),
            "1", Integer.class,
            ConverterType.CONVERTER
        );
    }

    @Test
    void lambdaConverterWithoutExplicitTypesIsRejected() {
        ConverterCache converterCache = new GenericConverters();
        ConverterCache defaultValueProviderCache = new GenericConverters();
        ConverterRegistry registry = new StandardConversionService(this.introspector(),
            converterCache,
            defaultValueProviderCache);
        // Not allowed because the source and target types cannot practically be determined due to
        // type erasure
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            registry.addConverter((Converter<String, Integer>) Integer::parseInt);
        });
    }

    @Test
    void lambdaConverterWithExplicitTypesIsAdaptedCorrectly() {
        this.testConverterTypeIsAdaptedCorrectly(
            registry -> registry.addConverter(String.class,
                Integer.class,
                source -> Integer.parseInt(source)),
            "1", Integer.class,
            ConverterType.CONVERTER
        );
    }

    @Test
    void explicitlyTypedConverterIsAdapterCorrectly() {
        this.testConverterTypeIsAdaptedCorrectly(
            registry -> registry.addConverter(String.class, Integer.class, new SimpleConverter()),
            "1", Integer.class,
            ConverterType.CONVERTER
        );
    }

    @Test
    void implicitDefaultValueProviderIsAdaptedCorrectly() {
        // Lambdas are not supported in this context due to the absence of sufficient type hints.
        // Though, using #addDefaultValueProvider(Class, DefaultValueProvider) it would be
        // supported in any practical scenario.
        //noinspection Convert2Lambda
        this.testConverterTypeIsAdaptedCorrectly(
            registry -> registry.addDefaultValueProvider(new DefaultValueProvider<String>() {
                @Override
                public String defaultValue() {
                    return "";
                }
            }),
            NullAccess.getInstance(), String.class,
            ConverterType.DEFAULT_VALUE_PROVIDER
        );
    }

    @Test
    void explicitDefaultValueProviderIsAdaptedCorrectly() {
        this.testConverterTypeIsAdaptedCorrectly(
            registry -> registry.addDefaultValueProvider(String.class, () -> ""),
            NullAccess.getInstance(), String.class,
            ConverterType.DEFAULT_VALUE_PROVIDER
        );
    }

    private void testConverterTypeIsAdaptedCorrectly(
        Consumer<ConverterRegistry> registerAction,
        Object source,
        Class<?> targetType,
        ConverterType converterType
    ) {
        ConverterCache converterCache = new GenericConverters();
        ConverterCache defaultValueProviderCache = new GenericConverters();
        ConverterRegistry registry = new StandardConversionService(this.introspector(),
            converterCache,
            defaultValueProviderCache);
        assertThat(converterCache.converters()).isEmpty();
        assertThat(defaultValueProviderCache.converters()).isEmpty();

        registerAction.accept(registry);

        ConverterCache shouldBeEmpty =
            converterType == ConverterType.CONVERTER ? defaultValueProviderCache : converterCache;
        ConverterCache shouldBePopulated =
            converterType == ConverterType.CONVERTER ? converterCache : defaultValueProviderCache;

        assertThat(shouldBeEmpty.converters()).isEmpty();
        assertThat(shouldBePopulated.converters()).isNotEmpty();
        assertThat(shouldBePopulated.converters()).hasSize(1);

        GenericConverter converter = shouldBePopulated.getConverter(source, targetType);
        assertThat(converter).isNotNull();
    }

    private enum ConverterType {
        CONVERTER,
        DEFAULT_VALUE_PROVIDER,
    }

    private static class SimpleConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return Integer.parseInt(source);
        }
    }
}
