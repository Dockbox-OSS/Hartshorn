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

package org.dockbox.selene.integrated.logs;

import com.google.inject.Inject;

import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.events.server.ServerEvent.ServerReloadEvent;
import org.dockbox.selene.core.events.server.ServerEvent.ServerStartedEvent;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension(id = "logarchival", name = "Log Archival", description = "Automatically organises old server logs", authors = "GuusLieben")
public class LogArchivalExtension {

    private final PathMatcher filter = FileSystems.getDefault().getPathMatcher("glob:*.log.gz");
    private final Pattern datePattern = Pattern.compile("((\\d{4})-(\\d{2})-\\d{2})");
    private final Pattern namePattern = Pattern.compile("(.*?)(-(\\d+))?.log.gz");
    private Path logPath;

    @Inject
    private FileManager fileManager;

    @Listener
    public void onLoad(ServerStartedEvent serverStartedEvent, ServerReloadEvent reloadEvent) {
        this.logPath = this.fileManager.getLogsDir();
        try {
            Selene.log().info("Checking for logs to archive in {}", this.logPath);
            Files.list(this.logPath).filter(p -> this.filter.matches(p.getFileName())).forEach(this::archive);
        } catch (IOException e) {
            Selene.handle(e);
        }
    }

    private void archive(Path source) {
        Exceptional<Path> directory = this.getArchiveDirectory(source);
        if (directory.isAbsent()) {
            Selene.log().warn("Unable to determine date of file {}", source);
            return;
        }

        Exceptional<Path> destination = this.getArchiveName(directory.get(), source);
        if (destination.isAbsent()) {
            Selene.log().warn("Unable to resolve archive name for file {}", source);
            return;
        }

        if (!this.fileManager.move(source, destination.get())) {
            Selene.log().warn("Unable to move file {} to {}", source, destination);
            return;
        }

        Selene.log().info("Archived file {} to {}", source, destination);
    }

    private Exceptional<Path> getArchiveDirectory(Path file) {
        Matcher dateMatcher = this.datePattern.matcher(file.getFileName().toString());

        String year;
        String month;
        if (dateMatcher.find()) {
            year = dateMatcher.group(2);
            month = dateMatcher.group(3);
        } else {
            try {
                FileTime time = Files.getLastModifiedTime(file);
                LocalDate date = LocalDate.from(time.toInstant());
                year = new SimpleDateFormat("yyyy").format(date);
                month = new SimpleDateFormat("MM").format(date);
            } catch (IOException | DateTimeException e) {
                return Exceptional.of(e);
            }
        }

        String folder = String.format("%s-%s", month, Month.of(Integer.parseInt(month)));

        return Exceptional.of(this.logPath.resolve(year).resolve(folder));
    }

    private Exceptional<Path> getArchiveName(Path dir, Path file) {
        Matcher nameMatcher = this.namePattern.matcher(file.getFileName().toString());
        if (nameMatcher.find()) {
            String name = nameMatcher.group(1);
            String nameFormat = "%s__%03d.log.gz";
            String filename = String.format(nameFormat, name, 0);

            Path destination = dir.resolve(filename);
            for (int i = 1; Files.exists(destination); i++) {
                filename = String.format(nameFormat, name, i);
                destination = dir.resolve(filename);
            }

            return Exceptional.of(destination);
        }
        return Exceptional.empty();
    }

}
