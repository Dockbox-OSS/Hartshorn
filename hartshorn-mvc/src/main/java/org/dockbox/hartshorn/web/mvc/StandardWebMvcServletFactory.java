package org.dockbox.hartshorn.web.mvc;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.mvc.template.ViewTemplate;

@Service
@RequiresActivator(UseMvcServer.class)
public class StandardWebMvcServletFactory implements WebMvcServletFactory {

    @Override
    public MvcServlet mvc(final MethodView<?, ViewTemplate> method) {
        return new MvcServlet(method);
    }
}
