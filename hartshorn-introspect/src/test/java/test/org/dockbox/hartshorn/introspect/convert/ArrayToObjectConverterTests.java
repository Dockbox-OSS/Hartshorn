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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayToObjectConverterTests {

    @Test
    void arrayConvertsToObjectIfLengthIsOne() {
        GenericConverter converter = new ArrayToObjectConverter(null);
        Object[] array = {"test"};
        Object result = converter.convert(array, Object[].class, Object.class);
        assertThat(result)
                .isNotNull()
                .isEqualTo("test");
    }

    @Test
    void arrayDoesNotConvertIfLengthIsZero() {
        GenericConverter converter = new ArrayToObjectConverter(null);
        Object[] array = {};
        Object result = converter.convert(array, Object[].class, Object.class);
        assertThat(result).isNull();
    }

    @Test
    void arrayDoesNotConvertIfLengthIsMoreThanOne() {
        GenericConverter converter = new ArrayToObjectConverter(null);
        Object[] array = {"test", "test"};
        Object result = converter.convert(array, Object[].class, Object.class);
        assertThat(result).isNull();
    }

    @Test
    void arrayElementIsConvertedIfComponentTypeDoesNotMatch() {
        TypeView<Integer> integerTypeView = Mockito.mock(TypeView.class);
        Mockito.when(integerTypeView.type()).thenReturn(Integer.class);
        Mockito.when(integerTypeView.cast(Mockito.any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(integerTypeView.defaultOrNull()).thenAnswer(invocation -> 0);

        Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(Integer.class)).thenReturn(integerTypeView);

        StandardConversionService conversionService = new StandardConversionService(introspector);
        conversionService.addConverter(String.class,
            Integer.class,
            new StringToNumberConverterFactory().create(Integer.class));

        GenericConverter converter = new ArrayToObjectConverter(conversionService);
        Object[] array = {"1"};
        // Use Object[].class as the source type, so we can test that the element type is used, instead of the array component type
        Object result = converter.convert(array, Object[].class, Integer.class);
        assertThat(result)
                .isNotNull()
                .isEqualTo(1);
    }
}