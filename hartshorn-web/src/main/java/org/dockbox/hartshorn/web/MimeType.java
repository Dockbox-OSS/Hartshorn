package org.dockbox.hartshorn.web;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record MimeType(String type, String subtype, Map<String, String> parameters) {

    private static final String WILDCARD_TYPE = "*";

    public MimeType(String type, String subtype) {
        this(type, subtype, Map.of());
    }

    public MimeType(String type) {
        this(type, "*");
    }

    public String parameter(String name) {
        return this.parameters.get(name);
    }

    @Override
    public Map<String, String> parameters() {
        return Map.copyOf(parameters);
    }

    public boolean isWildcardType() {
        return WILDCARD_TYPE.equals(this.type());
    }

    public boolean isWildcardSubtype() {
        String subtype = this.subtype();
        return (WILDCARD_TYPE.equals(subtype) || subtype.startsWith("*+"));
    }

    public boolean isConcrete() {
        return !isWildcardType() && !isWildcardSubtype();
    }

    @NotNull
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
