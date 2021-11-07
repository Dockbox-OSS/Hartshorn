package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;

import test.types.SampleInterface;

@Service
public interface SampleFactoryService {
    @Factory
    public SampleInterface createSample(String name);
}
