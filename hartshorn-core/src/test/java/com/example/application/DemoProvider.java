package com.example.application;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;

@Service
@RequiresActivator(UseDemo.class)
public class DemoProvider {

    @Provider
    public Demo demo() {
        return new DemoImpl();
    }
}
