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

package test.org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.util.introspect.scan.classpath.ClassPathScanner;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.introspect.types.ScanAnnotation;
import test.org.dockbox.hartshorn.introspect.types.ScanClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.NonStaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanClass.StaticInnerClass;
import test.org.dockbox.hartshorn.introspect.types.ScanEnum;
import test.org.dockbox.hartshorn.introspect.types.ScanInterface;
import test.org.dockbox.hartshorn.introspect.types.ScanRecord;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

class ClassPathScannerTests {

    @Test
    void canScanWithPackageFilter() throws Exception {
        ClassPathScanner scanner = ClassPathScanner.create()
                .includeDefaultClassPath()
                .filterPrefix("test.org.dockbox.hartshorn.introspect.types");

        Set<String> classes = new HashSet<>();
        scanner.scan(resource -> classes.add(resource.resourceName()));

        assertThat(classes)
                .hasSize(7)
                // 7 classes in the test package
                .contains(ScanAnnotation.class.getCanonicalName())
                .contains(ScanClass.class.getCanonicalName())
                // Inner classes' canonical name is OuterClass.InnerClass, while the resource name (which is also compatible with Class.forName) is OuterClass$InnerClass
                .contains(this.resourceNameFromCanonicalName(NonStaticInnerClass.class.getCanonicalName()))
                .contains(this.resourceNameFromCanonicalName(StaticInnerClass.class.getCanonicalName()))
                .contains(ScanEnum.class.getCanonicalName())
                .contains(ScanInterface.class.getCanonicalName())
                .contains(ScanRecord.class.getCanonicalName());
    }

    @Test
    void canScanWithEncodedCharacters() throws Exception {
        // Note that we use an empty directory here, so scanning will yield no results, but won't throw an exception either.
        Path dummyFolder = Files.createTempDirectory("dummy folder");
        dummyFolder.toFile().deleteOnExit();
        URL url = dummyFolder.toUri().toURL();

        assertThatCode(() -> {
            ClassPathScanner scanner = ClassPathScanner.create().addUrlForScanning(url);
            scanner.scan(resource -> fail("Should not have found any resources"));
        }).doesNotThrowAnyException();
    }

    private String resourceNameFromCanonicalName(String canonicalName) {
        int lastIndex = canonicalName.lastIndexOf('.');
        return canonicalName.substring(0, lastIndex) + '$' + canonicalName.substring(lastIndex + 1);
    }
}
