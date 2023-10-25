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

package org.dockbox.hartshorn.hsl.runtime;

public class ExecutionOptions {

    private boolean permitAmbiguousExternalFunctions = true;
    private boolean permitDuplicateModules = true;
    private boolean enableAssertions = true;

    public boolean permitAmbiguousExternalFunctions() {
        return this.permitAmbiguousExternalFunctions;
    }

    public ExecutionOptions permitAmbiguousExternalFunctions(boolean permitAmbiguousModuleFunctions) {
        this.permitAmbiguousExternalFunctions = permitAmbiguousModuleFunctions;
        return this;
    }

    public boolean permitDuplicateModules() {
        return this.permitDuplicateModules;
    }

    public ExecutionOptions permitDuplicateModules(boolean permitDuplicateModules) {
        this.permitDuplicateModules = permitDuplicateModules;
        return this;
    }

    public boolean enableAssertions() {
        return this.enableAssertions;
    }

    public ExecutionOptions enableAssertions(boolean enableAssertions) {
        this.enableAssertions = enableAssertions;
        return this;
    }
}
