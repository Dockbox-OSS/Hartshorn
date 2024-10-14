package test.org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.scope.Scope;

public class SampleBinderPostProcessor implements HierarchicalBinderPostProcessor {

    public static final String HELLO_WORLD = "Hello, World!";

    @Override
    public void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder) {
        binder.bind(String.class).singleton(HELLO_WORLD);
    }

    @Override
    public int priority() {
        return 0;
    }
}
