package org.dockbox.hartshorn.web.spec;

public enum PathSpecPartType {
    /**
     * A static path segment, e.g. "users" in "/users/profile".
     */
    STATIC,

    /**
     * A wildcard path segment, represented by "*", which matches any single segment.
     */
    WILDCARD,

    /**
     * A parameterized path segment, represented by "{param}", which captures the value of the segment as a parameter.
     */
    PARAMETER,
}
