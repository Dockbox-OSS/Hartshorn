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

package test.org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.data.annotations.Value;

@Service(singleton = false)
public class ValueTyped {

    @Value("demo")
    private String string;

    @Value("nested.demo")
    private String nestedString;

    public String string() {
        return this.string;
    }

    public String nestedString() {
        return this.nestedString;
    }
}
