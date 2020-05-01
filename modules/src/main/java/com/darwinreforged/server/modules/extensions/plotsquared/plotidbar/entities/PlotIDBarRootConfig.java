package com.darwinreforged.server.modules.extensions.plotsquared.plotidbar.entities;


import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class PlotIDBarRootConfig {

    @Setting("toggled")
    private List<PlotIDToggled> plotIDToggled = new ArrayList<>();

    public List<PlotIDToggled> getCategories() {
        return plotIDToggled;
    }
}
