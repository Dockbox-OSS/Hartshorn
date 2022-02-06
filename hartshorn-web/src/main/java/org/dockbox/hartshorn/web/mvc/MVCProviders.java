package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.processing.MvcParameterLoader;
import org.dockbox.hartshorn.web.servlet.MvcServlet;

@Service(activators = UseMvcServer.class)
public class MVCProviders {

    @Provider("mvc_webserver")
    public ParameterLoader mvcParameterLoader() {
        return new MvcParameterLoader();
    }

    @Provider
    public Class<MvcServlet> mvcServlet() {
        return MvcServlet.class;
    }
}
