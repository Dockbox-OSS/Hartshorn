package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

import lombok.Getter;

@Getter
@AutoCreating
public class BackingImplementationContext extends DefaultContext {

    private final Map<Class<?>, Object> implementations = HartshornUtils.emptyConcurrentMap();

    public <P> Exceptional<P> get(final Class<P> type) {
        return Exceptional.of(() -> (P) this.implementations.get(type));
    }

    public <P> P computeIfAbsent(final Class<P> key, @NotNull final Function<? super Class<P>, P> mappingFunction) {
        return (P) this.implementations.computeIfAbsent(key, (Function<? super Class<?>, ?>) mappingFunction);
    }
}
