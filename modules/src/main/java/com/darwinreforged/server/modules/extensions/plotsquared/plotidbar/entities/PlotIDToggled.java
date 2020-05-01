package com.darwinreforged.server.modules.extensions.plotsquared.plotidbar.entities;


import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class PlotIDToggled {

    @Setting("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Setting("toggledID")
    private List<UUID> toggledID = new ArrayList<>();  
    
    @Setting("toggledMem")
    private List<UUID> toggledMem = new ArrayList<>(); 
    @Setting
    private TextTemplate template;

    public void setToggledMem(List<UUID> toggledMem) {
        this.toggledMem = toggledMem;
    }
    public void setToggledID(List<UUID> toggledID) {
        this.toggledID = toggledID;
    }

    public void setTemplate(TextTemplate template) {
        this.template = template;
    }

    public List<UUID> getToggledID() {
        return toggledID;
    }
    
    public List<UUID> getToggledMem() {
        return toggledMem;
    }

    public TextTemplate getTemplate() {
        return template;
    }

}
