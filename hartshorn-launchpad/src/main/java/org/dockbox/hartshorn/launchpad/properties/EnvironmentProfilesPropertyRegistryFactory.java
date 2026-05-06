/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.launchpad.properties;

import org.dockbox.hartshorn.context.SingleElementContext;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.resources.ClassPathResourceLookupStrategy;
import org.dockbox.hartshorn.launchpad.resources.FileSystemLookupStrategy;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.launchpad.resources.StrategyResourceLookup;
import org.dockbox.hartshorn.profiles.support.ConfigurationProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.support.FromPropertyProfileNameResolver;
import org.dockbox.hartshorn.profiles.ProfileNameResolver;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.aggregation.ProfilePropertyRegistryAggregator;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistryFactory;
import org.dockbox.hartshorn.profiles.aggregation.SimpleProfilePropertyRegistryAggregator;
import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.SingleConfiguredProperty;
import org.dockbox.hartshorn.properties.loader.FilePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PropertyRegistryPathLoader;
import org.dockbox.hartshorn.properties.loader.support.CompositePredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.spi.DiscoveryService;
import org.dockbox.hartshorn.spi.ServiceDiscoveryException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.configure.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.configure.StreamableConfigurer;
import org.dockbox.hartshorn.util.stream.StreamGatherers;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory for creating {@link PropertyRegistry} instances based on a collection of
 * {@link PropertySourceResolver}s. The sources are resolved and loaded into the registry using a
 * {@link PropertyRegistryPathLoader}, which may be composed of multiple loaders. The
 * {@link PropertyRegistryPathLoader} instances are resolved from SPI providers, allowing for
 * extensibility.
 *
 * @see PropertyRegistryPathLoader
 * @see PropertySourceResolver
 * @see ResourceLookup
 * @see PropertyRegistry
 * @see ProfileRegistry
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class EnvironmentProfilesPropertyRegistryFactory implements PropertyRegistryFactory {

    public static final String DEFAULT_CONFIGURATION_NAME = "application";

    private final List<PropertySourceResolver> propertySourceResolvers;
    private final Properties additionalProperties;
    private final ResourceLookup resourceLookup;
    private final ProfileNameResolver profileNameResolver;

    public EnvironmentProfilesPropertyRegistryFactory(
        List<PropertySourceResolver> propertySourceResolvers,
        Properties additionalProperties,
        ResourceLookup resourceLookup,
        ProfileNameResolver profileNameResolver
    ) {
        this.propertySourceResolvers = propertySourceResolvers;
        this.additionalProperties = additionalProperties;
        this.resourceLookup = resourceLookup;
        this.profileNameResolver = profileNameResolver;
    }

    @Override
    public ProfilePropertyRegistry createRegistry() {
        try {
            Set<PropertyRegistryPathLoader> propertyRegistryLoaders = this.resolveRegistryLoaders();
            PropertyRegistryPathLoader propertyRegistryLoader =
                this.createRegistryLoader(propertyRegistryLoaders);
            PropertyRegistry defaultRegistry =
                this.loadDefaultPropertyRegistry(propertyRegistryLoader);
            Set<ConfiguredProperty> configuredProperties = this.loadAdditionalProperties();
            // Register early to allow overriding active profiles. Preferably this would only
            // register the specific `hartshorn.profiles` property, but as the profile name resolver
            // is external, we cannot be sure of that.
            defaultRegistry.registerAll(configuredProperties);

            ProfilePropertyRegistry registry =
                this.loadProfilePropertyRegistry(propertyRegistryLoader, defaultRegistry);
            // Register again to ensure additional properties override any profile properties
            registry.registerAll(configuredProperties);

            return registry;
        }
        catch (IOException e) {
            throw new ComponentInitializationException("Could not initialize property registry", e);
        }
    }

    private PropertyRegistry loadDefaultPropertyRegistry(
        PropertyRegistryPathLoader propertyRegistryLoader
    ) throws IOException {
        PropertyRegistry propertyRegistry = new MapPropertyRegistry();
        SequencedSet<URI> resources = this.resolveResources();
        for (URI resource : resources) {
            propertyRegistryLoader.loadRegistry(propertyRegistry, resource);
        }
        return propertyRegistry;
    }

    private ProfilePropertyRegistry loadProfilePropertyRegistry(
        PropertyRegistryPathLoader propertyRegistryLoader,
        PropertyRegistry defaultRegistry
    ) {
        ProfileRegistryFactory profileRegistryFactory = new ConfigurationProfileRegistryFactory(
            propertyRegistryLoader,
            this::resolveProfileResources,
            MapPropertyRegistry::new,
            this.profileNameResolver
        );
        ProfileRegistry profileRegistry = profileRegistryFactory.create(defaultRegistry);
        ProfilePropertyRegistryAggregator aggregator =
            new SimpleProfilePropertyRegistryAggregator();
        return aggregator.aggregate(profileRegistry);
    }

    private Set<ConfiguredProperty> loadAdditionalProperties() {
        return this.additionalProperties.stringPropertyNames().stream()
            .map(propertyName -> new SingleConfiguredProperty(propertyName,
                this.additionalProperties.getProperty(propertyName)))
            .collect(Collectors.toSet());
    }

    private SequencedSet<URI> resolveResources() {
        return this.propertySourceResolvers.stream()
            .flatMap(resolver -> resolver.resolve().stream())
            .flatMap(source -> this.resourceLookup.lookup(source).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private SequencedSet<URI> resolveProfileResources(String name) {
        SequencedSet<String> fileNames =
            getSources(DEFAULT_CONFIGURATION_NAME + "-%s".formatted(name));
        return fileNames.stream()
            .flatMap(source -> this.resourceLookup.lookup(source).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<PropertyRegistryPathLoader> resolveRegistryLoaders() {
        Set<PropertyRegistryPathLoader> propertyRegistryLoaders;
        try {
            propertyRegistryLoaders = DiscoveryService.instance()
                    .discoverAll(PropertyRegistryPathLoader.class);
        }
        catch (ServiceDiscoveryException e) {
            throw new ComponentInitializationException(
                "Failed to initialize PropertyRegistryLoaders",
                e);
        }
        return propertyRegistryLoaders;
    }

    private PropertyRegistryPathLoader createRegistryLoader(
        Collection<PropertyRegistryPathLoader> propertyRegistryLoaders
    ) {
        PropertyRegistryPathLoader propertyRegistryLoader;
        if (propertyRegistryLoaders.size() == 1) {
            propertyRegistryLoader = CollectionUtilities.first(propertyRegistryLoaders);
        }
        else {
            CompositePredicatePropertyRegistryLoader composite =
                new CompositePredicatePropertyRegistryLoader();
            for (PropertyRegistryPathLoader registryLoader : propertyRegistryLoaders) {
                if (registryLoader instanceof PredicatePropertyRegistryLoader predicateLoader) {
                    composite.addLoader(predicateLoader);
                }
                else {
                    throw new ApplicationRuntimeException("Found multiple PropertyRegistryLoaders, "
                        + "but cannot differentiate between them. "
                        + "Please implement PredicatePropertyRegistryLoader for "
                        + registryLoader.getClass().getName());
                }
            }
            propertyRegistryLoader = composite;
        }
        return propertyRegistryLoader;
    }

    /**
     * Creates a new {@link ContextualInitializer} for an
     * {@link EnvironmentProfilesPropertyRegistryFactory}, which can be configured using the given
     * {@link Customizer}.
     *
     * @param customizer the customizer to configure the factory
     *
     * @return the contextual initializer for the factory
     */
    public static ContextualInitializer<ApplicationEnvironment, ? extends PropertyRegistryFactory>
    create(Customizer<Configurer> customizer) {
        return environment -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            List<PropertySourceResolver> resolvers =
                configurer.propertySourceResolvers.initialize(environment);
            Properties additionalProperties = resolveAdditionalProperties(configurer, environment);
            ProfileNameResolver profileNameResolver =
                configurer.profileNameResolver.initialize(environment);

            return new EnvironmentProfilesPropertyRegistryFactory(
                resolvers,
                additionalProperties,
                environment.input().resourceLookup(),
                profileNameResolver
            );
        };
    }

    private static Properties resolveAdditionalProperties(
        Configurer configurer,
        SingleElementContext<? extends ApplicationEnvironment> environmentInitializerContext
    ) {
        List<CustomPropertiesResolver> customPropertiesResolvers =
            configurer.customPropertyResolvers.initialize(environmentInitializerContext);
        return customPropertiesResolvers.stream()
            .map(resolver -> resolver.resolveProperties(environmentInitializerContext))
            .reduce(new Properties(), (current, next) -> {
                current.putAll(next);
                return current;
            });
    }

    /**
     * Get possible sources for the given configuration name. By default, this supports YAML and
     * Java properties files, both from the file system and classpath.
     *
     * @param name the configuration name
     *
     * @return a set of possible sources
     */
    protected static SequencedSet<String> getSources(String name) {
        Set<PropertyRegistryPathLoader> loaders = getActivePathLoaders();
        Set<String> extensions = loaders.stream()
                .gather(StreamGatherers.filterByType(FilePropertyRegistryLoader.class))
                .flatMap(loader -> loader.supportedExtensions().stream())
                .collect(Collectors.toSet());

        if (extensions.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(StringUtilities.matrix()
            .segment(FileSystemLookupStrategy.NAME, ClassPathResourceLookupStrategy.NAME)
            .segment(StrategyResourceLookup.STRATEGY_SEPARATOR)
            .segment(name)
            .segment(".")
            .segment(extensions)
            .build()
        );
    }

    /**
     * Attempts to load available {@link PropertyRegistryPathLoader} from the discovery service. If
     * discovery fails for any reason whatsoever, an empty {@link Set} is returned instead.
     *
     * @return available {@link PropertyRegistryPathLoader}, or an empty {@link Set}.
     */
    protected static Set<PropertyRegistryPathLoader> getActivePathLoaders() {
        try {
            return DiscoveryService.instance()
                    .discoverAll(PropertyRegistryPathLoader.class);
        } catch (ServiceDiscoveryException e) {
            return Set.of();
        }
    }

    /**
     * Configurer for the {@link EnvironmentProfilesPropertyRegistryFactory}.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        // checkstyle:off LineLength
        private final LazyStreamableConfigurer<ApplicationEnvironment, PropertySourceResolver> propertySourceResolvers = LazyStreamableConfigurer.of(customizer -> {
            customizer.add(ContextualInitializer.of(TypeDiscoveryPropertySourceResolver::new));
            customizer.add(new PredefinedPropertySourceResolver(getSources(
                EnvironmentProfilesPropertyRegistryFactory.DEFAULT_CONFIGURATION_NAME)));
        });
        private final LazyStreamableConfigurer<ApplicationEnvironment, CustomPropertiesResolver> customPropertyResolvers = LazyStreamableConfigurer.of(customizer -> {
            customizer.add(new CommandLineArgumentsPropertiesResolver());
        });

        private ContextualInitializer<ApplicationEnvironment, ProfileNameResolver> profileNameResolver =
            ContextualInitializer.of(FromPropertyProfileNameResolver::new);
        // checkstyle:on LineLength

        /**
         * Adds a set of property source resolvers to be included in the property registry.
         *
         * @param resolvers the resolvers to add
         *
         * @return this configurer
         */
        public Configurer propertySourceResolvers(Collection<PropertySourceResolver> resolvers) {
            return this.propertySourceResolvers(configuration -> configuration.addAll(resolvers));
        }

        /**
         * Customizes the set of property source resolvers.
         *
         * @param customizer the customizer
         *
         * @return this configurer
         */
        public Configurer propertySourceResolvers(
            Customizer<StreamableConfigurer<ApplicationEnvironment, PropertySourceResolver>>
                customizer
        ) {
            this.propertySourceResolvers.customizer(customizer);
            return this;
        }

        /**
         * Adds a set of custom property resolvers to be included in the property registry.
         *
         * @param resolvers the resolvers to add
         *
         * @return this configurer
         */
        public Configurer customPropertyResolvers(Collection<CustomPropertiesResolver> resolvers) {
            return this.customPropertyResolvers(configuration -> configuration.addAll(resolvers));
        }

        /**
         * Customizes the set of custom property resolvers.
         *
         * @param customizer the customizer
         *
         * @return this configurer
         */
        public Configurer customPropertyResolvers(
            Customizer<StreamableConfigurer<ApplicationEnvironment, CustomPropertiesResolver>>
                customizer
        ) {
            this.customPropertyResolvers.customizer(customizer);
            return this;
        }

        /**
         * Adds a set of custom properties to be included in the property registry.
         *
         * @param properties the properties to add
         *
         * @return this configurer
         */
        public Configurer customProperties(Collection<String> properties) {
            return this.customPropertyResolvers(configuration -> configuration.add(
                new StringListCustomPropertiesResolver(List.copyOf(properties))
            ));
        }

        /**
         * Set the profile name resolver.
         *
         * @param profileNameResolver the resolver
         *
         * @return this configurer
         */
        public Configurer profileNameResolver(ProfileNameResolver profileNameResolver) {
            return this.profileNameResolver(ContextualInitializer.of(profileNameResolver));
        }

        /**
         * Set the profile name resolver.
         *
         * @param resolver the resolver
         *
         * @return this configurer
         */
        public Configurer profileNameResolver(
            ContextualInitializer<ApplicationEnvironment, ProfileNameResolver> resolver
        ) {
            this.profileNameResolver = resolver;
            return this;
        }
    }
}
