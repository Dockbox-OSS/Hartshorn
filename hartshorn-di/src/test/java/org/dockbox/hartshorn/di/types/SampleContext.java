package org.dockbox.hartshorn.di.types;

import org.dockbox.hartshorn.di.context.DefaultContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SampleContext extends DefaultContext {
    private String name;
}
