package org.dockbox.hartshorn.gradle.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;

import java.io.File;

public class JavadocVerifierPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project target) {
        target.getTasks().register("verifyJavadoc", task -> task.doLast(t -> {
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
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
