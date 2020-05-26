package com.darwinreforged.server.core.resources.translations;

import com.darwinreforged.server.core.resources.ConfigSetting;

@ConfigSetting("trustlimit")
public class TrustLimitTranslations {

    public static final Translation TRUST_LIMIT_AUTO_CLEANED = Translation.create("$1Automatically removed $2{0} $1from this plot because their rank is too low.");

    private TrustLimitTranslations() {
    }
}
