package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.util.collections.LFUCache;

public class MimeTypes {

    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MimeType APPLICATION_JSON = new MimeType("application", "json");

    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final MimeType APPLICATION_XML = new MimeType("application", "xml");

    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final MimeType TEXT_PLAIN = new MimeType("text", "plain");

    public static final String TEXT_HTML_VALUE = "text/html";
    public static final MimeType TEXT_HTML = new MimeType("text", "html");

    public static final String WILDCARD_VALUE = "*/*";
    public static final MimeType WILDCARD = new MimeType("*", "*");

    private static final LFUCache<String, MimeType> cachedMimeTypes = new LFUCache<>(
            64,
            MimeTypes::parseInternal
    );

    public static MimeType parse(String mimeType) {
        return cachedMimeTypes.get(mimeType);
    }

    private static MimeType parseInternal(String mimeType) {
        String[] parts = mimeType.split(";");
        String[] typeParts = parts[0].trim().split("/");
        if (typeParts.length != 2) {
            throw new IllegalArgumentException("Invalid MIME type: " + mimeType);
        }
        String type = typeParts[0].trim();
        String subtype = typeParts[1].trim();
        return new MimeType(type, subtype);
    }
}
