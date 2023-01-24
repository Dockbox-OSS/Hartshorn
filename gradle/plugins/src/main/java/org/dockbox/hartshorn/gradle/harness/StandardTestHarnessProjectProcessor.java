/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.gradle.harness;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.testing.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StandardTestHarnessProjectProcessor implements TestHarnessProjectProcessor {

    public static final String TEST_HARNESS_SOURCE_SET_NAME = "testHarness";
    public static final String TEST_HARNESS_EXTENSIONS_SOURCE_SET_NAME = TEST_HARNESS_SOURCE_SET_NAME + "Extensions";

    @Override
    public void configureHarnessDefinition(final Project target, final TestHarnessExtension extension) {
        this.createSourceSet(target, extension, TEST_HARNESS_SOURCE_SET_NAME);
    }

    @Override
    public void configureHarnessImplementation(final Project target, final TestHarnessExtension extension) {
        this.createSourceSet(target, extension, TEST_HARNESS_EXTENSIONS_SOURCE_SET_NAME)
                .filter(sourceSet -> target.getParent() != null)
                .ifPresent(extensionSourceSet -> {
                    this.lookupConfiguration(target, extensionSourceSet)
                            .flatMap(extensionConfiguration -> this.sourceSet(target.getParent(), TEST_HARNESS_SOURCE_SET_NAME))
                            .ifPresent(testHarnessSourceSet -> {
                                extensionSourceSet.setCompileClasspath(extensionSourceSet.getCompileClasspath().plus(testHarnessSourceSet.getOutput()));
                                extensionSourceSet.setRuntimeClasspath(extensionSourceSet.getRuntimeClasspath().plus(testHarnessSourceSet.getOutput()));
                            });
                });
        this.configureTests(target);
    }

    private Optional<SourceSet> createSourceSet(final Project target, final TestHarnessExtension extension, final String name) {
        final JavaPluginExtension javaExtension = target.getExtensions().getByType(JavaPluginExtension.class);
        final SourceSet sourceSet = javaExtension.getSourceSets().create(name);

        final Optional<Configuration> configuration = this.lookupConfiguration(target, sourceSet);
        if (configuration.isEmpty()) return Optional.empty();

        final Configuration config = configuration.get();
        final List<Dependency> dependencies = extension.getDependencyHandler().getDependencies(target.getDependencies());
        config.getDependencies().addAll(dependencies);

        this.configureConfigurationExtensions(target, config);

        this.sourceSet(target, SourceSet.MAIN_SOURCE_SET_NAME).ifPresent(mainSourceSet -> {
            sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(mainSourceSet.getOutput()));
            sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(mainSourceSet.getOutput()));
        });

        return Optional.of(sourceSet);
    }

    private void configureConfigurationExtensions(final Project target, final Configuration configuration) {
        final Set<Configuration> extendsFrom = new HashSet<>(configuration.getExtendsFrom());

        final Optional<Configuration> main = this.lookupConfiguration(target, SourceSet.MAIN_SOURCE_SET_NAME);
        main.ifPresent(extendsFrom::add);

        final Optional<Configuration> test = this.lookupConfiguration(target, SourceSet.TEST_SOURCE_SET_NAME);
        test.ifPresent(extendsFrom::add);

        configuration.setExtendsFrom(extendsFrom);
    }

    private Optional<Configuration> lookupConfiguration(final Project target, final String sourceSetName) {
        return this.sourceSet(target, sourceSetName)
                .flatMap(sourceSet -> this.lookupConfiguration(target, sourceSet));
    }

    private Optional<Configuration> lookupConfiguration(final Project target, final SourceSet sourceSet) {
        return Optional.ofNullable(sourceSet)
                .map(SourceSet::getImplementationConfigurationName)
                .map(target.getConfigurations()::findByName);
    }

    private Optional<SourceSet> sourceSet(final Project target, final String sourceSetName) {
        final JavaPluginExtension javaExtension = target.getExtensions().getByType(JavaPluginExtension.class);
        final SourceSetContainer sourceSets = javaExtension.getSourceSets();
        return Optional.ofNullable(sourceSets.findByName(sourceSetName));
    }

    private void configureTests(final Project target) {
        final Test integrationTestTask = target.getTasks().register("integrationTests", Test.class, task -> {

            final Project parent = task.getProject().getParent();
            final Optional<SourceSet> itSourceSet = this.sourceSet(parent, TEST_HARNESS_SOURCE_SET_NAME);
            if (itSourceSet.isEmpty()) return;

            final Optional<SourceSet> mainSourceSet = this.sourceSet(task.getProject(), SourceSet.MAIN_SOURCE_SET_NAME);
            if (mainSourceSet.isEmpty()) return;

            final FileCollection itClasses = itSourceSet.get().getOutput().getClassesDirs();
            task.setTestClassesDirs(itClasses);

            final FileCollection mainRuntimeClasspath = mainSourceSet.get().getRuntimeClasspath();
            final FileCollection itRuntimeClasspath = itSourceSet.get().getRuntimeClasspath();
            task.setClasspath(itRuntimeClasspath.plus(mainRuntimeClasspath));

            final Optional<SourceSet> extensionsSourceSet = this.sourceSet(task.getProject(), TEST_HARNESS_EXTENSIONS_SOURCE_SET_NAME);
            if (extensionsSourceSet.isPresent()) {
                final FileCollection extensionsRuntimeClasspath = extensionsSourceSet.get().getRuntimeClasspath();
                task.setClasspath(task.getClasspath().plus(extensionsRuntimeClasspath));
            }

            task.setGroup("verification");
            task.setDescription("Runs API defined integration tests.");
            task.useJUnitPlatform();
        }).get();

        target.getTasks().getByName("test").dependsOn(integrationTestTask);
    }
}
