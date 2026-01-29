package org.dockbox.hartshorn.web.route.rules;

import org.dockbox.hartshorn.util.introspect.convert.AdditionalTargetTypeContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.rest.Header;

public class HeaderValueParameterLoaderRule<C extends ParameterLoaderContext>
        implements ParameterLoaderRule<C> {

    private final WebRequest request;
    private final ConversionService conversionService;

    public HeaderValueParameterLoaderRule(WebRequest request, ConversionService conversionService) {
        this.request = request;
        this.conversionService = conversionService;
    }

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, C context, Object... args) {
        return parameter.annotations().has(Header.class);
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, C context, Object... args) {
        Header header = parameter.annotations().get(Header.class).orElseThrow(() -> {
            return new IllegalStateException("Parameter is not annotated with @Header");
        });
        String value = this.request.headers().get(header.value()).orNull();
        T result = this.conversionService.convert(
                value,
                parameter.type().type(),
                new AdditionalTargetTypeContext<>(parameter.type())
        );
        return Option.of(result);
    }
}
