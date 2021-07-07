package org.dockbox.hartshorn.blockregistry.models;

import java.util.List;

public class FamilyModel
{
    private String familyId;
    private List<VariantModel> variantModels;

    public FamilyModel() { }

    public String getFamilyId()
    {
        return this.familyId;
    }

    public void setFamilyId(String familyId)
    {
        this.familyId = familyId;
    }

    public List<VariantModel> getVariantModels()
    {
        return this.variantModels;
    }

    public void setVariantModels(List<VariantModel> variantModels)
    {
        this.variantModels = variantModels;
    }
}
