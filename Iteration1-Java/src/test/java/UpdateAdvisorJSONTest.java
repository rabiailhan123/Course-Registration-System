import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UpdateAdvisorJSONTest {

    // Create a mock Advisor class to simulate the real class
    static class Advisor {
        private String id;
        private String name;
        private String eMail;
        private String password;

        public Advisor(String id, String name, String eMail, String password) {
            this.id = id;
            this.name = name;
            this.eMail = eMail;
            this.password = password;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEMail() {
            return eMail;
        }

        public String getPassword() {
            return password;
        }
    }

    // The class under test (Assuming advisorMap is a field in the actual class)
    private class AdvisorService {
        private Map<String, Advisor> advisorMap = new HashMap<>();

        // Method to update advisor JSON files
        public void updateAdvisorJSON() {
            List<Advisor> advisors = new ArrayList<>(advisorMap.values());
            for (int i = 0; i < advisors.size(); i++) {
                JSONObject advisor = new JSONObject();
                advisor.put("id", advisors.get(i).getId());
                advisor.put("name", advisors.get(i).getName());
                advisor.put("eMail", advisors.get(i).getEMail());
                advisor.put("password", advisors.get(i).getPassword());

                try (FileWriter file = new FileWriter(advisors.get(i).getId() + ".json")) {
                    file.write(advisor.toString(4));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Method to add advisor to the map (used for testing)
        public void addAdvisor(Advisor advisor) {
            advisorMap.put(advisor.getId(), advisor);
        }
    }

    @Test
    void testUpdateAdvisorJSON(@TempDir Path tempDir) throws IOException {
        // Create a service instance and set temp directory as base for file creation
        AdvisorService advisorService = new AdvisorService();

        // Add some advisors to the service
        advisorService.addAdvisor(new Advisor("1", "John Doe", "john.doe@example.com", "password123"));
        advisorService.addAdvisor(new Advisor("2", "Jane Smith", "jane.smith@example.com", "password456"));

        // Temporarily override the file writing location (for testing purpose)
        System.setProperty("user.dir", tempDir.toString());

        // Call the method to update advisor JSON files
        advisorService.updateAdvisorJSON();

        // Verify that the JSON files have been created
        File file1 = new File(tempDir.toFile(), "1.json");
        File file2 = new File(tempDir.toFile(), "2.json");

        assertTrue(file1.exists(), "File for advisor 1 should exist");
        assertTrue(file2.exists(), "File for advisor 2 should exist");

        // Verify the content of the JSON files for advisor 1
        String content1 = new String(Files.readAllBytes(file1.toPath()));
        JSONObject json1 = new JSONObject(content1);
        assertEquals("1", json1.getString("id"));
        assertEquals("John Doe", json1.getString("name"));
        assertEquals("john.doe@example.com", json1.getString("eMail"));
        assertEquals("password123", json1.getString("password"));

        // Verify the content of the JSON files for advisor 2
        String content2 = new String(Files.readAllBytes(file2.toPath()));
        JSONObject json2 = new JSONObject(content2);
        assertEquals("2", json2.getString("id"));
        assertEquals("Jane Smith", json2.getString("name"));
        assertEquals("jane.smith@example.com", json2.getString("eMail"));
        assertEquals("password456", json2.getString("password"));
    }

    // Test case where no advisors are present (no files should be created)
    @Test
    void testNoAdvisors(@TempDir Path tempDir) {
        AdvisorService advisorService = new AdvisorService();

        // Temporarily override the file writing location (for testing purpose)
        System.setProperty("user.dir", tempDir.toString());

        // Call the method to update advisor JSON files
        advisorService.updateAdvisorJSON();

        // Verify that no files are created since there are no advisors
        File[] files = tempDir.toFile().listFiles();
        assertNotNull(files, "Files array should not be null");
        assertEquals(0, files.length, "No files should be created when there are no advisors");
    }
}

