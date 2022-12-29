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

package org.dockbox.hartshorn.gradle.testcontract;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.testing.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TestContractPlugin implements Plugin<Project> {

    private static final String TEST_CONTRACTS_SOURCE_SET_NAME = "testContracts";

    @Override
    public void apply(final Project target) {
        final ProjectType projectType = this.lookupProjectType(target);

        //noinspection UseOfSystemOutOrSystemErr
        System.out.printf("Determined project type %S for %s%n", projectType, target.getName());

        this.configureImplementationConfigurations(target, projectType);
        this.configureTests(target, projectType);
    }

    private void configureImplementationConfigurations(final Project target, final ProjectType projectType) {
        if (projectType == ProjectType.CONTRACT_API) {
            final JavaPluginExtension javaExtension = target.getExtensions().getByType(JavaPluginExtension.class);
            final SourceSet sourceSet = javaExtension.getSourceSets().create(TEST_CONTRACTS_SOURCE_SET_NAME);

            final Optional<Configuration> testContractsImplementationConfiguration = this.lookupConfiguration(target, TEST_CONTRACTS_SOURCE_SET_NAME);
            if (testContractsImplementationConfiguration.isEmpty()) return;

            testContractsImplementationConfiguration.ifPresent(configuration -> {
                this.configureConfigurationExtensions(target, configuration);
            });

            this.sourceSet(target, SourceSet.MAIN_SOURCE_SET_NAME).ifPresent(mainSourceSet -> {
                sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(mainSourceSet.getOutput()));
                sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(mainSourceSet.getOutput()));
            });
        }
    }

    private void configureConfigurationExtensions(final Project target, final Configuration testContractsImplementationConfiguration) {
        final Set<Configuration> extendsFrom = new HashSet<>();

        final Optional<Configuration> mainImplementationConfiguration = this.lookupConfiguration(target, SourceSet.MAIN_SOURCE_SET_NAME);
        mainImplementationConfiguration.ifPresent(extendsFrom::add);

        final Optional<Configuration> testImplementationConfiguration = this.lookupConfiguration(target, SourceSet.TEST_SOURCE_SET_NAME);
        testImplementationConfiguration.ifPresent(extendsFrom::add);

        testContractsImplementationConfiguration.setExtendsFrom(extendsFrom);
    }

    private Optional<Configuration> lookupConfiguration(final Project target, final String sourceSetName) {
        return this.sourceSet(target, sourceSetName)
                .map(SourceSet::getImplementationConfigurationName)
                .map(target.getConfigurations()::findByName);
    }

    private Optional<SourceSet> sourceSet(final Project target, final String sourceSetName) {
        final JavaPluginExtension javaExtension = target.getExtensions().getByType(JavaPluginExtension.class);
        final SourceSetContainer sourceSets = javaExtension.getSourceSets();
        return Optional.ofNullable(sourceSets.findByName(sourceSetName));
    }

    private ProjectType lookupProjectType(final Project target) {
        if (target.getParent() == null) return ProjectType.STANDALONE;

        final Set<Project> subprojects = target.getSubprojects();
        if (subprojects.isEmpty()) return ProjectType.CONTRACT_IMPLEMENTATION;

        return ProjectType.CONTRACT_API;
    }

    private void configureTests(final Project target, final ProjectType projectType) {
        if (projectType == ProjectType.CONTRACT_IMPLEMENTATION) {
            final Test integrationTestTask = target.getTasks().register("integrationTests", Test.class, task -> {

                final Project parent = task.getProject().getParent();
                final Optional<SourceSet> itSourceSet = this.sourceSet(parent, TEST_CONTRACTS_SOURCE_SET_NAME);
                if (itSourceSet.isEmpty()) return;

                final Optional<SourceSet> mainSourceSet = this.sourceSet(task.getProject(), SourceSet.MAIN_SOURCE_SET_NAME);
                if (mainSourceSet.isEmpty()) return;

                final FileCollection itClasses = itSourceSet.get().getOutput().getClassesDirs();
                task.setTestClassesDirs(itClasses);

                final FileCollection mainRuntimeClasspath = mainSourceSet.get().getRuntimeClasspath();
                final FileCollection itRuntimeClasspath = itSourceSet.get().getRuntimeClasspath();
                task.setClasspath(itRuntimeClasspath.plus(mainRuntimeClasspath));

                task.setGroup("verification");
                task.setDescription("Runs API defined integration tests.");
                task.useJUnitPlatform();
            }).get();

            target.getTasks().getByName("test").dependsOn(integrationTestTask);
        }
    }
}
