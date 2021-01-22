package org.dockbox.selene.core.proxy;

/**
 * The type passed into proxy functions. Cancellability is unused internally, but may be used by functions.
 */
public class ProxyHolder {

    private boolean cancelled;

    /**
     * {@code true} if the holder is cancelled, otherwise {@code false}.
     *
     * @return {@code true} if the holder is cancelled.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancels the holder. Internally this is ignored, and only provides interoperability between different proxy
     * functions.
     *
     * @param cancelled
     *         Whether the holder should be cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
