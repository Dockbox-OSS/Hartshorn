package org.dockbox.sample.web;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;

import java.nio.ByteBuffer;

@UseWebServer
public class SampleWebApplication {

    static void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    @CompositeMember
    Customizer<RouterPathConfigurer> routeCustomizer() {
        return routes -> {
            routes.get("/hello", (request, response) -> {
                response.write(ByteBuffer.wrap(
                        "Hello, you requested: %s %s".formatted(
                                request.method(),
                                request.path()
                        ).getBytes()
                ));
            });

            routes.get("/error", (request, response) -> {
                throw new ArrayIndexOutOfBoundsException(42);
            });
        };
    }
}
