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

package org.dockbox.hartshorn.archiving;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.server.minecraft.events.server.EngineChangedState;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Loading;

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

@Service
public class LogArchival {

    private final PathMatcher filter = FileSystems.getDefault().getPathMatcher("glob:*.log.gz");
    private final Pattern datePattern = Pattern.compile("((\\d{4})-(\\d{2})-\\d{2})");
    private final Pattern namePattern = Pattern.compile("(.*?)(-(\\d+))?.log.gz");
    private Path logPath;

    @Wired
    private FileManager fileManager;

    @Listener
    public void on(EngineChangedState<Loading> event) {
        this.logPath = this.fileManager.logs();
        try {
            Hartshorn.log().info("Checking for logs to archive in {}", this.logPath);
            Files.list(this.logPath)
                    .filter(p -> this.filter.matches(p.getFileName()))
                    .forEach(this::archive);
        }
        catch (IOException e) {
            Except.handle(e);
        }
    }

    private void archive(Path source) {
        Exceptional<Path> directory = this.archiveDirectory(source);
        if (directory.absent()) {
            Hartshorn.log().warn("Unable to determine date of file {}", source);
            return;
        }

        Exceptional<Path> destination = this.archiveName(directory.get(), source);
        if (destination.absent()) {
            Hartshorn.log().warn("Unable to resolve archive name for file {}", source);
            return;
        }

        Path dest = destination.get();
        if (!this.fileManager.move(source, dest)) {
            Hartshorn.log().warn("Unable to move file {} to {}", source, dest);
            return;
        }

        Hartshorn.log().info("Archived file {} to {}", source, dest);
    }

    /**
     * Returns the target archive directory for the file (if applicable). Formatted as {@code
     * YEAR/00-MONTH}, for example {@code 2020/12-DECEMBER}.
     *
     * @param file
     *         The file to archive
     *
     * @return The target archive directory
     */
    private Exceptional<Path> archiveDirectory(Path file) {
        Matcher dateMatcher = this.datePattern.matcher(file.getFileName().toString());

        String year;
        String month;
        if (dateMatcher.find()) {
            year = dateMatcher.group(2);
            month = dateMatcher.group(3);
        }
        else {
            try {
                FileTime time = Files.getLastModifiedTime(file);
                LocalDate date = LocalDate.from(time.toInstant());
                year = new SimpleDateFormat("yyyy").format(date);
                month = new SimpleDateFormat("MM").format(date);
            }
            catch (IOException | DateTimeException e) {
                return Exceptional.of(e);
            }
        }

        String folder = String.format("%s-%s", month, Month.of(Integer.parseInt(month)));

        return Exceptional.of(this.logPath.resolve(year).resolve(folder));
    }

    /**
     * Returns the target archive file located in the given directory for the given file. If a file
     * with the same name already exists at the target location, it will be appended by {@code __n}, n
     * being the next number available.
     *
     * @param dir
     *         The directory to place the target file in
     * @param file
     *         The source file to archive
     *
     * @return The target archive file
     */
    private Exceptional<Path> archiveName(Path dir, Path file) {
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
