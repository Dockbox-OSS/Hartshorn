package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.RequestQueryParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletResponseParameterRule;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("mvc_webserver"))
public class MvcParameterLoader extends RuleBasedParameterLoader<MvcParameterLoaderContext> {

    public MvcParameterLoader() {
        this.add(new ViewModelParameterRule());
        this.add(new BodyRequestParameterRule());
        this.add(new HeaderRequestParameterRule());
        this.add(new RequestQueryParameterRule());
        this.add(new ServletRequestParameterRule());
        this.add(new ServletResponseParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterContext<T> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return context.applicationContext().get(parameter.type());
    }
}
