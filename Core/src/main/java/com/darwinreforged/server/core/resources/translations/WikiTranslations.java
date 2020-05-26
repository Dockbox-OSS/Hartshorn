package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("wiki")
public class WikiTranslations {

    public static final Translation WIKI_NOT_ALLOWED = Translation.create("$4You do not have permission to view this wiki '{0}'");
    public static final Translation WIKI_NOT_FOUND = Translation.create("No wiki entries were found for the requested value '{0}'");
    public static final Translation WIKI_LIST_ROW = Translation.create(" $3- $1{0} $2[View]");
    public static final Translation WIKI_SHARE_BUTTON = Translation.create("$2[Share $1'{0}'$2]");
    public static final Translation WIKI_SHARE_BUTTON_HOVER = Translation.create("$1Share wiki with another player");
    public static final Translation WIKI_VIEW_BUTTON = Translation.create("$2[$1View$2]");
    public static final Translation WIKI_VIEW_BUTTON_HOVER = Translation.create("$1View entry '{0}'");
    public static final Translation WIKI_LIST_ROW_HOVER = Translation.create("$1More information about {0}");
    public static final Translation WIKI_NO_ENTRIES = Translation.create("$1No wiki entries were found");
    public static final Translation WIKI_SHARED_USER = Translation.create("$2{0} $1shared the $2'{1}' $1entry with you");
    public static final Translation WIKI_SHARED_WITH = Translation.create("$2You shared the $1'{0}' $2entry with $1{1}");
    public static final Translation WIKI_OPEN_ENTRY_HOVER = Translation.create("Open entry '{0}'");

    private WikiTranslations() {
    }
}
