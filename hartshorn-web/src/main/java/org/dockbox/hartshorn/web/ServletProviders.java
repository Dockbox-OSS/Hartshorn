package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.dockbox.hartshorn.web.servlet.StandardWebServletFactory;
import org.dockbox.hartshorn.web.servlet.WebServletFactory;

@Service
@RequiresActivator(UseHttpServer.class)
public class ServletProviders {

    @Binds
    public ServletFactory servletFactory() {
        return new StandardServletFactory();
    }

    @Binds
    public WebServletFactory webServletFactory() {
        return new StandardWebServletFactory();
    }
}
