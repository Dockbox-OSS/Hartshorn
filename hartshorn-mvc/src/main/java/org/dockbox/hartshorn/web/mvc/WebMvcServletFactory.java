package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.mvc.template.ViewTemplate;

@Service
@RequiresActivator(UseMvcServer.class)
public interface WebMvcServletFactory {

    @Factory(required = false)
    MvcServlet mvc(final MethodView<?, ViewTemplate> method);
}
