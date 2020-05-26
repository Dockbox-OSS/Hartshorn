package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("schematicbrush")
public class SchematicBrushTranslations {

    public static final Translation SCHEMATIC_EMPTY = Translation.create("Schematic is empty");
    public static final Translation SCHEMATIC_APPLIED_CLIPBOARD = Translation.create("Applied '{0}', flip={1}, rot={2}, place={3}");
    public static final Translation SCHEMATIC_SET_NOT_ALLOWED = Translation.create("Not permitted to use schematic sets");
    public static final Translation SCHEMATIC_SET_NOT_FOUND = Translation.create("Schematic set '{0}' not found");
    public static final Translation SCHEMATIC_INVALID_DEFINITION = Translation.create("Invalid schematic definition: {0}");
    public static final Translation SCHEMATIC_INVALID_FILENAME = Translation.create("Invalid filename pattern: {0} - {1}");
    public static final Translation SCHEMATIC_BAD_OFFSET_Y = Translation.create("Bad y-offset value: {0}");
    public static final Translation SCHEMATIC_BAD_PLACE_CENTER = Translation.create("Bad place value ({0}) - using CENTER");
    public static final Translation SCHEMATIC_BRUSH_SET = Translation.create("Schematic brush set");
    public static final Translation COULD_NOT_DETECT_WORLDEDIT = Translation.create("Could not detect a supported version of WorldEdit");
    public static final Translation SCHEMATIC_PATTERN_REQUIRED = Translation.create("Schematic brush requires &set-id or one or more schematic patterns");
    public static final Translation SCHEMATIC_SET_LIST_ROW = Translation.create("{0}: desc='{1}'");
    public static final Translation SCHEMATIC_SET_LIST_COUNT = Translation.create("{0} sets returned");
    public static final Translation SCHEMATIC_SET_ID_MISSING = Translation.create("Missing set ID");
    public static final Translation SCHEMATIC_SET_ALREADY_DEFINED = Translation.create("Set '{0}' already defined");
    public static final Translation SCHEMATIC_SET_NOT_DEFINED = Translation.create("Set '{0}' not defined");
    public static final Translation SCHEMATIC_SET_INVALID = Translation.create("Schematic '{0}' invalid - ignored");
    public static final Translation SCHEMATIC_SET_CREATED = Translation.create("Set '{0}' created");
    public static final Translation SCHEMATIC_SET_DELETED = Translation.create("Set '{0}' deleted");
    public static final Translation SCHEMATIC_SET_UPDATED = Translation.create("Set '{0}' updated");
    public static final Translation SCHEMATIC_REMOVED_SET = Translation.create("Schematic '{0}' removed");
    public static final Translation SCHEMATIC_NOT_FOUND_SET = Translation.create("Schematic '{0}' not found in set");
    public static final Translation SCHEMATIC_SET_DESCRIPTION = Translation.create("Description: {0}");
    public static final Translation SCHEMATIC_DESCRIPTION = Translation.create("Schematic: {0} ({1})");
    public static final Translation SCHEMATIC_SET_WEIGHT_TOO_HIGH = Translation.create("Warning: total weights exceed 100 - schematics without weights will never be selected");
    public static final Translation SCHEMATIC_INVALID_FORMAT = Translation.create("Invalid format: {0}");
    public static final Translation SCHEMATIC_LIST_PAGINATION_FOOTER = Translation.create("Page {0} of {1} ({2} files)");
    public static final Translation SCHEMATIC_FILE_NOT_FOUND = Translation.create("Schematic '{0}' file not found");
    public static final Translation SCHEMATIC_FORMAT_NOT_FOUND = Translation.create("Schematic '{0}' format not found");
    public static final Translation SCHEMATIC_READ_ERROR = Translation.create("$1Error reading schematic '{0}' - {1}");

    private SchematicBrushTranslations() {
    }
}
