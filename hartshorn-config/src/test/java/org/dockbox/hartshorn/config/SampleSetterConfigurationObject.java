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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.config.annotations.ConfigurationObject;

@ConfigurationObject(prefix = "user")
public class SampleSetterConfigurationObject {

    private String name;
    private int age;

    public String name() {
        return this.name;
    }

    // Classic bean-style setter
    public void setName(final String name) {
        this.name = name + "!";
    }

    public int age() {
        return this.age;
    }

    // Fluent-style setter, private
    private SampleSetterConfigurationObject setAge(final int age) {
        this.age = age + 10;
        return this;
    }
}
