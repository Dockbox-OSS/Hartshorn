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

package org.dockbox.hartshorn.logging;

public class Slf4jApplicationLogger extends CallerLookupApplicationLogger {

    @Override
    public void enableDebugLogging(boolean active) {
        // Not supported by SLF4J directly, so we do nothing. Specific implementations may support this.
        log().warn("Changing level at runtime is not supported by SLF4J, please use a different application logger");
    }
}
