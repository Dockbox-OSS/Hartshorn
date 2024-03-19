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

    // Release 0.4.x 'Antelope' series
    V0_4_0("Antelope", 0, 4, 0, ReleaseStatus.RELEASED),
    V0_4_1(V0_4_0.alias(), 0, 4, 1, ReleaseStatus.RELEASED),
    V0_4_2(V0_4_0.alias(), 0, 4, 2, ReleaseStatus.RELEASED),
    V0_4_3(V0_4_0.alias(), 0, 4, 3, ReleaseStatus.RELEASED),
    V0_4_4(V0_4_0.alias(), 0, 4, 4, ReleaseStatus.RELEASED),
    V0_4_5(V0_4_0.alias(), 0, 4, 5, ReleaseStatus.RELEASED),
    V0_4_6(V0_4_0.alias(), 0, 4, 6, ReleaseStatus.RELEASED),
    V0_4_7(V0_4_0.alias(), 0, 4, 7, ReleaseStatus.RELEASED),
    V0_4_8(V0_4_0.alias(), 0, 4, 8, ReleaseStatus.RELEASED),
    V0_4_9(V0_4_0.alias(), 0, 4, 9, ReleaseStatus.RELEASED),
    V0_4_10(V0_4_0.alias(), 0, 4, 10, ReleaseStatus.RELEASED),
    V0_4_11(V0_4_0.alias(), 0, 4, 11, ReleaseStatus.RELEASED),
    V0_4_12(V0_4_0.alias(), 0, 4, 12, ReleaseStatus.RELEASED),
    V0_4_13(V0_4_0.alias(), 0, 4, 13, ReleaseStatus.RELEASED),

    // Release 0.5.x 'Bambi' series
    V0_5_0("Bambi", 0, 5, 0, ReleaseStatus.RELEASED),
    V0_5_1(V0_5_0.alias(), 0, 5, 1, ReleaseStatus.DEVELOPMENT),

    // Release 0.6.x 'Caribou' series
    V0_6_0("Caribou", 0, 6, 0, ReleaseStatus.DEVELOPMENT),

    // Release 0.7.x 'Doe' series
    V0_7_0("Doe", 0, 7, 0, ReleaseStatus.PLANNED),

    // Release 0.8.x 'Elk' series
    V0_8_0("Elk", 0, 8, 0, ReleaseStatus.PLANNED),

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

    public String alias() {
        return this.alias;
    }

    public int major() {
        return this.major;
    }

    public int minor() {
        return this.minor;
    }

    public int patch() {
        return this.patch;
    }

    public boolean isAtLeast(HartshornVersion version) {
        return this.major > version.major || (this.major == version.major && this.minor > version.minor) || (this.major == version.major && this.minor == version.minor && this.patch >= version.patch);
    }

    public boolean isAtMost(HartshornVersion version) {
        return this.major < version.major || (this.major == version.major && this.minor < version.minor) || (this.major == version.major && this.minor == version.minor && this.patch <= version.patch);
    }

    public boolean is(HartshornVersion version) {
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    public boolean isBefore(HartshornVersion version) {
        return this.major < version.major || (this.major == version.major && this.minor < version.minor) || (this.major == version.major && this.minor == version.minor && this.patch < version.patch);
    }

    public boolean isAfter(HartshornVersion version) {
        return this.major > version.major || (this.major == version.major && this.minor > version.minor) || (this.major == version.major && this.minor == version.minor && this.patch > version.patch);
    }

    public boolean isBetweenExclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAfter(min) && this.isBefore(max);
    }

    public boolean isBetweenInclusive(HartshornVersion min, HartshornVersion max) {
        return this.isAtLeast(min) && this.isAtMost(max);
    }

    public ReleaseStatus status() {
        return this.releaseStatus;
    }

    public boolean isMajorRelease() {
        // Patches for major releases are also considered major releases
        return this.major > 0 && this.minor == 0;
    }

    public boolean isMinorRelease() {
        // Patches for minor releases are also considered minor releases
        return this.minor > 0;
    }

    public boolean isPatchRelease() {
        return this.patch > 0;
    }

    public boolean isReleased() {
        return this.releaseStatus == ReleaseStatus.RELEASED;
    }

    public boolean isSnapshot() {
        return this.releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    public boolean isReleaseCandidate() {
        return this.releaseStatus == ReleaseStatus.CANDIDATE;
    }

    public boolean isDevelopment() {
        return this.releaseStatus == ReleaseStatus.DEVELOPMENT;
    }

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

        public String suffix() {
            return this.suffix;
        }
    }
}
