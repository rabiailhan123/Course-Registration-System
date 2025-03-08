import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TakeJsonFileTest {

    
    private File[] takeJsonFiles(String dirPath) {
        File directory = new File(dirPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        return files;
    }

   
    @Test
    void testTakeJsonFiles(@TempDir Path tempDir) throws IOException {
        
        File jsonFile1 = new File(tempDir.toFile(), "test1.json");
        File jsonFile2 = new File(tempDir.toFile(), "test2.json");
        File txtFile = new File(tempDir.toFile(), "test3.txt");
        
        // Create files
        Files.createFile(jsonFile1.toPath());
        Files.createFile(jsonFile2.toPath());
        Files.createFile(txtFile.toPath());

        // Call the method under test
        File[] jsonFiles = takeJsonFiles(tempDir.toString());

        
        assertNotNull(jsonFiles, "The result should not be null.");
        assertEquals(2, jsonFiles.length, "There should be 2 JSON files.");
        
        // Verify that the files returned are indeed JSON files
        assertTrue(jsonFiles[0].getName().endsWith(".json"));
        assertTrue(jsonFiles[1].getName().endsWith(".json"));

        // Check that txtFile is not included
        boolean hasTxtFile = false;
        for (File file : jsonFiles) {
            if (file.getName().endsWith(".txt")) {
                hasTxtFile = true;
                break;
            }
        }
        assertFalse(hasTxtFile, "TXT files should not be included.");
    }

    // Test case for an empty directory (no JSON files)
    @Test
    void testNoJsonFiles(@TempDir Path tempDir) {
        // Call the method under test when the directory is empty
        File[] jsonFiles = takeJsonFiles(tempDir.toString());

        // Assert that the result is an empty array
        assertNotNull(jsonFiles, "The result should not be null.");
        assertEquals(0, jsonFiles.length, "The directory should contain no JSON files.");
    }

    // Test case for a directory with no files
    @Test
    void testEmptyDirectory(@TempDir Path tempDir) {
        // Call the method under test when the directory has no files at all
        File[] jsonFiles = takeJsonFiles(tempDir.toString());

        // Assert that the result is an empty array
        assertNotNull(jsonFiles, "The result should not be null.");
        assertEquals(0, jsonFiles.length, "The directory should contain no files.");
    }
}

