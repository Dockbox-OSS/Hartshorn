package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

/**
 * Represents a media type, consisting of a type, subtype, and optional parameters.
 *
 * @param type the general category into which the datatype falls
 * @param subtype the exact kind of data of the specified type
 * @param parameters optional additional details
 *
 * @see <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">
 * Media Types - IANA
 * </a>
 * @see <a href="https://tools.ietf.org/html/rfc2046">RFC2046</a>
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record MediaType(String type, String subtype, Map<String, String> parameters) {

    private static final String WILDCARD_TYPE = "*";

    public MediaType(String type, String subtype) {
        this(type, subtype, Map.of());
    }

    public MediaType(String type) {
        this(type, "*");
    }

    /**
     * Attempts to resolve a parameter with the given name. If no parameter with the given name
     * exists, an empty {@link Option} is returned.
     *
     * @param name the name of the parameter
     * @return the parameter value, or an empty {@link Option} if no parameter with the given name
     * exists
     */
    public Option<String> parameter(String name) {
        return Option.of(this.parameters.get(name));
    }

    @Override
    public Map<String, String> parameters() {
        return Map.copyOf(parameters);
    }

    /**
     * Determines whether this media type is a wildcard type. A media type is considered a wildcard
     * if its type equals {@code *}.
     *
     * @return {@code true} if this media type is a wildcard type, {@code false} otherwise
     */
    public boolean isWildcardType() {
        return WILDCARD_TYPE.equals(this.type());
    }

    /**
     * Determines whether this media type has a wildcard subtype. A subtype is considered a wildcard
     * if it equals {@code *} or starts with {@code *+}.
     *
     * @return {@code true} if this media type has a wildcard subtype, {@code false} otherwise
     */
    public boolean isWildcardSubtype() {
        String subtype = this.subtype();
        return (WILDCARD_TYPE.equals(subtype) || subtype.startsWith("*+"));
    }

    /**
     * Determines whether this media type is a concrete type, meaning neither its type nor its
     * subtype is a wildcard.
     *
     * @return {@code true} if this media type is a concrete type, {@code false} otherwise
     */
    public boolean isConcrete() {
        return !isWildcardType() && !isWildcardSubtype();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.type).append("/").append(this.subtype);
        if (!this.parameters.isEmpty()) {
            this.parameters.forEach((key, value) -> sb
                    .append(";")
                    .append(key)
                    .append("=")
                    .append(value)
            );
        }
        return sb.toString();
    }
}
