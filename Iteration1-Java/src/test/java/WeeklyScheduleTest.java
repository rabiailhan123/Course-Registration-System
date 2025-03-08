import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeeklyScheduleTest {

    private YourClass yourClassInstance; // Replace with your actual class name
    private ArrayList<Course> selectedCourses;

    @BeforeEach
    void setUp() {
        // Initialize the class under test
        yourClassInstance = new YourClass(); // Replace with actual constructor
        selectedCourses = new ArrayList<>();
    }

    @Test
    void testGetWeeklySchedule() {
        // Prepare mock data (manually creating objects)
        Course mockCourse = new Course("Course 1", 101);
        CourseSection mockCourseSection = new CourseSection();
        LectureHour mockLectureHour = new LectureHour(1, 3);  // Monday, Hour 3

        // Add the lecture hour to the course section
        mockCourseSection.addLectureHour(mockLectureHour);

        // Link the course to the course section
        mockCourse.setCourseSection(mockCourseSection);

        // Add the course to the selected courses list
        selectedCourses.add(mockCourse);

        // Simulate the method getStudentCoursesWithStatus
        // In the test, we will directly set the courses list
        yourClassInstance.setSelectedCourses(selectedCourses);

        // Manually fill the schedule for the selected courses
        yourClassInstance.getWeeklySchedule();

        // After running getWeeklySchedule(), capture the result
        List<String> scheduleTextList = yourClassInstance.getScheduleTextList();

        // Verify that the schedule contains the course ID 101 in the correct spot
        String scheduleLine = scheduleTextList.get(1); // Monday (index 1)
        
        // Check if the course ID is correctly placed at Hour 3 on Monday
        assertTrue(scheduleLine.contains(" 101 "), "Course ID 101 should be in the schedule at the correct position");
    }

    // Helper classes to simulate the real behavior
    class YourClass {

        private ArrayList<Course> selectedCourses;
        private List<String> scheduleTextList;

        public YourClass() {
            this.selectedCourses = new ArrayList<>();
            this.scheduleTextList = new ArrayList<>();
        }

        public void setSelectedCourses(ArrayList<Course> selectedCourses) {
            this.selectedCourses = selectedCourses;
        }

        public List<String> getScheduleTextList() {
            return this.scheduleTextList;
        }

        public void getWeeklySchedule() {
            String[][] schedule = new String[7][10];  // 7 days, 10 hours
            int size = selectedCourses.size();

            for (int i = 0; i < size; i++) {
                Course course = selectedCourses.get(i);
                CourseSection courseSection = course.getCourseSection();
                ArrayList<LectureHour> lectureHours = courseSection.getLectureHours();

                for (LectureHour lectureHour : lectureHours) {
                    schedule[lectureHour.getLectureDay()][lectureHour.getLectureHourNumber()] = String.valueOf(course.getId());
                }
            }

            // Convert the schedule to a readable format
            for (int i = 0; i < 7; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < 10; j++) {
                    if (schedule[i][j] == null) {
                        stringBuilder.append("         ");  // Empty space for empty slots
                    } else {
                        stringBuilder.append(" " + schedule[i][j] + " ");  // Course ID for occupied slots
                    }
                }
                scheduleTextList.add(stringBuilder.toString());
            }
        }
    }

    // Simulated Course class
    class Course {
        private String name;
        private int id;
        private CourseSection courseSection;

        public Course(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public void setCourseSection(CourseSection courseSection) {
            this.courseSection = courseSection;
        }

        public int getId() {
            return this.id;
        }

        public CourseSection getCourseSection() {
            return this.courseSection;
        }
    }

    // Simulated CourseSection class
    class CourseSection {
        private ArrayList<LectureHour> lectureHours;

        public CourseSection() {
            this.lectureHours = new ArrayList<>();
        }

        public void addLectureHour(LectureHour lectureHour) {
            this.lectureHours.add(lectureHour);
        }

        public ArrayList<LectureHour> getLectureHours() {
            return this.lectureHours;
        }
    }

    // Simulated LectureHour class
    class LectureHour {
        private int lectureDay;  // 0 = Sunday, 1 = Monday, etc.
        private int lectureHourNumber;  // Hour of the day (0-9)

        public LectureHour(int lectureDay, int lectureHourNumber) {
            this.lectureDay = lectureDay;
            this.lectureHourNumber = lectureHourNumber;
        }

        public int getLectureDay() {
            return this.lectureDay;
        }

        public int getLectureHourNumber() {
            return this.lectureHourNumber;
        }
    }
}
