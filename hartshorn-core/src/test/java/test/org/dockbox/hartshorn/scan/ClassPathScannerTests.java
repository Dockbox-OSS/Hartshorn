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

package test.org.dockbox.hartshorn.scan;

import org.dockbox.hartshorn.application.scan.classpath.ClassPathScanner;
import org.dockbox.hartshorn.application.scan.classpath.ClassPathWalkingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import test.org.dockbox.hartshorn.scan.types.ScanAnnotation;
import test.org.dockbox.hartshorn.scan.types.ScanClass;
import test.org.dockbox.hartshorn.scan.types.ScanClass.NonStaticInnerClass;
import test.org.dockbox.hartshorn.scan.types.ScanClass.StaticInnerClass;
import test.org.dockbox.hartshorn.scan.types.ScanEnum;
import test.org.dockbox.hartshorn.scan.types.ScanInterface;
import test.org.dockbox.hartshorn.scan.types.ScanRecord;

public class ClassPathScannerTests {

    @Test
    void testCanScanWithPackageFilter() throws ClassPathWalkingException {
        final ClassPathScanner scanner = ClassPathScanner.create()
                .includeDefaultClassPath()
                .filterPrefix("test.org.dockbox.hartshorn.scan.types");

        final Set<String> classes = new HashSet<>();
        scanner.scan(resource -> classes.add(resource.resourceName()));

        Assertions.assertEquals(7, classes.size()); // 7 classes in the test package

        Assertions.assertTrue(classes.contains(ScanAnnotation.class.getCanonicalName()));
        Assertions.assertTrue(classes.contains(ScanClass.class.getCanonicalName()));
        // Inner classes' canonical name is OuterClass.InnerClass, while the resource name (which is also compatible with Class.forName) is OuterClass$InnerClass
        Assertions.assertTrue(classes.contains(this.resourceNameFromCanonicalName(NonStaticInnerClass.class.getCanonicalName())));
        Assertions.assertTrue(classes.contains(this.resourceNameFromCanonicalName(StaticInnerClass.class.getCanonicalName())));
        Assertions.assertTrue(classes.contains(ScanEnum.class.getCanonicalName()));
        Assertions.assertTrue(classes.contains(ScanInterface.class.getCanonicalName()));
        Assertions.assertTrue(classes.contains(ScanRecord.class.getCanonicalName()));
    }

    private String resourceNameFromCanonicalName(final String canonicalName) {
        final int lastIndex = canonicalName.lastIndexOf('.');
        return canonicalName.substring(0, lastIndex) + '$' + canonicalName.substring(lastIndex + 1);
    }
}
