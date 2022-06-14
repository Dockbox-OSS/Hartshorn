package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CyclingConstructorAnalyzer<T> {

    private static final Map<TypeContext<?>, ConstructorContext<?>> cache = new ConcurrentHashMap<>();
    private final TypeContext<T> type;

    public CyclingConstructorAnalyzer(final TypeContext<T> type) {
        this.type = type;
    }

    public Result<ConstructorContext<T>> findOptimalConstructor() {
        if (this.type.isAbstract()) return Result.empty();
        if (cache.containsKey(this.type)) {
            return Result.of(cache.get(this.type))
                    .map(c -> (ConstructorContext<T>) c);
        }

        ConstructorContext<T> optimalConstructor = null;
        final List<? extends ConstructorContext<T>> constructors = this.type.injectConstructors();
        if (constructors.isEmpty()) {
            final Result<? extends ConstructorContext<T>> defaultConstructor = this.type.defaultConstructor();
            if (defaultConstructor.absent()) {
                return Result.of(new IllegalStateException("No injectable constructors found for " + this.type.type()));
            }
            else optimalConstructor = defaultConstructor.get();
        }
        else {
            // An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
            // can be satiated at once.
            optimalConstructor = constructors.get(0);
            for (final ConstructorContext<T> constructor : constructors) {
                if (optimalConstructor.parameterCount() < constructor.parameterCount()) {
                    optimalConstructor = constructor;
                }
            }
        }

        return Result.of(optimalConstructor).present(c -> cache.put(this.type, c));
    }

    // TODO: Find literally any way to detect cycles in the constructor graph.
    public Result<ConstructorContext<T>> findCycle() {
        return Result.empty();
    }
}
