package org.dockbox.hartshorn.web.processing;

import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;
import org.dockbox.hartshorn.web.mvc.ViewModel;

public class ViewModelParameterRule implements ParameterLoaderRule<MvcParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return parameter.type().childOf(ViewModel.class);
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final MvcParameterLoaderContext context, final Object... args) {
        return Exceptional.of((T) context.viewModel());
    }
}
