package com.darwinreforged.servermodifications.resources;

import com.intellectualcrafters.plot.util.StringMan;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Translations {

//    Default formats
    PREFIX("$3[] $1"),
    COLOR_PRIMARY("b"),
    COLOR_SECONDARY("3"),
    COLOR_MINOR("7"),
    COLOR_ERROR("c"),
    PLAYER_ONLY_COMMAND("$4This command can only be executed by players"),
    PLOTS1_NAME("Plots1"),
    PLOTS2_NAME("Plots2"),
    PLOTS500_NAME("Plots500"),
    MASTERPLOTS_NAME("MasterPlots"),
    MEMBER_RANK_DISPLAY("Member"),
    EXPERT_RANK_DISPLAY("Expert"),
    MASTER_ARCHITECTURE_DISPLAY("Mastered Skill Architecture"),
    MASTER_NATURE_DISPLAY("Mastered Skill Nature"),
    MASTER_BOTH_DISPLAY("both Mastered Skills"),
    MASTER_RANK_DISPLAY("Master"),
    DEFAULT_SEPARATOR(" - "),
    DEFAULT_PADDING(" $1- "),
    DEFAULT_ON("On"),
    DEFAULT_OFF("Off"),
    UNKNOWN("Unknown"),
    NONE("None"),
    CONSOLE("Console"),
    ONLINE_PLAYER("$1{0}"),
    OFFLINE_PLAYER("$2{0}"),
    UNOWNED("Unowned"),
    EVERYONE("Everyone"),

    //    Time differences
    TIME_DAYS_AGO("$1{0} $2days ago"),
    TIME_YESTERDAY("$1Yesterday"),
    TIME_HOURS_AGO("$1{0} $2hours ago"),
    TIME_HOUR_AGO("$1One $2hour ago"),
    TIME_MINUTES_AGO("$1{0} $2minutes ago"),
    TIME_MINUTE_AGO("$1One $2minute ago"),
    TIME_SECONDS_AGO("$1{0} $2seconds ago"),
    TIME_JUST_NOW("$1Just now"),

//    Tickets
    // STATUS_ not used directly but using valueOf, DO NOT REMOVE
    TICKET_STATUS_OPEN("$2Open"),
    TICKET_STATUS_HELD("$2Held"),
    TICKET_STATUS_CLAIMED("$2Claimed"),
    TICKET_STATUS_CLOSED("$2Closed"),
    REJECTED_TICKET_SOURCE("Rejected and closed ticket #{0}"),
    REJECTED_TICKET_TARGET("$4Your application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!"),
    TICKET_CLAIM_BUTTON("$1[$2Claim$1]"),
    TICKET_CLAIM_BUTTON_HOVER("$1Click here to claim this ticket."),
    TICKET_UNCLAIM_BUTTON("$1[$2Unclaim$1]"),
    TICKET_UNCLAIM_BUTTON_HOVER("$1Click here to unclaim this ticket."),
    TICKET_REOPEN_BUTTON("$1[$2Reopen$1]"),
    TICKET_REOPEN_BUTTON_HOVER("$1Click here to reopen this ticket."),
    TICKET_COMMENT_BUTTON("$1[$2Comment$1]"),
    TICKET_COMMENT_BUTTON_HOVER("$1Click here to put a comment on this ticket."),
    TICKET_HOLD_BUTTON("$1[$2Hold$1]"),
    TICKET_HOLD_BUTTON_HOVER("$1Click here to put this ticket on hold."),
    TICKET_YES_BUTTON("$1[$2Yes$1]"),
    TICKET_YES_BUTTON_HOVER("$1Click here to confirm the overwrite."),
    TICKET_REJECT_BUTTON("$1[$2Reject$1]"),
    TICKET_REJECT_BUTTON_HOVER("$1Click here to reject and close this ticket."),
    UNKNOWN_ERROR("$1An error occurred. {0}"),
    TICKET_ERROR_INCORRECT_USAGE("$1Incorrect Usage: {0}"),
    TICKET_ERROR_BANNED("$1You are not allowed to open new ticket."),
    TICKET_ERROR_BANNED_ALREADY("$1{0} is already banned from opening tickets."),
    TICKET_ERROR_BAN_USER("$1Cannot ban {0} from opening new ticket."),
    TICKET_ERROR_UNBAN_USER("$1Cannot unban {0} from opening new ticket."),
    TICKET_ERROR_NOT_BANNED("$1{0} is not banned from opening tickets."),
    TICKET_ERROR_PERMISSION("$1You need permission \"{0}\" to do that."),
    TICKET_ERROR_ALREADY_CLOSED("$1Ticket is already closed."),
    TICKET_ERROR_NOT_CLOSED("$1Ticket #{0} is not closed or on hold."),
    TICKET_ERROR_ALREADY_HOLD("$1Ticket is already on hold."),
    TICKET_ERROR_OWNER("$1You are not the owner of that ticket."),
    TICKET_ERROR_CLAIM("$1Ticket #{0} is already claimed by {1}."),
    TICKET_ERROR_UNCLAIM("$1Ticket #{0} is claimed by {1}."),
    TICKET_ERROR_USER_NOT_EXIST("$1The specified user {0} does not exist or contains invalid characters."),
    TICKET_ERROR_SERVER("$1Ticket #{0} was opened on another server."),
    TICKET_TELEPORT("$1Teleported to ticket #{0}."),
    TICKET_ASSIGN("$2{0} has been assigned to ticket #{1}."),
    TICKET_ASSIGN_USER("$2Your ticket #{0} has been assigned to {1}."),
    TICKET_COMMENT_EDIT("$2Ticket #{0} already has a comment attached. Do you wish to overwrite this?"),
    TICKET_COMMENT("$2A comment was added to ticket #{0} by {1}."),
    TICKET_COMMENT_USER("$2Your comment was added to ticket #{0}."),
    TICKET_CLAIM("$2{0} is now handling ticket #{1}."),
    TICKET_CLAIM_USER("$2{0} is now handling your ticket #{1}."),
    TICKET_CLOSE("$2Ticket #{0} was closed by {1}."),
    TICKET_CLOSE_OFFLINE("$2A ticket has been closed while you were offline."),
    TICKET_CLOSE_OFFLINE_MULTI("$2While you were gone, {0} tickets were closed. Use /{1} to see your currently open tickets."),
    TICKET_CLOSE_USER("$2Your Ticket #{0} has been closed by {1}."),
    TICKET_DUPLICATE("$1Your ticket has not been opened because it was detected as a duplicate."),
    TICKET_OPEN("$1A new ticket has been opened by {0}, id assigned #{1}."),
    TICKET_OPEN_USER("$2You opened a ticket, it has been assigned ID #{0}. A staff member should be with you soon."),
    TICKET_TELEPORT_HOVER("Click here to teleport to this tickets location."),
    TICKET_READ_NONE_OPEN("$2There are no open tickets."),
    TICKET_READ_NONE_SELF("$2You have no open tickets."),
    TICKET_READ_NONE_CLOSED("$2There are no closed tickets."),
    TICKET_READ_NONE_HELD("$2There are no tickets currently on hold."),
    TICKET_HOLD("$2Ticket #{0} was put on hold by {1}"),
    TICKET_HOLD_USER("$2Your ticket #{0} was put on hold by {1}"),
    TICKET_UNRESOLVED("$1There are {0} open tickets. Type /{1} to see them."),
    TICKET_UNRESOLVED_HELD("$1There are {0} open tickets and {1} ticket on hold. Type /{2} to see them."),
    TICKET_UNCLAIM("$2{0} is no longer handling ticket #{1}."),
    TICKET_UNCLAIM_USER("$2{0} is no longer handling your ticket #{1}."),
    TICKET_NOT_EXIST("$1Ticket #{0} does not exist."),
    TICKET_NOT_CLAIMED("$1Ticket #{0} is not claimed."),
    TICKET_NOT_OPEN("$1The ticket #{0} is not open."),
    TICKET_REOPEN("$2{0} has reopened ticket #{1}"),
    TICKET_REOPEN_USER("$2{0} has reopened your ticket #{1}"),
    TICKET_TOO_SHORT("$1Your ticket needs to contain at least {0} words."),
    TICKET_TOO_MANY("$1You have too many open tickets, please wait before opening more."),
    TICKET_TOO_FAST("$1You need to wait {0} seconds before attempting to open another ticket."),
    PROMOTE_MEMBER_BUTTON("$3[&bPromote - Member$3]"),
    PROMOTE_BUTTON_HOVER("Promote to {0} and close ticket"),
    PROMOTE_EXPERT_BUTTON("$3[&ePromote - Expert$3]"),
    PROMOTE_MASTER_ARCHITECTURE_BUTTON("$3[$1Promote - MS Arch$3]"),
    PROMOTE_MASTER_NATURE_BUTTON("$3[$1Promote - MS Nature$3]"),
    PROMOTE_MASTER_BOTH_BUTTON("$3[$1Promote - MS Both$3]"),
    TICKET_MORE_INFO("Click here to get more details for ticket #{0}"),
    CLOSED_TICKETS_TITLE("$2Closed Tickets"),
    HELD_TICKETS_TITLE("$2Held Tickets"),
    SELF_TICKETS_TITLE("$2Your Tickets"),
    TICKET_ROW_SINGLE("{5} $2#{0} {1} by {2} $2on {3} $2- $3{4}"),
    TICKET_HELP_TITLE("$2Tickets Help"),
    TICKET_SYNTAX_HINT("$3[] = required  () = optional"),
    NOT_PERMITTED_CMD_USE("$4You are not allowed to use this command"),
    TICKET_OPEN_TITLE("$2{0} Open Tickets"),
    TICKET_CLAIMED_BY("$1Claimed by: $3{0}"),
    TICKET_HANDLED_BY("$1Handled by: $3{0}"),
    TICKET_COMMENT_CONTENT("$1Comment: $3{0}"),
    TICKET_OPENED_BY("$1Opened by: {0} $2| Submission #{1}"),
    TICKET_OPENED_WHEN("$1When: {0}"),
    TICKET_OPENED_SERVER("$1Server: "),
    TICKET_MESSAGE_LONG("$3{0}"),
    TICKET_SINGLE_TITLE("$2Ticket #{0} $1- $2{1}"),
    TICKET_RELOAD_SUCCESS("$1Ticket and Player data reloaded"),
    SUBMISSION_REJECTED("Submission rejected"),
    SUBMISSION_APPROVED("Submission approved"),
    SUBMISSION_ON_HOLD("Submission on hold"),
    SUBMISSION_NEW("New submission"),
    TICKET_DISCORD_SUBMITTED_BY("Submitted by : {0}"),
    TICKET_DISCORD_CLOSED_COMBINED("ID : #{0}\nPlot : {1}\nClosed by : {2}\nComments : {3}\nTime closed : {4}\nTime opened : {5}"),
    TICKET_DISCORD_NEW_COMBINED("ID : #{0}\nPlot : {1}\nTime opened : {2}"),
    TICKET_DISCORD_RESOURCE_REJECTED("https://app.buildersrefuge.com/img/rejected.png"),
    TICKET_DISCORD_RESOURCE_APPROVED("https://app.buildersrefuge.com/img/approved.png"),
    TICKET_DISCORD_RESOURCE_HELD("https://icon-library.net/images/stop-sign-icon-png/stop-sign-icon-png-8.jpg"),
    TICKET_DISCORD_RESOURCE_NEW("https://app.buildersrefuge.com/img/created.png"),
    TICKET_DISCORD_PROMOTED_TO("\nPromoted to : {0}"),
    COMMAND_HELP_COMMENT("$2{0} $3- $1{1}"),
    COMMAND_HELP_COMMENT_ARGS("$2{0} $1{1} $3- $1{2}"),
    TICKET_COMMAND_STAFFLIST("Display a list of online staff members."),
    TICKET_COMMAND_OPEN("Open a ticket."),
    TICKET_COMMAND_CLOSE("Close an open ticket."),
    TICKET_COMMAND_ASSIGN("Assign an open ticket to a specified user."),
    TICKET_COMMAND_HOLD("Put an open ticket on hold."),
    TICKET_COMMAND_CHECK("Display a list of open tickets / Give more detail of a ticketID."),
    TICKET_COMMAND_REOPEN("Reopen a closed ticket."),
    TICKET_COMMAND_TP("Teleport to where a ticket was created."),
    TICKET_COMMAND_CLAIM("Claim an open ticket to let people know you are working on it."),
    TICKET_COMMAND_UNCLAIM("Unclaim a claimed ticket"),
    TICKET_COMMAND_BAN("Ban a player from opening new tickets"),
    TICKET_COMMAND_UNBAN("Unban a player from opening new tickets"),
    TICKET_COMMAND_COMMENT("Put a comment on a ticket"),
    TICKET_COMMAND_RELOAD("Reload ticket and player data."),
    TICKET_ERROR_SUBMIT_OUTSIDE_PLOT("$4You can only open a submission while standing inside your own plot!"),
    TICKET_CLAIMED_PREFIX("$1Claimed - "),
    TICKET_OPEN_PREFIX("$1Open - "),
    TICKET_CLOSED_PREFIX("$1Closed - "),
    TICKET_HELD_PREFIX("$2Held $1- "),
    TICKET_DISCORD_DETAILS_BODY("Player : {0}\nSubmitted : {1}\nLocation : {2}\nPlot : {3}\nSubmission : #{4}\nComments : {5}"),
    TICKET_DISCORD_DETAILS_TITLE("Ticket : #{0}"),
    TICKET_DISCORD_DETAIL_NOT_FOUND(":no_entry: **Unable to find ticket**"),
    TICKET_DISCORD_ROW_BODY("By : {0}\nSubmitted : {1}"),
    TICKET_DISCORD_ROW_TITLE("Open tickets ({0})"),

    //    BrushToolTips
    HOLDING_UNSET_TOOL("$4It seems you are holding a brush tool but it is not set. To suggest it, click this command : $2"),
    BRUSH_DESCRIPTION_DEPTH("Depth"),
    BRUSH_DESCRIPTION_RADIUS("Radius"),
    BRUSH_DESCRIPTION_COMMANDS("Commands"),
    BRUSH_DESCRIPTION_MASK("Mask"),
    BRUSH_DESCRIPTION_SOURCE("Source"),
    BRUSH_DESCRIPTION_POINTS("Points"),
    BRUSH_DESCRIPTION_COMMAND_RADIUS("Radius"),
    BRUSH_DESCRIPTION_SCATTER_RADIUS("Radius"),
    BRUSH_DESCRIPTION_PATTERN("Pattern"),
    BRUSH_DESCRIPTION_COUNT("Count"),
    BRUSH_DESCRIPTION_ROTATION("Rotation"),
    BRUSH_DESCRIPTION_YSCALE("Y-scale"),
    BRUSH_DESCRIPTION_PATTERN_TO("To pattern"),
    BRUSH_DESCRIPTION_COPIES("Copies"),
    BRUSH_DESCRIPTION_LENGTHFACTOR("Length factor"),
    BRUSH_DESCRIPTION_SIZE("Radius"),
    BRUSH_DESCRIPTION_TENSION("Tension"),
    BRUSH_DESCRIPTION_BIAS("Bias"),
    BRUSH_DESCRIPTION_CONTINUITY("Continuity"),
    BRUSH_DESCRIPTION_QUALITY("Quality"),
    BRUSH_DESCRIPTION_ROUNDNESS("Roundness"),
    BRUSH_DESCRIPTION_FREQUENCY("Frequency"),
    BRUSH_DESCRIPTION_AMPLITUDE("Amplitude"),
    BRUSH_DESCRIPTION_SEEDS("Seeds"),
    BRUSH_DESCRIPTION_RECURSION("Recursion"),
    BRUSH_DESCRIPTION_SOLID("Solid"),
    BRUSH_DESCRIPTION_ITERATIONS("Iterations"),
    BRUSH_DESCRIPTION_DISTANCE("Distance"),
    BRUSH_DESCRIPTION_HEIGHT("Height"),
    BRUSH_NAME_COPYPASTE("Copy paste"),
    BRUSH_NAME_COMMAND("Commands"),
    BRUSH_NAME_POPULATE_SCHEMATIC("Populate Schematic"),
    BRUSH_NAME_SCATTER_COMMAND("Commands scatter"),
    BRUSH_NAME_SHATTER("Shatter"),
    BRUSH_NAME_ERODE("Erode"),
    BRUSH_NAME_SPHERE("Sphere"),
    BRUSH_NAME_PULL("Pull"),
    BRUSH_NAME_STENCIL("Stencil"),
    BRUSH_NAME_RECURSIVE("Recursive"),
    BRUSH_NAME_SPLINE("Spline"),
    BRUSH_NAME_SWEEP("Sweep"),
    BRUSH_NAME_CATENARY("Catenary"),
    BRUSH_NAME_LINE("Line"),
    BRUSH_NAME_SURFACE_SPLINE("Surface Spline"),
    BRUSH_NAME_BLENDBALL("Blend Ball"),
    BRUSH_NAME_CIRCLE("Circle"),
    BRUSH_NAME_ROCK("Rock"),
    BRUSH_NAME_HEIGHT("Height"),
    BRUSH_NAME_FLATTEN("Flatten"),
    BRUSH_NAME_LAYER("Layer"),
    BRUSH_NAME_CYLINDER("Cylinder"),
    BRUSH_NAME_SURFACE("Surface"),
    BRUSH_NAME_EXTINGUISHER("Extinguisher"),
    BRUSH_NAME_GRAVITY("Gravity"),
    BRUSH_NAME_CLIPBOARD("Clipboard"),
    BRUSH_NAME_SPLATTER("Splatter"),
    BRUSH_NAME_CLIFF("Cliff"),
    BRUSH_NAME_SMOOTH("Smooth"),
    BRUSH_NAME_SCATTER("Scatter"),
    BRUSH_FLAG_DESCRIPTION_AUTO_VIEW("Auto view"),
    BRUSH_FLAG_DESCRIPTION_RANDOM_ROTATION("Random rotation"),
    BRUSH_FLAG_DESCRIPTION_HOLLOW("Hollow"),
    BRUSH_FLAG_DESCRIPTION_FALLING("Falling"),
    BRUSH_FLAG_DESCRIPTION_FLAT_LINE("Flat line"),
    BRUSH_FLAG_DESCRIPTION_MAX_SATURATION("Maximum saturation"),
    BRUSH_FLAG_DESCRIPTION_DEPTH_FIRST("Depth first"),
    BRUSH_FLAG_DESCRIPTION_SNOW_LAYERS("Snow layers"),
    BRUSH_FLAG_DESCRIPTION_DISABLE_SMOOTHING("Smoothing disabled"),
    BRUSH_FLAG_DESCRIPTION_NO_AIR("No air"),
    BRUSH_FLAG_DESCRIPTION_RELATIVE_LOC("Relative location"),
    BRUSH_FLAG_DESCRIPTION_NATURAL_OCCURRING("Naturally occurring"),
    BRUSH_FLAG_DESCRIPTION_SELECT_AFTER("Select after drawing"),
    BRUSH_FLAG_DESCRIPTION_OVERLAY("Overlay"),
    REPLACER_TOOL_DISPLAY_NAME("$1Replacer $3[$2{0}$3]"),

    //    Friends
    ACCEPTING_TP("$2Accept teleports from friends : $1{0}"),
    FRIEND_TELEPORTED("$2{0} $1teleported to your location"),
    NO_TP_NOT_FRIENDS("$4You are not friends with that user so you cannot teleport to them"),
    ALREADY_FRIENDS("You are already friends with $2{0}"),
    FRIEND_ADDED("You are now freinds with $2{0}"),
    REQUEST_SENT("$1A friend request was sent to $2{0}"),
    REQUEST_RECEIVED("$2{0} $1has requested to befriend you"),
    FRIEND_REMOVED("$3{0} $1was removed as friend"),
    FRIEND_ACCEPT_BUTTON("&f&mAccept"),
    FRIEND_ACCEPT_BUTTON_HOVER("$1Click to accept request from {0}"),
    FRIEND_DENY_BUTTON("&f&mDeny"),
    FRIEND_DENY_BUTTON_HOVER("$1Click to deny request from {0}"),
    FRIEND_ROW_REQUEST("$1{0} $2- Request"),
    FRIEND_LIST_TITLE("$1Friends"),

    //    WeatherPlugin
    UNKNOWN_WEATHER_TYPE("$4That weather type is unknown"),
    PLOT_WEATHER_SET("$2Plot weather set to: $1{0}"),
    WEATHER_ERROR_NO_OWNER("You must be the owner of the plot to execute this command"),
    WEATHER_ERROR_NO_WEATHER_TYPE("You must enter a weather type"),

    //    HeadsEvolved
    OPEN_GUI_ERROR("$4Failed to open Head Database GUI"),

    //    Layerheight
    HEIGHT_TOO_HIGH("$4Height cannot be above 8"),
    HEIGHT_TOO_LOW("$4Height cannot be below 1"),
    HEIGHTTOOL_NAME("$1Layer Height Tool: $2{0}"),
    HEIGHTTOOL_SET("Successfully set the layer height to $2{0}"),
    HEIGHTTOOL_FAILED_BIND("$4Tool cannot be bound to a block"),
    OUTSIDE_PLOT("$4You are not standing inside a plot"),

    //    HotbarShare
    SHARED_HOTBAR_WITH("You shared your hotbar with {0}"),
    PLAYER_SHARED_HOTBAR("$2{0} $1shared their hotbar"),
    FULL_HOTBAR("$4Your hotbar is full, please clear one or more slots"),
    HOTBAR_SHARE_HEADER("$1§m------- §r$2{0}'s Hotbar $1§m-------"),
    HOTBAR_SHARE_INDEX("$2#{0} : $1{1}"),
    HOTBAR_SHARE_ENCHANTED("$2  Enchanted : "),
    HOTBAR_SHARE_LORE("$2  Lore : "),

    //    Paintings
    PNG_URL_REQUIRED("$4URLs have to end with .png, please make sure to upload an image rather than a webpage including it"),
    PAINTING_TOO_BIG("$4Paintings can have a maximum size of 3x3"),
    PAINTING_EXEMPT("You are exempt from needing permission, check your list of paintings"),
    PAINTING_SUBMITTED("Submitted a new request, once accepted the painting will be automatically uploaded"),
    PAINTING_STATUS_SUBMITTED("Submitted"),
    PAINTING_STATUS_REJECTED("Rejected"),
    PAINTING_STATUS_APPROVED("Approved"),
    PAINTING_DISCORD_TITLE("Submissions"),
    PAINTING_DISCORD_FIELD_TITLE("Submission ID : #{0}"),
    PAINTING_DISCORD_FIELD_VALUE("Submitted by : {0}\nStatus : {1}"),
    PAINTING_SUBMISSION_NOT_FOUND("Cannot find submission : {0}"),
    PAINTING_SUBMISSION_LIST("Submission IDs : {0}"),
    PAINTING_APPROVING("Approving Submission : {0}"),
    PAINTING_REJECTING("Rejecting Submission : {0}"),
    PAINTING_CANNOT_UPDATE_STATUS("Cannot {0} this submission (#{1}). Are you sure it exists?"),

    //    UserData
    USER_DATA_HEADER("$1§m------- §r$2{0} $1§m-------"),
    USER_DATA_FAILED_COLLECT("$4Could not collect data for {0}"),

    //    MultiCommand
    MULTI_CMD_PERFORMING("Performing command : $2{0}"),

    //    Dave
    DAVE_LINK_SUGGESTION("Here's a useful link, $1{0}"),
    DAVE_LINK_SUGGESTION_HOVER("$2Click to open $1{0}"),
    DAVE_DISCORD_FORMAT("**Dave** ≫ {0}"),

    //    Plot ID Bar
    PID_USERS_TRUSTED_MORE("{0}, {1} and {2} others"),
    PID_USERS_TRUSTED("{0}, {1}"),
    PID_WORLD_FORMAT("$2World ID : $1{0}"),
    PID_PLOT_FORMAT("$2Plot ID : $1{0}, {1}"),
    PID_OWNER_FORMAT("$2Owner : $1{0}"),
    PID_BAR_SEPARATOR(" &f|-=-| "),
    PID_BAR_MEMBERS("$2Members : $1{0}"),

//    Trust Limiter
    TRUST_LIMIT_AUTO_CLEANED("$1Automatically removed $2{0} $1from this plot because their rank is too low.")
    ;

    private String s;

    Translations(String s) {
        this.s = s;
    }

    public String s() {
        return parseColors(this.s);
    }

    public String f(final Object... args) {
        return format(this.s, args);
    }

    public static String shorten(String m) {
        return shorten(m, 20);
    }

    public String sh() {
        return shorten(s());
    }

    public String sh(int maxChars) {
        return shorten(s(), maxChars);
    }

    public Text ft(final Object... args) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(f(args));
    }

    public Text t() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(s());
    }

    // Format value placeholders and colors
    public static String format(String m, Object... args) {
        if (args.length == 0) return m;
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "");
                else map.put("%s" + i, arg);
                if (i == 0) map.put("%s", arg);
            }
        }
        m = StringMan.replaceFromMap(m, map);
        return parseColors(m);
    }

    // Format only integrated colors (Text serializing is done in their respective methods only)
    private static String parseColors(String m) {
        return m
                .replaceAll("\\$1", String.format("§%s", COLOR_PRIMARY.s))
                .replaceAll("\\$2", String.format("§%s", COLOR_SECONDARY.s))
                .replaceAll("\\$3", String.format("§%s", COLOR_MINOR.s))
                .replaceAll("\\$4", String.format("§%s", COLOR_ERROR.s));
    }

    // Shorten the value
    public static String shorten(String m, int maxChars) {
        return (m.length() > maxChars) ? String.format("%s...", m.substring(0, maxChars)) : m;
    }

    // Plain-ify the value, removing integrated, native, and legacy color codes. Keeps value placeholders
    public String p() {
        String copy = s();
        copy = copy.replaceAll("§", "&");
        for (String regex : new String[]{"(&)([a-f])+", "(&)([0-9])+", "&l", "&n", "&o", "&k", "&m", "&r"})
            copy = copy.replaceAll(regex, "");
        return copy;
    }
}
