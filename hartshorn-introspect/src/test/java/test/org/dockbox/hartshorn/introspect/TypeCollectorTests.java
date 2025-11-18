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

package test.org.dockbox.hartshorn.introspect;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.util.introspect.scan.AggregateTypeReferenceCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.dockbox.hartshorn.util.introspect.scan.CachedTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.ClassReferenceLoadException;
import org.dockbox.hartshorn.util.introspect.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.introspect.types.ScanAnnotation;
import test.org.dockbox.hartshorn.introspect.types.ScanClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.NonStaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.StaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanEnum;
import test.org.dockbox.hartshorn.introspect.types.ScanInterface;
import test.org.dockbox.hartshorn.introspect.types.ScanRecord;

class TypeCollectorTests {

    @Test
    void classPathScannerTypeCollector() throws Exception {
        TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector("test.org.dockbox.hartshorn.introspect.types");
        Set<TypeReference> typeReferences = collector.collect();

        assertThat(typeReferences).hasSize(7);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad(classLoader);
            }
            catch (ClassReferenceLoadException e) {
                return fail(e);
            }
        }).collect(Collectors.toSet());

        assertThat(types).contains(ScanAnnotation.class);
        assertThat(types).contains(ScanClass.class);
        assertThat(types).contains(NonStaticInnerClass.class);
        assertThat(types).contains(StaticInnerClass.class);
        assertThat(types).contains(ScanEnum.class);
        assertThat(types).contains(ScanInterface.class);
        assertThat(types).contains(ScanRecord.class);
    }

    @Test
    void cachedTypeCollector() throws Exception {
        TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector("test.org.dockbox.hartshorn.introspect.types");
        TypeReferenceCollector cachedCollector = new CachedTypeReferenceCollector(collector);

        Set<TypeReference> typeReferencesA = cachedCollector.collect();
        Set<TypeReference> typeReferencesB = cachedCollector.collect();

        assertThat(typeReferencesB).isSameAs(typeReferencesA);
    }

    @Test
    void aggregateTypeCollector() throws Exception {
        PredefinedSetTypeReferenceCollector enumCollector = PredefinedSetTypeReferenceCollector.of(ScanEnum.class);
        PredefinedSetTypeReferenceCollector classCollector = PredefinedSetTypeReferenceCollector.of(ScanClass.class);
        PredefinedSetTypeReferenceCollector interfaceCollector = PredefinedSetTypeReferenceCollector.of(ScanInterface.class);

        TypeReferenceCollector collector = new AggregateTypeReferenceCollector(enumCollector, classCollector, interfaceCollector);
        Set<TypeReference> typeReferences = collector.collect();

        assertThat(typeReferences).hasSize(3);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad(classLoader);
            }
            catch (ClassReferenceLoadException e) {
                return fail(e);
            }
        }).collect(Collectors.toSet());

        assertThat(types).contains(ScanEnum.class);
        assertThat(types).contains(ScanClass.class);
        assertThat(types).contains(ScanInterface.class);
    }

}
