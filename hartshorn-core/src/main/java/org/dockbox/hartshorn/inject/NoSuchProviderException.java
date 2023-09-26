package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationException;

public class NoSuchProviderException extends ApplicationException {

    public enum ProviderType {
        TYPE_AWARE,
        NON_TYPE_AWARE,
        ANY,
    }

    public NoSuchProviderException(ComponentKey<?> componentKey) {
        this(ProviderType.ANY, componentKey);
    }

    public NoSuchProviderException(ProviderType providerType, ComponentKey<?> componentKey) {
        super("No %s found for component key '%s'".formatted(
                switch(providerType) {
                    case TYPE_AWARE -> "type-aware provider";
                    case NON_TYPE_AWARE -> "non type-aware provider";
                    case ANY -> "provider";
                },
                componentKey));
    }
}
