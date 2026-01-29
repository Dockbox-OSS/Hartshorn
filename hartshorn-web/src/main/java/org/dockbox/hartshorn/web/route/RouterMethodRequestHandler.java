package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.introspect.InjectorExecutableInvocationAdapter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.WebRequestScope;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.rules.HeaderValueParameterLoaderRule;

public class RouterMethodRequestHandler<P, R> implements RequestHandler, Reportable {

    private final InjectionCapableApplication application;
    private final ConversionService conversionService;
    private final MethodView<P, R> methodView;

    public RouterMethodRequestHandler(
            InjectionCapableApplication application,
            ConversionService conversionService,
            MethodView<P, R> methodView
    ) {
        this.application = application;
        this.conversionService = conversionService;
        this.methodView = methodView;
    }

    @Override
    public boolean handle(WebRequest request, WebResponse response) throws Exception {
        WebRequestScope scope = new WebRequestScope(request, response);
        var adapter = new InjectorExecutableInvocationAdapter(this.application)
                .scope(scope)
                .addParameterLoaderRule(new HeaderValueParameterLoaderRule<>(
                        request,
                        this.conversionService
                ));

        P instance = this.application.defaultProvider().get(this.methodView.declaredBy().type());
        try {
            // TODO: Handle the result appropriately. Currently does not support non-void methods.
            Option<R> result = adapter.invoke(this.methodView, instance);
            return true;
        }
        catch (Exception e) {
            // Will be handled upstream
            throw e;
        }
        catch (Throwable t) {
            // Will be handled upstream
            throw new Exception(t);
        }
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("method").writeString(this.methodView.qualifiedName());
    }
}
