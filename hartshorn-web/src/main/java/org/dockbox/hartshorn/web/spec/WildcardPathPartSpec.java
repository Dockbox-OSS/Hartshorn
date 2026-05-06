/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.spec;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public class WildcardPathPartSpec implements PathPartSpec {

    private final String captureName;

    public WildcardPathPartSpec() {
        this.captureName = null;
    }

    public WildcardPathPartSpec(String captureName) {
        this.captureName = captureName;
    }

    public static Option<WildcardPathPartSpec> parse(String part) {
        if (part.equals("*")) {
            return Option.of(new WildcardPathPartSpec());
        }
        if (part.startsWith("{*") && part.endsWith("}")) {
            String captureName = part.substring(2, part.length() - 1);
            if (captureName.isEmpty()) {
                return Option.empty();
            }
            return Option.of(new WildcardPathPartSpec(captureName));
        }
        return Option.empty();
    }

    @Override
    public String stringValue() {
        if (this.captureName != null) {
            return "{*%s}".formatted(this.captureName);
        }
        return "*";
    }

    @Override
    public boolean matches(String pathPart, Map<String, String> pathParameters) {
        if (this.captureName != null) {
            pathParameters.put(this.captureName, pathPart);
        }
        return true;
    }
}
