/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.ObjectDescriber;
import org.dockbox.hartshorn.util.ObjectDescriptionStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectDescriberTest {

    @Test
    void testStyle() {
        String description = ObjectDescriber.of(new Object(), new TestObjectDescriptionStyle())
                .field("field1", "value1")
                .field("field2", "value2")
                .describe();

        String[] descriptionElements = description.split("\\+");
        Assertions.assertEquals(5, descriptionElements.length);
        Assertions.assertEquals(TestObjectDescriptionStyle.START, descriptionElements[0]);
        Assertions.assertEquals("FIELD:field1:value1", descriptionElements[1]);
        Assertions.assertEquals(TestObjectDescriptionStyle.FIELD_SEPARATOR, descriptionElements[2]);
        Assertions.assertEquals("FIELD:field2:value2", descriptionElements[3]);
        Assertions.assertEquals(TestObjectDescriptionStyle.END, descriptionElements[4]);
    }

    private static class TestObjectDescriptionStyle implements ObjectDescriptionStyle {

        private static final String START = "START";
        private static final String END = "END";
        private static final String FIELD = "FIELD:%s:%s";
        private static final String FIELD_SEPARATOR = "SEP";

        @Override
        public void describeStart(StringBuilder builder, Object object) {
            builder.append(START).append("+");
        }

        @Override
        public void describeEnd(StringBuilder builder, Object object) {
            builder.append(END);
        }

        @Override
        public void describeField(StringBuilder builder, Object object, String fieldName, Object fieldValue) {
            builder.append(FIELD.formatted(fieldName, fieldValue)).append("+");
        }

        @Override
        public void describeFieldSeparator(StringBuilder builder, Object object) {
            builder.append(FIELD_SEPARATOR).append("+");
        }
    }
}
