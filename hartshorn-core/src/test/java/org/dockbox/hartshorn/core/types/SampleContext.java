package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.context.DefaultContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SampleContext extends DefaultContext {
    private String name;
}
