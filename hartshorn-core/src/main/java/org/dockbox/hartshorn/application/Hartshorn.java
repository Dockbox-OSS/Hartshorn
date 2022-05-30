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

package org.dockbox.hartshorn.application;

/**
 * The utility type to grant easy access to project metadata.
 *
 * @author Guus Lieben
 * @since 21.1
 */
public final class Hartshorn {

    /**
     * The default package prefix to use when scanning Hartshorn internals.
     */
    public static final String PACKAGE_PREFIX = "org.dockbox.hartshorn";
    /**
     * The (human-readable) display name of Hartshorn.
     */
    public static final String PROJECT_NAME = "Hartshorn";
    /**
     * The simplified identifier for Hartshorn-default identifiers.
     */
    public static final String PROJECT_ID = "hartshorn";
    /**
     * The semantic version of the current/latest release of Hartshorn
     */
    public static final String VERSION = "22.3";

    private Hartshorn() {}
}

