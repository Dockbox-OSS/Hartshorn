package test;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;

import test.types.SampleInterface;
import test.types.SampleMetaAnnotatedImplementation;

@Service
public class SampleProviders {

    @Provider("meta")
    public SampleInterface sampleInterface = new SampleMetaAnnotatedImplementation();

}
