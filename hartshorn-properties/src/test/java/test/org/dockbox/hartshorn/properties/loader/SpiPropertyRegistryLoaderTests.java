package test.org.dockbox.hartshorn.properties.loader;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.properties.loader.FilePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonJavaPropsPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonJavaPropsSpiPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonYamlPropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.JacksonYamlSpiPropertyRegistryLoader;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URI;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SpiPropertyRegistryLoaderTests {

    // checkstyle:off LineLength
    public static Stream<Arguments> spiLoaderArguments() {
        Stream<Arguments> yaml = JacksonYamlPropertyRegistryLoader.DEFAULT_EXTENSIONS.stream()
                .map(extension -> Arguments.of(
                        "tools.jackson.dataformat.yaml.YAMLMapper",
                        extension,
                        (Supplier<PredicatePropertyRegistryLoader>) JacksonYamlSpiPropertyRegistryLoader::new
                ));
        Stream<Arguments> javaProps = JacksonJavaPropsPropertyRegistryLoader.DEFAULT_EXTENSIONS.stream()
                .map(extension -> Arguments.of(
                        "tools.jackson.dataformat.javaprop.JavaPropsMapper",
                        extension,
                        (Supplier<PredicatePropertyRegistryLoader>) JacksonJavaPropsSpiPropertyRegistryLoader::new
                ));
        return Stream.concat(yaml, javaProps);
    }
    // checkstyle:on LineLength

    @ParameterizedTest
    @MethodSource("spiLoaderArguments")
    void spiLoaderIsDisabledIfRequiredClassIsAbsent(
            String requiredClass,
            String extension,
            Supplier<PredicatePropertyRegistryLoader> loaderSupplier
    ) {
        MockedStatic<TypeUtils> typeUtils = Mockito.mockStatic(TypeUtils.class);
        typeUtils.when(() -> TypeUtils.exists(requiredClass))
                .thenReturn(false);

        PredicatePropertyRegistryLoader loader = loaderSupplier.get();
        // URI's that are typically compatible, should now be incompatible as they cannot be parsed
        Assertions.assertThat(loader.isCompatible(
                URI.create("noop://properties.%s".formatted(extension))
        )).isFalse();

        // As files cannot be parsed, none of the extensions are supported
        Assertions.assertThat(((FilePropertyRegistryLoader) loader).supportedExtensions())
                .isEmpty();

        // Should never call loadRegistry on a non-compatible loader
        Assertions.assertThatThrownBy(() -> loader.loadRegistry(null, null))
                .isInstanceOf(IllegalStateException.class);

        // Release for next mock invocation
        typeUtils.close();
    }
}
