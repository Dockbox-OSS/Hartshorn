package org.dockbox.hartshorn.blockregistry.models;

import org.dockbox.hartshorn.blockregistry.VariantIdentifier;

import java.util.Set;

public class VariantModel
{
    private VariantIdentifier variantIdentifier;
    private String rootId;
    private Set<String> aliases;

    public VariantModel() { }

    public VariantIdentifier getVariantIdentifier()
    {
        return this.variantIdentifier;
    }

    public void setVariantIdentifier(VariantIdentifier variantIdentifier)
    {
        this.variantIdentifier = variantIdentifier;
    }

    public String getRootId()
    {
        return this.rootId;
    }

    public void setRootId(String rootId)
    {
        this.rootId = rootId;
    }

    public Set<String> getAliases()
    {
        return this.aliases;
    }

    public void setAliases(Set<String> aliases)
    {
        this.aliases = aliases;
    }
}
