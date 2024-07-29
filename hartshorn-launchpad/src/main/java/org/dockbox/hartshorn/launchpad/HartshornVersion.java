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

package org.dockbox.hartshorn.launchpad;

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
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum HartshornVersion implements Reportable {

    // Release 0.x 'Antelope' series
    // Release 0.4.x series
    V0_4_0("Antelope", 0, 4, 0, ReleaseStatus.RELEASED),
    V0_4_1(V0_4_0, 4, 1, ReleaseStatus.RELEASED),
    V0_4_2(V0_4_1, 4, 2, ReleaseStatus.RELEASED),
    V0_4_3(V0_4_2, 4, 3, ReleaseStatus.RELEASED),
    V0_4_4(V0_4_3, 4, 4, ReleaseStatus.RELEASED),
    V0_4_5(V0_4_4, 4, 5, ReleaseStatus.RELEASED),
    V0_4_6(V0_4_5, 4, 6, ReleaseStatus.RELEASED),
    V0_4_7(V0_4_6, 4, 7, ReleaseStatus.RELEASED),
    V0_4_8(V0_4_7, 4, 8, ReleaseStatus.RELEASED),
    V0_4_9(V0_4_8, 4, 9, ReleaseStatus.RELEASED),
    V0_4_10(V0_4_9, 4, 10, ReleaseStatus.RELEASED),
    V0_4_11(V0_4_10, 4, 11, ReleaseStatus.RELEASED),
    V0_4_12(V0_4_11, 4, 12, ReleaseStatus.RELEASED),
    V0_4_13(V0_4_12, 4, 13, ReleaseStatus.RELEASED),

    // Release 0.5.x series
    V0_5_0(V0_4_13, 5, 0, ReleaseStatus.RELEASED),
    V0_5_1(V0_5_0, 5, 1, ReleaseStatus.DEVELOPMENT),

    // Release 0.6.x series
    V0_6_0(V0_5_1, 6, 0, ReleaseStatus.DEVELOPMENT),

    // Release 0.7.x series
    V0_7_0(V0_6_0, 7, 0, ReleaseStatus.PLANNED),

    // Release 0.8.x series
    V0_8_0(V0_7_0, 8, 0, ReleaseStatus.PLANNED),

    ;

    private final String alias;
    private final int major;
    private final int minor;
    private final int patch;

    private final ReleaseStatus releaseStatus;

    HartshornVersion(HartshornVersion previous, int minor, int patch, ReleaseStatus releaseStatus) {
        this.alias = previous.alias();
        this.major = previous.major();
        this.minor = minor;
        this.patch = patch;
        this.releaseStatus = releaseStatus;
    }

    HartshornVersion(String alias, int major, int minor, int patch, ReleaseStatus releaseStatus) {
        this.alias = alias;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.releaseStatus = releaseStatus;
    }

    /**
     * Returns the alias of the version. The alias is a human-readable name for the version, which is
     * used to identify the release series. The alias is not unique, and is shared across all versions
     * in a release series. A series is defined by the major version number.
     *
     * @return The alias of the version
     */
    public String alias() {
        return this.alias;
    }

    /**
     * Returns the major version number of the version. The major version number is incremented for
     * significant changes to the API. Major versions are considered stable.
     *
     * @return The major version number
     */
    public int major() {
        return this.major;
    }

    /**
     * Returns the minor version number of the version. The minor version number is incremented for
     * new features.
     *
     * @return The minor version number
     */
    public int minor() {
        return this.minor;
    }

    /**
     * Returns the patch version number of the version. The patch version number is incremented for
     * bug fixes.
     *
     * @return The patch version number
     */
    public int patch() {
        return this.patch;
    }

    /**
     * Returns whether this version is at least the given version. A version is considered at least
     * the given version if:
     * <ul>
     *     <li>The major version is greater than the given version's major version</li>
     *     <li>The major version is equal, and the minor version is greater</li>
     *     <li>The major and minor versions are equal, and the patch version is greater or equal</li>
     * </ul>
     *
     * @param version The version to compare to
     * @return {@code true} if this version is at least the given version, {@code false} otherwise
     */
    public boolean isAtLeast(HartshornVersion version) {
        return this.major > version.major
                || (this.major == version.major && this.minor > version.minor)
                || (this.major == version.major && this.minor == version.minor && this.patch >= version.patch);
    }

    /**
     * Returns whether this version is at most the given version. A version is considered at most
     * the given version if:
     * <ul>
     *     <li>The major version is less than the given version's major version</li>
     *     <li>The major version is equal, and the minor version is less</li>
     *     <li>The major and minor versions are equal, and the patch version is less or equal</li>
     * </ul>
     *
     * @param version The version to compare to
     * @return {@code true} if this version is at most the given version, {@code false} otherwise
     */
    public boolean isAtMost(HartshornVersion version) {
        return this.major < version.major
                || (this.major == version.major && this.minor < version.minor)
                || (this.major == version.major && this.minor == version.minor && this.patch <= version.patch);
    }

    /**
     * Returns whether this version is equal to the given version. A version is considered equal to
     * the given version if the major, minor, and patch versions are equal.
     *
     * @param version The version to compare to
     * @return {@code true} if this version is equal to the given version, {@code false} otherwise
     */
    public boolean is(HartshornVersion version) {
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    /**
     * Returns whether this version is before the given version. A version is considered before the
     * given version if it is not equal to or at least the given version.
     *
     * @param version The version to compare to
     * @return {@code true} if this version is before the given version, {@code false} otherwise
     */
    public boolean isBefore(HartshornVersion version) {
        return this.major < version.major
                || (this.major == version.major && this.minor < version.minor)
                || (this.major == version.major && this.minor == version.minor && this.patch < version.patch);
    }

    /**
     * Returns whether this version is after the given version. A version is considered after the
     * given version if it is not equal to or at most the given version.
     *
     * @param version The version to compare to
     * @return {@code true} if this version is after the given version, {@code false} otherwise
     */
    public boolean isAfter(HartshornVersion version) {
        return this.major > version.major
                || (this.major == version.major && this.minor > version.minor)
                || (this.major == version.major && this.minor == version.minor && this.patch > version.patch);
    }

    /**
     * Returns whether this version is between the given versions. A version is considered between
     * the given versions if it is after the minimum version and before the maximum version, and
     * not equal to either.
     *
     * @param min The minimum version
     * @param max The maximum version
     * @return {@code true} if this version is between the given versions, {@code false} otherwise
     */
    public boolean isBetweenExclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAfter(min) && this.isBefore(max);
    }

    /**
     * Returns whether this version is between the given versions, inclusive. A version is considered
     * between the given versions if it is at least the minimum version and at most the maximum version.
     *
     * @param min The minimum version
     * @param max The maximum version
     * @return {@code true} if this version is between the given versions, {@code false} otherwise
     */
    public boolean isBetweenInclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAtLeast(min) && this.isAtMost(max);
    }

    /**
     * Returns the release status of the version. The release status indicates the stability of the
     * version. Note that the release status of a version may change over time, and this is purely
     * indicative of the status at the time of the current version's release.
     *
     * @return The release status of the version
     */
    public ReleaseStatus status() {
        return this.releaseStatus;
    }

    /**
     * Returns whether this version is a major release. A release is considered major if it has no
     * minor or patch version.
     *
     * @return {@code true} if this version is a major release, {@code false} otherwise
     */
    public boolean isMajorRelease() {
        // Patches for major releases are also considered major releases
        return this.major > 0 && this.minor == 0;
    }

    /**
     * Returns whether this version is a minor release. A release is considered minor if it has a
     * minor version. If a version has a patch version, it is also considered a minor release.
     *
     * @return {@code true} if this version is a minor release, {@code false} otherwise
     */
    public boolean isMinorRelease() {
        // Patches for minor releases are also considered minor releases
        return this.minor > 0;
    }

    /**
     * Returns whether this version is a patch release. A release is considered a patch release if it
     * has a patch version.
     *
     * @return {@code true} if this version is a patch release, {@code false} otherwise
     */
    public boolean isPatchRelease() {
        return this.patch > 0;
    }

    /**
     * Returns whether this version is {@link ReleaseStatus#RELEASED released}. Note that this represents
     * the status of the version at the time of the current version's release, and may change over time.
     *
     * @return {@code true} if this version is released, {@code false} otherwise
     */
    public boolean isReleased() {
        return this.releaseStatus == ReleaseStatus.RELEASED;
    }

    /**
     * Returns whether this version is a {@link ReleaseStatus#SNAPSHOT snapshot}. Note that this represents
     * the status of the version at the time of the current version's release, and may change over time.
     *
     * @return {@code true} if this version is a snapshot, {@code false} otherwise
     */
    public boolean isSnapshot() {
        return this.releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    /**
     * Returns whether this version is a {@link ReleaseStatus#CANDIDATE release candidate}. Note that this
     * represents the status of the version at the time of the current version's release, and may change
     * over time.
     *
     * @return {@code true} if this version is a release candidate, {@code false} otherwise
     */
    public boolean isReleaseCandidate() {
        return this.releaseStatus == ReleaseStatus.CANDIDATE;
    }

    /**
     * Returns whether this version is in {@link ReleaseStatus#DEVELOPMENT development}. Note that this
     * represents the status of the version at the time of the current version's release, and may change
     * over time.
     *
     * @return {@code true} if this version is in development, {@code false} otherwise
     */
    public boolean isDevelopment() {
        return this.releaseStatus == ReleaseStatus.DEVELOPMENT;
    }

    /**
     * Returns whether this version is a {@link ReleaseStatus#PLANNED planned} version. Planned versions
     * are not yet released or actively developed, and serve as placeholders for future releases.
     *
     * @return {@code true} if this version is planned, {@code false} otherwise
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
        collector.property("formatted").writeString(this.toString());
        collector.property("major").writeInt(this.major);
        collector.property("minor").writeInt(this.minor);
        collector.property("patch").writeInt(this.patch);
        collector.property("alias").writeString(this.alias);
        collector.property("status").writeString(this.releaseStatus.name());
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
        CANDIDATE("rc"),
        /**
         * Indicates that a version is released. Released versions are considered stable, and are
         * suitable for production use.
         */
        RELEASED(null),
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
         * Returns the suffix of the release status. The suffix is a short string that indicates the
         * status of the version. The suffix is used in version strings to indicate the status of the
         * version.
         *
         * @return The suffix of the release status
         */
        public String suffix() {
            return this.suffix;
        }
    }
}
