package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.proxy.NativeProxyLookup;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;

import test.org.dockbox.hartshorn.util.introspect.convert.ConversionServiceTests;

public class ReflectionConversionServiceTests extends ConversionServiceTests {
    @Override
    protected Introspector introspector() {
        return new ReflectionIntrospector(new NativeProxyLookup(), new VirtualHierarchyAnnotationLookup());
    }
}
