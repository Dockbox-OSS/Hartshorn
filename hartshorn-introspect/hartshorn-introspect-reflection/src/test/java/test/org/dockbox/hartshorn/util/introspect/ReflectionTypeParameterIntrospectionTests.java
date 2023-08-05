package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.proxy.lookup.NativeProxyLookup;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;

public class ReflectionTypeParameterIntrospectionTests extends TypeParameterIntrospectionTests {
    @Override
    protected Introspector introspector() {
        return new ReflectionIntrospector(new NativeProxyLookup(), new VirtualHierarchyAnnotationLookup());
    }
}
