package test.org.dockbox.hartshorn.web.route.support;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.SimpleComponentKeyMatcher;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.launchpad.component.SimpleComponentRegistry;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.GetRoute;
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
        Assertions.assertThat(mappings)
                .hasSize(2);
//        Assertions.assertThat(mappings.entrySet())
//                .extracting(
//                        entry -> entry.getValue().method(),
//                        entry -> entry.getKey().pattern()
//                )
//                .containsExactlyInAnyOrder(
//                        Assertions.tuple(HttpMethod.GET, "/api/test"),
//                        Assertions.tuple(HttpMethod.POST, "/api/test")
//                );
    }

    @Router
    static class TestRouter {

        @GetRoute("/api/test")
        public void testGet() {}

        @PostRoute("/api/test")
        public void testPost() {}
    }
}
