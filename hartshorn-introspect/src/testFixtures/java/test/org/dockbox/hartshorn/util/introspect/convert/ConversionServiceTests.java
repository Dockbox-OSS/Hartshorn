/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ConversionServiceTests {

    protected abstract Introspector introspector();

    protected ConversionService conversionService() {
        return new StandardConversionService(this.introspector()).withDefaults();
    }

    enum DemoABC {
        A, B, C,
    }

    @Test
    void testPrimitiveWrapperFromEmptyString() {
        final ConversionService conversionService = this.conversionService();
        final Integer empty = conversionService.convert("", Integer.class);
        Assertions.assertNotNull(empty);
        Assertions.assertEquals(0, empty);
    }

    @Test
    void testNumberFromString() {
        final ConversionService conversionService = this.conversionService();
        final Integer valid = conversionService.convert("12", Integer.class);
        Assertions.assertNotNull(valid);
        Assertions.assertEquals(12, valid);
    }

    @Test
    void testPrimitiveFromString() {
        final ConversionService conversionService = this.conversionService();
        final int primitive = conversionService.convert("12", int.class);
        Assertions.assertEquals(12, primitive);
    }

    @Test
    void testEnumFromString() {
        final ConversionService conversionService = this.conversionService();
        final DemoABC a = conversionService.convert("A", DemoABC.class);
        Assertions.assertNotNull(a);
        Assertions.assertSame(DemoABC.A, a);

        final DemoABC d = conversionService.convert("D", DemoABC.class);
        Assertions.assertNull(d);
    }

    @Test
    void testOptionFromObject() {
        final ConversionService conversionService = this.conversionService();
        final Option<?> option = conversionService.convert("test", Option.class);
        Assertions.assertNotNull(option);
        Assertions.assertTrue(option.present());
        Assertions.assertEquals("test", option.get());
    }

    @Test
    void testObjectFromOption() {
        final ConversionService conversionService = this.conversionService();
        final String converted = conversionService.convert(Option.of("test"), String.class);
        Assertions.assertNotNull(converted);
        Assertions.assertEquals("test", converted);
    }

    @Test
    void testOptionDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final Option<?> optionFromNull = conversionService.convert(null, Option.class);
        Assertions.assertNotNull(optionFromNull);
        Assertions.assertFalse(optionFromNull.present());
    }

    @Test
    void testStringDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final String stringFromNull = conversionService.convert(null, String.class);
        Assertions.assertNotNull(stringFromNull);
        Assertions.assertEquals("", stringFromNull);
    }

    @Test
    void testPrimitiveDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final int intFromNull = conversionService.convert(null, int.class);
        Assertions.assertEquals(0, intFromNull);
    }

    @Test
    void testPrimitiveWrapperDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final Integer integerFromNull = conversionService.convert(null, Integer.class);
        Assertions.assertNotNull(integerFromNull);
        Assertions.assertEquals(0, integerFromNull);
    }

    @Test
    void testConcreteSetDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final Set<?> hashSet = conversionService.convert(null, TreeSet.class);
        Assertions.assertNotNull(hashSet);
        Assertions.assertTrue(hashSet.isEmpty());
        Assertions.assertTrue(hashSet instanceof TreeSet);
    }

    @Test
    void testSetDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final Set<?> set = conversionService.convert(null, Set.class);
        Assertions.assertNotNull(set);
        Assertions.assertTrue(set.isEmpty());
    }

    @Test
    void testListDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final List<?> list = conversionService.convert(null, List.class);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void testConcreteListDefaultValue() {
        final ConversionService conversionService = this.conversionService();
        final List<?> arrayList = conversionService.convert(null, CopyOnWriteArrayList.class);
        Assertions.assertNotNull(arrayList);
        Assertions.assertTrue(arrayList.isEmpty());
        Assertions.assertTrue(arrayList instanceof CopyOnWriteArrayList);
    }

    @Test
    void testCollectionFromObject() {
        final ConversionService conversionService = this.conversionService();
        final LinkedList<?> linkedList = conversionService.convert("test", LinkedList.class);
        Assertions.assertNotNull(linkedList);
        Assertions.assertFalse(linkedList.isEmpty());
        Assertions.assertEquals("test", linkedList.get(0));
    }

    @Test
    void testCollectionFromCollection() {
        final ConversionService conversionService = this.conversionService();
        final Set<?> setFromList = conversionService.convert(List.of(1, 2, 3), Set.class);
        Assertions.assertNotNull(setFromList);
        Assertions.assertFalse(setFromList.isEmpty());
        Assertions.assertEquals(3, setFromList.size());
    }

    @Test
    public void testCollectionFromOption() {
        final ConversionService conversionService = this.conversionService();
        final List<?> listFromOption = conversionService.convert(Option.of("123"), List.class);
        Assertions.assertNotNull(listFromOption);
        Assertions.assertFalse(listFromOption.isEmpty());
        Assertions.assertEquals(1, listFromOption.size());
        Assertions.assertEquals("123", listFromOption.get(0));
    }

    @Test
    void testArrayFromCollection() {
        final ConversionService conversionService = this.conversionService();
        final String[] array = conversionService.convert(List.of("1", "2", "3"), String[].class);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(3, array.length);
        Assertions.assertEquals("1", array[0]);
        Assertions.assertEquals("2", array[1]);
        Assertions.assertEquals("3", array[2]);
    }

    @Test
    void testArrayFromObject() {
        final ConversionService conversionService = this.conversionService();
        final String[] array = conversionService.convert("test", String[].class);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(1, array.length);
        Assertions.assertEquals("test", array[0]);
    }
}
