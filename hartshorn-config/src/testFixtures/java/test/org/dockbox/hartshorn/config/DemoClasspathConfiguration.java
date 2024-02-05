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

package test.org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.config.annotations.IncludeResourceConfiguration;
import org.dockbox.hartshorn.config.annotations.Value;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@IncludeResourceConfiguration({ "fs:junit", "classpath:junit" })
public class DemoClasspathConfiguration {

    @Value("junit.cp")
    private String classPathValue;

    @Value("junit.unset")
    private String classPathValueWithDefault = "myDefaultValue";

    @Value("junit.number")
    private int number;

    @Value("junit.list")
    private Collection<Integer> list;

    @Value("junit.list")
    private CopyOnWriteArrayList<Integer> copyOnWriteArrayList;

    public String classPathValue() {
        return this.classPathValue;
    }

    public String classPathValueWithDefault() {
        return this.classPathValueWithDefault;
    }

    public int number() {
        return this.number;
    }

    public Collection<Integer> list() {
        return this.list;
    }

    public List<Integer> copyOnWriteArrayList() {
        return this.copyOnWriteArrayList;
    }
}
