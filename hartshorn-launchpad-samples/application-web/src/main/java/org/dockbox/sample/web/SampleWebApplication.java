package org.dockbox.sample.web;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.UseReporting;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.error.ErrorHandlerRegistry;

@UseWebServer
@UseReporting
public class SampleWebApplication {

    void main(String[] args) {
        HartshornApplication.createApplication(args).initialize(application -> {
            application.includeBasePackages(false);
        });
    }

    @Singleton
    @CompositeMember
    public Customizer<ErrorHandlerRegistry> customErrorHandlers() {
        return registry -> {
            registry.register(ArrayIndexOutOfBoundsException.class, (e, _, res) -> {
                res.setStatus(HttpStatus.OK.code());
                res.getWriter().write("Manually captured " + e.getMessage());
            });
        };
    }
}
