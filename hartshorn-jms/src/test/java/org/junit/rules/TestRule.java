/*
 * Copyright 2019-2022 the original author or authors.
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

package org.junit.rules;

/**
 * TestContainers has a hard dependency on this class and {@link org.junit.runners.model.Statement}, both of which
 * are only present in JUnit 4. However, these types are not used if the active environment is JUnit 5. This is
 * purely a placeholder to avoid runtime classloader issues.
 * @see <a href="https://github.com/testcontainers/testcontainers-java/issues/970">testcontainers/testcontainers-java #970</a>
 */
public interface TestRule {
}
