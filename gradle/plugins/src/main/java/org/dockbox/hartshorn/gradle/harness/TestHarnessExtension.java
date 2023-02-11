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
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestHarnessExtension {

    public static final String EXTENSION_NAME = "testHarness";

    private final TestHarnessDependencyHandler dependencyHandler;

    private TestHarnessProjectType projectType = TestHarnessProjectType.STANDALONE;
    private Project defaultProject;

    public TestHarnessExtension() {
        this.dependencyHandler = new TestHarnessDependencyHandler();
    }

    public TestHarnessProjectType getProjectType() {
        return this.projectType;
    }

    public void setProjectType(final TestHarnessProjectType projectType) {
        this.projectType = projectType;
    }

    public TestHarnessDependencyHandler getDependencyHandler() {
        return this.dependencyHandler;
    }

    public Project getDefaultProject() {
        return this.defaultProject;
    }

    public void setDefaultProject(final Project defaultProject) {
        if (this.projectType != TestHarnessProjectType.DEFINITION) {
            throw new IllegalStateException("Default project can only be set when project type is DEFINITION");
        }
        this.defaultProject = defaultProject;
    }

    public static Project defaultProject(final ProjectDependency dependency) {
        return defaultProject(dependency.getDependencyProject());
    }

    public static Project defaultProject(final Project project) {
        final TestHarnessExtension extension = project.getExtensions().getByType(TestHarnessExtension.class);
        if (extension == null) {
            throw new IllegalStateException("Test harness extension not found on project " + project.getName());
        }
        final Project defaultProject = extension.getDefaultProject();
        if (defaultProject == null) {
            throw new IllegalStateException("Default project not set on test harness extension on project " + project.getName());
        }
        return defaultProject;
    }

    public static class TestHarnessDependencyHandler {

        private final List<Object> dependencies = new ArrayList<>();

        public void add(final Object notation) {
            this.dependencies.add(notation);
        }

        public void add(final Provider<?> property) {
            this.dependencies.add(property.get());
        }

        public List<Dependency> getDependencies(final DependencyHandler handler) {
            return this.dependencies.stream()
                    .flatMap(notation -> {
                        if (notation instanceof ExternalModuleDependencyBundle bundle) return bundle.stream().map(Dependency.class::cast);
                        else if (notation instanceof Dependency dependency) return Stream.of(dependency);
                        else return Stream.of(handler.create(notation));
                    })
                    .collect(Collectors.toList());
        }
    }
}
