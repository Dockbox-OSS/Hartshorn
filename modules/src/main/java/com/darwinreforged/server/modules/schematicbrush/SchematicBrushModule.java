package com.darwinreforged.server.modules.schematicbrush;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;
import com.darwinreforged.server.api.resources.Permissions;
import com.darwinreforged.server.api.resources.Translations;
import com.darwinreforged.server.api.utils.PlayerUtils;
import com.darwinreforged.server.modules.schematicbrush.adapters.SchemBrushAdapter;
import com.darwinreforged.server.modules.schematicbrush.adapters.SchemBrushAdapterFactory;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.registry.WorldData;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.text.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


@ModuleInfo(id = "schematicbrush", name = "schematicbrush", version = "1.1.2", dependencies = @Dependency(id = "worldedit"), description = "Sponge implementation of Schematic Brush")
public class SchematicBrushModule extends PluginModule {

    final Pattern uuidRegexp = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static final int DEFAULT_WEIGHT = -1;

    public SchematicBrushModule() {
    }

    public enum Flip {
        NONE, NS, EW, RANDOM;
    }

    public enum Rotation {
        ROT0(0), ROT90(90), ROT180(180), ROT270(270), RANDOM(-1);

        final int deg;

        Rotation(int deg) {
            this.deg = deg;
        }
    }

    public enum Placement {
        CENTER, BOTTOM, DROP
    }

    public CommandsManager<CommandSource> cmdmgr;
    private static Random rnd = new Random();

    public static class SchematicDef {
        public String name;
        public String format;
        public Rotation rotation;
        public Flip flip;
        public int weight;      // If -1, equal weight with other -1 schematics
        public int offset;

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null) return false;
            if (o instanceof SchematicDef) {
                SchematicDef sd = (SchematicDef) o;
                return sd.name.equals(this.name) && (sd.rotation == this.rotation) && (sd.flip == this.flip) && (sd.weight == this.weight) && (sd.offset == this.offset);
            }
            return false;
        }

        @Override
        public String toString() {
            String n = this.name;
            if (format != null)
                n += "#" + format;
            if ((rotation != Rotation.ROT0) || (flip != Flip.NONE)) {
                n += "@";
                if (rotation == Rotation.RANDOM)
                    n += '*';
                else
                    n += (90 * rotation.ordinal());
            }
            if (flip == Flip.RANDOM) {
                n += '*';
            } else if (flip == Flip.NS) {
                n += 'N';
            } else if (flip == Flip.EW) {
                n += 'E';
            }
            if (weight >= 0) {
                n += ":" + weight;
            }
            if (offset != 0) {
                n += "^" + offset;
            }
            return n;
        }

        public Rotation getRotation() {
            if (rotation == Rotation.RANDOM) {
                return Rotation.values()[rnd.nextInt(4)];
            }
            return rotation;
        }

        public Flip getFlip() {
            if (flip == Flip.RANDOM) {
                return Flip.values()[rnd.nextInt(3)];
            }
            return flip;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static class SchematicSet {
        public String name;
        public String desc;
        public List<SchematicDef> schematics;

        public SchematicSet() {
        }

        public SchematicSet(String n, String d, List<SchematicDef> sch) {
            this.name = n;
            this.desc = (d == null) ? "" : d;
            this.schematics = (sch == null) ? (new ArrayList<>()) : sch;
        }

        public int getTotalWeights() {
            int wt = 0;
            for (SchematicDef sd : schematics) {
                if (sd.weight > 0)
                    wt += sd.weight;
            }
            return wt;
        }

        public int getEqualWeightCount() {
            int cnt = 0;
            for (SchematicDef sd : schematics) {
                if (sd.weight <= 0) {
                    cnt++;
                }
            }
            return cnt;
        }

        public SchematicDef getRandomSchematic() {
            int total = getTotalWeights();
            int cnt = getEqualWeightCount();
            int rndval = 0;
            // If any fixed weights
            if (total > 0) {
                // If total fixed more than 100, or equal weight count is zero
                if ((total > 100) || (cnt == 0)) {
                    rndval = rnd.nextInt(total);    // Random from 0 to total-1
                } else {
                    rndval = rnd.nextInt(100);      // From 0 to 100
                }
                if (rndval < total) {   // Fixed weight match
                    for (SchematicDef def : schematics) {
                        if (def.weight > 0) {
                            rndval -= def.weight;
                            if (rndval < 0) {   // Match?
                                return def;
                            }
                        }
                    }
                }
            }
            if (cnt > 0) {
                rndval = rnd.nextInt(cnt);  // Pick from equal weight values
                for (SchematicDef def : schematics) {
                    if (def.weight < 0) {
                        rndval--;
                        if (rndval < 0) {
                            return def;
                        }
                    }
                }
            }
            return null;
        }
    }

    private HashMap<String, SchematicSet> sets = new HashMap<String, SchematicSet>();

    public class SchematicBrushInstance implements Brush {

        private static final long DELAY = 500;

        SchematicSet set;
        Player player;
        boolean skipair;
        boolean replaceall;
        int yoff;
        Placement place;
        long time = 0;

        @Override
        public void build(EditSession editsession, Vector pos, com.sk89q.worldedit.function.pattern.Pattern mat, double size) throws MaxChangedBlocksException {

            SchematicDef def = set.getRandomSchematic();    // Pick schematic from set
            if (def == null) return;

            LocalSession sess = WorldEdit.getInstance().getSessionManager().get(player);
            int[] minY = new int[1];
            String schfilename = loadSchematicIntoClipboard(player, sess, def.name, def.format, minY);
            if (schfilename == null) {
                return;
            }
            ClipboardHolder cliph = null;
            Clipboard clip = null;
            try {
                cliph = sess.getClipboard();
            } catch (EmptyClipboardException e) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()), Translations.SCHEMATIC_EMPTY.t());
                return;
            }
            AffineTransform trans = new AffineTransform();
            // Get rotation for clipboard
            Rotation rot = def.getRotation();
            if (rot != Rotation.ROT0) {
                trans = rotate2D(trans, rot);
            }
            // Get flip option
            Flip flip = def.getFlip();
            switch (flip) {
                case NS:
                    trans = flip(trans, Direction.NORTH);
                    break;
                case EW:
                    trans = flip(trans, Direction.WEST);
                    break;
                default:
                    break;
            }
            cliph.setTransform(trans);

            clip = cliph.getClipboard();
            Region region = clip.getRegion();
            Vector clipOrigin = clip.getOrigin();
            Vector centerOffset = region.getCenter().subtract(clipOrigin);

            // And apply clipboard to edit session
            Vector ppos;
            if (place == Placement.DROP) {
                ppos = new Vector(centerOffset.getX(), -def.offset - yoff - minY[0] + 1, centerOffset.getZ());
            } else if (place == Placement.BOTTOM) {
                ppos = new Vector(centerOffset.getX(), -def.offset - yoff + 1, centerOffset.getZ());
            } else { // Else, default is CENTER (same as clipboard brush
                ppos = new Vector(centerOffset.getX(), centerOffset.getY() - def.offset - yoff + 1, centerOffset.getZ());
            }
            ppos = trans.apply(ppos);
            ppos = pos.subtract(ppos);

            if (!replaceall) {
                editsession.setMask(new BlockMask(editsession, new BaseBlock(BlockID.AIR, -1)));
            }
            PasteBuilder pb = cliph.createPaste(editsession, editsession.getWorld().getWorldData()).to(ppos)
                    .ignoreAirBlocks(skipair);
            Operations.completeLegacy(pb.build());
            schfilename = schfilename.contains("/") ? "..." + schfilename.substring(schfilename.lastIndexOf("/") - 5, schfilename.length()) : schfilename;

            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_APPLIED_CLIPBOARD.ft(schfilename, flip.name(), rot.deg, place.name()));
        }
    }

    private AffineTransform rotate2D(AffineTransform trans, Rotation rot) {
        return trans.rotateY(rot.ordinal() * 90);
    }

    private AffineTransform flip(AffineTransform trans, Direction dir) {
        return trans.scale(dir.toVector().positive().multiply(-2).add(1, 1, 1));
    }

    private AffineTransform doOffset(AffineTransform trans, Vector off) {
        return trans.translate(off.multiply(-1));
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File config;

    @Inject
    private Logger log;

    private SchemBrushAdapter schemBrushAdapter = SchemBrushAdapterFactory.DUMMY;

    @Listener
    public void onEnable(GameInitializationEvent event) {
        schemBrushAdapter = SchemBrushAdapterFactory.getAdapter();

        if (schemBrushAdapter.isPresent()) {
            log.info("Successfully created adapter for the current version of WorldEdit");
        } else {
            log.info("Could not create an adapter for the current version of WorldEdit");
        }

        if (!Files.exists(Paths.get(config.getAbsolutePath()))) {
            try {
                new File(Paths.get(config.getAbsolutePath()).toUri()).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Initialize bo2 directory, if needed
        File bo2dir = this.getDirectoryForFormat("bo2");
        bo2dir.mkdirs();

        // Load existing schematics
        loadSchematicSets();


        DarwinServer.registerCommand(CommandSpec.builder()
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(this::handleSCHBRCommand)
                .build(), "/schbr");

        DarwinServer.registerCommand(CommandSpec.builder()
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(this::handleSCHSETCommand)
                .build(), "/schset");

        DarwinServer.registerCommand(CommandSpec.builder()
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(this::handleSCHLISTCommand)
                .build(), "/schlist");
    }

    private CommandResult handleSCHBRCommand(CommandSource commandSource, CommandContext commandContext) {
        if (!(commandSource instanceof org.spongepowered.api.entity.living.player.Player)) {
            PlayerUtils.tell(commandSource, Translations.PLAYER_ONLY_COMMAND.t());
            return CommandResult.empty();
        }


        org.spongepowered.api.entity.living.player.Player player0 = (org.spongepowered.api.entity.living.player.Player) commandSource;
        Optional<Player> playerOptional = schemBrushAdapter.wrapPlayer(player0);
        if (!playerOptional.isPresent()) {
            PlayerUtils.tell(player0, Translations.COULD_NOT_DETECT_WORLDEDIT.t());
            return CommandResult.empty();
        }

        Player player = playerOptional.get();

        // Test for command access
        if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_SET_USE.p())) {
            PlayerUtils.tell(player0, Translations.COMMAND_NO_PERMISSION.t());
            return CommandResult.empty();
        }

        Optional<String> argv = commandContext.getOne("args");

        if (!argv.isPresent()) {
            player.print("Schematic brush requires &set-id or one or more schematic patterns");
            return CommandResult.empty();
        }
        String setid = null;
        SchematicSet ss = null;
        String[] args = argv.get().split(" ");
        if (args[0].startsWith("&")) {  // If set ID
            if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_SET_USE.p())) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_SET_NOT_ALLOWED.t());
                return CommandResult.empty();
            }
            setid = args[0].substring(1);
            ss = sets.get(setid);
            if (ss == null) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_SET_NOT_FOUND.ft(setid));
                return CommandResult.empty();
            }
        } else {
            ArrayList<SchematicDef> defs = new ArrayList<SchematicDef>();
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) { // Option
                } else {
                    SchematicDef sd = parseSchematic(player, args[i]);
                    if (sd == null) {
                        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                                Translations.SCHEMATIC_INVALID_DEFINITION.ft(args[i]));
                        return CommandResult.empty();
                    }
                    defs.add(sd);
                }
            }
            ss = new SchematicSet(null, null, defs);
        }
        boolean skipair = true;
        boolean replaceall = false;
        int yoff = 0;
        Placement place = Placement.CENTER;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) { // Option
                if (args[i].equals("-incair")) {
                    skipair = false;
                } else if (args[i].equals("-replaceall")) {
                    replaceall = true;
                } else if (args[i].startsWith("-yoff:")) {
                    String offval = args[i].substring(args[i].indexOf(':') + 1);
                    try {
                        yoff = Integer.parseInt(offval);
                    } catch (NumberFormatException nfx) {
                        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                                Translations.SCHEMATIC_BAD_OFFSET_Y.ft(offval));
                    }
                } else if (args[i].startsWith("-place:")) {
                    String pval = args[i].substring(args[i].indexOf(':') + 1).toUpperCase();
                    place = Placement.valueOf(pval);
                    if (place == null) {
                        place = Placement.CENTER;
                        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                                Translations.SCHEMATIC_BAD_PLACE_CENTER.ft(pval));
                    }
                }
            }
        }
        // Connect to world edit session
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(player);

        SchematicBrushInstance sbi = new SchematicBrushInstance();
        sbi.set = ss;
        sbi.player = player;
        sbi.skipair = skipair;
        sbi.yoff = yoff;
        sbi.place = place;
        sbi.replaceall = replaceall;
        // Get brush tool
        BrushTool tool;
        try {
            tool = session.getBrushTool(player.getItemInHand());
            tool.setBrush(sbi, Permissions.SCHEMATIC_BRUSH_SET_USE.p());
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_BRUSH_SET.t());
        } catch (InvalidToolBindException e) {
            player.print(e.getMessage());
        }

        return CommandResult.success();
    }

    private CommandResult handleSCHSETCommand(CommandSource commandSource, CommandContext commandContext) {
        if (!(commandSource instanceof org.spongepowered.api.entity.living.player.Player)) {
            PlayerUtils.tell(commandSource, Translations.PLAYER_ONLY_COMMAND.t());
            return CommandResult.empty();
        }


        org.spongepowered.api.entity.living.player.Player player0 = (org.spongepowered.api.entity.living.player.Player) commandSource;
        Optional<Player> playerOptional = schemBrushAdapter.wrapPlayer(player0);
        if (!playerOptional.isPresent()) {
            PlayerUtils.tell(player0, Translations.COULD_NOT_DETECT_WORLDEDIT.t());
            return CommandResult.empty();
        }

        Player player = playerOptional.get();

        // Test for command access
        if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_SET_USE.p())) {
            PlayerUtils.tell(player0, Translations.COMMAND_NO_PERMISSION.t());
            return CommandResult.empty();
        }

        Optional<String> argv = commandContext.getOne("args");

        if (!argv.isPresent()) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_PATTERN_REQUIRED.t());
            return CommandResult.empty();
        }
        String[] args = argv.get().split(" ");

        if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_SET.f(args[0]))) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.COMMAND_NO_PERMISSION.t());
            return CommandResult.empty();
        }
        if (args[0].equals("list")) {
            return handleSCHSETList(player, args);
        } else if (args[0].equals("create")) {
            return handleSCHSETCreate(player, args);
        } else if (args[0].equals("delete")) {
            return handleSCHSETDelete(player, args);
        } else if (args[0].equals("append")) {
            return handleSCHSETAppend(player, args);
        } else if (args[0].equals("get")) {
            return handleSCHSETGet(player, args);
        } else if (args[0].equals("remove")) {
            return handleSCHSETRemove(player, args);
        } else if (args[0].equals("setdesc")) {
            return handleSCHSETSetDesc(player, args);
        }

        return CommandResult.empty();
    }

    private CommandResult handleSCHSETList(Actor player, String[] args) {
        String contains = null;
        if (args.length > 2) {
            contains = args[1];
        }
        int cnt = 0;
        TreeSet<String> keys = new TreeSet<String>(sets.keySet());
        for (String k : keys) {
            if ((contains != null) && (!k.contains(contains))) {
                continue;
            }
            SchematicSet ss = sets.get(k);
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_LIST_ROW.ft(ss.name, ss.desc));
            cnt++;
        }
        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_LIST_COUNT.ft(cnt));

        return CommandResult.success();
    }

    private CommandResult handleSCHSETCreate(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.success();
        }
        String setid = args[1];
        if (sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ALREADY_DEFINED.ft(setid));
            return CommandResult.success();
        }
        SchematicSet ss = new SchematicSet(setid, "", null);
        sets.put(setid, ss);
        // Any other arguments are schematic IDs to add
        for (int i = 2; i < args.length; i++) {
            SchematicDef def = parseSchematic(player, args[i]);
            if (def == null) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_SET_INVALID.ft(args[i]));
            } else {
                ss.schematics.add(def);
            }
        }
        saveSchematicSets();

        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_CREATED.ft(setid));

        return CommandResult.success();
    }

    private CommandResult handleSCHSETDelete(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.empty();
        }
        String setid = args[1];
        if (!sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_NOT_DEFINED.ft(setid));
            return CommandResult.empty();
        }
        sets.remove(setid);

        saveSchematicSets();

        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_DELETED.ft(setid));

        return CommandResult.empty();
    }

    private CommandResult handleSCHSETAppend(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.empty();
        }
        String setid = args[1];
        if (!sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_NOT_DEFINED.ft(setid));
            return CommandResult.empty();
        }
        SchematicSet ss = sets.get(setid);
        // Any other arguments are schematic IDs to add
        for (int i = 2; i < args.length; i++) {
            SchematicDef def = parseSchematic(player, args[i]);
            if (def == null) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_SET_INVALID.ft(args[i]));
            } else {
                ss.schematics.add(def);
            }
        }
        saveSchematicSets();

        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_UPDATED.ft(setid));

        return CommandResult.empty();
    }

    private CommandResult handleSCHSETRemove(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.empty();
        }
        String setid = args[1];
        if (!sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_NOT_DEFINED.ft(setid));
            return CommandResult.empty();
        }
        SchematicSet ss = sets.get(setid);
        // Any other arguments are schematic IDs to remove
        for (int i = 2; i < args.length; i++) {
            SchematicDef def = parseSchematic(player, args[i]);
            if (def == null) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_SET_INVALID.ft(args[i]));
            } else {  // Now look for match
                int idx = ss.schematics.indexOf(def);
                if (idx >= 0) {
                    ss.schematics.remove(idx);
                    PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                            Translations.SCHEMATIC_REMOVED_SET.ft(args[i]));
                } else {
                    PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                            Translations.SCHEMATIC_NOT_FOUND_SET.ft(args[i]));
                }
            }
        }
        saveSchematicSets();

        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_UPDATED.ft(setid));

        return CommandResult.empty();
    }

    private CommandResult handleSCHSETSetDesc(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.empty();
        }
        String setid = args[1];
        if (!sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_NOT_DEFINED.ft(setid));
            return CommandResult.empty();
        }
        SchematicSet ss = sets.get(setid);
        // Any other arguments are descrption
        String desc = "";
        for (int i = 2; i < args.length; i++) {
            if (i == 2)
                desc = args[i];
            else
                desc = desc + " " + args[i];
        }
        ss.desc = desc;

        saveSchematicSets();

        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_UPDATED.ft(setid));

        return CommandResult.empty();
    }

    private CommandResult handleSCHSETGet(Actor player, String[] args) {
        if (args.length < 2) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_ID_MISSING.t());
            return CommandResult.empty();
        }
        String setid = args[1];
        if (!sets.containsKey(setid)) {  // Existing ID?
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_NOT_DEFINED.ft(setid));
            return CommandResult.empty();
        }
        SchematicSet ss = sets.get(setid);
        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_SET_DESCRIPTION.ft(ss.desc), false);
        for (SchematicDef sd : ss.schematics) {
            String det = sd.name;
            if (sd.format != null) {
                det += ", fmt=" + sd.format;
            }
            if (sd.rotation != Rotation.ROT0) {
                if (sd.rotation == Rotation.RANDOM)
                    det += ", rotate=RANDOM";
                else
                    det += ", rotate=" + (90 * sd.rotation.ordinal()) + "\u00B0";
            }
            if (sd.flip != Flip.NONE) {
                det += ", flip=" + sd.flip;
            }
            if (sd.weight > 0) {
                det += ", weight=" + sd.weight;
            }

            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_DESCRIPTION.ft(sd.toString(), det));
        }
        if ((ss.getTotalWeights() > 100) && (ss.getEqualWeightCount() > 0)) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_SET_WEIGHT_TOO_HIGH.t());
        }

        return CommandResult.empty();
    }

    private File getDirectoryForFormat(String fmt) {
        if (fmt.equals("schematic")) {  // Get from worldedit directory
            return WorldEdit.getInstance().getWorkingDirectoryFile(WorldEdit.getInstance().getConfiguration().saveDir);
        } else {  // Else, our own type specific directory
            return new File(config.getParent(), fmt);
        }
    }


    private static final int LINES_PER_PAGE = 10;

    private CommandResult handleSCHLISTCommand(CommandSource commandSource, CommandContext commandContext) {
        if (!(commandSource instanceof org.spongepowered.api.entity.living.player.Player)) {
            PlayerUtils.tell(commandSource, Translations.PLAYER_ONLY_COMMAND.t());
            return CommandResult.empty();
        }


        org.spongepowered.api.entity.living.player.Player player0 = (org.spongepowered.api.entity.living.player.Player) commandSource;
        Optional<Player> playerOptional = schemBrushAdapter.wrapPlayer(player0);
        if (!playerOptional.isPresent()) {
            PlayerUtils.tell(player0, Translations.COULD_NOT_DETECT_WORLDEDIT.t());
            return CommandResult.empty();
        }

        Player player = playerOptional.get();
        // Test for command access
        if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_SET_USE.p())) {
            PlayerUtils.tell(player0, Translations.COMMAND_NO_PERMISSION.t());
            return CommandResult.empty();
        }

        Optional<String> argv = commandContext.getOne("args");

        if (!argv.isPresent()) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_PATTERN_REQUIRED.t());
            return CommandResult.empty();
        }
        String[] args = argv.get().split(" ");
        // Test for command access
        if (!player.hasPermission(Permissions.SCHEMATIC_BRUSH_LIST.p())) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.COMMAND_NO_PERMISSION.t());
            return CommandResult.empty();
        }
        int page = 1;
        String fmt = "schematic";
        for (int i = 0; i < args.length; i++) {
            try {
                page = Integer.parseInt(args[i]);
            } catch (NumberFormatException nfx) {
                fmt = args[i];
            }
        }
        File dir = getDirectoryForFormat(fmt);  // Get directory for extension
        if (dir == null) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_INVALID_FORMAT.ft(fmt));
            return CommandResult.empty();
        }
        final Pattern p = Pattern.compile(".*\\." + fmt);
        List<String> files = getMatchingFiles(dir, p, playerOptional.get());
        Collections.sort(files);
        int cnt = (files.size() + LINES_PER_PAGE - 1) / LINES_PER_PAGE;  // Number of pages
        if (page < 1) page = 1;
        if (page > cnt) page = cnt;
        PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                Translations.SCHEMATIC_LIST_PAGINATION_FOOTER.ft(page, cnt, files.size()));
        for (int i = (page - 1) * LINES_PER_PAGE; (i < (page * LINES_PER_PAGE)) && (i < files.size()); i++) {
            player.print(files.get(i));
        }
        return CommandResult.empty();
    }

    private static final Pattern schsplit = Pattern.compile("[@:#^]");

    private SchematicDef parseSchematic(Actor player, String sch) {
        String[] toks = schsplit.split(sch, 0);
        final String name = toks[0];  // Name is first
        String formatName = "schematic";
        Rotation rot = Rotation.ROT0;
        Flip flip = Flip.NONE;
        int wt = DEFAULT_WEIGHT;
        int offset = 0;

        for (int i = 1, off = toks[0].length(); i < toks.length; i++) {
            char sep = sch.charAt(off);
            off = off + 1 + toks[i].length();
            if (sep == '@') { // Rotation/flip?
                String v = toks[i];
                if (v.startsWith("*")) {  // random rotate?
                    rot = Rotation.RANDOM;
                    v = v.substring(1);
                } else {  // Else, must be number
                    rot = Rotation.ROT0;
                    int coff;
                    int val = 0;
                    for (coff = 0; coff < v.length(); coff++) {
                        if (Character.isDigit(v.charAt(coff))) {
                            val = (val * 10) + (v.charAt(coff) - '0');
                        } else {
                            break;
                        }
                    }
                    // If not multiple of 90, error
                    if ((val % 90) != 0) {
                        return null;
                    }
                    rot = Rotation.values()[((val / 90) % 4)];    // Clamp to 0-270
                    v = v.substring(coff);
                }
                if (v.length() == 0) {
                    flip = Flip.NONE;
                } else {
                    char c = v.charAt(0);
                    switch (c) {
                        case '*':
                            flip = Flip.RANDOM;
                            break;
                        case 'N':
                        case 'S':
                        case 'n':
                        case 's':
                            flip = Flip.NS;
                            break;
                        case 'E':
                        case 'W':
                        case 'e':
                        case 'w':
                            flip = Flip.EW;
                            break;
                        default:
                            return null;
                    }
                }
            } else if (sep == ':') { // weight
                try {
                    wt = Integer.parseInt(toks[i]);
                } catch (NumberFormatException nfx) {
                    return null;
                }
            } else if (sep == '#') { // format name
                formatName = toks[i];
            } else if (sep == '^') { // Offset
                try {
                    offset = Integer.parseInt(toks[i]);
                } catch (NumberFormatException nfx) {
                    return null;
                }
            }
        }
        // See if schematic name is valid
        File dir = getDirectoryForFormat(formatName);
        try {
            String fname = resolveName(player, dir, name, formatName);
            if (fname == null) {
                return null;
            }
            File f = WorldEdit.getInstance().getSafeOpenFile(null, dir, fname, formatName);
            if (!f.exists()) {
                return null;
            }
            if ((!formatName.equals("schematic")) && (!formatName.equals("bo2"))) {
                return null;
            }
            // If we're here, everything is good - make schematic object
            SchematicDef schematic = new SchematicDef();
            schematic.name = name;
            schematic.format = formatName;
            schematic.rotation = rot;
            schematic.flip = flip;
            schematic.weight = wt;
            schematic.offset = offset;

            return schematic;
        } catch (FilenameException fx) {
            return null;
        }
    }

    private void loadSchematicSets() {
        Gson g = new Gson();
        try {
            String s = new String(Files.readAllBytes(Paths.get(config.getAbsolutePath())));
            Type type = new TypeToken<LinkedHashMap<String, SchematicDef>>() {
            }.getType();
            LinkedHashMap obj = g.fromJson(s, type);
            sets = new HashMap<>(obj == null ? Collections.EMPTY_MAP : obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSchematicSets() {
        Gson g = new GsonBuilder().setPrettyPrinting().create();

        String s = g.toJson(sets);
        try {
            Files.write(Paths.get(config.getAbsolutePath()), s.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getMatchingFiles(File dir, Pattern p, Actor actor) {
        return getMatchingFiles(dir.listFiles(), p, null, actor);
    }

    private List<String> getMatchingFiles(File[] files, Pattern p, String path, Actor player) {
        List<String> res = new ArrayList<>();
        for (File f : files) {
            String n = (path == null) ? f.getName() : (path + "/" + f.getName());
            if (isAnyPlayersDir(f) && f.getName().equalsIgnoreCase(player.getUniqueId().toString())) {
                res.addAll(getMatchingFiles(f.listFiles(), p, n, player));
            }
            Matcher m = p.matcher(f.getName());
            if (m.matches()) {
                res.add(n);
            }

        }
        return res;
    }

    private boolean isAnyPlayersDir(File f) {
        if (!f.isDirectory()) {
            return false;
        }
        Matcher matcher = uuidRegexp.matcher(f.getName());
        return matcher.matches();
    }

    /* Resolve name to loadable name - if contains wildcards, pic random matching file */
    private String resolveName(Actor player, File dir, String fname, final String ext) {
        // If command-line style wildcards
        if ((!fname.startsWith("^")) && ((fname.indexOf('*') >= 0) || (fname.indexOf('?') >= 0))) {
            // Compile to regex
            fname = "^" + fname.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        }
        if (fname.startsWith("^")) { // If marked as regex
            final int extlen = ext.length();
            try {
                final Pattern p = Pattern.compile(fname + "\\." + ext);
                List<String> files = getMatchingFiles(dir, p, player);
                if (!files.isEmpty()) {    // Multiple choices?
                    String n = files.get(rnd.nextInt(files.size()));
                    n = n.substring(0, n.length() - extlen - 1);
                    return n;
                } else {
                    return null;
                }
            } catch (PatternSyntaxException x) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_INVALID_FILENAME.ft(fname, x.getMessage()));
                return null;
            }
        }
        return fname;
    }

    private String loadSchematicIntoClipboard(Player player, LocalSession sess, String fname, String format, int[] bottomY) {
        File dir = getDirectoryForFormat(format);
        if (dir == null) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_INVALID_FORMAT.ft(format));
            return null;
        }
        String name = resolveName(player, dir, fname, format);
        if (name == null) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_FILE_NOT_FOUND.ft(fname));
            return null;
        }
        File f;
        boolean rslt = false;
        try {
            f = WorldEdit.getInstance().getSafeOpenFile(null, dir, name, format);
            if (!f.exists()) {
                PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                        Translations.SCHEMATIC_FILE_NOT_FOUND.ft(fname));
                return null;
            }
            // Figure out format to use
            if (format.equals("schematic")) {
                ClipboardFormat fmt = ClipboardFormat.findByFile(f);

                if (fmt == null) {
                    PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                            Translations.SCHEMATIC_FORMAT_NOT_FOUND.ft(name));
                    return null;
                }
                if (!fmt.isFormat(f)) {
                    PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                            Translations.SCHEMATIC_INVALID_FORMAT.ft(fmt));
                    return null;
                }
                String filePath = f.getCanonicalPath();
                String dirPath = dir.getCanonicalPath();

                if (!filePath.substring(0, dirPath.length()).equals(dirPath)) {
                    return null;
                } else {
                    FileInputStream fis = new FileInputStream(f);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    ClipboardReader reader = fmt.getReader(bis);

                    WorldData worldData = player.getWorld().getWorldData();
                    Clipboard cc = reader.read(player.getWorld().getWorldData());
                    if (cc != null) {
                        Region reg = cc.getRegion();
                        int minY = reg.getHeight() - 1;
                        for (int y = 0; (minY == -1) && (y < reg.getHeight()); y++) {
                            for (int x = 0; (minY == -1) && (x < reg.getWidth()); x++) {
                                for (int z = 0; (minY == -1) && (z < reg.getLength()); z++) {
                                    if (cc.getBlock(new Vector(x, y, z)) != null) {
                                        minY = y;
                                        break;
                                    }
                                }
                            }
                        }
                        bottomY[0] = minY;
                        sess.setClipboard(new ClipboardHolder(cc, worldData));
                        rslt = true;
                    }
                }
            }
            // Else if BO2 file
            else if (format.equals("bo2")) {
                Clipboard cc = loadBOD2File(f);
                if (cc != null) {
                    WorldData worldData = player.getWorld().getWorldData();
                    sess.setClipboard(new ClipboardHolder(cc, worldData));
                    rslt = true;
                    bottomY[0] = 0; // Always zero for these: we compact things to bottom
                }
            } else {
                return null;
            }
        } catch (FilenameException e1) {
            player.printError(e1.getMessage());
        } catch (IOException e) {
            PlayerUtils.tellIfPresent(PlayerUtils.getPlayerFromUUID(player.getUniqueId()),
                    Translations.SCHEMATIC_READ_ERROR.ft(name, e.getMessage()));
        }

        return (rslt) ? name : null;
    }

    private Clipboard loadBOD2File(File f) throws IOException {
        Clipboard cc = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.US_ASCII));
        try {
            Map<String, String> properties = new HashMap<String, String>();
            Map<Vector, int[]> blocks = new HashMap<Vector, int[]>();
            boolean readingMetaData = false, readingData = false;
            String line;
            int lowestX = Integer.MAX_VALUE, highestX = Integer.MIN_VALUE;
            int lowestY = Integer.MAX_VALUE, highestY = Integer.MIN_VALUE;
            int lowestZ = Integer.MAX_VALUE, highestZ = Integer.MIN_VALUE;
            while ((line = in.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (readingMetaData) {
                    if (line.equals("[DATA]")) {
                        readingMetaData = false;
                        readingData = true;
                    } else {
                        int p = line.indexOf('=');
                        String name = line.substring(0, p).trim();
                        String value = line.substring(p + 1).trim();
                        properties.put(name, value);
                    }
                } else if (readingData) {
                    int p = line.indexOf(':');
                    String coordinates = line.substring(0, p);
                    String spec = line.substring(p + 1);
                    p = coordinates.indexOf(',');
                    int x = Integer.parseInt(coordinates.substring(0, p));
                    int p2 = coordinates.indexOf(',', p + 1);
                    int y = Integer.parseInt(coordinates.substring(p + 1, p2));
                    int z = Integer.parseInt(coordinates.substring(p2 + 1));
                    p = spec.indexOf('.');
                    int blockId, data = 0;
                    int[] branch = null;
                    if (p == -1) {
                        blockId = Integer.parseInt(spec);
                    } else {
                        blockId = Integer.parseInt(spec.substring(0, p));
                        p2 = spec.indexOf('#', p + 1);
                        if (p2 == -1) {
                            data = Integer.parseInt(spec.substring(p + 1));
                        } else {
                            data = Integer.parseInt(spec.substring(p + 1, p2));
                            p = spec.indexOf('@', p2 + 1);
                            branch = new int[]{Integer.parseInt(spec.substring(p2 + 1, p)), Integer.parseInt(spec.substring(p + 1))};
                        }
                    }
                    if (blockId == 0) continue; // Skip air blocks;

                    if (x < lowestX) {
                        lowestX = x;
                    }
                    if (x > highestX) {
                        highestX = x;
                    }
                    if (y < lowestY) {
                        lowestY = y;
                    }
                    if (y > highestY) {
                        highestY = y;
                    }
                    if (z < lowestZ) {
                        lowestZ = z;
                    }
                    if (z > highestZ) {
                        highestZ = z;
                    }
                    Vector coords = new Vector(x, y, z);
                    blocks.put(coords, new int[]{blockId, data});
                } else {
                    if (line.equals("[META]")) {
                        readingMetaData = true;
                    }
                }
            }
            Vector size = new Vector(highestX - lowestX + 1, highestZ - lowestZ + 1, highestY - lowestY + 1);
            Vector offset = new Vector(-lowestX, -lowestZ, -lowestY);
            Region reg = new CuboidRegion(size, offset);
            cc = new BlockArrayClipboard(reg);
            for (Vector v : blocks.keySet()) {
                int[] ids = blocks.get(v);
                Vector vv = new Vector(v.getX() - lowestX, v.getZ() - lowestZ, v.getY() - lowestY);
                cc.setBlock(vv, new BaseBlock(ids[0], ids[1]));
            }
        } catch (WorldEditException e) {
            log.info("WorldEdit exception: " + e.getMessage());
        } finally {
            in.close();
        }

        return cc;
    }
}
