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

package org.dockbox.hartshorn.i18n.text.actions;

import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.api.exceptions.Except;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

@SuppressWarnings("ClassReferencesSubclass")
public class ClickAction<R> extends TextAction<R> {

    protected ClickAction(R result) {
        super(result);
    }

    public static OpenUrl openUrl(String raw) {
        try {
            URL url = new URL(raw);
            return openUrl(url);
        }
        catch (MalformedURLException e) {
            Except.handle(e);
            return new OpenUrl(null);
        }
    }

    public static OpenUrl openUrl(URL url) {
        return new OpenUrl(url);
    }

    public static ChangePage changePage(int page) {
        return new ChangePage(page);
    }

    public static SuggestCommand suggestCommand(String command) {
        return new SuggestCommand(command);
    }

    public static ExecuteCallback executeCallback(Consumer<Subject> consumer) {
        return new ExecuteCallback(consumer);
    }

    public static final class OpenUrl extends ClickAction<URL> {
        private OpenUrl(URL result) {
            super(result);
        }
    }

    public static final class ChangePage extends ClickAction<Integer> {
        private ChangePage(Integer result) {
            super(result);
        }
    }

    public static final class SuggestCommand extends ClickAction<String> {
        private SuggestCommand(String result) {
            super(result);
        }
    }

    public static final class ExecuteCallback extends ClickAction<Consumer<Subject>> {
        private ExecuteCallback(Consumer<Subject> result) {
            super(result);
        }
    }
}
