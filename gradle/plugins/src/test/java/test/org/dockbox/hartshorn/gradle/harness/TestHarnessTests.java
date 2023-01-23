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

package test.org.dockbox.hartshorn.gradle.harness;

import org.dockbox.hartshorn.gradle.harness.StandardTestHarnessProjectProcessor;
import org.dockbox.hartshorn.gradle.harness.TestHarnessExtension;
import org.dockbox.hartshorn.gradle.harness.TestHarnessPlugin;
import org.dockbox.hartshorn.gradle.harness.TestHarnessProjectProcessor;
import org.dockbox.hartshorn.gradle.harness.TestHarnessProjectType;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.execution.ProjectConfigurer;
import org.gradle.execution.TaskPathProjectEvaluator;
import org.gradle.initialization.DefaultBuildCancellationToken;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestHarnessTests {

    @Test
    void testDefinitionProjectTypeHasCorrectProcessor() {
        final TestHarnessProjectProcessor processor = new TestHarnessProjectProcessor() {
            @Override
            public void configureHarnessDefinition(final Project target, final TestHarnessExtension extension) {
                // Ok, do nothing
            }

            @Override
            public void configureHarnessImplementation(final Project target, final TestHarnessExtension extension) {
                Assertions.fail("Should not be called");
            }
        };
        final TestHarnessProjectType projectType = TestHarnessProjectType.DEFINITION;
        projectType.process(processor, null, null);
    }

    @Test
    void testImplementationProjectTypeHasCorrectProcessor() {
        final TestHarnessProjectProcessor processor = new TestHarnessProjectProcessor() {
            @Override
            public void configureHarnessDefinition(final Project target, final TestHarnessExtension extension) {
                Assertions.fail("Should not be called");
            }

            @Override
            public void configureHarnessImplementation(final Project target, final TestHarnessExtension extension) {
                // Ok, do nothing
            }
        };
        final TestHarnessProjectType projectType = TestHarnessProjectType.IMPLEMENTATION;
        projectType.process(processor, null, null);
    }

    @Test
    void testStandaloneProjectTypeHasCorrectProcessor() {
        final TestHarnessProjectProcessor processor = new TestHarnessProjectProcessor() {
            @Override
            public void configureHarnessDefinition(final Project target, final TestHarnessExtension extension) {
                Assertions.fail("Should not be called");
            }

            @Override
            public void configureHarnessImplementation(final Project target, final TestHarnessExtension extension) {
                Assertions.fail("Should not be called");
            }
        };
        final TestHarnessProjectType projectType = TestHarnessProjectType.STANDALONE;
        projectType.process(processor, null, null);
    }

    @Test
    void testExtensionIsRegisteredBeforeEvaluation() {
        final Project project = this.createProject();
        this.preConfigureProject(project);
        final TestHarnessExtension extension = project.getExtensions().findByType(TestHarnessExtension.class);
        Assertions.assertNotNull(extension);
    }

    @Test
    void testNoSourceSetIsCreatedForStandaloneProject() {
        final Project project = this.createProject();
        this.preConfigureProject(project);
        this.evaluateProjectWithType(project, TestHarnessProjectType.STANDALONE);

        final JavaPluginExtension extension = project.getExtensions().findByType(JavaPluginExtension.class);
        final SourceSetContainer sourceSets = extension.getSourceSets();
        Assertions.assertEquals(2, sourceSets.size());

        assertJavaSourceSetsExist(sourceSets);
    }

    @Test
    void testSourceSetIsCreatedForDefinitionProject() {
        final Project project = this.createProject();
        this.preConfigureProject(project);
        this.evaluateProjectWithType(project, TestHarnessProjectType.DEFINITION);

        final JavaPluginExtension extension = project.getExtensions().findByType(JavaPluginExtension.class);
        final SourceSetContainer sourceSets = extension.getSourceSets();
        Assertions.assertEquals(3, sourceSets.size());

        assertJavaSourceSetsExist(sourceSets);

        final SourceSet definition = sourceSets.findByName(StandardTestHarnessProjectProcessor.TEST_HARNESS_SOURCE_SET_NAME);
        Assertions.assertNotNull(definition);
    }

    @Test
    void testSourceSetIsCreatedForImplementationProject() {
        final Project parent = this.createProject();
        this.preConfigureProject(parent);
        this.evaluateProjectWithType(parent, TestHarnessProjectType.DEFINITION);

        final Project project = this.createProject(parent);
        this.preConfigureProject(project);
        this.evaluateProjectWithType(project, TestHarnessProjectType.IMPLEMENTATION);

        final JavaPluginExtension extension = project.getExtensions().findByType(JavaPluginExtension.class);
        final SourceSetContainer sourceSets = extension.getSourceSets();
        Assertions.assertEquals(3, sourceSets.size());

        assertJavaSourceSetsExist(sourceSets);

        final SourceSet implementation = sourceSets.findByName(StandardTestHarnessProjectProcessor.TEST_HARNESS_EXTENSIONS_SOURCE_SET_NAME);
        Assertions.assertNotNull(implementation);
    }

    private static void assertJavaSourceSetsExist(final SourceSetContainer sourceSets) {
        final SourceSet main = sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME);
        Assertions.assertNotNull(main);

        final SourceSet test = sourceSets.findByName(SourceSet.TEST_SOURCE_SET_NAME);
        Assertions.assertNotNull(test);
    }

    private Project createProject() {
        return this.createProject(null);
    }

    private Project createProject(final Project parent) {
        return ProjectBuilder.builder()
                .withParent(parent)
                .build();
    }

    private void preConfigureProject(final Project project) {
        project.getPluginManager().apply("java");
        project.getPluginManager().apply(TestHarnessPlugin.class);
    }

    private void evaluateProjectWithType(final Project project, final TestHarnessProjectType type) {
        this.preConfigureProject(project);
        project.getExtensions().configure(TestHarnessExtension.class, extension -> extension.setProjectType(type));

        final ProjectConfigurer evaluator = new TaskPathProjectEvaluator(new DefaultBuildCancellationToken());
        Assertions.assertTrue(project instanceof ProjectInternal);

        evaluator.configureFully((ProjectInternal) project);
    }
}
