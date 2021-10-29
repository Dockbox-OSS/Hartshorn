package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.properties.Attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DelegatedAttributesContext extends DefaultContext {
    private final Attribute<?>[] attributes;
}
