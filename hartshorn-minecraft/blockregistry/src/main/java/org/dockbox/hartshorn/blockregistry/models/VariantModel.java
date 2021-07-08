package org.dockbox.hartshorn.blockregistry.models;

import org.dockbox.hartshorn.blockregistry.VariantIdentifier;

import java.util.Set;

public record VariantModel(VariantIdentifier variantIdentifier, String rootId, Set<String> aliases) { }
