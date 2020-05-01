package com.darwinreforged.server.modules.brushtooltip.enums;

import com.darwinreforged.server.core.resources.Translations;

public enum Flags {
    AUTO_VIEW("-a", Translations.BRUSH_FLAG_DESCRIPTION_AUTO_VIEW.s()),
    RANDOM_ROTATION("-r", Translations.BRUSH_FLAG_DESCRIPTION_RANDOM_ROTATION.s()),
    HOLLOW("-h", Translations.BRUSH_FLAG_DESCRIPTION_HOLLOW.s()),
    FALLING("-f", Translations.BRUSH_FLAG_DESCRIPTION_FALLING.s()),
    FLAT_LINE("-f", Translations.BRUSH_FLAG_DESCRIPTION_FLAT_LINE.s()),
    MAX_SATURATION("-w", Translations.BRUSH_FLAG_DESCRIPTION_MAX_SATURATION.s()),
    DEPTH_FIRST("-d", Translations.BRUSH_FLAG_DESCRIPTION_DEPTH_FIRST.s()),
    SNOW_LAYERS("-l", Translations.BRUSH_FLAG_DESCRIPTION_SNOW_LAYERS.s()),
    DISABLE_SMOOTHING("-s", Translations.BRUSH_FLAG_DESCRIPTION_DISABLE_SMOOTHING.s()),
    NO_AIR("-a", Translations.BRUSH_FLAG_DESCRIPTION_NO_AIR.s()),
    RELATIVE_LOC("-p", Translations.BRUSH_FLAG_DESCRIPTION_RELATIVE_LOC.s()),
    NATURAL_OCCURRING("-n", Translations.BRUSH_FLAG_DESCRIPTION_NATURAL_OCCURRING.s()),
    SELECT_AFTER("-s", Translations.BRUSH_FLAG_DESCRIPTION_SELECT_AFTER.s()),
    OVERLAY("-o", Translations.BRUSH_FLAG_DESCRIPTION_OVERLAY.s());

    private Flag flag;

    Flags(String flag, String description) {
        this.flag = new Flag(flag, description);
    }

    public Flag getFlag() {
        return flag;
    }
}
