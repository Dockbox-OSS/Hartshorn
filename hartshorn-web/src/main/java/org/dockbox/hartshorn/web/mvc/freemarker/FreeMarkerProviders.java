package org.dockbox.hartshorn.web.mvc.freemarker;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.web.annotations.UseMvcServer;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;

import javax.inject.Singleton;

@Service(activators = UseMvcServer.class, requires = "freemarker.template.Template")
public class FreeMarkerProviders {

    @Provider
    @Singleton
    public MVCInitializer mvcInitializer() {
        return new FreeMarkerMVCInitializer();
    }
}
