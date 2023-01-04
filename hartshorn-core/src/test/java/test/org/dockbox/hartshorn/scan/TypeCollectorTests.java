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

package test.org.dockbox.hartshorn.scan;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.scan.AggregateTypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.CachedTypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.TypeReference;
import org.dockbox.hartshorn.application.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.application.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import test.org.dockbox.hartshorn.scan.types.ScanAnnotation;
import test.org.dockbox.hartshorn.scan.types.ScanClass;
import test.org.dockbox.hartshorn.scan.types.ScanClass.NonStaticInnerClass;
import test.org.dockbox.hartshorn.scan.types.ScanClass.StaticInnerClass;
import test.org.dockbox.hartshorn.scan.types.ScanEnum;
import test.org.dockbox.hartshorn.scan.types.ScanInterface;
import test.org.dockbox.hartshorn.scan.types.ScanRecord;

@HartshornTest(includeBasePackages = false)
public class TypeCollectorTests {

    @InjectTest
    void testClassPathScannerTypeCollector(final ApplicationEnvironment environment) {
        final TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector(environment, "test.org.dockbox.hartshorn.scan.types");
        final Set<TypeReference> typeReferences = collector.collect();

        Assertions.assertEquals(7, typeReferences.size());

        final Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad();
            }
            catch (final Exception e) {
                return Assertions.fail(e);
            }
        }).collect(Collectors.toSet());

        Assertions.assertTrue(types.contains(ScanAnnotation.class));
        Assertions.assertTrue(types.contains(ScanClass.class));
        Assertions.assertTrue(types.contains(NonStaticInnerClass.class));
        Assertions.assertTrue(types.contains(StaticInnerClass.class));
        Assertions.assertTrue(types.contains(ScanEnum.class));
        Assertions.assertTrue(types.contains(ScanInterface.class));
        Assertions.assertTrue(types.contains(ScanRecord.class));
    }

    @InjectTest
    void testCachedTypeCollector(final ApplicationEnvironment environment) {
        final TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector(environment, "test.org.dockbox.hartshorn.scan.types");
        final TypeReferenceCollector cachedCollector = new CachedTypeReferenceCollector(collector);

        final Set<TypeReference> typeReferencesA = cachedCollector.collect();
        final Set<TypeReference> typeReferencesB = cachedCollector.collect();

        Assertions.assertSame(typeReferencesA, typeReferencesB);
    }

    @Test
    void testAggregateTypeCollector() {
        final PredefinedSetTypeReferenceCollector enumCollector = PredefinedSetTypeReferenceCollector.of(ScanEnum.class);
        final PredefinedSetTypeReferenceCollector classCollector = PredefinedSetTypeReferenceCollector.of(ScanClass.class);
        final PredefinedSetTypeReferenceCollector interfaceCollector = PredefinedSetTypeReferenceCollector.of(ScanInterface.class);

        final TypeReferenceCollector collector = new AggregateTypeReferenceCollector(enumCollector, classCollector, interfaceCollector);
        final Set<TypeReference> typeReferences = collector.collect();

        Assertions.assertEquals(3, typeReferences.size());

        final Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad();
            }
            catch (final Exception e) {
                return Assertions.fail(e);
            }
        }).collect(Collectors.toSet());

        Assertions.assertTrue(types.contains(ScanEnum.class));
        Assertions.assertTrue(types.contains(ScanClass.class));
        Assertions.assertTrue(types.contains(ScanInterface.class));
    }

}
