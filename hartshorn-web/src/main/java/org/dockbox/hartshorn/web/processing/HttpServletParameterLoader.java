package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoader;
import org.dockbox.hartshorn.core.services.parameter.RuleBasedParameterLoader;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.ServletResponseParameterRule;

import javax.inject.Named;

@Binds(value = ParameterLoader.class, named = @Named("http_webserver"))
public class HttpServletParameterLoader extends RuleBasedParameterLoader<HttpRequestParameterLoaderContext> {

    public HttpServletParameterLoader() {
        this.add(new BodyRequestParameterRule());
        this.add(new HeaderRequestParameterRule());
        this.add(new ServletRequestParameterRule());
        this.add(new ServletResponseParameterRule());
    }
}
