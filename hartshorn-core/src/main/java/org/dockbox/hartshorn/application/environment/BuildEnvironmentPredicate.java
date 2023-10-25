package org.dockbox.hartshorn.application.environment;

import java.util.Set;

public class BuildEnvironmentPredicate {

    private static final Set<String> BUILD_ENVIRONMENT_VARIABLES = Set.of(
            "GITLAB_CI",
            "JENKINS_HOME",
            "TRAVIS",
            "GITHUB_ACTIONS",
            "APPVEYOR"
    );

    public static boolean isBuildEnvironment() {
        for(String buildEnvironmentVariable : BUILD_ENVIRONMENT_VARIABLES) {
            if(System.getenv().containsKey(buildEnvironmentVariable)) {
                return true;
            }
        }
        return false;
    }

}
