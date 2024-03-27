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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a version of Hartshorn. Versions are defined as major.minor.patch, where minor versions
 * indicate a release series. The start of a new release series will also define a new version alias.
 *
 * <p>Major versions are considered stable, and include significant changes to the API. Minor versions
 * are considered stable, and include new features. Patch versions are considered stable, and include
 * bug fixes.
 *
 * <p>Version numbers are always positive integers. A patch release is indicated by a version number
 * that is greater than zero.
 *
 * @see Hartshorn#VERSION
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public enum HartshornVersion implements Reportable {

    // 0.x.x - Initial development versions
    V0_4_0(VersionAlias.V0, 0, 4, 0, ReleaseStatus.GENERAL_ACCESS),
    V0_4_1(VersionAlias.V0, 0, 4, 1, ReleaseStatus.GENERAL_ACCESS),
    V0_4_2(VersionAlias.V0, 0, 4, 2, ReleaseStatus.GENERAL_ACCESS),
    V0_4_3(VersionAlias.V0, 0, 4, 3, ReleaseStatus.GENERAL_ACCESS),
    V0_4_4(VersionAlias.V0, 0, 4, 4, ReleaseStatus.GENERAL_ACCESS),
    V0_4_5(VersionAlias.V0, 0, 4, 5, ReleaseStatus.GENERAL_ACCESS),
    V0_4_6(VersionAlias.V0, 0, 4, 6, ReleaseStatus.GENERAL_ACCESS),
    V0_4_7(VersionAlias.V0, 0, 4, 7, ReleaseStatus.GENERAL_ACCESS),
    V0_4_8(VersionAlias.V0, 0, 4, 8, ReleaseStatus.GENERAL_ACCESS),
    V0_4_9(VersionAlias.V0, 0, 4, 9, ReleaseStatus.GENERAL_ACCESS),
    V0_4_10(VersionAlias.V0, 0, 4, 10, ReleaseStatus.GENERAL_ACCESS),
    V0_4_11(VersionAlias.V0, 0, 4, 11, ReleaseStatus.GENERAL_ACCESS),
    V0_4_12(VersionAlias.V0, 0, 4, 12, ReleaseStatus.GENERAL_ACCESS),
    V0_4_13(VersionAlias.V0, 0, 4, 13, ReleaseStatus.GENERAL_ACCESS),
    V0_5_0(VersionAlias.V0, 0, 5, 0, ReleaseStatus.GENERAL_ACCESS),
    V0_6_0(VersionAlias.V0, 0, 6, 0, ReleaseStatus.DEVELOPMENT),
    V0_7_0(VersionAlias.V0, 0, 7, 0, ReleaseStatus.PLANNED),

    // 1.x.x - First stable release series
    V1_0_0(VersionAlias.V1, 1, 0, 0, ReleaseStatus.PLANNED),

    ;

    private final String alias;
    private final int major;
    private final int minor;
    private final int patch;

    private final ReleaseStatus releaseStatus;

    HartshornVersion(String alias, int major, int minor, int patch, ReleaseStatus releaseStatus) {
        this.alias = alias;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.releaseStatus = releaseStatus;
    }

    /**
     * Returns the alias of the version. The alias is a human-readable name for the version, which
     * is used to identify the release series. The alias is not unique, and may be shared between
     * multiple versions.
     *
     * @return The alias of the version
     */
    public String alias() {
        return this.alias;
    }

    /**
     * Returns the major version number of the version. Major versions indicate significant changes
     * to the API, and are considered stable.
     *
     * @return The major version number
     */
    public int major() {
        return this.major;
    }

    /**
     * Returns the minor version number of the version. Minor versions indicate new features, and are
     * considered stable.
     *
     * @return The minor version number
     */
    public int minor() {
        return this.minor;
    }

    /**
     * Returns the patch version number of the version. Patch versions indicate bug fixes, and are
     * considered stable.
     *
     * @return The patch version number
     */
    public int patch() {
        return this.patch;
    }

    /**
     * Returns whether the current version is at least the specified version (inclusive). This method will
     * return {@code true} if the current version is equal to or greater than the specified version, following
     * the semantic versioning rules.
     *
     * @param version The version to compare to
     * @return {@code true} if the current version is at least the specified version, {@code false} otherwise
     */
    public boolean isAtLeast(HartshornVersion version) {
        return this.major > version.major
            || (this.major == version.major && this.minor > version.minor)
            || (this.major == version.major && this.minor == version.minor && this.patch >= version.patch);
    }

    /**
     * Returns whether the current version is at most the specified version (inclusive). This method will
     * return {@code true} if the current version is equal to or less than the specified version, following
     * the semantic versioning rules.
     *
     * @param version The version to compare to
     * @return {@code true} if the current version is at most the specified version, {@code false} otherwise
     */
    public boolean isAtMost(HartshornVersion version) {
        return this.major < version.major
            || (this.major == version.major && this.minor < version.minor)
            || (this.major == version.major && this.minor == version.minor && this.patch <= version.patch);
    }

    /**
     * Returns whether the current version is equal to the specified version, following the semantic
     * versioning rules.
     *
     * @param version The version to compare to
     * @return {@code true} if the current version is equal to the specified version, {@code false} otherwise
     */
    public boolean is(HartshornVersion version) {
        return this == version
            || (this.major == version.major && this.minor == version.minor && this.patch == version.patch);
    }

    /**
     * Returns whether the current version is before the specified version (exclusive), following the semantic
     * versioning rules.
     *
     * @param version The version to compare to
     * @return {@code true} if the current version is before the specified version, {@code false} otherwise
     */
    public boolean isBefore(HartshornVersion version) {
        return this.major < version.major
            || (this.major == version.major && this.minor < version.minor)
            || (this.major == version.major && this.minor == version.minor && this.patch < version.patch);
    }

    /**
     * Returns whether the current version is after the specified version (exclusive), following the semantic
     * versioning rules.
     *
     * @param version The version to compare to
     * @return {@code true} if the current version is after the specified version, {@code false} otherwise
     */
    public boolean isAfter(HartshornVersion version) {
        return this.major > version.major
            || (this.major == version.major && this.minor > version.minor)
            || (this.major == version.major && this.minor == version.minor && this.patch > version.patch);
    }

    /**
     * Returns whether the current version is between the specified versions (exclusive), following the semantic
     * versioning rules.
     *
     * @param min The minimum version
     * @param max The maximum version
     * @return {@code true} if the current version is between the specified versions, {@code false} otherwise
     */
    public boolean isBetweenExclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAfter(min) && this.isBefore(max);
    }

    /**
     * Returns whether the current version is between the specified versions (inclusive), following the semantic
     * versioning rules.
     *
     * @param min The minimum version
     * @param max The maximum version
     * @return {@code true} if the current version is between the specified versions, {@code false} otherwise
     */
    public boolean isBetweenInclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAtLeast(min) && this.isAtMost(max);
    }

    /**
     * Returns the release status of the version. The release status indicates the stability of the version.
     *
     * @return The release status of the version
     */
    public ReleaseStatus status() {
        return this.releaseStatus;
    }

    /**
     * Returns whether the current version is a major release. A release is considered a major release if the
     * minor version is zero. Patch releases for major releases are also considered major releases. Note that
     * the major version is always greater than zero, as version 0.x is considered a development version.
     *
     * @return {@code true} if the current version is a major release, {@code false} otherwise
     */
    public boolean isMajorRelease() {
        // Patches for major releases are also considered major releases
        return this.major > 0 && this.minor == 0;
    }

    /**
     * Returns whether the current version is a minor release. A release is considered a minor release if the
     * minor version is greater than zero. Patch releases for minor releases are also considered minor releases.
     *
     * @return {@code true} if the current version is a minor release, {@code false} otherwise
     */
    public boolean isMinorRelease() {
        // Patches for minor releases are also considered minor releases
        return this.minor > 0;
    }

    /**
     * Returns whether the current version is a patch release. A release is considered a patch release if the
     * patch version is greater than zero.
     *
     * @return {@code true} if the current version is a patch release, {@code false} otherwise
     */
    public boolean isPatchRelease() {
        return this.patch > 0;
    }

    /**
     * Returns whether the current version is a released version. A released version is considered stable and
     * suitable for production use.
     *
     * @return {@code true} if the current version is a released version, {@code false} otherwise
     */
    public boolean isReleased() {
        return this.releaseStatus == ReleaseStatus.GENERAL_ACCESS;
    }

    /**
     * Returns whether the current version is a snapshot version. A snapshot version is considered stable but
     * experimental, and suitable for development use.
     *
     * @return {@code true} if the current version is a snapshot version, {@code false} otherwise
     */
    public boolean isSnapshot() {
        return this.releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    /**
     * Returns whether the current version is a release candidate. A release candidate version is considered
     * stable and no longer subject to change, and suitable for QA use and early adoption.
     *
     * @return {@code true} if the current version is a release candidate, {@code false} otherwise
     */
    public boolean isReleaseCandidate() {
        return this.releaseStatus == ReleaseStatus.RELEASE_CANDIDATE;
    }

    /**
     * Returns whether the current version is a development version. A development version is considered unstable
     * and not suitable for production use, but may be suitable for bleeding-edge development use.
     *
     * @return {@code true} if the current version is a development version, {@code false} otherwise
     */
    public boolean isDevelopment() {
        return this.releaseStatus == ReleaseStatus.DEVELOPMENT;
    }

    /**
     * Returns whether the current version is a planned version. A planned version is not yet released or actively
     * developed, and serves as a placeholder for future releases.
     *
     * @return {@code true} if the current version is a planned version, {@code false} otherwise
     */
    public boolean isPlaceholder() {
        return this.releaseStatus == ReleaseStatus.PLANNED;
    }

    /**
     * Parses a version string into a {@link HartshornVersion} instance. The version string must be in the format
     * major.minor.patch, where patch is optional, or equal the alias of a version.
     *
     * @param version The version string to parse
     * @return The parsed version, or {@link Option#empty()} if the version string could not be parsed or no matching
     *         version was found
     */
    public static Option<HartshornVersion> parse(String version) {
        final String[] split = version.split("\\.");
        if (split.length < 2) {
            for(HartshornVersion value : HartshornVersion.values()) {
                if (value.alias != null && value.alias.equalsIgnoreCase(version)) {
                    return Option.of(value);
                }
            }
        }
        else {
            int major = Integer.parseInt(split[0]);
            int minor = Integer.parseInt(split[1]);
            int patch = split.length > 2 ? Integer.parseInt(split[2]) : 0;
            for(HartshornVersion value : HartshornVersion.values()) {
                if(value.major == major && value.minor == minor && value.patch == patch) {
                    return Option.of(value);
                }
            }
        }
        return Option.empty();
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.patch
                + (this.releaseStatus.suffix() != null ? "-" + this.releaseStatus.suffix() : "")
                + (this.alias != null ? " (" + this.alias + ")" : "");
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("formatted").write(this.toString());
        collector.property("major").write(this.major);
        collector.property("minor").write(this.minor);
        collector.property("patch").write(this.patch);
        collector.property("alias").write(this.alias);
        // Use name, as suffix is not always present
        collector.property("status").write(this.releaseStatus.name());
    }

    /**
     * The release status of a version. This indicates the stability of a version. The support status
     * is not included in this enum, as it is not relevant to the API, and subject to external factors.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public enum ReleaseStatus {
        /**
         * Indicates that a version is in development. Development versions are considered unstable
         * and are not suitable for production use. Development versions may be suitable for
         * bleeding-edge development use.
         */
        DEVELOPMENT("dev"),
        /**
         * Indicates that a version is a snapshot. Snapshot versions are considered stable but are
         * also considered experimental. Snapshot versions are suitable for development use.
         */
        SNAPSHOT("snapshot"),
        /**
         * Indicates that a version is a release candidate. Release candidate versions are considered
         * stable and no longer subject to change. Release candidate versions are suitable for
         * QA use and early adoption.
         */
        RELEASE_CANDIDATE("rc"),
        /**
         * Indicates that a version is released. Released versions are considered stable, and are
         * suitable for production use.
         */
        GENERAL_ACCESS(null),
        /**
         * Indicates that a version is planned. Planned versions are not yet released or actively
         * developed. Planned versions serve as placeholders for future releases.
         */
        PLANNED(null),

        ;

        private final String suffix;

        ReleaseStatus(String suffix) {
            this.suffix = suffix;
        }

        /**
         * Returns the suffix of the release status. The suffix is a human-readable name for the release
         * status, which is used to identify the stability of a version.
         *
         * @return The suffix of the release status
         */
        public String suffix() {
            return this.suffix;
        }
    }

    /**
     * Version aliases for specific versions. Version aliases are human-readable names for versions,
     * which are used to identify release series. Stored as a static inner class to allow for easy
     * re-use of the aliases in different versions.
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    private static class VersionAlias {
        private static final String V0 = "Antelope";
        private static final String V1 = "Bambi";
    }
}
