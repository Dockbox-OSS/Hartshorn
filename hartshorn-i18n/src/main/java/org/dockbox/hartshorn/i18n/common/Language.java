/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.i18n.common;

import org.dockbox.hartshorn.persistence.PersistentCapable;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language implements PersistentCapable<PersistentLanguageModel> {
    CS_CZ(new Locale("cs"), "cs_CZ", "Czech", "Čeština"),
    DE_CH(new Locale("de_ch"), "de_CH", "German (Switzerland)", "Deutsch (Schweiz)"),
    ES_ES(new Locale("es"), "es_ES", "Spanish", "Español"),
    FI(new Locale("fi"), "fi", "Finnish", "Suomi"),
    NO_NO(new Locale("no"), "no_no", "Norwegian", "Norsk"),
    PT_PT(new Locale("pt"), "pt_PT", "Portuguese", "Português"),
    RU(new Locale("ru"), "ru", "Russian", "Pусский"),
    SV_SE(new Locale("sv"), "sv_SE", "Swedish", "Svenska"),
    TR(new Locale("tr"), "tr", "Turkish", "Türk"),
    NL_NL(new Locale("nl"), "nl_NL", "Dutch", "Nederlands"),
    EN_US(Locale.ENGLISH, "en_US", "English", "English"),
    FR_FR(Locale.FRENCH, "fr_FR", "French", "Français"),
    DE_DE(Locale.GERMANY, "de_DE", "German", "Deutsch");

    private final Locale locale;
    private final String code;
    private final String nameEnglish;
    private final String nameLocalized;

    public static Language of(String language) {
        for (Language value : Language.values()) {
            if (value.code.equals(language)
                    || value.nameEnglish.equals(language)
                    || value.nameLocalized.equals(language)) {
                return value;
            }
        }
        return Language.EN_US;
    }

    public static Language of(Locale locale) {
        for (Language value : Language.values()) {
            // Compare based on language, as e.g. NL_NL will not match with Locale(nl, NL)
            if (value.locale().getLanguage().equalsIgnoreCase(locale.getLanguage())) return value;
        }
        return Language.EN_US;
    }

    @Override
    public Class<PersistentLanguageModel> type() {
        return PersistentLanguageModel.class;
    }

    @Override
    public PersistentLanguageModel model() {
        return new PersistentLanguageModel(this.code);
    }
}
