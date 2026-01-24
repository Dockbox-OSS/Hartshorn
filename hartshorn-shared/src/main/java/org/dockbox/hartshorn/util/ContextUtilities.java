package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.NamedContext;
import org.dockbox.hartshorn.context.SimpleContextIdentity;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public final class ContextUtilities {

    private ContextUtilities() {
    }

    public static <C extends Context> Option<C> first(
            Iterable<Context> contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

    public static <C extends Context> Option<C> first(
            Iterable<Context> contexts,
            ContextIdentity<C> identity
    ) {
        for (Context context : contexts) {
            if (identity.type().isInstance(context)) {
                if (context instanceof NamedContext namedContext && identity.name() != null) {
                    if (identity.name().equals(namedContext.name())) {
                        return Option.of(identity.type().cast(context));
                    }
                }
                else {
                    return Option.of(identity.type().cast(context));
                }
            }
        }
        return Option.empty();
    }

    public static <C extends Context> Option<C> first(
            Context[] contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

    public static <C extends Context> Option<C> first(
            Context[] contexts,
            ContextIdentity<C> identity
    ) {
        return first(List.of(contexts), identity);
    }
}
