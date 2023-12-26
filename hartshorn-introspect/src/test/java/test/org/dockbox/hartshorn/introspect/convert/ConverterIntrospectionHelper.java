/*
 * Copyright 2019-2024 the original author or authors.
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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SimpleTypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class ConverterIntrospectionHelper {

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(Class<T> type, Supplier<T> supplier) {
        return createIntrospectorForCollection(type, supplier, capacity -> Assertions.fail("Unexpected call to capacity constructor"));
    }

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(Class<T> type, Supplier<T> supplier, IntFunction<T> capacityConstructor) {
        TypeConstructorsIntrospector<T> constructors = Mockito.mock(TypeConstructorsIntrospector.class);
        configureDefaultConstructor(type, supplier, constructors);
        configureInitialCapacityConstructor(type, capacityConstructor, constructors);

        TypeParametersIntrospector parametersIntrospector = Mockito.mock(TypeParametersIntrospector.class);
        Mockito.when(parametersIntrospector.inputFor(Collection.class)).thenReturn(new SimpleTypeParameterList(List.of()));

        TypeView<T> typeView = Mockito.mock(TypeView.class);
        Mockito.when(typeView.constructors()).thenReturn(constructors);
        Mockito.when(typeView.typeParameters()).thenReturn(parametersIntrospector);

        Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(type)).thenReturn(typeView);
        return introspector;
    }

    private static <T extends Collection<?>> void configureDefaultConstructor(Class<T> type, Supplier<T> supplier, TypeConstructorsIntrospector<T> constructors) {
        try {
            Constructor<T> defaultConstructor = type.getConstructor();
            Assertions.assertNotNull(defaultConstructor);

            ConstructorView<T> constructorView = Mockito.mock(ConstructorView.class);
            Mockito.when(constructorView.constructor()).thenReturn(Option.of(defaultConstructor));
            try {
                Mockito.when(constructorView.create()).thenAnswer(invocation -> Option.of(supplier.get()));
            }
            catch (Throwable throwable) {
                Assertions.fail("On-going stub yielded an unexpected exception", throwable);
            }

            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.of(constructorView));
        }
        catch (NoSuchMethodException e) {
            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.empty());
        }
    }

    private static <T extends Collection<?>> void configureInitialCapacityConstructor(Class<T> type, IntFunction<T> supplier, TypeConstructorsIntrospector<T> constructors) {
        try {
            Constructor<T> capacityConstructor = type.getConstructor(int.class);
            Assertions.assertNotNull(capacityConstructor);

            ConstructorView<T> constructorView = Mockito.mock(ConstructorView.class);
            Mockito.when(constructorView.constructor()).thenReturn(Option.of(capacityConstructor));
            try {
                Mockito.when(constructorView.create(Mockito.anyInt())).thenAnswer(invocation -> Option.of(supplier.apply((int) invocation.getArguments()[0])));
            }
            catch (Throwable throwable) {
                Assertions.fail("On-going stub yielded an unexpected exception", throwable);
            }

            Mockito.when(constructors.withParameters(int.class)).thenReturn(Option.of(constructorView));
        }
        catch (NoSuchMethodException e) {
            Mockito.when(constructors.withParameters(int.class)).thenReturn(Option.empty());
        }
    }

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(Class<T> type) {
        return createIntrospectorForCollection(type, () -> null);
    }
}
