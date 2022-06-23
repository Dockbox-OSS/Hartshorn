package org.dockbox.hartshorn.hsl.runtime;

public class Return extends RuntimeException {

    private final Object value;

    public Return(final Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    public Object value() {
        return this.value;
    }
}
