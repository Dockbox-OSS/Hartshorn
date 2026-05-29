/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.web.route.support;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.SimpleComponentKeyMatcher;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.launchpad.component.SimpleComponentRegistry;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.PostRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.PathHandlerSpec;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.support.DeclarativeRouterPathConfigurer;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.StaticPathPartSpec;
import org.dockbox.hartshorn.web.spec.parser.SimplePathParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

@HartshornIntegrationTest(includeBasePackages = false)
public class DeclarativeRouterPathConfigurerTests {

    @Inject
    private Introspector introspector;

    @Test
    void routerComponentsWithRoutesAreRegistered() {
        SimpleComponentRegistry componentRegistry = new SimpleComponentRegistry(
                SimpleComponentKeyMatcher.StrictComponentKeyMatcher.INSTANCE
        );
        TypeView<TestRouter> view = introspector.introspect(TestRouter.class);
        componentRegistry.addCustomContainer(new AnnotatedComponentContainer<>(view));

        RouterCustomizer configurer = new DeclarativeRouterPathConfigurer(
                componentRegistry, null, null, null,
                new SimplePathParser('/', List.of(
                        StaticPathPartSpec::parse
                ))
        );
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry);
        configurer.configure(registrar);

        MultiMap<PathSpec, PathHandlerSpec> mappings = registry.mappings();
        Assertions.assertThat(mappings).hasSize(1);
        PathSpec pathSpec = CollectionUtilities.first(mappings.keySet());
        Assertions.assertThat(pathSpec)
                .extracting(PathSpec::pattern, Assertions.STRING)
                .matches("api/test");
        Collection<PathHandlerSpec> handlers = mappings.get(pathSpec);
        Assertions.assertThat(handlers)
                .anyMatch(handler -> handler.method() == HttpMethod.GET)
                .anyMatch(handler -> handler.method() == HttpMethod.POST);
    }

    @Router
    static class TestRouter {

        @GetRoute("/api/test")
        public void testGet() {}

        @PostRoute("/api/test")
        public void testPost() {}
    }
}
