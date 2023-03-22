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

rootProject.name = "Hartshorn"

gradle.startParameter.isContinueOnFailure = true

includeBuild("gradle/plugins")

includeReconfigured(
        ":hartshorn-util",
        ":hartshorn-discovery",
        ":hartshorn-introspect",
        ":hartshorn-introspect:hartshorn-introspect-reflection",
        ":hartshorn-core",
        ":hartshorn-proxy",
        ":hartshorn-proxy:hartshorn-proxy-cglib",
        ":hartshorn-proxy:hartshorn-proxy-javassist",
        ":hartshorn-cdi",
        ":hartshorn-reporting",
        ":hartshorn-hsl",
        ":hartshorn-config",
        ":hartshorn-config:hartshorn-config-jackson",
        ":hartshorn-i18n",
        ":hartshorn-events",
        ":hartshorn-commands",
        ":hartshorn-cache",
        ":hartshorn-cache:hartshorn-cache-caffeine",
        ":hartshorn-jpa",
        ":hartshorn-jpa:hartshorn-jpa-hibernate",
        ":hartshorn-web",
        ":hartshorn-web:hartshorn-web-jetty",
        ":hartshorn-web:hartshorn-web-freemarker",
        ":hartshorn-mvc",
        ":hartshorn-mvc:hartshorn-mvc-freemarker",
        ":hartshorn-test-suite",
)

fun includeReconfigured(vararg paths: String) {
    paths.forEach { path ->
        include(path)
        val project = project(path)
        project.buildFileName = "${project.name}.gradle.kts"
    }
}
