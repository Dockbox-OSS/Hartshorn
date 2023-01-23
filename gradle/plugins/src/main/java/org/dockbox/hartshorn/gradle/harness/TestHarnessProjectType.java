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

public enum TestHarnessProjectType {
    DEFINITION(TestHarnessProjectProcessor::configureHarnessDefinition),
    IMPLEMENTATION(TestHarnessProjectProcessor::configureHarnessImplementation),
    STANDALONE((processor, project, extension) -> {}),
    ;

    private final TriConsumer<TestHarnessProjectProcessor, Project, TestHarnessExtension> processor;

    TestHarnessProjectType(final TriConsumer<TestHarnessProjectProcessor, Project, TestHarnessExtension> processor) {
        this.processor = processor;
    }

    public void process(final TestHarnessProjectProcessor processor, final Project project,
                        final TestHarnessExtension extension) {
        this.processor.accept(processor, project, extension);
    }
}
