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

package test.org.dockbox.hartshorn.populate;

import java.util.List;
import java.util.function.Function;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.populate.ComponentFieldInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentMethodInjectionPoint;
import org.dockbox.hartshorn.component.populate.ComponentPopulationStrategy;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.component.populate.inject.InjectPopulationStrategy;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class ComponentPopulationTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testContextFieldPopulation() {
        SampleContext sampleContext = new SampleContext();
        this.applicationContext.add(sampleContext);

        ComponentPopulationStrategy strategy = InjectPopulationStrategy.create(Customizer.useDefaults())
                .initialize(SimpleSingleElementContext.create(this.applicationContext));

        PopulationTestComponent component = createAndPopulateComponent(strategy, type -> createFieldInjectionPoint("context", type));
        Assertions.assertNotNull(component.context);
        Assertions.assertSame(sampleContext, component.context);
    }

    @Test
    void testInjectFieldPopulation() {
        ComponentPopulationStrategy strategy = InjectPopulationStrategy.create(Customizer.useDefaults())
                .initialize(SimpleSingleElementContext.create(this.applicationContext));

        PopulationTestComponent component = createAndPopulateComponent(strategy, type -> createFieldInjectionPoint("applicationContext", type));
        Assertions.assertNotNull(component.applicationContext);
        Assertions.assertSame(this.applicationContext, component.applicationContext);
    }

    @Test
    void testInjectMethodPopulation() {
        ComponentPopulationStrategy strategy = InjectPopulationStrategy.create(Customizer.useDefaults())
                .initialize(SimpleSingleElementContext.create(this.applicationContext));

        PopulationTestComponent component = createAndPopulateComponent(strategy, type -> createMethodInjectionPoint("setApplicationContext", List.of(ApplicationContext.class), type));
        Assertions.assertNotNull(component.applicationContext);
        Assertions.assertSame(this.applicationContext, component.applicationContext);
    }

    @Test
    void testInjectMixedMethodPopulation() {
        SampleContext sampleContext = new SampleContext();
        this.applicationContext.add(sampleContext);

        ComponentPopulationStrategy strategy = InjectPopulationStrategy.create(Customizer.useDefaults())
                .initialize(SimpleSingleElementContext.create(this.applicationContext));

        PopulationTestComponent component = createAndPopulateComponent(strategy, type -> createMethodInjectionPoint("setContexts", List.of(ApplicationContext.class, SampleContext.class), type));
        Assertions.assertNotNull(component.applicationContext);
        Assertions.assertSame(this.applicationContext, component.applicationContext);

        Assertions.assertNotNull(component.context);
        Assertions.assertSame(sampleContext, component.context);
    }

    private PopulationTestComponent createAndPopulateComponent(ComponentPopulationStrategy strategy, Function<TypeView<PopulationTestComponent>, ComponentInjectionPoint<PopulationTestComponent>> injectionPointProvider) {
        PopulationTestComponent component = new PopulationTestComponent();
        TypeView<PopulationTestComponent> typeView = this.applicationContext.environment().introspector().introspect(component);
        PopulateComponentContext<PopulationTestComponent> componentContext = new PopulateComponentContext<>(component, component, typeView, this.applicationContext);

        ComponentInjectionPoint<PopulationTestComponent> injectionPoint = injectionPointProvider.apply(typeView);
        Assertions.assertDoesNotThrow(() -> strategy.populate(componentContext, injectionPoint));

        return component;
    }

    @NotNull
    private static ComponentInjectionPoint<PopulationTestComponent> createFieldInjectionPoint(String fieldName, TypeView<PopulationTestComponent> typeView) {
        FieldView<PopulationTestComponent, ?> fieldView = typeView.fields().named(fieldName).get();
        return new ComponentFieldInjectionPoint<>(fieldView);
    }

    @NotNull
    private static ComponentInjectionPoint<PopulationTestComponent> createMethodInjectionPoint(String methodName, List<Class<?>> parameterTypes, TypeView<PopulationTestComponent> typeView) {
        MethodView<PopulationTestComponent, ?> methodView = typeView.methods().named(methodName, parameterTypes).get();
        return new ComponentMethodInjectionPoint<>(methodView);
    }

    public static class PopulationTestComponent {

        @Inject
        private SampleContext context;

        @Inject
        private ApplicationContext applicationContext;

        @Inject
        public void setApplicationContext(ApplicationContext context) {
            this.applicationContext = context;
        }

        @Inject
        public void setContexts(ApplicationContext applicationContext, SampleContext context) {
            this.applicationContext = applicationContext;
            this.context = context;
        }
    }

    public static class SampleContext extends DefaultContext {}
}
