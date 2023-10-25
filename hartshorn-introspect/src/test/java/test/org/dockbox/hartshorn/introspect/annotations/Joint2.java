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

package test.org.dockbox.hartshorn.introspect.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@Route(method = HttpMethod.POST, path = "joint")
public @interface Joint2 {
    @AttributeAlias(target = Get.class, value = "path")
    String path();
}
