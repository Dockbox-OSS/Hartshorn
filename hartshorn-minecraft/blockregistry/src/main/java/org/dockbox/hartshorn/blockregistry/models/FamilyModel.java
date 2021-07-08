package org.dockbox.hartshorn.blockregistry.models;

import java.util.List;

public record FamilyModel(String familyId, List<VariantModel> variantModels) { }
