package org.dockbox.sample.web;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

@UseWebServer
public class SampleWebApplication {

    static void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    @CompositeMember
    Customizer<RouterPathConfigurer> routeCustomizer(ApplicationContext applicationContext) {
        return routes -> {
            routes.get("/validate", (request, response) -> validationOutput(applicationContext, request, response));
            routes.get("/error", (_, _) -> {
                throw new ArrayIndexOutOfBoundsException(42);
            });
        };
    }

    private static void validationOutput(ApplicationContext applicationContext, WebRequest request, WebResponse response) throws Exception {
        response.status(200);
        response.headers().set("Content-Type", "application/json; charset=utf-8");

        ValidationResponseBody body = new ValidationResponseBody(
                request.method().name(),
                request.path(),
                ((StandardMultiMap)request.query().asMultiMap()).map(),
                request.headers().asMap(),
                request.body().asString()
        );
        ObjectMapper mapper = new ObjectMapper();
        response.write(ByteBuffer.wrap(mapper.writeValueAsBytes(body)));
    }

    private record ValidationResponseBody(
            String method,
            String path,
            Map<String, Collection<String>> queryParameters,
            Map<String, String> headers,
            String body
    ) {}
}
