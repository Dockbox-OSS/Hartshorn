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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SimpleTypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ConverterIntrospectionHelper {

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(Class<T> type, Supplier<T> supplier) {
        TypeConstructorsIntrospector<T> constructors = Mockito.mock(TypeConstructorsIntrospector.class);
        try {
            Constructor<T> defaultConstructor = type.getConstructor();
            Assertions.assertNotNull(defaultConstructor);

            ConstructorView<T> constructorView = Mockito.mock(ConstructorView.class);
            Mockito.when(constructorView.constructor()).thenReturn(Option.of(defaultConstructor));
            Mockito.when(constructorView.create()).thenAnswer(invocation -> Attempt.of(supplier.get()));

            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.of(constructorView));
        }
        catch (NoSuchMethodException e) {
            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.empty());
        }

        TypeParametersIntrospector parametersIntrospector = Mockito.mock(TypeParametersIntrospector.class);
        Mockito.when(parametersIntrospector.resolveInputFor(Collection.class)).thenReturn(new SimpleTypeParameterList(List.of()));

        TypeView<T> typeView = Mockito.mock(TypeView.class);
        Mockito.when(typeView.constructors()).thenReturn(constructors);
        Mockito.when(typeView.typeParameters()).thenReturn(parametersIntrospector);

        Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(type)).thenReturn(typeView);
        return introspector;
    }

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(Class<T> type) {
        return createIntrospectorForCollection(type, () -> null);
    }
}
