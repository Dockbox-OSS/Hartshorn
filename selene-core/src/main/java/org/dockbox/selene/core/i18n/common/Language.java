/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.i18n.common;

public enum Language {
    EN_US("en_US", "English", "US"),
    NL_NL("nl_NL", "Dutch", "Nederlands"),
    FR_FR("fr_FR", "French", "Français"),
    DE_DE("en_US", "German", "Deutsch")
    ;
    private final String code;
    private final String nameEnglish;
    private final String nameLocalized;

    Language(String code, String nameEnglish, String nameLocalized) {
        this.code = code;
        this.nameEnglish = nameEnglish;
        this.nameLocalized = nameLocalized;
    }

    public String getCode() {
        return this.code;
    }

    public String getNameEnglish() {
        return this.nameEnglish;
    }

    public String getNameLocalized() {
        return this.nameLocalized;
    }
}
