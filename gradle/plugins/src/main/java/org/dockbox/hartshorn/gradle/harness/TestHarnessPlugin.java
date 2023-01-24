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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TestHarnessPlugin implements Plugin<Project> {

    @Override
    public void apply(final @NonNull Project target) {
        target.getExtensions().create(TestHarnessExtension.EXTENSION_NAME, TestHarnessExtension.class);

        target.afterEvaluate((project) -> {
            final TestHarnessExtension extension = project.getExtensions().getByType(TestHarnessExtension.class);
            final TestHarnessProjectType projectType = extension.getProjectType();
            final TestHarnessProjectProcessor processor = new StandardTestHarnessProjectProcessor();
            projectType.process(processor, project, extension);
        });
    }
}
