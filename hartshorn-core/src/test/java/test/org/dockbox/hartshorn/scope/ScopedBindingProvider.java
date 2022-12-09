package test.org.dockbox.hartshorn.scope;

import org.dockbox.hartshorn.component.InstallTo;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Binds;

@Service
public class ScopedBindingProvider {

    @Binds
    @InstallTo(SampleScope.class)
    public String bind() {
        return "test";
    }

    public static class SampleScope implements Scope {

        @Override
        public Class<? extends Scope> installableScopeType() {
            return SampleScope.class;
        }
    }
}
