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
import org.dockbox.hartshorn.util.introspect.scan.CachedTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.ClassReferenceLoadException;
import org.dockbox.hartshorn.util.introspect.scan.PredefinedSetTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.classpath.ClassPathScannerTypeReferenceCollector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.introspect.types.ScanAnnotation;
import test.org.dockbox.hartshorn.introspect.types.ScanClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.NonStaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.StaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanEnum;
import test.org.dockbox.hartshorn.introspect.types.ScanInterface;
import test.org.dockbox.hartshorn.introspect.types.ScanRecord;

public class TypeCollectorTests {

    @Test
    void testClassPathScannerTypeCollector() throws TypeCollectionException {
        TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector("test.org.dockbox.hartshorn.introspect.types");
        Set<TypeReference> typeReferences = collector.collect();

        Assertions.assertEquals(7, typeReferences.size());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad(classLoader);
            }
            catch (ClassReferenceLoadException e) {
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

    @Test
    void testCachedTypeCollector() throws TypeCollectionException {
        TypeReferenceCollector collector = new ClassPathScannerTypeReferenceCollector("test.org.dockbox.hartshorn.introspect.types");
        TypeReferenceCollector cachedCollector = new CachedTypeReferenceCollector(collector);

        Set<TypeReference> typeReferencesA = cachedCollector.collect();
        Set<TypeReference> typeReferencesB = cachedCollector.collect();

        Assertions.assertSame(typeReferencesA, typeReferencesB);
    }

    @Test
    void testAggregateTypeCollector() throws TypeCollectionException {
        PredefinedSetTypeReferenceCollector enumCollector = PredefinedSetTypeReferenceCollector.of(ScanEnum.class);
        PredefinedSetTypeReferenceCollector classCollector = PredefinedSetTypeReferenceCollector.of(ScanClass.class);
        PredefinedSetTypeReferenceCollector interfaceCollector = PredefinedSetTypeReferenceCollector.of(ScanInterface.class);

        TypeReferenceCollector collector = new AggregateTypeReferenceCollector(enumCollector, classCollector, interfaceCollector);
        Set<TypeReference> typeReferences = collector.collect();

        Assertions.assertEquals(3, typeReferences.size());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<? extends Class<?>> types = typeReferences.stream().map(typeReference -> {
            try {
                return typeReference.getOrLoad(classLoader);
            }
            catch (ClassReferenceLoadException e) {
                return Assertions.fail(e);
            }
        }).collect(Collectors.toSet());

        Assertions.assertTrue(types.contains(ScanEnum.class));
        Assertions.assertTrue(types.contains(ScanClass.class));
        Assertions.assertTrue(types.contains(ScanInterface.class));
    }

}
