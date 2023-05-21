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

package org.dockbox.hartshorn.gradle.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.plugins.JavaPluginExtension;

import java.io.File;
import java.io.IOException;

public class JavadocVerifierPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project target) {
        target.getTasks().register("verifyJavadoc", task -> task.doLast(task0 -> {
            final Project project = task.getProject();
            try {
                final File javaSourceDirectory = project.getExtensions().getByType(JavaPluginExtension.class)
                        .getSourceSets()
                        .getByName("main")
                        .getJava()
                        .getSourceDirectories()
                        .getSingleFile();

                // Do not attempt to verify if we are running on the root project
                if (javaSourceDirectory.exists()) {
                    JavadocVerifier.verify(javaSourceDirectory.toPath());
                }
                else {
                    // Make sure we're actually running on the root project if we can't find
                    // the source directory, otherwise something is wrong
                    assert project.getParent() == null;
                }
            }
            catch (IOException | UnknownDomainObjectException | IllegalStateException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
