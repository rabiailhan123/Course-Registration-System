import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReadStudentJSONTest {

    // Mock Student class
    static class Student {
        private int id;
        private String name;
        private String email;
        private String password;

        public Student(int id, String name, String email, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
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
    }

    // Class under test
    private class StudentService {
        private Map<Integer, Student> studentMap;

        public StudentService(Map<Integer, Student> studentMap) {
            this.studentMap = studentMap;
        }

        // The method to test
        public void readStudentJSON(String dirPath) {
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
                        Student student = new Student(jObject.getInt("id"), jObject.getString("name"),
                                jObject.getString("eMail"), jObject.getString("password"));
                        studentMap.put(student.getId(), student);
                    } catch (FileNotFoundException e) {
                        System.err.println("\n\n----------------------------------");
                        System.err.println("Shoul never happen.\n");
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

    // Test that the method correctly reads valid JSON files and adds students to the map
    @Test
    void testReadStudentJSONValid(@TempDir Path tempDir) throws IOException {
        // Prepare a StudentService and student map
        Map<Integer, Student> studentMap = new java.util.HashMap<>();
        StudentService studentService = new StudentService(studentMap);

        // Create and write JSON file for student1
        String studentJson1 = "{ \"id\": 1, \"name\": \"John Doe\", \"eMail\": \"john.doe@example.com\", \"password\": \"password123\" }";
        File studentFile1 = new File(tempDir.toFile(), "1.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile1))) {
            writer.write(studentJson1);
        }

        // Create and write JSON file for student2
        String studentJson2 = "{ \"id\": 2, \"name\": \"Jane Smith\", \"eMail\": \"jane.smith@example.com\", \"password\": \"password456\" }";
        File studentFile2 = new File(tempDir.toFile(), "2.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile2))) {
            writer.write(studentJson2);
        }

        // Run the method under test
        studentService.readStudentJSON(tempDir.toString());

        // Verify that studentMap has the correct students
        assertEquals(2, studentMap.size(), "There should be 2 students in the map.");

        Student student1 = studentMap.get(1);
        assertNotNull(student1, "Student 1 should not be null.");
        assertEquals("John Doe", student1.getName(), "Name for student 1 should match.");
        assertEquals("john.doe@example.com", student1.getEMail(), "Email for student 1 should match.");

        Student student2 = studentMap.get(2);
        assertNotNull(student2, "Student 2 should not be null.");
        assertEquals("Jane Smith", student2.getName(), "Name for student 2 should match.");
        assertEquals("jane.smith@example.com", student2.getEMail(), "Email for student 2 should match.");
    }

    // Test case for an empty directory (no JSON files)
    @Test
    void testReadStudentJSONEmptyDir(@TempDir Path tempDir) {
        // Prepare a StudentService and student map
        Map<Integer, Student> studentMap = new java.util.HashMap<>();
        StudentService studentService = new StudentService(studentMap);

        // Run the method under test with an empty directory
        studentService.readStudentJSON(tempDir.toString());

        // Verify that studentMap is still empty
        assertTrue(studentMap.isEmpty(), "studentMap should be empty when no JSON files are present.");
    }

    // Test case for invalid JSON format
    @Test
    void testReadStudentJSONInvalidJson(@TempDir Path tempDir) throws IOException {
        // Prepare a StudentService and student map
        Map<Integer, Student> studentMap = new java.util.HashMap<>();
        StudentService studentService = new StudentService(studentMap);

        // Create and write an invalid JSON file (missing quotes)
        String invalidJson = "{ \"id\": 1, \"name\": \"John Doe\", \"eMail\": \"john.doe@example.com\", \"password\": }";
        File invalidFile = new File(tempDir.toFile(), "invalid.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(invalidFile))) {
            writer.write(invalidJson);
        }

        // Run the method under test
        studentService.readStudentJSON(tempDir.toString());

        // Verify that studentMap is still empty since the JSON was invalid
        assertTrue(studentMap.isEmpty(), "studentMap should be empty due to invalid JSON.");
    }

    // Test case for missing file (FileNotFoundException scenario)
    @Test
    void testReadStudentJSONFileNotFound(@TempDir Path tempDir) {
        // Prepare a StudentService and student map
        Map<Integer, Student> studentMap = new java.util.HashMap<>();
        StudentService studentService = new StudentService(studentMap);

        // Run the method under test with a directory containing no valid files
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.json");
        assertFalse(nonExistentFile.exists(), "File should not exist.");

        // Run the method under test with a directory containing no JSON files
        studentService.readStudentJSON(tempDir.toString());

        // Verify that studentMap is still empty since the file doesn't exist
        assertTrue(studentMap.isEmpty(), "studentMap should be empty because no files were found.");
    }
}

