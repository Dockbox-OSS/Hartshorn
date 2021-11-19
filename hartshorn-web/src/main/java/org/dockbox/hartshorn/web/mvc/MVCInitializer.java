package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpWebServer;

import java.util.Set;

public interface MVCInitializer {

    public static final String TEMPLATE_ROOT = HttpWebServer.WEB_INF + "templates/";

    void initialize(ApplicationContext applicationContext) throws ApplicationException;
    String transform(ViewTemplate template, ViewModel model) throws ApplicationException;
    Set<ViewTemplate> templates();
}
