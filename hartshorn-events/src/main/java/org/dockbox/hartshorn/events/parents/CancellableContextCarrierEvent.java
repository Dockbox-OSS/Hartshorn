package org.dockbox.hartshorn.events.parents;

import org.jetbrains.annotations.NotNull;

public abstract class CancellableContextCarrierEvent extends ContextCarrierEvent implements Cancellable {

    @Override
    public @NotNull Cancellable post() {
        return (Cancellable) super.post();
    }
}
