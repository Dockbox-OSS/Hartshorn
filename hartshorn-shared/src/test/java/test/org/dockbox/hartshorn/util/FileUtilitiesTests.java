package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.FileUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

public class FileUtilitiesTests {

    @TempDir
    private Path testDirectory;

    @Test
    void testHasFileExtensionForPresentExtension() {
        Path path = testDirectory.resolve("file.txt");
        Assertions.assertTrue(FileUtilities.hasFileExtension(path));
    }

    @Test
    void testHasFileExtensionForAbsentExtension() {
        Path path = testDirectory.resolve("file");
        Assertions.assertFalse(FileUtilities.hasFileExtension(path));
    }

    @Test
    void testGetFileExtensionForPresentExtension() {
        Path path = testDirectory.resolve("file.txt");
        String extension = FileUtilities.getFileExtension(path);
        Assertions.assertEquals("txt", extension);
    }

    @Test
    void testGetFileExtensionForAbsentExtension() {
        Path path = testDirectory.resolve("file");
        String extension = FileUtilities.getFileExtension(path);
        Assertions.assertNull(extension);
    }

    @Test
    void testExistsForExistingFile() throws IOException {
        Path path = testDirectory.resolve("file");
        Assertions.assertTrue(path.toFile().createNewFile());
        Assertions.assertTrue(FileUtilities.exists(path));
    }

    @Test
    void testExistsForNotExistingFile() {
        Path path = testDirectory.resolve("doesnotexist");
        Assertions.assertFalse(FileUtilities.exists(path));
    }
}
