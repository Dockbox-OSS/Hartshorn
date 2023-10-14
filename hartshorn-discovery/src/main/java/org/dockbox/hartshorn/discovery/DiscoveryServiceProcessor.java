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

package org.dockbox.hartshorn.discovery;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("org.dockbox.hartshorn.discovery.ServiceLoader")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class DiscoveryServiceProcessor extends AbstractProcessor {

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {
        final Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ServiceLoader.class);
        if (elements.isEmpty()) {
            return true;
        }

        for (final Element element : elements) {
            final ServiceLoader annotation = element.getAnnotation(ServiceLoader.class);

            final TypeMirror implementationType = element.asType();
            final TypeMirror targetType = getTargetType(annotation);

            final FileObject resource = this.createResource(targetType.toString());
            if (resource == null) {
                continue;
            }

            try {
                final Writer writer = resource.openWriter();
                writer.write(implementationType.toString());
                writer.close();
            }
            catch (final IOException e) {
                System.err.println("Failed to create service loader file for " + annotation.value().getName() + " with implementation " + implementationType.toString());
            }
        }
        return true;
    }

    private FileObject createResource(final String name) {
        try {
            return this.processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    DiscoveryService.getDiscoveryFileName(name)
            );
        }
        catch (final IOException e) {
            System.err.println("Failed to create Hartshorn service discovery file for " + name);
            return null;
        }
    }

    private static TypeMirror getTargetType(final ServiceLoader annotation) {
        try {
            //noinspection ResultOfMethodCallIgnored
            annotation.value();
            assert false : "This code should not be executed, because the annotation processor should have thrown an exception";
            return null;
        }
        catch (final MirroredTypeException mirroredTypeException) {
            return mirroredTypeException.getTypeMirror();
        }
    }
}
