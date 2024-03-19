package test.org.dockbox.hartshorn.profiles;

import java.util.Map;
import java.util.Vector;

import org.dockbox.hartshorn.profiles.ComposableProfileHolder;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleComposableProfileHolder;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.MapApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.profiles.parse.support.ConversionServiceProfilePropertyParser;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.NativeProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SampleProfileApplication {

    @Test
    public void test() throws ApplicationException {
        ApplicationProfileLoader loader = this.createLoader();
        ComposableProfileHolder profileHolder = new SimpleComposableProfileHolder(loader.loadProfile("application"));
        ProfilePropertyRegistry composedRegistry = profileHolder.registry();
        Assertions.assertTrue(composedRegistry.has("hartshorn.name"));
        Assertions.assertTrue(composedRegistry.has("hartshorn.debug"));

        Option<ProfileProperty> debugPropertyCandidate = composedRegistry.property("hartshorn.debug");
        Assertions.assertTrue(debugPropertyCandidate.present());

        ConversionService service = new StandardConversionService(
                new ReflectionIntrospector(
                        new NativeProxyLookup(),
                        new VirtualHierarchyAnnotationLookup()
                )
        ).withDefaults();
        ProfilePropertyParser<Boolean> booleanParser = new ConversionServiceProfilePropertyParser<>(boolean.class, service);
        Option<Boolean> debugPropertyValue = debugPropertyCandidate.get().parseValue(booleanParser);
        Assertions.assertTrue(debugPropertyValue.present());
        Assertions.assertTrue(debugPropertyValue.get());

        Option<ProfileProperty> namePropertyCandidate = composedRegistry.property("hartshorn.name");
        Assertions.assertTrue(namePropertyCandidate.present());

        ValueProfileProperty property = Assertions.assertInstanceOf(ValueProfileProperty.class, namePropertyCandidate.get());
        Option<String> namePropertyValue = property.rawValue();
        Assertions.assertTrue(namePropertyValue.present());
        Assertions.assertEquals("Hartshorn", namePropertyValue.get());
    }

    private ApplicationProfileLoader createLoader() {
        return new MapApplicationProfileLoader(Map.ofEntries(
                Map.entry("hartshorn.name", "Hartshorn"),
                Map.entry("hartshorn.debug", "true")
        ));
    }
}
