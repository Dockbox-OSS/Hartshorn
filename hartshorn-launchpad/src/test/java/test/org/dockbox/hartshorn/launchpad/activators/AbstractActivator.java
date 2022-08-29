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

<<<<<<<< HEAD:hartshorn-launchpad/src/test/java/test/org/dockbox/hartshorn/launchpad/activators/AbstractActivator.java
package test.org.dockbox.hartshorn.launchpad.activators;

public abstract class AbstractActivator {
========
apply { 
    from("${project.rootDir}/gradle/publications.gradle.kts")
}

dependencies {
    implementation("org.dockbox.hartshorn:hartshorn-core")
>>>>>>>> b725fb7e3 (Draft: HSL: Common diagnostic message specification):hartshorn-hsl/hartshorn-hsl.gradle.kts
}
