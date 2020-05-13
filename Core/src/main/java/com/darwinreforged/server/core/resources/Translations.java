package com.darwinreforged.server.core.resources;

import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.core.util.StringUtils;
import com.darwinreforged.server.modules.internal.darwin.DarwinServerModule;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 The enum Translations.
 */
public enum Translations {

    /**
     The Prefix.
     */
//    Default formats
    PREFIX("$3[] $1"),
    /**
     Color primary translations.
     */
    COLOR_PRIMARY("b"),
    /**
     Color secondary translations.
     */
    COLOR_SECONDARY("3"),
    /**
     Color minor translations.
     */
    COLOR_MINOR("7"),
    /**
     Color error translations.
     */
    COLOR_ERROR("c"),
    /**
     The Player only command.
     */
    PLAYER_ONLY_COMMAND("$4This command can only be executed by players"),
    /**
     The Command no permission.
     */
    COMMAND_NO_PERMISSION("$4You do not have permission to use this command $3({0})"),
    /**
     Plots 1 name translations.
     */
    PLOTS1_NAME("Plots1"),
    /**
     Plots 2 name translations.
     */
    PLOTS2_NAME("Plots2"),
    /**
     Plots 500 name translations.
     */
    PLOTS500_NAME("Plots500"),
    /**
     Masterplots name translations.
     */
    MASTERPLOTS_NAME("MasterPlots"),
    /**
     Member rank display translations.
     */
    MEMBER_RANK_DISPLAY("Member"),
    /**
     Expert rank display translations.
     */
    EXPERT_RANK_DISPLAY("Expert"),
    /**
     The Master architecture display.
     */
    MASTER_ARCHITECTURE_DISPLAY("Mastered Skill Architecture"),
    /**
     The Master nature display.
     */
    MASTER_NATURE_DISPLAY("Mastered Skill Nature"),
    /**
     The Master both display.
     */
    MASTER_BOTH_DISPLAY("both Mastered Skills"),
    /**
     Master rank display translations.
     */
    MASTER_RANK_DISPLAY("Master"),
    /**
     Default separator translations.
     */
    DEFAULT_SEPARATOR(" - "),
    /**
     Default padding translations.
     */
    DEFAULT_PADDING(" $1- "),
    /**
     Default on translations.
     */
    DEFAULT_ON("On"),
    /**
     Default off translations.
     */
    DEFAULT_OFF("Off"),
    /**
     Unknown translations.
     */
    UNKNOWN("Unknown"),
    /**
     None translations.
     */
    NONE("None"),
    /**
     Console translations.
     */
    CONSOLE("Console"),
    /**
     Online player translations.
     */
    ONLINE_PLAYER("$1{0}"),
    /**
     Offline player translations.
     */
    OFFLINE_PLAYER("$2{0}"),
    /**
     Unowned translations.
     */
    UNOWNED("Unowned"),
    /**
     Everyone translations.
     */
    EVERYONE("Everyone"),

    /**
     The Time days ago.
     */
//    Time differences
    TIME_DAYS_AGO("$1{0} $2days ago"),
    /**
     Time yesterday translations.
     */
    TIME_YESTERDAY("$1Yesterday"),
    /**
     The Time hours ago.
     */
    TIME_HOURS_AGO("$1{0} $2hours ago"),
    /**
     The Time hour ago.
     */
    TIME_HOUR_AGO("$1One $2hour ago"),
    /**
     The Time minutes ago.
     */
    TIME_MINUTES_AGO("$1{0} $2minutes ago"),
    /**
     The Time minute ago.
     */
    TIME_MINUTE_AGO("$1One $2minute ago"),
    /**
     The Time seconds ago.
     */
    TIME_SECONDS_AGO("$1{0} $2seconds ago"),
    /**
     The Time just now.
     */
    TIME_JUST_NOW("$1Just now"),

    /**
     The Ticket status open.
     */
//    Tickets
    // STATUS_ not used directly but using valueOf, DO NOT REMOVE
    TICKET_STATUS_OPEN("$2Open"),
    /**
     Ticket status held translations.
     */
    TICKET_STATUS_HELD("$2Held"),
    /**
     Ticket status claimed translations.
     */
    TICKET_STATUS_CLAIMED("$2Claimed"),
    /**
     Ticket status closed translations.
     */
    TICKET_STATUS_CLOSED("$2Closed"),
    /**
     The Rejected ticket source.
     */
    REJECTED_TICKET_SOURCE("Rejected and closed ticket #{0}"),
    /**
     The Rejected ticket target.
     */
    REJECTED_TICKET_TARGET("$4Your application was reviewed but was not yet approved, make sure to read the given feedback on your plot, and apply again once ready!"),
    /**
     Ticket claim button translations.
     */
    TICKET_CLAIM_BUTTON("$1[$2Claim$1]"),
    /**
     The Ticket claim button hover.
     */
    TICKET_CLAIM_BUTTON_HOVER("$1Click here to claim this ticket."),
    /**
     Ticket unclaim button translations.
     */
    TICKET_UNCLAIM_BUTTON("$1[$2Unclaim$1]"),
    /**
     The Ticket unclaim button hover.
     */
    TICKET_UNCLAIM_BUTTON_HOVER("$1Click here to unclaim this ticket."),
    /**
     Ticket reopen button translations.
     */
    TICKET_REOPEN_BUTTON("$1[$2Reopen$1]"),
    /**
     The Ticket reopen button hover.
     */
    TICKET_REOPEN_BUTTON_HOVER("$1Click here to reopen this ticket."),
    /**
     Ticket comment button translations.
     */
    TICKET_COMMENT_BUTTON("$1[$2Comment$1]"),
    /**
     The Ticket comment button hover.
     */
    TICKET_COMMENT_BUTTON_HOVER("$1Click here to put a comment on this ticket."),
    /**
     Ticket hold button translations.
     */
    TICKET_HOLD_BUTTON("$1[$2Hold$1]"),
    /**
     The Ticket hold button hover.
     */
    TICKET_HOLD_BUTTON_HOVER("$1Click here to put this ticket on hold."),
    /**
     Ticket yes button translations.
     */
    TICKET_YES_BUTTON("$1[$2Yes$1]"),
    /**
     The Ticket yes button hover.
     */
    TICKET_YES_BUTTON_HOVER("$1Click here to confirm the overwrite."),
    /**
     Ticket reject button translations.
     */
    TICKET_REJECT_BUTTON("$1[$2Reject$1]"),
    /**
     The Ticket reject button hover.
     */
    TICKET_REJECT_BUTTON_HOVER("$1Click here to reject and close this ticket."),
    /**
     The Unknown error.
     */
    UNKNOWN_ERROR("$1An error occurred. {0}"),
    /**
     The Ticket error incorrect usage.
     */
    TICKET_ERROR_INCORRECT_USAGE("$1Incorrect Usage: {0}"),
    /**
     The Ticket error banned.
     */
    TICKET_ERROR_BANNED("$1You are not allowed to open new ticket."),
    /**
     The Ticket error banned already.
     */
    TICKET_ERROR_BANNED_ALREADY("$1{0} is already banned from opening tickets."),
    /**
     The Ticket error ban user.
     */
    TICKET_ERROR_BAN_USER("$1Cannot ban {0} from opening new ticket."),
    /**
     The Ticket error unban user.
     */
    TICKET_ERROR_UNBAN_USER("$1Cannot unban {0} from opening new ticket."),
    /**
     The Ticket error not banned.
     */
    TICKET_ERROR_NOT_BANNED("$1{0} is not banned from opening tickets."),
    /**
     The Ticket error permission.
     */
    TICKET_ERROR_PERMISSION("$1You need permission \"{0}\" to do that."),
    /**
     The Ticket error already closed.
     */
    TICKET_ERROR_ALREADY_CLOSED("$1Ticket is already closed."),
    /**
     The Ticket error not closed.
     */
    TICKET_ERROR_NOT_CLOSED("$1Ticket #{0} is not closed or on hold."),
    /**
     The Ticket error already hold.
     */
    TICKET_ERROR_ALREADY_HOLD("$1Ticket is already on hold."),
    /**
     The Ticket error owner.
     */
    TICKET_ERROR_OWNER("$1You are not the owner of that ticket."),
    /**
     The Ticket error claim.
     */
    TICKET_ERROR_CLAIM("$1Ticket #{0} is already claimed by {1}."),
    /**
     The Ticket error unclaim.
     */
    TICKET_ERROR_UNCLAIM("$1Ticket #{0} is claimed by {1}."),
    /**
     The Ticket error user not exist.
     */
    TICKET_ERROR_USER_NOT_EXIST("$1The specified user {0} does not exist or contains invalid characters."),
    /**
     The Ticket error server.
     */
    TICKET_ERROR_SERVER("$1Ticket #{0} was opened on another server."),
    /**
     The Ticket teleport.
     */
    TICKET_TELEPORT("$1Teleported to ticket #{0}."),
    /**
     The Ticket assign.
     */
    TICKET_ASSIGN("$2{0} has been assigned to ticket #{1}."),
    /**
     The Ticket assign user.
     */
    TICKET_ASSIGN_USER("$2Your ticket #{0} has been assigned to {1}."),
    /**
     The Ticket comment edit.
     */
    TICKET_COMMENT_EDIT("$2Ticket #{0} already has a comment attached. Do you wish to overwrite this?"),
    /**
     The Ticket comment.
     */
    TICKET_COMMENT("$2A comment was added to ticket #{0} by {1}."),
    /**
     The Ticket comment user.
     */
    TICKET_COMMENT_USER("$2Your comment was added to ticket #{0}."),
    /**
     The Ticket claim.
     */
    TICKET_CLAIM("$2{0} is now handling ticket #{1}."),
    /**
     The Ticket claim user.
     */
    TICKET_CLAIM_USER("$2{0} is now handling your ticket #{1}."),
    /**
     The Ticket close.
     */
    TICKET_CLOSE("$2Ticket #{0} was closed by {1}."),
    /**
     The Ticket close offline.
     */
    TICKET_CLOSE_OFFLINE("$2A ticket has been closed while you were offline."),
    /**
     The Ticket close offline multi.
     */
    TICKET_CLOSE_OFFLINE_MULTI("$2While you were gone, {0} tickets were closed. Use /{1} to see your currently open tickets."),
    /**
     The Ticket close user.
     */
    TICKET_CLOSE_USER("$2Your Ticket #{0} has been closed by {1}."),
    /**
     The Ticket duplicate.
     */
    TICKET_DUPLICATE("$1Your ticket has not been opened because it was detected as a duplicate."),
    /**
     The Ticket open.
     */
    TICKET_OPEN("$1A new ticket has been opened by {0}, id assigned #{1}."),
    /**
     The Ticket open user.
     */
    TICKET_OPEN_USER("$2You opened a ticket, it has been assigned ID #{0}. A staff member should be with you soon."),
    /**
     The Ticket teleport hover.
     */
    TICKET_TELEPORT_HOVER("Click here to teleport to this tickets location."),
    /**
     The Ticket read none open.
     */
    TICKET_READ_NONE_OPEN("$2There are no open tickets."),
    /**
     The Ticket read none self.
     */
    TICKET_READ_NONE_SELF("$2You have no open tickets."),
    /**
     The Ticket read none closed.
     */
    TICKET_READ_NONE_CLOSED("$2There are no closed tickets."),
    /**
     The Ticket read none held.
     */
    TICKET_READ_NONE_HELD("$2There are no tickets currently on hold."),
    /**
     The Ticket hold.
     */
    TICKET_HOLD("$2Ticket #{0} was put on hold by {1}"),
    /**
     The Ticket hold user.
     */
    TICKET_HOLD_USER("$2Your ticket #{0} was put on hold by {1}"),
    /**
     The Ticket unresolved.
     */
    TICKET_UNRESOLVED("$1There are {0} open tickets. Type /{1} to see them."),
    /**
     The Ticket unresolved held.
     */
    TICKET_UNRESOLVED_HELD("$1There are {0} open tickets and {1} ticket on hold. Type /{2} to see them."),
    /**
     The Ticket unclaim.
     */
    TICKET_UNCLAIM("$2{0} is no longer handling ticket #{1}."),
    /**
     The Ticket unclaim user.
     */
    TICKET_UNCLAIM_USER("$2{0} is no longer handling your ticket #{1}."),
    /**
     The Ticket not exist.
     */
    TICKET_NOT_EXIST("$1Ticket #{0} does not exist."),
    /**
     The Ticket not claimed.
     */
    TICKET_NOT_CLAIMED("$1Ticket #{0} is not claimed."),
    /**
     The Ticket not open.
     */
    TICKET_NOT_OPEN("$1The ticket #{0} is not open."),
    /**
     The Ticket reopen.
     */
    TICKET_REOPEN("$2{0} has reopened ticket #{1}"),
    /**
     The Ticket reopen user.
     */
    TICKET_REOPEN_USER("$2{0} has reopened your ticket #{1}"),
    /**
     The Ticket too short.
     */
    TICKET_TOO_SHORT("$1Your ticket needs to contain at least {0} words."),
    /**
     The Ticket too many.
     */
    TICKET_TOO_MANY("$1You have too many open tickets, please wait before opening more."),
    /**
     The Ticket too fast.
     */
    TICKET_TOO_FAST("$1You need to wait {0} seconds before attempting to open another ticket."),
    /**
     Promote member button translations.
     */
    PROMOTE_MEMBER_BUTTON("$3[&bPromote - Member$3]"),
    /**
     The Promote button hover.
     */
    PROMOTE_BUTTON_HOVER("Promote to {0} and close ticket"),
    /**
     Promote expert button translations.
     */
    PROMOTE_EXPERT_BUTTON("$3[&ePromote - Expert$3]"),
    /**
     The Promote master architecture button.
     */
    PROMOTE_MASTER_ARCHITECTURE_BUTTON("$3[$1Promote - MS Arch$3]"),
    /**
     The Promote master nature button.
     */
    PROMOTE_MASTER_NATURE_BUTTON("$3[$1Promote - MS Nature$3]"),
    /**
     The Promote master both button.
     */
    PROMOTE_MASTER_BOTH_BUTTON("$3[$1Promote - MS Both$3]"),
    /**
     The Ticket more info.
     */
    TICKET_MORE_INFO("Click here to get more details for ticket #{0}"),
    /**
     The Closed tickets title.
     */
    CLOSED_TICKETS_TITLE("$2Closed Tickets"),
    /**
     The Held tickets title.
     */
    HELD_TICKETS_TITLE("$2Held Tickets"),
    /**
     The Self tickets title.
     */
    SELF_TICKETS_TITLE("$2Your Tickets"),
    /**
     Ticket row single translations.
     */
    TICKET_ROW_SINGLE("{5} $2#{0} {1} by {2} $2on {3} $2- $3{4}"),
    /**
     The Ticket help title.
     */
    TICKET_HELP_TITLE("$2Tickets Help"),
    /**
     Ticket syntax hint translations.
     */
    TICKET_SYNTAX_HINT("$3[] = required  () = optional"),
    /**
     The Not permitted cmd use.
     */
    NOT_PERMITTED_CMD_USE("$4You are not allowed to use this command $3({0})"),
    /**
     The Ticket open title.
     */
    TICKET_OPEN_TITLE("$2{0} Open Tickets"),
    /**
     The Ticket claimed by.
     */
    TICKET_CLAIMED_BY("$1Claimed by: $3{0}"),
    /**
     The Ticket handled by.
     */
    TICKET_HANDLED_BY("$1Handled by: $3{0}"),
    /**
     Ticket comment content translations.
     */
    TICKET_COMMENT_CONTENT("$1Comment: $3{0}"),
    /**
     The Ticket opened by.
     */
    TICKET_OPENED_BY("$1Opened by: {0} $2| Submission #{1}"),
    /**
     Ticket opened when translations.
     */
    TICKET_OPENED_WHEN("$1When: {0}"),
    /**
     Ticket opened server translations.
     */
    TICKET_OPENED_SERVER("$1Server: "),
    /**
     Ticket message long translations.
     */
    TICKET_MESSAGE_LONG("$3{0}"),
    /**
     Ticket single title translations.
     */
    TICKET_SINGLE_TITLE("$2Ticket #{0} $1- $2{1}"),
    /**
     The Ticket reload success.
     */
    TICKET_RELOAD_SUCCESS("$1Ticket and Player data reloaded"),
    /**
     The Submission rejected.
     */
    SUBMISSION_REJECTED("Submission rejected"),
    /**
     The Submission approved.
     */
    SUBMISSION_APPROVED("Submission approved"),
    /**
     The Submission on hold.
     */
    SUBMISSION_ON_HOLD("Submission on hold"),
    /**
     The Submission new.
     */
    SUBMISSION_NEW("New submission"),
    /**
     The Ticket discord submitted by.
     */
    TICKET_DISCORD_SUBMITTED_BY("Submitted by : {0}"),
    /**
     The Ticket discord closed combined.
     */
    TICKET_DISCORD_CLOSED_COMBINED("ID : #{0}\nPlot : {1}\nClosed by : {2}\nComments : {3}\nTime closed : {4}\nTime opened : {5}"),
    /**
     The Ticket discord new combined.
     */
    TICKET_DISCORD_NEW_COMBINED("ID : #{0}\nPlot : {1}\nTime opened : {2}"),
    /**
     Ticket discord resource rejected translations.
     */
    TICKET_DISCORD_RESOURCE_REJECTED("https://app.buildersrefuge.com/img/rejected.png"),
    /**
     Ticket discord resource approved translations.
     */
    TICKET_DISCORD_RESOURCE_APPROVED("https://app.buildersrefuge.com/img/approved.png"),
    /**
     Ticket discord resource held translations.
     */
    TICKET_DISCORD_RESOURCE_HELD("https://icon-library.net/images/stop-sign-icon-png/stop-sign-icon-png-8.jpg"),
    /**
     Ticket discord resource new translations.
     */
    TICKET_DISCORD_RESOURCE_NEW("https://app.buildersrefuge.com/img/created.png"),
    /**
     The Ticket discord promoted to.
     */
    TICKET_DISCORD_PROMOTED_TO("\nPromoted to : {0}"),
    /**
     Command help comment translations.
     */
    COMMAND_HELP_COMMENT("$2{0} $3- $1{1}"),
    /**
     Command help comment args translations.
     */
    COMMAND_HELP_COMMENT_ARGS("$2{0} $1{1} $3- $1{2}"),
    /**
     The Ticket command stafflist.
     */
    TICKET_COMMAND_STAFFLIST("Display a list of online staff members."),
    /**
     The Ticket command open.
     */
    TICKET_COMMAND_OPEN("Open a ticket."),
    /**
     The Ticket command close.
     */
    TICKET_COMMAND_CLOSE("Close an open ticket."),
    /**
     The Ticket command assign.
     */
    TICKET_COMMAND_ASSIGN("Assign an open ticket to a specified user."),
    /**
     The Ticket command hold.
     */
    TICKET_COMMAND_HOLD("Put an open ticket on hold."),
    /**
     The Ticket command check.
     */
    TICKET_COMMAND_CHECK("Display a list of open tickets / Give more detail of a ticketID."),
    /**
     The Ticket command reopen.
     */
    TICKET_COMMAND_REOPEN("Reopen a closed ticket."),
    /**
     The Ticket command tp.
     */
    TICKET_COMMAND_TP("Teleport to where a ticket was created."),
    /**
     The Ticket command claim.
     */
    TICKET_COMMAND_CLAIM("Claim an open ticket to let people know you are working on it."),
    /**
     The Ticket command unclaim.
     */
    TICKET_COMMAND_UNCLAIM("Unclaim a claimed ticket"),
    /**
     The Ticket command ban.
     */
    TICKET_COMMAND_BAN("Ban a player from opening new tickets"),
    /**
     The Ticket command unban.
     */
    TICKET_COMMAND_UNBAN("Unban a player from opening new tickets"),
    /**
     The Ticket command comment.
     */
    TICKET_COMMAND_COMMENT("Put a comment on a ticket"),
    /**
     The Ticket command reload.
     */
    TICKET_COMMAND_RELOAD("Reload ticket and player data."),
    /**
     The Ticket error submit outside plot.
     */
    TICKET_ERROR_SUBMIT_OUTSIDE_PLOT("$4You can only open a submission while standing inside your own plot!"),
    /**
     Ticket claimed prefix translations.
     */
    TICKET_CLAIMED_PREFIX("$1Claimed - "),
    /**
     Ticket open prefix translations.
     */
    TICKET_OPEN_PREFIX("$1Open - "),
    /**
     Ticket closed prefix translations.
     */
    TICKET_CLOSED_PREFIX("$1Closed - "),
    /**
     Ticket held prefix translations.
     */
    TICKET_HELD_PREFIX("$2Held $1- "),
    /**
     Ticket discord details body translations.
     */
    TICKET_DISCORD_DETAILS_BODY("Player : {0}\nSubmitted : {1}\nLocation : {2}\nPlot : {3}\nSubmission : #{4}\nComments : {5}"),
    /**
     Ticket discord details title translations.
     */
    TICKET_DISCORD_DETAILS_TITLE("Ticket : #{0}"),
    /**
     The Ticket discord detail not found.
     */
    TICKET_DISCORD_DETAIL_NOT_FOUND(":no_entry: **Unable to find ticket**"),
    /**
     Ticket discord row body translations.
     */
    TICKET_DISCORD_ROW_BODY("By : {0}\nSubmitted : {1}"),
    /**
     The Ticket discord row title.
     */
    TICKET_DISCORD_ROW_TITLE("Open tickets ({0})"),

    /**
     The Holding unset tool.
     */
//    BrushToolTips
    HOLDING_UNSET_TOOL("$4It seems you are holding a brush tool but it is not set. To suggest it, click this command : $2"),
    /**
     Brush description depth translations.
     */
    BRUSH_DESCRIPTION_DEPTH("Depth"),
    /**
     Brush description radius translations.
     */
    BRUSH_DESCRIPTION_RADIUS("Radius"),
    /**
     Brush description commands translations.
     */
    BRUSH_DESCRIPTION_COMMANDS("Commands"),
    /**
     Brush description mask translations.
     */
    BRUSH_DESCRIPTION_MASK("Mask"),
    /**
     Brush description source translations.
     */
    BRUSH_DESCRIPTION_SOURCE("Source"),
    /**
     Brush description points translations.
     */
    BRUSH_DESCRIPTION_POINTS("Points"),
    /**
     Brush description command radius translations.
     */
    BRUSH_DESCRIPTION_COMMAND_RADIUS("Radius"),
    /**
     Brush description scatter radius translations.
     */
    BRUSH_DESCRIPTION_SCATTER_RADIUS("Radius"),
    /**
     Brush description pattern translations.
     */
    BRUSH_DESCRIPTION_PATTERN("Pattern"),
    /**
     Brush description count translations.
     */
    BRUSH_DESCRIPTION_COUNT("Count"),
    /**
     Brush description rotation translations.
     */
    BRUSH_DESCRIPTION_ROTATION("Rotation"),
    /**
     Brush description yscale translations.
     */
    BRUSH_DESCRIPTION_YSCALE("Y-scale"),
    /**
     The Brush description pattern to.
     */
    BRUSH_DESCRIPTION_PATTERN_TO("To pattern"),
    /**
     Brush description copies translations.
     */
    BRUSH_DESCRIPTION_COPIES("Copies"),
    /**
     The Brush description lengthfactor.
     */
    BRUSH_DESCRIPTION_LENGTHFACTOR("Length factor"),
    /**
     Brush description size translations.
     */
    BRUSH_DESCRIPTION_SIZE("Radius"),
    /**
     Brush description tension translations.
     */
    BRUSH_DESCRIPTION_TENSION("Tension"),
    /**
     Brush description bias translations.
     */
    BRUSH_DESCRIPTION_BIAS("Bias"),
    /**
     Brush description continuity translations.
     */
    BRUSH_DESCRIPTION_CONTINUITY("Continuity"),
    /**
     Brush description quality translations.
     */
    BRUSH_DESCRIPTION_QUALITY("Quality"),
    /**
     Brush description roundness translations.
     */
    BRUSH_DESCRIPTION_ROUNDNESS("Roundness"),
    /**
     Brush description frequency translations.
     */
    BRUSH_DESCRIPTION_FREQUENCY("Frequency"),
    /**
     Brush description amplitude translations.
     */
    BRUSH_DESCRIPTION_AMPLITUDE("Amplitude"),
    /**
     Brush description seeds translations.
     */
    BRUSH_DESCRIPTION_SEEDS("Seeds"),
    /**
     Brush description recursion translations.
     */
    BRUSH_DESCRIPTION_RECURSION("Recursion"),
    /**
     Brush description solid translations.
     */
    BRUSH_DESCRIPTION_SOLID("Solid"),
    /**
     Brush description iterations translations.
     */
    BRUSH_DESCRIPTION_ITERATIONS("Iterations"),
    /**
     Brush description distance translations.
     */
    BRUSH_DESCRIPTION_DISTANCE("Distance"),
    /**
     Brush description height translations.
     */
    BRUSH_DESCRIPTION_HEIGHT("Height"),
    /**
     The Brush name copypaste.
     */
    BRUSH_NAME_COPYPASTE("Copy paste"),
    /**
     Brush name command translations.
     */
    BRUSH_NAME_COMMAND("Commands"),
    /**
     The Brush name populate schematic.
     */
    BRUSH_NAME_POPULATE_SCHEMATIC("Populate Schematic"),
    /**
     The Brush name scatter command.
     */
    BRUSH_NAME_SCATTER_COMMAND("Commands scatter"),
    /**
     Brush name shatter translations.
     */
    BRUSH_NAME_SHATTER("Shatter"),
    /**
     Brush name erode translations.
     */
    BRUSH_NAME_ERODE("Erode"),
    /**
     Brush name sphere translations.
     */
    BRUSH_NAME_SPHERE("Sphere"),
    /**
     Brush name pull translations.
     */
    BRUSH_NAME_PULL("Pull"),
    /**
     Brush name stencil translations.
     */
    BRUSH_NAME_STENCIL("Stencil"),
    /**
     Brush name recursive translations.
     */
    BRUSH_NAME_RECURSIVE("Recursive"),
    /**
     Brush name spline translations.
     */
    BRUSH_NAME_SPLINE("Spline"),
    /**
     Brush name sweep translations.
     */
    BRUSH_NAME_SWEEP("Sweep"),
    /**
     Brush name catenary translations.
     */
    BRUSH_NAME_CATENARY("Catenary"),
    /**
     Brush name line translations.
     */
    BRUSH_NAME_LINE("Line"),
    /**
     The Brush name surface spline.
     */
    BRUSH_NAME_SURFACE_SPLINE("Surface Spline"),
    /**
     The Brush name blendball.
     */
    BRUSH_NAME_BLENDBALL("Blend Ball"),
    /**
     Brush name circle translations.
     */
    BRUSH_NAME_CIRCLE("Circle"),
    /**
     Brush name rock translations.
     */
    BRUSH_NAME_ROCK("Rock"),
    /**
     Brush name height translations.
     */
    BRUSH_NAME_HEIGHT("Height"),
    /**
     Brush name flatten translations.
     */
    BRUSH_NAME_FLATTEN("Flatten"),
    /**
     Brush name layer translations.
     */
    BRUSH_NAME_LAYER("Layer"),
    /**
     Brush name cylinder translations.
     */
    BRUSH_NAME_CYLINDER("Cylinder"),
    /**
     Brush name surface translations.
     */
    BRUSH_NAME_SURFACE("Surface"),
    /**
     Brush name extinguisher translations.
     */
    BRUSH_NAME_EXTINGUISHER("Extinguisher"),
    /**
     Brush name gravity translations.
     */
    BRUSH_NAME_GRAVITY("Gravity"),
    /**
     Brush name clipboard translations.
     */
    BRUSH_NAME_CLIPBOARD("Clipboard"),
    /**
     Brush name splatter translations.
     */
    BRUSH_NAME_SPLATTER("Splatter"),
    /**
     Brush name cliff translations.
     */
    BRUSH_NAME_CLIFF("Cliff"),
    /**
     Brush name smooth translations.
     */
    BRUSH_NAME_SMOOTH("Smooth"),
    /**
     Brush name scatter translations.
     */
    BRUSH_NAME_SCATTER("Scatter"),
    /**
     The Brush flag description auto view.
     */
    BRUSH_FLAG_DESCRIPTION_AUTO_VIEW("Auto view"),
    /**
     The Brush flag description random rotation.
     */
    BRUSH_FLAG_DESCRIPTION_RANDOM_ROTATION("Random rotation"),
    /**
     Brush flag description hollow translations.
     */
    BRUSH_FLAG_DESCRIPTION_HOLLOW("Hollow"),
    /**
     Brush flag description falling translations.
     */
    BRUSH_FLAG_DESCRIPTION_FALLING("Falling"),
    /**
     The Brush flag description flat line.
     */
    BRUSH_FLAG_DESCRIPTION_FLAT_LINE("Flat line"),
    /**
     The Brush flag description max saturation.
     */
    BRUSH_FLAG_DESCRIPTION_MAX_SATURATION("Maximum saturation"),
    /**
     The Brush flag description depth first.
     */
    BRUSH_FLAG_DESCRIPTION_DEPTH_FIRST("Depth first"),
    /**
     The Brush flag description snow layers.
     */
    BRUSH_FLAG_DESCRIPTION_SNOW_LAYERS("Snow layers"),
    /**
     The Brush flag description disable smoothing.
     */
    BRUSH_FLAG_DESCRIPTION_DISABLE_SMOOTHING("Smoothing disabled"),
    /**
     The Brush flag description no air.
     */
    BRUSH_FLAG_DESCRIPTION_NO_AIR("No air"),
    /**
     The Brush flag description relative loc.
     */
    BRUSH_FLAG_DESCRIPTION_RELATIVE_LOC("Relative location"),
    /**
     The Brush flag description natural occurring.
     */
    BRUSH_FLAG_DESCRIPTION_NATURAL_OCCURRING("Naturally occurring"),
    /**
     The Brush flag description select after.
     */
    BRUSH_FLAG_DESCRIPTION_SELECT_AFTER("Select after drawing"),
    /**
     Brush flag description overlay translations.
     */
    BRUSH_FLAG_DESCRIPTION_OVERLAY("Overlay"),
    /**
     Replacer tool display name translations.
     */
    REPLACER_TOOL_DISPLAY_NAME("$1Replacer $3[$2{0}$3]"),
    /**
     Brush tooltip lore flags translations.
     */
    BRUSH_TOOLTIP_LORE_FLAGS("$3[] $2Flags : $1{0}"),
    /**
     Brush tooltip lore argument translations.
     */
    BRUSH_TOOLTIP_LORE_ARGUMENT("$3[] $2{0} : $1'{1}'"),
    /**
     Brush tooltip display name translations.
     */
    BRUSH_TOOLTIP_DISPLAY_NAME("$1{0} $3[$2{1}$3]"),
    /**
     Brush tooltip lore separator translations.
     */
    BRUSH_TOOLTIP_LORE_SEPARATOR("&8&m-------------"),

    /**
     The Accepting tp.
     */
//    Friends
    ACCEPTING_TP("$2Accept teleports from friends : $1{0}"),
    /**
     The Friend teleported.
     */
    FRIEND_TELEPORTED("$2{0} $1teleported to your location"),
    /**
     The No tp not friends.
     */
    NO_TP_NOT_FRIENDS("$4You are not friends with that user so you cannot teleport to them"),
    /**
     The Already friends.
     */
    ALREADY_FRIENDS("You are already friends with $2{0}"),
    /**
     The Friend added.
     */
    FRIEND_ADDED("You are now freinds with $2{0}"),
    /**
     The Request sent.
     */
    REQUEST_SENT("$1A friend request was sent to $2{0}"),
    /**
     The Request received.
     */
    REQUEST_RECEIVED("$2{0} $1has requested to befriend you"),
    /**
     The Friend removed.
     */
    FRIEND_REMOVED("$3{0} $1was removed as friend"),
    /**
     Friend accept button translations.
     */
    FRIEND_ACCEPT_BUTTON("&f&mAccept"),
    /**
     The Friend accept button hover.
     */
    FRIEND_ACCEPT_BUTTON_HOVER("$1Click to accept request from {0}"),
    /**
     Friend deny button translations.
     */
    FRIEND_DENY_BUTTON("&f&mDeny"),
    /**
     The Friend deny button hover.
     */
    FRIEND_DENY_BUTTON_HOVER("$1Click to deny request from {0}"),
    /**
     Friend row request translations.
     */
    FRIEND_ROW_REQUEST("$1{0} $2- Request"),
    /**
     Friend list title translations.
     */
    FRIEND_LIST_TITLE("$1Friends"),

    /**
     The Unknown weather type.
     */
//    WeatherPlugin
    UNKNOWN_WEATHER_TYPE("$4That weather type is unknown"),
    /**
     The Plot weather set.
     */
    PLOT_WEATHER_SET("$2Plot weather set to: $1{0}"),
    /**
     The Weather error no owner.
     */
    WEATHER_ERROR_NO_OWNER("You must be the owner of the plot to execute this command"),
    /**
     The Weather error no weather type.
     */
    WEATHER_ERROR_NO_WEATHER_TYPE("You must enter a weather type"),
    /**
     The Weather using global.
     */
    WEATHER_USING_GLOBAL("Using global weather : {0}"),
    /**
     The Weather debug.
     */
    WEATHER_DEBUG("Current Weather Type: {0}\nIs Lightning Player: {1}\n{2}"),
    /**
     The Weather disabled user.
     */
    WEATHER_DISABLED_USER("$4Sorry, but pweather is currently disabled"),
    /**
     The Lightning schedule active.
     */
    LIGHTNING_SCHEDULE_ACTIVE("Lightning schedular active with {0} players"),
    /**
     The Lightning schedule inactive.
     */
    LIGHTNING_SCHEDULE_INACTIVE("Lightning schedular inactive with {0} players"),

    /**
     The Open gui error.
     */
//    HeadDatabase
    OPEN_GUI_ERROR("$4Failed to open Head Database GUI"),
    /**
     Heads evolved api url translations.
     */
    HEADS_EVOLVED_API_URL("https://minecraft-heads.com/scripts/api.php?tags=true&cat={0}"),
    /**
     The Heads evolved failed load.
     */
    HEADS_EVOLVED_FAILED_LOAD("$4Failed to load {0} heads"),

    /**
     The Height too high.
     */
//    Layerheight
    HEIGHT_TOO_HIGH("$4Height cannot be above 8"),
    /**
     The Height too low.
     */
    HEIGHT_TOO_LOW("$4Height cannot be below 1"),
    /**
     The Heighttool name.
     */
    HEIGHTTOOL_NAME("$1Layer Height Tool: $2{0}"),
    /**
     The Heighttool set.
     */
    HEIGHTTOOL_SET("Successfully set the layer height to $2{0}"),
    /**
     The Heighttool failed bind.
     */
    HEIGHTTOOL_FAILED_BIND("$4Tool cannot be bound to a block"),
    /**
     The Outside plot.
     */
    OUTSIDE_PLOT("$4You are not standing inside a plot"),

    /**
     The Shared hotbar with.
     */
//    HotbarShare
    SHARED_HOTBAR_WITH("You shared your hotbar with {0}"),
    /**
     The Player shared hotbar.
     */
    PLAYER_SHARED_HOTBAR("$2{0} $1shared their hotbar"),
    /**
     The Full hotbar.
     */
    FULL_HOTBAR("$4Your hotbar is full, please clear one or more slots"),
    /**
     The Hotbar share header.
     */
    HOTBAR_SHARE_HEADER("$1&m------- &r$2{0}'s Hotbar $1&m-------"),
    /**
     Hotbar share index translations.
     */
    HOTBAR_SHARE_INDEX("$2#{0} : $1{1}"),
    /**
     Hotbar share enchanted translations.
     */
    HOTBAR_SHARE_ENCHANTED("$2  Enchanted : "),
    /**
     Hotbar share lore translations.
     */
    HOTBAR_SHARE_LORE("$2  Lore : "),
    /**
     Hotbar view button translations.
     */
    HOTBAR_VIEW_BUTTON("$2[$1View$2]"),
    /**
     The Hotbar view button hover.
     */
    HOTBAR_VIEW_BUTTON_HOVER("View hotbar"),
    /**
     Hotbar load button translations.
     */
    HOTBAR_LOAD_BUTTON("$2[$1Load$2]"),
    /**
     The Hotbar load button hover.
     */
    HOTBAR_LOAD_BUTTON_HOVER("Load hotbar"),

    /**
     The Png url required.
     */
//    Paintings
    PNG_URL_REQUIRED("$4URLs have to end with .png, please make sure to upload an image rather than a webpage including it"),
    /**
     The Painting too big.
     */
    PAINTING_TOO_BIG("$4Paintings can have a maximum size of 3x3"),
    /**
     The Painting exempt.
     */
    PAINTING_EXEMPT("You are exempt from needing permission, check your list of paintings"),
    /**
     The Painting submitted.
     */
    PAINTING_SUBMITTED("Submitted a new request, once accepted the painting will be automatically uploaded"),
    /**
     Painting status submitted translations.
     */
    PAINTING_STATUS_SUBMITTED("Submitted"),
    /**
     Painting status rejected translations.
     */
    PAINTING_STATUS_REJECTED("Rejected"),
    /**
     Painting status approved translations.
     */
    PAINTING_STATUS_APPROVED("Approved"),
    /**
     Painting discord title translations.
     */
    PAINTING_DISCORD_TITLE("Submissions"),
    /**
     The Painting discord field title.
     */
    PAINTING_DISCORD_FIELD_TITLE("Submission ID : #{0}"),
    /**
     The Painting discord field value.
     */
    PAINTING_DISCORD_FIELD_VALUE("Submitted by : {0}\nStatus : {1}"),
    /**
     The Painting submission not found.
     */
    PAINTING_SUBMISSION_NOT_FOUND("Cannot find submission : {0}"),
    /**
     The Painting submission list.
     */
    PAINTING_SUBMISSION_LIST("Submission IDs : {0}"),
    /**
     The Painting approving.
     */
    PAINTING_APPROVING("Approving Submission : {0}"),
    /**
     The Painting rejecting.
     */
    PAINTING_REJECTING("Rejecting Submission : {0}"),
    /**
     The Painting cannot update status.
     */
    PAINTING_CANNOT_UPDATE_STATUS("Cannot {0} this submission (#{1}). Are you sure it exists?"),

    /**
     The Painting new submission title.
     */
    PAINTING_NEW_SUBMISSION_TITLE("New submission : #{0}"),
    /**
     The Painting new exempt submission title.
     */
    PAINTING_NEW_EXEMPT_SUBMISSION_TITLE("New exempt submission : #{0}"),
    /**
     The Painting submission author.
     */
    PAINTING_SUBMISSION_AUTHOR("Submitted by : {0}"),
    /**
     Painting submission size title translations.
     */
    PAINTING_SUBMISSION_SIZE_TITLE("Size"),
    /**
     Painting submission size value translations.
     */
    PAINTING_SUBMISSION_SIZE_VALUE("X: {0}\nY: {1}"),

    /**
     User data header translations.
     */
//    UserData
    USER_DATA_HEADER("$1&m------- &r$2{0} $1&m-------"),
    /**
     The User data failed collect.
     */
    USER_DATA_FAILED_COLLECT("$4Could not collect data for {0}"),

    /**
     The Multi cmd performing.
     */
//    MultiCommand
    MULTI_CMD_PERFORMING("Performing command : $2{0}"),

    /**
     The Dave link suggestion.
     */
//    Dave
    DAVE_LINK_SUGGESTION("Here's a useful link, $1{0}"),
    /**
     The Dave link suggestion hover.
     */
    DAVE_LINK_SUGGESTION_HOVER("$2Click to open $1{0}"),
    /**
     Dave discord format translations.
     */
    DAVE_DISCORD_FORMAT("**Dave** ≫ {0}"),
    /**
     The Dave muted.
     */
    DAVE_MUTED("Muted Dave, note that important triggers will always show"),
    /**
     The Dave unmuted.
     */
    DAVE_UNMUTED("Unmuted Dave"),
    /**
     The Dave reloaded user.
     */
    DAVE_RELOADED_USER("{0} &f: Reloaded Dave without breaking stuff, whoo!"),

    /**
     The Pid users trusted more.
     */
//    Plot ID Bar
    PID_USERS_TRUSTED_MORE("{0}, {1} and {2} others"),
    /**
     Pid users trusted translations.
     */
    PID_USERS_TRUSTED("{0}, {1}"),
    /**
     The Pid world format.
     */
    PID_WORLD_FORMAT("$2World ID : $1{0}"),
    /**
     The Pid plot format.
     */
    PID_PLOT_FORMAT("$2Plot ID : $1{0}, {1}"),
    /**
     Pid owner format translations.
     */
    PID_OWNER_FORMAT("$2Owner : $1{0}"),
    /**
     Pid bar separator translations.
     */
    PID_BAR_SEPARATOR(" &f|-=-| "),
    /**
     Pid bar members translations.
     */
    PID_BAR_MEMBERS("$2Members : $1{0}"),
    /**
     The Pid toggle bar.
     */
    PID_TOGGLE_BAR("Updated PlotID Bar preference to $2{0}"),
    /**
     The Pid toggle members.
     */
    PID_TOGGLE_MEMBERS("Updated PlotID Members preference to $2{0}"),

    /**
     The Trust limit auto cleaned.
     */
//    Trust Limiter
    TRUST_LIMIT_AUTO_CLEANED("$1Automatically removed $2{0} $1from this plot because their rank is too low."),

    /**
     Wiki breakline translations.
     */
//    Wiki
    WIKI_BREAKLINE("$2&m============&r $1{0} $2&m============"),
    /**
     The Wiki not allowed.
     */
    WIKI_NOT_ALLOWED("$4You do not have permission to view this wiki '{0}'"),
    /**
     The Wiki not found.
     */
    WIKI_NOT_FOUND("No wiki entries were found for the requested value '{0}'"),
    /**
     Wiki list row translations.
     */
    WIKI_LIST_ROW("\n $3- $1{0} $2[View]"),
    /**
     Wiki share button translations.
     */
    WIKI_SHARE_BUTTON("$2[Share '{0}']$2]"),
    /**
     The Wiki share button hover.
     */
    WIKI_SHARE_BUTTON_HOVER("$1Share wiki with another player"),
    /**
     Wiki view button translations.
     */
    WIKI_VIEW_BUTTON("$2[$1View$2]"),
    /**
     The Wiki view button hover.
     */
    WIKI_VIEW_BUTTON_HOVER("$1View entry '{0}'"),
    /**
     The Wiki list row hover.
     */
    WIKI_LIST_ROW_HOVER("$1More information about {0}"),
    /**
     The Wiki no entries.
     */
    WIKI_NO_ENTRIES("$1No wiki entries were found"),
    /**
     The Wiki shared user.
     */
    WIKI_SHARED_USER("$2{0} $1shared the '{1}' wiki with you"),
    /**
     The Wiki reloaded user success.
     */
    WIKI_RELOADED_USER_SUCCESS("Successfully reloaded wiki"),
    /**
     The Wiki reloaded user failure.
     */
    WIKI_RELOADED_USER_FAILURE("Failed to reload wiki, see console for more information"),
    /**
     The Wiki open entry hover.
     */
    WIKI_OPEN_ENTRY_HOVER("Open entry '{0}'"),
    /**
     The Wiki share failed.
     */
    WIKI_SHARE_FAILED("Could not share entry"),

    /**
     The Ptime invalid number.
     */
//    PlayerTime
    PTIME_INVALID_NUMBER("'{0}' is not a valid number"),
    /**
     The Ptime number too small.
     */
    PTIME_NUMBER_TOO_SMALL("The number you have entered ({0}) is too small, it must be at least 0"),
    /**
     The Ptime in sync.
     */
    PTIME_IN_SYNC("Your time is currently in sync with the server's time"),
    /**
     The Ptime ahead.
     */
    PTIME_AHEAD("Your time is currently running {0} ticks ahead of the server"),

    /**
     The Schematic empty.
     */
//    Schematic Brush
    SCHEMATIC_EMPTY("Schematic is empty"),
    /**
     Schematic applied clipboard translations.
     */
    SCHEMATIC_APPLIED_CLIPBOARD("Applied '{0}', flip={1}, rot={2}, place={3}"),
    /**
     The Schematic set not allowed.
     */
    SCHEMATIC_SET_NOT_ALLOWED("Not permitted to use schematic sets"),
    /**
     The Schematic set not found.
     */
    SCHEMATIC_SET_NOT_FOUND("Schematic set '{0}' not found"),
    /**
     The Schematic invalid definition.
     */
    SCHEMATIC_INVALID_DEFINITION("Invalid schematic definition: {0}"),
    /**
     The Schematic invalid filename.
     */
    SCHEMATIC_INVALID_FILENAME("Invalid filename pattern: {0} - {1}"),
    /**
     The Schematic bad offset y.
     */
    SCHEMATIC_BAD_OFFSET_Y("Bad y-offset value: {0}"),
    /**
     The Schematic bad place center.
     */
    SCHEMATIC_BAD_PLACE_CENTER("Bad place value ({0}) - using CENTER"),
    /**
     The Schematic brush set.
     */
    SCHEMATIC_BRUSH_SET("Schematic brush set"),
    /**
     The Could not detect worldedit.
     */
    COULD_NOT_DETECT_WORLDEDIT("Could not detect a supported version of WorldEdit"),
    /**
     The Schematic pattern required.
     */
    SCHEMATIC_PATTERN_REQUIRED("Schematic brush requires &set-id or one or more schematic patterns"),
    /**
     Schematic set list row translations.
     */
    SCHEMATIC_SET_LIST_ROW("{0}: desc='{1}'"),
    /**
     The Schematic set list count.
     */
    SCHEMATIC_SET_LIST_COUNT("{0} sets returned"),
    /**
     The Schematic set id missing.
     */
    SCHEMATIC_SET_ID_MISSING("Missing set ID"),
    /**
     The Schematic set already defined.
     */
    SCHEMATIC_SET_ALREADY_DEFINED("Set '{0}' already defined"),
    /**
     The Schematic set not defined.
     */
    SCHEMATIC_SET_NOT_DEFINED("Set '{0}' not defined"),
    /**
     Schematic set invalid translations.
     */
    SCHEMATIC_SET_INVALID("Schematic '{0}' invalid - ignored"),
    /**
     Schematic set created translations.
     */
    SCHEMATIC_SET_CREATED("Set '{0}' created"),
    /**
     Schematic set deleted translations.
     */
    SCHEMATIC_SET_DELETED("Set '{0}' deleted"),
    /**
     Schematic set updated translations.
     */
    SCHEMATIC_SET_UPDATED("Set '{0}' updated"),
    /**
     Schematic removed set translations.
     */
    SCHEMATIC_REMOVED_SET("Schematic '{0}' removed"),
    /**
     The Schematic not found set.
     */
    SCHEMATIC_NOT_FOUND_SET("Schematic '{0}' not found in set"),
    /**
     Schematic set description translations.
     */
    SCHEMATIC_SET_DESCRIPTION("Description: {0}"),
    /**
     Schematic description translations.
     */
    SCHEMATIC_DESCRIPTION("Schematic: {0} ({1})"),
    /**
     The Schematic set weight too high.
     */
    SCHEMATIC_SET_WEIGHT_TOO_HIGH("Warning: total weights exceed 100 - schematics without weights will never be selected"),
    /**
     The Schematic invalid format.
     */
    SCHEMATIC_INVALID_FORMAT("Invalid format: {0}"),
    /**
     Schematic list pagination footer translations.
     */
    SCHEMATIC_LIST_PAGINATION_FOOTER("Page {0} of {1} ({2} files)"),
    /**
     The Schematic file not found.
     */
    SCHEMATIC_FILE_NOT_FOUND("Schematic '{0}' file not found"),
    /**
     The Schematic format not found.
     */
    SCHEMATIC_FORMAT_NOT_FOUND("Schematic '{0}' format not found"),
    /**
     The Schematic read error.
     */
    SCHEMATIC_READ_ERROR("$1Error reading schematic '{0}' - {1}"),

    /**
     The Data unavailable offline player.
     */
//    Player Data
    DATA_UNAVAILABLE_OFFLINE_PLAYER("$3&oNot available if player is offline"),
    /**
     Player data plotsquared translations.
     */
    PLAYER_DATA_PLOTSQUARED("$2Worlds: $1{0}\n$2Plots: $1{1}"),
    /**
     The Player data luckperms.
     */
    PLAYER_DATA_LUCKPERMS("$2Primary group: $1{0}\n$2Prefix: $1{1}"),
    /**
     The Player data nucleus.
     */
    PLAYER_DATA_NUCLEUS("$2First joined: $1{0}\n" +
            "$2Last known IP: $1{1}\n" +
            "$2Last known name: $1{2}\n" +
            "$2Last login: $1{3}\n" +
            "$2Last logout: $1{4}\n" +
            "$2Last seen: $1{5}\n" +
            "$2Alt accounts: $1{6}"),
    /**
     The Player data collect error.
     */
    PLAYER_DATA_COLLECT_ERROR("$4Could not collect data for {0}"),

    /**
     The Rate limit kick message.
     */
//    Rate Limit
    RATE_LIMIT_KICK_MESSAGE("$4You are being rate limited. To prevent spam, relogging is limited to once per minute."),

    /**
     The Disabled module row.
     */
//    Darwin CMD
    DISABLED_MODULE_ROW("$2 - &7[Disabled] $3{0} $3- $2{1} {2}"),
    /**
     Failed module row translations.
     */
    FAILED_MODULE_ROW("$2 - $4[Failed] {0}"),
    /**
     Active module row translations.
     */
    ACTIVE_MODULE_ROW("$2 - &a[Loaded] $1{0} $3- $2{1} {2}"),
    /**
     The Darwin module title.
     */
    DARWIN_MODULE_TITLE("$1Darwin Server Info"),
    /**
     Darwin module padding translations.
     */
    DARWIN_MODULE_PADDING("&m$2="),
    /**
     The Darwin server version.
     */
    DARWIN_SERVER_VERSION("$2&lDarwin Server &r$3($1Version$3: $1{0}$3)"),
    /**
     The Darwin server update.
     */
    DARWIN_SERVER_UPDATE("$2&lLast updated&r$3: $1{0}"),
    /**
     Darwin server author translations.
     */
    DARWIN_SERVER_AUTHOR("$2&lAuthor&r$3: $1{0}"),
    /**
     Darwin server module head translations.
     */
    DARWIN_SERVER_MODULE_HEAD("$2&lModules&r$3:"),
    /**
     Module source translations.
     */
    MODULE_SOURCE("&e[{0}]"),

    /**
     The Wu added.
     */
//   World Unloader
    WU_ADDED("$1Added $2{0} $1to the unload blacklist"),
    /**
     The World not found.
     */
    WORLD_NOT_FOUND("$4Could not find that world!"),

    /**
     The Spectator tp disallowed.
     */
//  Spectator TP
    SPECTATOR_TP_DISALLOWED("$3You are not allowed to teleport while in spectator mode"),

    /**
     The Cu title.
     */
//  Command usage
    CU_TITLE("$3$2Usage for $1/{0}"),
    /**
     Cu usage translations.
     */
    CU_USAGE("$3- $1/{0}"),
    /**
     Cu flags translations.
     */
    CU_FLAGS("$3- $2Flags: $1{0}"),
    /**
     Cu description translations.
     */
    CU_DESCRIPTION("$3- $2Summary: $1{0}");

    private String s;

    Translations(String s) {
        this.s = s;
    }

    /**
     U string.

     @return the string
     */
// Unparsed
    public String u() {
        return s;
    }

    /**
     S string.

     @return the string
     */
    public String s() {
        return parseColors(this.s);
    }

    /**
     F string.

     @param args
     the args

     @return the string
     */
    public String f(final Object... args) {
        return format(this.s, args);
    }

    /**
     Shorten string.

     @param m
     the m

     @return the string
     */
    public static String shorten(String m) {
        return shorten(m, 20);
    }

    /**
     Sh string.

     @return the string
     */
    public String sh() {
        return shorten(s());
    }

    /**
     Sh string.

     @param maxChars
     the max chars

     @return the string
     */
    public String sh(int maxChars) {
        return shorten(s(), maxChars);
    }

    /**
     Format string.

     @param m
     the m
     @param args
     the args

     @return the string
     */
// Format value placeholders and colors
    public static String format(String m, Object... args) {
        if (args.length == 0) return m;
        Map<String, String> map = new LinkedHashMap<>();
        if (args.length > 0) {
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = "" + args[i];
                if (arg == null || arg.isEmpty()) map.put(String.format("{%d}", i), "");
                else map.put(String.format("{%d}", i), arg);
                if (i == 0) map.put("%s", arg);
            }
        }
        m = StringUtils.replaceFromMap(m, map);
        return parseColors(m);
    }

    private static String parseColors(String m) {
        char[] nativeFormats = "abcdef1234567890klmnor".toCharArray();
        for (char c : nativeFormats) m = m.replaceAll(String.format("&%s", c), String.format("\u00A7%s", c));

        return "\u00A7r" + m
                .replaceAll("\\$1", String.format("\u00A7%s", COLOR_PRIMARY.s))
                .replaceAll("\\$2", String.format("\u00A7%s", COLOR_SECONDARY.s))
                .replaceAll("\\$3", String.format("\u00A7%s", COLOR_MINOR.s))
                .replaceAll("\\$4", String.format("\u00A7%s", COLOR_ERROR.s));
    }

    /**
     Shorten string.

     @param m
     the m
     @param maxChars
     the max chars

     @return the string
     */
// Shorten the value
    public static String shorten(String m, int maxChars) {
        return (m.length() > maxChars) ? String.format("%s...", m.substring(0, maxChars)) : m;
    }

    /**
     Collect.
     */
    public static void collect() {
        DarwinServer.getModule(DarwinServerModule.class).ifPresent(module -> {
            Map<String, Object> configMap;
            File file = new File(DarwinServer.getUtilChecked(FileUtils.class).getConfigDirectory(module).toFile(), "translations.yml");
            if (!file.exists()) {
                configMap = new HashMap<>();
                Arrays.stream(Translations.values()).forEach(translation -> configMap.put(translation.name().toLowerCase().replaceAll("_", "."), translation.u()));
                DarwinServer.getUtilChecked(FileUtils.class).writeYamlDataToFile(configMap, file);
            } else configMap = DarwinServer.getUtilChecked(FileUtils.class).getYamlDataFromFile(file);

            configMap.forEach((k, v) -> {
                Translations t = Translations.valueOf(k.toUpperCase().replaceAll("\\.", "_"));
                if (t != null) t.c(v.toString());
            });
        });
    }

    private void c(String s) {
        this.s = s;
    }
}
