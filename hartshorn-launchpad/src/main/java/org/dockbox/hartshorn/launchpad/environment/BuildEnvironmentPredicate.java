/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.environment;

import java.util.Set;

import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.parse.StandardPropertyParsers;

/**
 * A predicate that checks if the application is running in a build environment. This is useful
 * to determine if certain features should be enabled or disabled.
 *
 * <p>Supported environments include:
 * <ul>
 *     <li>Any environment that configured the property {@code hartshorn.environment.build}</li>
 *     <li>GitLab CI, using the <a href="https://docs.gitlab.com/ee/ci/variables/predefined_variables.html">{@code GITLAB_CI}</a> environment variable</li>
 *     <li>Jenkins, using the <a href="https://www.jenkins.io/doc/book/managing/system-configuration/">{@code JENKINS_HOME}</a> environment variable</li>
 *     <li>Travis CI, using the <a href="https://docs.travis-ci.com/user/environment-variables/#default-environment-variables">{@code TRAVIS}</a> environment variable</li>
 *     <li>GitHub Actions, using the <a href="https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables">{@code GITHUB_ACTIONS}</a> environment variable</li>
 *     <li>AppVeyor, using the <a href="https://www.appveyor.com/docs/environment-variables/">{@code APPVEYOR}</a> environment variable</li>
 *     <li>Any environment that defines any of the above environment variables, where the value equals {@code "true"}</li>
 * </ul>
 *
 * <p>If an environment does not define any of the above environment variables, this predicate will
 * return {@code false}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class BuildEnvironmentPredicate {

    private static final Set<String> BUILD_ENVIRONMENT_VARIABLES = Set.of(
            "GITLAB_CI",
            "JENKINS_HOME",
            "TRAVIS",
            "GITHUB_ACTIONS",
            "APPVEYOR"
    );

    /**
     * Checks if the application is running in a build environment. Returns {@code true} if any of the
     * supported environment variables are defined, and the value of the variable equals {@code "true"}.
     *
     * @return whether the application is running in a build environment
     */
    public static boolean isBuildEnvironment(PropertyRegistry propertyRegistry) {
        boolean configuredBuildSetting = propertyRegistry
                .value("hartshorn.environment.build", StandardPropertyParsers.BOOLEAN)
                .test(Boolean::booleanValue);
        if (configuredBuildSetting) {
            return true;
        }

        for(String buildEnvironmentVariable : BUILD_ENVIRONMENT_VARIABLES) {
            if(System.getenv().containsKey(buildEnvironmentVariable)) {
                String value = System.getenv().get(buildEnvironmentVariable);
                if (Boolean.parseBoolean(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
