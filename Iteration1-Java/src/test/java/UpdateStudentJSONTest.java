import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UpdateStudentJSONTest {

    // Create mock Advisor class
    static class Advisor {
        private String id;
        private String name;

        public Advisor(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // Create mock Course class
    static class Course {
        private String id;
        private String name;

        public Course(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // Create mock Student class
    static class Student {
        private String id;
        private String name;
        private String email;
        private String password;
        private Advisor advisor;
        private Map<Course, String> courses;

        public Student(String id, String name, String email, String password, Advisor advisor) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.advisor = advisor;
            this.courses = new HashMap<>();
        }

        public void addCourse(Course course, String grade) {
            courses.put(course, grade);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEMail() {
            return email;
        }

        public Advisor getAdvisor() {
            return advisor;
        }

        public String getPassword() {
            return password;
        }

        public Map<Course, String> getCourses() {
            return courses;
        }
    }

    // The class under test (Assuming studentMap is a field in the actual class)
    private class StudentService {
        private Map<String, Student> studentMap = new HashMap<>();

        // Method to update student JSON files
        public void updateStudentJSON() {
            List<Student> students = new ArrayList<>(studentMap.values());
            for (int i = 0; i < students.size(); i++) {
                JSONObject student = new JSONObject();
                student.put("id", students.get(i).getId());
                student.put("name", students.get(i).getName());
                student.put("eMail", students.get(i).getEMail());
                student.put("AdvisorId", students.get(i).getAdvisor().getId());
                student.put("password", students.get(i).getPassword());

                JSONArray courseTaken = new JSONArray();
                for (Course course : students.get(i).getCourses().keySet()) {
                    JSONObject courseObject = new JSONObject();
                    courseObject.put(course.getId(), students.get(i).getCourses().get(course));
                    courseTaken.put(courseObject);
                }

                student.put("CourseTaken", courseTaken);

                try (FileWriter file = new FileWriter(students.get(i).getId() + ".json")) {
                    file.write(student.toString(4));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Method to add student to the map (used for testing)
        public void addStudent(Student student) {
            studentMap.put(student.getId(), student);
        }
    }

    @Test
    void testUpdateStudentJSON(@TempDir Path tempDir) throws IOException {
        // Create service instance and set temp directory as base for file creation
        StudentService studentService = new StudentService();

        // Create mock objects
        Advisor advisor1 = new Advisor("A1", "Dr. Smith");
        Advisor advisor2 = new Advisor("A2", "Dr. Johnson");
        Course course1 = new Course("C1", "Mathematics");
        Course course2 = new Course("C2", "Physics");

        // Create students and add courses
        Student student1 = new Student("S1", "John Doe", "john.doe@example.com", "password123", advisor1);
        student1.addCourse(course1, "A");
        student1.addCourse(course2, "B");

        Student student2 = new Student("S2", "Jane Smith", "jane.smith@example.com", "password456", advisor2);
        student2.addCourse(course1, "B");

        // Add students to the service
        studentService.addStudent(student1);
        studentService.addStudent(student2);

        // Temporarily override the file writing location (for testing purposes)
        System.setProperty("user.dir", tempDir.toString());

        // Call the method to update student JSON files
        studentService.updateStudentJSON();

        // Verify that the JSON files for students are created
        File file1 = new File(tempDir.toFile(), "S1.json");
        File file2 = new File(tempDir.toFile(), "S2.json");

        assertTrue(file1.exists(), "File for student 1 should exist");
        assertTrue(file2.exists(), "File for student 2 should exist");

        // Verify the content of the JSON files for student 1
        String content1 = new String(Files.readAllBytes(file1.toPath()));
        JSONObject json1 = new JSONObject(content1);
        assertEquals("S1", json1.getString("id"));
        assertEquals("John Doe", json1.getString("name"));
        assertEquals("john.doe@example.com", json1.getString("eMail"));
        assertEquals("A1", json1.getString("AdvisorId"));
        assertEquals("password123", json1.getString("password"));

        // Verify the courses array for student 1
        JSONArray courses1 = json1.getJSONArray("CourseTaken");
        assertEquals(2, courses1.length(), "Student 1 should have 2 courses");
        assertEquals("C1", courses1.getJSONObject(0).keys().next());
        assertEquals("A", courses1.getJSONObject(0).getString("C1"));
        assertEquals("C2", courses1.getJSONObject(1).keys().next());
        assertEquals("B", courses1.getJSONObject(1).getString("C2"));

        // Verify the content of the JSON files for student 2
        String content2 = new String(Files.readAllBytes(file2.toPath()));
        JSONObject json2 = new JSONObject(content2);
        assertEquals("S2", json2.getString("id"));
        assertEquals("Jane Smith", json2.getString("name"));
        assertEquals("jane.smith@example.com", json2.getString("eMail"));
        assertEquals("A2", json2.getString("AdvisorId"));
        assertEquals("password456", json2.getString("password"));

        // Verify the courses array for student 2
        JSONArray courses2 = json2.getJSONArray("CourseTaken");
        assertEquals(1, courses2.length(), "Student 2 should have 1 course");
        assertEquals("C1", courses2.getJSONObject(0).keys().next());
        assertEquals("B", courses2.getJSONObject(0).getString("C1"));
    }

    // Test case where no students are present (no files should be created)
    @Test
    void testNoStudents(@TempDir Path tempDir) {
        StudentService studentService = new StudentService();

        // Temporarily override the file writing location (for testing purposes)
        System.setProperty("user.dir", tempDir.toString());

        // Call the method to update student JSON files
        studentService.updateStudentJSON();

        // Verify that no files are created since there are no students
        File[] files = tempDir.toFile().listFiles();
        assertNotNull(files, "Files array should not be null");
        assertEquals(0, files.length, "No files should be created when there are no students");
    }
}

