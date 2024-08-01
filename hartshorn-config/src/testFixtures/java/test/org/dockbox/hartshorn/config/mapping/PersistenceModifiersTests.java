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

package test.org.dockbox.hartshorn.config.mapping;

import java.util.List;

import org.dockbox.hartshorn.config.JsonInclusionRule;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.annotations.UseSerialization;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.StringUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HartshornTest(includeBasePackages = false)
@UseSerialization
public abstract class PersistenceModifiersTests {

    protected abstract ObjectMapper objectMapper();

    @Test
    void testSkipEmptyKeepsNonEmpty() throws ObjectMappingException {
        ObjectMapper mapper = this.objectMapper().skipBehavior(JsonInclusionRule.SKIP_EMPTY);
        ModifierElement element = new ModifierElement(List.of("sample", "other"));
        String out = mapper.write(element);

        Assertions.assertNotNull(out);
        Assertions.assertEquals("{\"names\":[\"sample\",\"other\"]}", StringUtilities.strip(out));
    }

    @Test
    void testSkipEmptySkipsEmpty() throws ObjectMappingException {
        ObjectMapper mapper = this.objectMapper().skipBehavior(JsonInclusionRule.SKIP_EMPTY);
        ModifierElement element = new ModifierElement(List.of());
        String out = mapper.write(element);

        Assertions.assertNotNull(out);
        Assertions.assertEquals("{}", StringUtilities.strip(out));
    }
}
