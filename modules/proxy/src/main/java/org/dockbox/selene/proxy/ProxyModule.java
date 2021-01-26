package org.dockbox.selene.proxy;

import org.dockbox.selene.core.annotations.module.Module;
import org.dockbox.selene.core.server.bootstrap.Preloadable;

@Module(id = "proxies", name = "Proxies", description = "Adds global and dynamic proxy handling",
        authors = "GuusLieben")
public class ProxyModule implements Preloadable {

    @Override
    public void preload() {
        ProxyableBootstrap.boostrapDelegates();
    }
}
