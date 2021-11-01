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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.PersistentModel;

import java.util.Locale;

import lombok.Data;

@Data
public class PersistentLanguageModel implements PersistentModel<Language> {

    private String localeLang;
    private String localeCountry;
    private String code;
    private String nameEnglish;
    private String nameLocalized;

    public PersistentLanguageModel(final Language language) {
        this.localeLang = language.locale().getLanguage();
        this.localeCountry = language.locale().getCountry();
        this.code = language.code();
        this.nameEnglish = language.nameEnglish();
        this.nameLocalized = language.nameLocalized();
    }

    @Override
    public Class<Language> type() {
        return Language.class;
    }

    @Override
    public Language restore(final ApplicationContext context) {
        for (final Language value : Languages.values()) {
            if (value.code().equals(this.code)) return value;
        }
        return new DefinedLanguage(
                new Locale(this.localeLang, this.localeCountry),
                this.code, this.nameEnglish, this.nameLocalized
        );
    }
}
