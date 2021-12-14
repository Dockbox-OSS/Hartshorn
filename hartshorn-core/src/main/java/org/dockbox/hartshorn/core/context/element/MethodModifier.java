package org.dockbox.hartshorn.core.context.element;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
public class MethodModifier<T, P> implements ElementModifier<MethodContext<T, P>> {

    @Getter
    private final MethodContext<T, P> element;

    public void invoker(final MethodInvoker<T, P> invoker) {
        this.element.invoker(invoker);
    }

    public void access(final boolean expose) {
        this.element().method().setAccessible(expose);
    }

    public static void defaultInvoker(final MethodInvoker<?, ?> invoker) {
        MethodContext.defaultInvoker(invoker);
    }
}
