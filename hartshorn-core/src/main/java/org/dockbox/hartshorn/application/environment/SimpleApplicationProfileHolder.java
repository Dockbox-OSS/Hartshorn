package org.dockbox.hartshorn.application.environment;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ComposableProfileHolder;
import org.dockbox.hartshorn.profiles.SimpleComposableProfileHolder;
import org.dockbox.hartshorn.profiles.loader.ApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.CommandLineArgumentProfileLoader;
import org.dockbox.hartshorn.profiles.loader.CompositeProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.props.PropertiesApplicationProfileLoader;
import org.dockbox.hartshorn.profiles.loader.jackson.yaml.YamlApplicationProfileLoader;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.resources.ClassLoaderClasspathResourceLocator;
import org.dockbox.hartshorn.util.resources.ClassPathResourceLookupStrategy;
import org.dockbox.hartshorn.util.resources.FallbackResourceLookup;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public final class SimpleApplicationProfileHolder extends SimpleComposableProfileHolder {

    public SimpleApplicationProfileHolder(Set<ApplicationProfile> profiles) {
        super(profiles);
    }

    public static ContextualInitializer<ApplicationEnvironment, ComposableProfileHolder> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            String defaultProfile = configurer.defaultProfile.initialize(context);
            ResourceLookup resourceLookup = configurer.resourceLookup.initialize(context);

            ProfileLoaderConfiguration configuration = new ProfileLoaderConfiguration(defaultProfile, resourceLookup, context.input());
            List<ApplicationProfileLoader> profileLoaders = configurer.profileLoaders.initialize(context.transform(configuration));

            CompositeProfileLoader profileLoader = new CompositeProfileLoader(profileLoaders);
            try {
                Set<ApplicationProfile> applicationProfiles = profileLoader.loadProfile(defaultProfile);
                return new SimpleApplicationProfileHolder(applicationProfiles);
            }
            catch(ApplicationException e) {
                throw new ComponentInitializationException("Failed to load application profiles", e);
            }
        };
    }

    public static class Configurer {

        private final LazyStreamableConfigurer<ProfileLoaderConfiguration, ApplicationProfileLoader> profileLoaders = LazyStreamableConfigurer.of(configurer -> {
            configurer.addAll(
                    configuration -> new PropertiesApplicationProfileLoader(configuration.input().resourceLookup()),
                    configuration -> new YamlApplicationProfileLoader(configuration.input().resourceLookup()),
                    configuration -> new CommandLineArgumentProfileLoader(configuration.input().environment().applicationArguments())
            );
        });

        private ContextualInitializer<ApplicationEnvironment, String> defaultProfile = ContextualInitializer.of("application");
        private ContextualInitializer<ApplicationEnvironment, ResourceLookup> resourceLookup = ContextualInitializer.of(environment -> {
            ClassLoaderClasspathResourceLocator locator = new ClassLoaderClasspathResourceLocator();
            ClassPathResourceLookupStrategy strategy = new ClassPathResourceLookupStrategy(locator);
            return new FallbackResourceLookup(strategy);
        });

        public Configurer defaultProfile(String defaultProfile) {
            this.defaultProfile = ContextualInitializer.of(defaultProfile);
            return this;
        }

        public Configurer defaultProfile(ContextualInitializer<ApplicationEnvironment, String> defaultProfile) {
            this.defaultProfile = defaultProfile;
            return this;
        }

        public Configurer resourceLookup(ResourceLookup resourceLookup) {
            this.resourceLookup = ContextualInitializer.of(resourceLookup);
            return this;
        }

        public Configurer resourceLookup(ContextualInitializer<ApplicationEnvironment, ResourceLookup> resourceLookup) {
            this.resourceLookup = resourceLookup;
            return this;
        }

        public Configurer profileLoaders(ApplicationProfileLoader... profileLoaders) {
            return this.profileLoaders(List.of(profileLoaders));
        }

        public Configurer profileLoaders(Collection<ApplicationProfileLoader> profileLoaders) {
            this.profileLoaders.customizer(configurer -> configurer.addAll(profileLoaders));
            return this;
        }

        public Configurer profileLoaders(Customizer<StreamableConfigurer<ProfileLoaderConfiguration, ApplicationProfileLoader>> customizer) {
            this.profileLoaders.customizer(customizer);
            return this;
        }
    }

    public record ProfileLoaderConfiguration(String defaultProfile, ResourceLookup resourceLookup, ApplicationEnvironment environment) {}
}
