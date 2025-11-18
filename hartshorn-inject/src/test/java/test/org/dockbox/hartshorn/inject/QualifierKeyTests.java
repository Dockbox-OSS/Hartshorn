/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.CompositeQualifier;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class QualifierKeyTests {

    @Test
    void qualifierKeysWithoutMetaEqual() {
        QualifierKey<SampleQualifier> expected = QualifierKey.of(SampleQualifier.class);
        QualifierKey<SampleQualifier> actual = QualifierKey.of(SampleQualifier.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void qualifierKeysWithMetaEqual() {
        QualifierKey<Named> expected = QualifierKey.of(Named.class, Map.of("value", "sample"));
        QualifierKey<Named> actual = QualifierKey.of(Named.class, Map.of("value", "sample"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void qualifierWithoutMetaFailsIfTypeRequiresMeta() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> QualifierKey.of(Named.class));
    }

    @Test
    void qualifierWithMetaFailsIfTypeDoesNotRequireMeta() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> QualifierKey.of(SampleQualifier.class, Map.of("value", "sample")));
    }

    @Test
    void compositeKeyEqualsIfKeysEqual() {
        QualifierKey<Named> namedQualifierOneA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        QualifierKey<Named> namedQualifierOneB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierOne = new CompositeQualifier();
        compositeQualifierOne.add(namedQualifierOneA);
        compositeQualifierOne.add(namedQualifierOneB);

        QualifierKey<Named> namedQualifierTwoA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        QualifierKey<Named> namedQualifierTwoB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierTwo = new CompositeQualifier();
        compositeQualifierTwo.add(namedQualifierTwoA);
        compositeQualifierTwo.add(namedQualifierTwoB);

        assertThat(compositeQualifierTwo).isEqualTo(compositeQualifierOne);
    }

    @Test
    void compositeKeyDoesNotEqualIfOneKeyDoesNotEqual() {
        QualifierKey<Named> namedQualifierOneA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        CompositeQualifier compositeQualifierOne = new CompositeQualifier();
        compositeQualifierOne.add(namedQualifierOneA);

        QualifierKey<Named> namedQualifierTwoB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierTwo = new CompositeQualifier();
        compositeQualifierTwo.add(namedQualifierTwoB);

        assertThat(compositeQualifierTwo).isNotEqualTo(compositeQualifierOne);
    }

    public @interface SampleQualifier {}
}
