package test.org.dockbox.hartshorn.web.route.support;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.SimpleComponentKeyMatcher;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.launchpad.component.SimpleComponentRegistry;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.PostRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.RouteMapping;
import org.dockbox.hartshorn.web.route.RouterCustomizer;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.support.DeclarativeRouterPathConfigurer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Map;

@HartshornIntegrationTest(includeBasePackages = false)
public class DeclarativeRouterPathConfigurerTests {

    @Inject
    private Logger logger;

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
                componentRegistry, null, null, null
        );
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry, logger);
        configurer.configure(registrar);

        Map<RouteMapping, RequestHandler> mappings = registry.mappings();
        Assertions.assertThat(mappings)
                .hasSize(2);
        Assertions.assertThat(mappings.keySet())
                .extracting(RouteMapping::method, RouteMapping::pathPattern)
                .containsExactlyInAnyOrder(
                        Assertions.tuple(HttpMethod.GET, "/api/test"),
                        Assertions.tuple(HttpMethod.POST, "/api/test")
                );
    }

    @Router
    static class TestRouter {

        @GetRoute("/api/test")
        public void testGet() {}

        @PostRoute("/api/test")
        public void testPost() {}
    }
}
