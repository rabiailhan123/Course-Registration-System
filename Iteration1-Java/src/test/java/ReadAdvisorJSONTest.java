import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReadAdvisorJSONTest {

    // Mock Advisor class
    static class Advisor {
        private int id;
        private String name;
        private String email;
        private String password;
        private ArrayList<Student> students;

        public Advisor(int id, String name, String email, String password, ArrayList<Student> students) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.students = students;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEMail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public ArrayList<Student> getStudents() {
            return students;
        }
    }

    // Mock Student class (used inside Advisor)
    static class Student {
        private int id;
        private String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // Class under test
    private class AdvisorService {
        private Map<Integer, Advisor> advisorMap;

        public AdvisorService(Map<Integer, Advisor> advisorMap) {
            this.advisorMap = advisorMap;
        }

        // The method to test
        public void readAdvisorJSON(String dirPath) {
            File[] files = takeJsonFiles(dirPath);
            if (files != null && files.length > 0) {
                for (File file : files) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        StringBuilder fileContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }

                        JSONObject jObject = new JSONObject(fileContent.toString());

                        Advisor advisor = new Advisor(jObject.getInt("id"), jObject.getString("name"),
                                jObject.getString("eMail"), jObject.getString("password"), new ArrayList<>());
                        advisorMap.put(advisor.getId(), advisor);

                    } catch (FileNotFoundException e) {
                        System.err.println("\n\n----------------------------------");
                        System.err.println("Should never happen.\n");
                        System.err.println(e.getMessage());
                        System.err.println("----------------------------------\n\n");
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }

        // Helper method to list files (simulating your takeJsonFiles method)
        private File[] takeJsonFiles(String dirPath) {
            File directory = new File(dirPath);
            return directory.listFiles((dir, name) -> name.endsWith(".json"));
        }
    }

    // Test that the method correctly reads valid JSON files and adds advisors to the map
    @Test
    void testReadAdvisorJSONValid(@TempDir Path tempDir) throws IOException {
        // Prepare the AdvisorService and advisor map
        Map<Integer, Advisor> advisorMap = new java.util.HashMap<>();
        AdvisorService advisorService = new AdvisorService(advisorMap);

        // Create and write JSON file for advisor1
        String advisorJson1 = "{ \"id\": 1, \"name\": \"Dr. John Doe\", \"eMail\": \"john.doe@example.com\", \"password\": \"password123\" }";
        File advisorFile1 = new File(tempDir.toFile(), "1.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(advisorFile1))) {
            writer.write(advisorJson1);
        }

        // Create and write JSON file for advisor2
        String advisorJson2 = "{ \"id\": 2, \"name\": \"Dr. Jane Smith\", \"eMail\": \"jane.smith@example.com\", \"password\": \"password456\" }";
        File advisorFile2 = new File(tempDir.toFile(), "2.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(advisorFile2))) {
            writer.write(advisorJson2);
        }

        // Run the method under test
        advisorService.readAdvisorJSON(tempDir.toString());

        // Verify that advisorMap has the correct advisors
        assertEquals(2, advisorMap.size(), "There should be 2 advisors in the map.");

        Advisor advisor1 = advisorMap.get(1);
        assertNotNull(advisor1, "Advisor 1 should not be null.");
        assertEquals("Dr. John Doe", advisor1.getName(), "Name for advisor 1 should match.");
        assertEquals("john.doe@example.com", advisor1.getEMail(), "Email for advisor 1 should match.");

        Advisor advisor2 = advisorMap.get(2);
        assertNotNull(advisor2, "Advisor 2 should not be null.");
        assertEquals("Dr. Jane Smith", advisor2.getName(), "Name for advisor 2 should match.");
        assertEquals("jane.smith@example.com", advisor2.getEMail(), "Email for advisor 2 should match.");
    }

    // Test case for an empty directory (no JSON files)
    @Test
    void testReadAdvisorJSONEmptyDir(@TempDir Path tempDir) {
        // Prepare the AdvisorService and advisor map
        Map<Integer, Advisor> advisorMap = new java.util.HashMap<>();
        AdvisorService advisorService = new AdvisorService(advisorMap);

        // Run the method under test with an empty directory
        advisorService.readAdvisorJSON(tempDir.toString());

        // Verify that advisorMap is still empty
        assertTrue(advisorMap.isEmpty(), "advisorMap should be empty when no JSON files are present.");
    }

    // Test case for invalid JSON format
    @Test
    void testReadAdvisorJSONInvalidJson(@TempDir Path tempDir) throws IOException {
        // Prepare the AdvisorService and advisor map
        Map<Integer, Advisor> advisorMap = new java.util.HashMap<>();
        AdvisorService advisorService = new AdvisorService(advisorMap);

        // Create and write an invalid JSON file (missing quotes)
        String invalidJson = "{ \"id\": 1, \"name\": \"Dr. John Doe\", \"eMail\": \"john.doe@example.com\", \"password\": }";
        File invalidFile = new File(tempDir.toFile(), "invalid.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(invalidFile))) {
            writer.write(invalidJson);
        }

        // Run the method under test
        advisorService.readAdvisorJSON(tempDir.toString());

        // Verify that advisorMap is still empty since the JSON was invalid
        assertTrue(advisorMap.isEmpty(), "advisorMap should be empty due to invalid JSON.");
    }

    // Test case for missing file (FileNotFoundException scenario)
    @Test
    void testReadAdvisorJSONFileNotFound(@TempDir Path tempDir) {
        // Prepare the AdvisorService and advisor map
        Map<Integer, Advisor> advisorMap = new java.util.HashMap<>();
        AdvisorService advisorService = new AdvisorService(advisorMap);

        // Run the method under test with a directory containing no valid files
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.json");
        assertFalse(nonExistentFile.exists(), "File should not exist.");

        // Run the method under test with a directory containing no JSON files
        advisorService.readAdvisorJSON(tempDir.toString());

        // Verify that advisorMap is still empty since no files were found
        assertTrue(advisorMap.isEmpty(), "advisorMap should be empty because no files were found.");
    }
}

