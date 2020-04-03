package com.darwinreforged.server.modules.schematicbrush.adapters;

public class SchemBrushAdapterFactory {

    public static final SchemBrushAdapter DUMMY = new DummySchemBrushAdapter();

    public static SchemBrushAdapter getAdapter() {
        SchemBrushAdapter forge = get("com.sk89q.worldedit.forge.ForgeWorldEdit", "com.mikeprimm.schematicbrush.adapter.ForgeAdapter");
        if (forge != null && forge.isPresent()) {
            return forge;
        }

        SchemBrushAdapter sponge = get("com.sk89q.worldedit.sponge.SpongeWorldEdit", "com.mikeprimm.schematicbrush.adapter.SpongeAdapter");
        if (sponge != null && sponge.isPresent()) {
            return sponge;
        }

        return DUMMY;
    }

    private static SchemBrushAdapter get(String testFor, String adapter) {
        try {
            Class.forName(testFor);
            Class<?> target = Class.forName(adapter);
            return (SchemBrushAdapter) target.newInstance();
        } catch (Throwable t) {
            return null;
        }
    }
}
