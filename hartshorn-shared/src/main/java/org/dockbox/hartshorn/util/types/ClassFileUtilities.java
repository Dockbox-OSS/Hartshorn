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

package org.dockbox.hartshorn.util.types;

import org.dockbox.hartshorn.util.collections.GathererUtilities;
import org.dockbox.hartshorn.util.option.Option;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationElement;
import java.lang.classfile.AnnotationValue;
import java.lang.classfile.Attributes;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.classfile.constantpool.Utf8Entry;
import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for working with Java's {@link ClassFile} API, primarily focused on extracting
 * annotation information from class files.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public final class ClassFileUtilities {

    private ClassFileUtilities() {
    }

    /**
     * Get the {@link ClassModel} for the given class type.
     *
     * @param type the class type
     * @return an {@link Option} containing the {@link ClassModel} if found, otherwise an empty
     * {@link Option}
     */
    public static Option<ClassModel> getClassModel(Class<?> type) {
        return getClassModel(type.getName());
    }

    /**
     * Get the {@link ClassModel} for the given resource name or class name.
     *
     * @param resourceOrClassName the resource name (e.g. "org/example/MyClass.class") or class name
     * (e.g. "org.example.MyClass")
     *
     * @return an {@link Option} containing the {@link ClassModel} if found, otherwise an empty
     * {@link Option}
     */
    public static Option<ClassModel> getClassModel(String resourceOrClassName) {
        if (TypeUtils.isClassName(resourceOrClassName)) {
            resourceOrClassName = TypeUtils.classNameToResourceName(resourceOrClassName);
        }
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourceOrClassName);
        if (in == null) {
            return Option.empty();
        }
        try {
            byte[] bytes = in.readAllBytes();
            ClassFile classFile = ClassFile.of();
            ClassModel classModel = classFile.parse(bytes);
            return Option.of(classModel);
        } catch (IOException e) {
            return Option.empty();
        }
    }

    /**
     * Get all annotations present on the given {@link ClassModel}.
     *
     * @param classModel the {@link ClassModel} to extract annotations from
     * @return a list of {@link Annotation}s present on the class model
     */
    public static List<Annotation> getAnnotations(ClassModel classModel) {
        List<RuntimeVisibleAnnotationsAttribute> attributes = classModel.findAttributes(
                Attributes.runtimeVisibleAnnotations()
        );
        return attributes.stream()
                .flatMap(attr -> attr.annotations().stream())
                .toList();
    }

    /**
     * Get all meta-annotations of a specific type present on the given {@link ClassModel}.
     *
     * @param classModel the {@link ClassModel} to extract meta-annotations from
     * @param annotationType the type of meta-annotation to look for
     *
     * @return a list of {@link Annotation}s that are meta-annotated with the specified type
     */
    public static List<Annotation> getMetaAnnotations(
        ClassModel classModel,
        Class<? extends java.lang.annotation.Annotation> annotationType
    ) {
        List<Annotation> annotations = getAnnotations(classModel);
        return annotations.stream()
                .filter(annotation -> {
                    ClassModel annotationModel = getClassModel(annotation.classSymbol());
                    return getAnnotation(annotationModel, annotationType).present();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a specific annotation present on the given {@link ClassModel}.
     *
     * @param classModel the {@link ClassModel} to extract the annotation from
     * @param annotationType the type of annotation to look for
     *
     * @return an {@link Option} containing the {@link Annotation} if found, otherwise an empty
     * {@link Option}
     */
    public static Option<Annotation> getAnnotation(
            ClassModel classModel,
            Class<? extends java.lang.annotation.Annotation> annotationType
    ) {
        return getAnnotation(classModel, annotationType.getName());
    }

    /**
     * Get a specific annotation present on the given {@link ClassModel}.
     *
     * @param classModel the {@link ClassModel} to extract the annotation from
     * @param annotationQualifiedName the fully qualified name of the annotation to look for
     *
     * @return an {@link Option} containing the {@link Annotation} if found, otherwise an empty
     * {@link Option}
     */
    public static Option<Annotation> getAnnotation(
            ClassModel classModel,
            String annotationQualifiedName
    ) {
        return Option.of(getAnnotations(classModel).stream()
                .filter(annotation -> matchesConstantPoolName(
                        annotation.className(),
                        annotationQualifiedName
                ))
                .findFirst());
    }

    /**
     * Check if a {@link Utf8Entry} from the constant pool matches the expected fully qualified
     * class name. Constant pool entries for class names are expected to be equal to field
     * descriptors as defined in JVM Specification 4.3.2, thus being in the format {@code
     * Lorg/example/MyClass;}, while the expected value is in the format {@code
     * org.example.MyClass}.
     *
     * <p>Only reference type field descriptors are supported (i.e., those with term {@code L}).
     * primitive field descriptors (including array dimensions) are not supported.
     *
     * @param entry the {@link Utf8Entry} from the constant pool
     * @param expectedValue the expected fully qualified class name
     *
     * @return true if the entry matches the expected value, false otherwise
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.2">
     * JVM Specification 4.3.2 - Field Descriptors. Table 4.3-A Interpretation of field descriptors
     * </a>
     */
    public static boolean matchesConstantPoolName(
            Utf8Entry entry,
            String expectedValue
    ) {
        String entryValue = entry.stringValue();
        if (entryValue.startsWith("L") && entryValue.endsWith(";")) {
            entryValue = entryValue.substring(1, entryValue.length() - 1);
        }
        entryValue = entryValue.replace('/', '.');
        return entryValue.equals(expectedValue);
    }

    /**
     * Convert a constant pool class name to a fully qualified class name.
     *
     * @param entry the {@link Utf8Entry} from the constant pool
     * @return the fully qualified class name
     */
    public static String constantPoolNameToQualifiedName(Utf8Entry entry) {
        return constantPoolNameToQualifiedName(entry.stringValue());
    }

    /**
     * Convert a constant pool class name to a fully qualified class name.
     *
     * @param constant the constant pool class name
     * @return the fully qualified class name
     */
    public static String constantPoolNameToQualifiedName(String constant) {
        if (constant.startsWith("L") && constant.endsWith(";")) {
            constant = constant.substring(1, constant.length() - 1);
        }
        return constant.replace('/', '.');
    }

    /**
     * Convert the elements of an {@link Annotation} to a map of element names to their values.
     *
     * @param annotation the {@link Annotation} to convert
     * @return a map of element names to their corresponding {@link AnnotationValue}s
     */
    public static Map<String, AnnotationValue> annotationValuesAsMap(Annotation annotation) {
        return annotation.elements()
                .stream()
                .collect(Collectors.toMap(
                        element -> element.name().stringValue(),
                        AnnotationElement::value
                ));
    }

    /**
     * Get the value of a specific element from an {@link Annotation}.
     *
     * @param annotation the {@link Annotation} to extract the value from
     * @param name the name of the element to look for
     * @return an {@link Option} containing the {@link AnnotationValue} if found, otherwise an empty
     * {@link Option}
     */
    public static Option<AnnotationValue> getAnnotationValue(Annotation annotation, String name) {
        for (AnnotationElement element : annotation.elements()) {
            if (element.name().equalsString(name)) {
                return Option.of(element.value());
            }
        }
        return Option.empty();
    }

    /**
     * Get the value of a specific element from an {@link Annotation}, cast to a specific type.
     *
     * @param annotation the {@link Annotation} to extract the value from
     * @param name the name of the element to look for
     * @param type the expected type of the {@link AnnotationValue}
     * @param <T> the type of the {@link AnnotationValue}
     *
     * @return an {@link Option} containing the {@link AnnotationValue} of the specified type if
     * found, otherwise an empty {@link Option}
     */
    public static <T extends AnnotationValue> Option<T> getAnnotationValue(
        Annotation annotation,
        String name,
        Class<T> type
    ) {
        return getAnnotationValue(annotation, name).ofType(type);
    }

    /**
     * Get the array of values of a specific element from an {@link Annotation}. If the element is
     * not found or is not an array, an empty list is returned.
     *
     * @param annotation the {@link Annotation} to extract the values from
     * @param name the name of the element to look for
     * @return a list of {@link AnnotationValue}s contained in the array element, or an empty list
     * if the element is not found or is not an array
     */
    public static List<AnnotationValue> getAnnotationValues(Annotation annotation, String name) {
        return getAnnotationValue(annotation, name)
                .ofType(AnnotationValue.OfArray.class)
                .map(AnnotationValue.OfArray::values)
                .orElseGet(List::of);
    }

    /**
     * Get the array of values of a specific element from an {@link Annotation}, cast to a specific
     * type. If the element is not found or is not an array, an empty list is returned.
     *
     * @param annotation the {@link Annotation} to extract the values from
     * @param name the name of the element to look for
     * @param type the expected type of the {@link AnnotationValue}s
     * @param <T> the type of the {@link AnnotationValue}s
     *
     * @return a list of {@link AnnotationValue}s of the specified type contained in the array
     * element, or an empty list if the element is not found or is not an array
     */
    public static <T extends AnnotationValue> List<T> getAnnotationValues(
        Annotation annotation,
        String name,
        Class<T> type
    ) {
        return getAnnotationValues(annotation, name).stream()
                .gather(GathererUtilities.filterByType(type))
                .toList();
    }

    /**
     * Get the {@link ClassModel} for the given {@link ClassDesc} descriptor.
     *
     * @param descriptor the {@link ClassDesc} descriptor
     * @return the corresponding {@link ClassModel}
     *
     * @throws IllegalStateException if the {@link ClassModel} could not be loaded
     */
    public static ClassModel getClassModel(ClassDesc descriptor) {
        Option<ClassModel> classModelOption = ClassFileUtilities.getClassModel(
            constantPoolNameToQualifiedName(descriptor.descriptorString())
        );
        return classModelOption.orElseThrow(() -> new IllegalStateException(
            "Could not load class model for descriptor: " + descriptor
        ));
    }
}
