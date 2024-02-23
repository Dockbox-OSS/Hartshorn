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

package org.dockbox.hartshorn.util.introspect.view;

/**
 * Represents a view of a package. This view can be used to introspect the package's annotations, as
 * well as its name and qualified name. A package is a grouping of related types providing access
 * protection and name space management.
 *
 * <p>The set of classes that make up the run-time package may implement a particular specification. The
 * title, version, and vendor (indicating the owner/maintainer of the specification) of both the
 * specification and implementation are available via this interface.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface PackageView extends AnnotatedElementView {

    /**
     * Returns the title of the specification that this package implements.
     * @return the specification title, an empty string is returned if it is not known.
     */
    String specificationTitle();

    /**
     * Returns the name of the organization, vendor, or company that owns and maintains the
     * specification that this package implements.
     *
     * @return the specification vendor, an empty string is returned if it is not known.
     */
    String specificationVendor();

    /**
     * Returns the version number of the specification that this package implements. This version
     * string must be a sequence of non-negative decimal integers separated by "."'s and may have
     * leading zeros. When version strings are compared the most significant numbers are compared.
     *
     * <p>Specification version numbers use a syntax that consists of non-negative decimal integers
     * separated by periods ".", for example "2.0" or "1.2.3.4.5.6.7".  This allows an extensible
     * number to be used to represent major, minor, micro, etc. versions.  The version specification
     * is described by the following formal grammar:
     * <blockquote>
     * <dl>
     * <dt><i>SpecificationVersion:</i>
     * <dd><i>Digits RefinedVersion<sub>opt</sub></i>
     * <dt><i>RefinedVersion:</i>
     * <dd>{@code .} <i>Digits</i>
     * <dd>{@code .} <i>Digits RefinedVersion</i>
     * <dt><i>Digits:</i>
     * <dd><i>Digit</i>
     * <dd><i>Digits</i>
     * <dt><i>Digit:</i>
     * <dd>any character for which {@link Character#isDigit} returns {@code true},
     * e.g. 0, 1, 2, ...
     * </dl>
     * </blockquote>
     *
     * @return the specification version, an empty string is returned if it is not known.
     */
    String specificationVersion();

    /**
     * Return the title of this package.
     *
     * @return the title of the implementation, an empty string is returned if it is not known.
     */
    String implementationTitle();

    /**
     * Returns the vendor that implemented this package, an empty string is returned if it is not known.
     *
     * @return the vendor that implemented this package, an empty string is returned if it is not known.
     */
    String implementationVendor();


    /**
     * Return the version of this implementation. It consists of any string assigned by the vendor of
     * this implementation and does not have any particular syntax specified or expected by the Java
     * runtime. It may be compared for equality with other package version strings used for this
     * implementation by this vendor for this package.
     *
     * @return the version of the implementation, an empty string is returned if it is not known.
     */
    String implementationVersion();

    /**
     * Returns true if this package is sealed.
     *
     * @return {@code true} if the package is sealed, {@code false} otherwise.
     */
    boolean isSealed();
}
