package test.types.factory;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import test.types.SampleInterface;

@Binds(SampleInterface.class)
@AllArgsConstructor(onConstructor_ = @Bound)
public class FactorySampleImplementation implements SampleInterface {
    @Getter
    private final String name;
}
