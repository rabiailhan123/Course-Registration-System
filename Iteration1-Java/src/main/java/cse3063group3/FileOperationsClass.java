package cse3063group3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.*;

public class FileOperationsClass {
    public HashMap<Integer, Course> courseMap = new HashMap<>();
    private Map<Integer, Advisor> advisorMap = new HashMap<>();
    private Map<Integer, Student> studentMap = new HashMap<>();
    private ArrayList<Person> people = new ArrayList<>();
    ArrayList<Classroom> classrooms = new ArrayList<>();
    final private CourseRegistrationSystem controllerInstance;

    public FileOperationsClass() {
        this.controllerInstance = CourseRegistrationSystem.getCourseRegistrationSystemInstance();
    }

    public Person loginCheck(int userID, String password) {
        for (int i = 0; i < this.people.size(); i++) {
            if (this.people.get(i) == null)
                continue;

            if (userID == this.people.get(i).getId()) {
                if (this.people.get(i).checkPassword(password)) {
                    return this.people.get(i);
                } else {
                    return null;
                }
            } else {
                continue;
            }
        }
        return null;
    }

    public void readJson(String dirPath) {
        String StudentDirPath = dirPath + "/Student";
        String classroomDirPath = dirPath + "/Classroom";
        String CourseDirPath = dirPath + "/Course";
        String AdvisorDirPath = dirPath + "/Advisor";
        String DepartmentSchedulardirPath = dirPath + "/DepartmentScheduler";
        readClassroomJSON(classroomDirPath);
        readCourseJSON(CourseDirPath);
        readStudentJSON(StudentDirPath);
        readAdvisorJSON(AdvisorDirPath);
        readDepartmentScheduler(DepartmentSchedulardirPath);
        mapCoursesAndAdvisorToStudent(StudentDirPath, studentMap, courseMap, advisorMap);
        // mapPrerequsiteToCourse(CourseDirPath, courseMap);
        mapPrerequsiteToCourse(CourseDirPath, courseMap);
        people.addAll(new ArrayList<Person>(advisorMap.values()));
        people.addAll(studentMap.values());
    }

    private void mapCoursesAndAdvisorToStudent(String studentDirPath, Map<Integer, Student> studentMap,
            Map<Integer, Course> CourseMap, Map<Integer, Advisor> advisorMap) {
        File[] studenFiles = takeJsonFiles(studentDirPath);
        for (File file : studenFiles) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file)); // No possibility of // files.
                String line;
                StringBuilder fileContent = new StringBuilder();
                try {
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line);
                    }
                } catch (IOException e) {
                    // TODO send this to a logger.
                    System.err.println(e.getMessage());
                }
                JSONObject jObject = new JSONObject(fileContent.toString());
                int studentId = jObject.getInt("id");
                Student student = (Student) studentMap.get(studentId);
                // TODO: Add coursection numbering.
                JSONArray courseTaken = jObject.getJSONArray("CourseSectionsTaken");
                for (int j = 0; j < courseTaken.length(); j++) {
                    JSONArray row = courseTaken.getJSONArray(j);
                    int courseId = row.getInt(0);
                    String courseStatus = row.getString(1);
                    Course course = courseMap.get(courseId);

                    for (CourseSection courseSection : course.getCourseSections()) {
                        if (courseSection.getCourseSectionNumber() == row.getInt(2)) {
                            // add course to student
                            student.setCourseSectionStatus(courseSection, courseStatus);
                            // TODO: Depriciated!
                            // add student to course
                            // course.addStudent(student);

                            courseSection.addStudent(student);
                            break;
                        }
                    }

                }

                Advisor advisor = (Advisor) advisorMap.get(jObject.getInt("AdvisorId"));
                student.setAdvisor(advisor);
                advisor.addAdvisee(student);
            }

            catch (FileNotFoundException e) {
                // TODO send this to a logger.
                System.err.println("\n\n----------------------------------");
                System.err.println("Shoul never happen.\n");
                System.err.println(e.getMessage());
                System.err.println("----------------------------------\n\n");
            }
        }
    }

    private void readDepartmentSchucler(String dirPath) {
        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        // TODO send this to a logger.
                        System.err.println(e.getMessage());
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());
                    DepartmentScheduler departmentSchedular = new DepartmentScheduler(jObject.getInt("id"),
                            jObject.getString("name"), jObject.getString("eMail"), jObject.getString("password"),
                            jObject.getString("departmentName"));
                    this.people.add(departmentSchedular);
                } catch (FileNotFoundException e) {
                    // shouldnt happend
                }
            }
        }
    }

    private void mapPrerequsiteToCourse(String courseDirPath, Map<Integer, Course> courseMap) {

        File[] CourseFiles = takeJsonFiles(courseDirPath);
        for (File file : CourseFiles) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file)); // No possibility of
                                                                                  // filenotfoundexception. file is
                                                                                  // returned from a list of known
                                                                                  // files.
                String line;
                StringBuilder fileContent = new StringBuilder();

                try {
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line);
                    }
                } catch (IOException e) {
                    // TODO send this to a logger.
                    System.err.println(e.getMessage());
                }

                JSONObject jObject = new JSONObject(fileContent.toString());
                int courseId = jObject.getInt("id");
                Course course = (Course) courseMap.get(courseId);
                JSONArray prerequisites = jObject.getJSONArray("prerequisite");
                for (int j = 0; j < prerequisites.length(); j++) {
                    int course_Id = prerequisites.getInt(j);
                    Course prerequisite = courseMap.get(course_Id);

                    course.setPrerequisites(prerequisite);
                }
            }

            catch (FileNotFoundException e) {
                // TODO send this to a logger.
                System.err.println("\n\n----------------------------------");
                System.err.println("Should never happen.\n");
                System.err.println(e.getMessage());
                System.err.println("----------------------------------\n\n");
            }
        }
    }

    private void readClassroomJSON(String dirPath) {
        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        // TODO send this to a logger.
                        System.err.println(e.getMessage());
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());
                    Classroom classroom = new Classroom(jObject.getString("classroomID"),
                            jObject.getInt("classroomSeatingCapacity"), controllerInstance);
                    classrooms.add(classroom);
                } catch (FileNotFoundException e) {
                    // not expected
                }
            }
        }
    }

    private void readDepartmentScheduler(String dirPath) {
        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        this.controllerInstance.menuOperation(new ArrayList<String>(Arrays.asList(e.getMessage())));
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());
                    DepartmentScheduler departmentScheduler = new DepartmentScheduler(jObject.getInt("id"),
                            jObject.getString("name"), jObject.getString("eMail"), jObject.getString("password"),
                            jObject.getString("departmentName"));
                    this.people.add(departmentScheduler);
                } catch (FileNotFoundException e) {
                    // shouldnt happend
                }
            }
        }
    }

    // TODO read more data for course
    // TODO: Actually read courseSections from Course jsons. Skipping for now.
    private void readCourseJSON(String dirPath) {

        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        // TODO send this to a logger.
                        System.err.println(e.getMessage());
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());
                    JSONArray courseSections = jObject.getJSONArray("courseSection");
                    int creditsRequired = jObject.getInt("creditsRequired");
                    Course course = null;
                    if (creditsRequired == 0) {
                        // parsering the json to a course object
                        course = new Course(jObject.getInt("id"), jObject.getString("name"), jObject.getInt("credits"));
                    } else {
                        course = new TECourse(jObject.getInt("id"), jObject.getString("name"),
                                jObject.getInt("credits"), creditsRequired);
                    }
                    for (int i = 0; i < courseSections.length(); i++) {
                        JSONObject object = courseSections.getJSONObject(i);
                        ArrayList<LectureHour> lectureHours = new ArrayList<>();

                        JSONArray lectureHour = object.getJSONArray("lectureHour");
                        for (int k = 0; k < lectureHour.length(); k++) {
                            JSONObject ob = lectureHour.getJSONObject(k);
                            LectureHour lectureH = new LectureHour(ob.getInt("lectureDay"),
                                    ob.getInt("lectureHourNumber"));
                            // to assign the classroom to lecture hour by searching with ıd
                            for (int j = 0; j < classrooms.size(); j++) {
                                if (ob.getString("classroomID").equals(classrooms.get(j).getclassroomID())) {
                                    lectureH.setClassroom(classrooms.get(j));
                                }
                            }
                            lectureHours.add(lectureH);
                        }
                        CourseSection courseSection = new CourseSection(lectureHours, object.getString("lecturer"),
                                object.getInt("courseSectionNumber"), object.getInt("courseCapacity"), course);
                        course.addCourseSection(courseSection);

                    }

                    // put course to map so it can be addable to other objects
                    courseMap.put(course.getId(), course);
                } catch (FileNotFoundException e) {
                    // TODO send this to a logger.
                    System.err.println("\n\n----------------------------------");
                    System.err.println("Shoul never happen.\n");
                    System.err.println(e.getMessage());
                    System.err.println("----------------------------------\n\n");
                }
            }
        }
    }

    private void readAdvisorJSON(String dirPath) {
        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        // TODO send this to a logger.
                        System.err.println(e.getMessage());
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());

                    // parsering the json to a advisor object
                    Advisor advisor = new Advisor(jObject.getInt("id"), jObject.getString("name"),
                            jObject.getString("eMail"), jObject.getString("password"), new ArrayList<Student>());

                    advisorMap.put(advisor.getId(), advisor);

                } catch (FileNotFoundException e) {
                    // TODO send this to a logger.
                    System.err.println("\n\n----------------------------------");
                    System.err.println("Shoul never happen.\n");
                    System.err.println(e.getMessage());
                    System.err.println("----------------------------------\n\n");
                }
            }
        }
    }

    private void readStudentJSON(String dirPath) {
        File[] files = takeJsonFiles(dirPath);
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder fileContent = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }
                    } catch (IOException e) {
                        // TODO send this to a logger.
                        System.err.println(e.getMessage());
                    }
                    JSONObject jObject = new JSONObject(fileContent.toString());
                    // parsing the json to a advsor object
                    Student student = new Student(jObject.getInt("id"), jObject.getString("name"),
                            jObject.getString("eMail"), jObject.getString("password"));
                    studentMap.put(student.getId(), student);
                } catch (FileNotFoundException e) {
                    // TODO send this to a logger.
                    System.err.println("\n\n----------------------------------");
                    System.err.println("Shoul never happen.\n");
                    System.err.println(e.getMessage());
                    System.err.println("----------------------------------\n\n");
                }
            }
        }
    }

    // to take all json files in a given directory
    private File[] takeJsonFiles(String dirPath) {
        File directory = new File(dirPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        return files;
    }

    public void updateJSON() {
        updateStudentJSON();
        updateAdvisorJSON();
    }

    private void updateStudentJSON() {
        List<Student> students = new ArrayList<>(studentMap.values());
        for (int i = 0; i < students.size(); i++) {
            JSONObject student = new JSONObject();
            student.put("id", students.get(i).getId());
            student.put("name", students.get(i).getName());
            student.put("eMail", students.get(i).getEMail());
            student.put("AdvisorId", students.get(i).getAdvisor().getId());
            student.put("password", students.get(i).getPassword());

            JSONArray courseTaken = new JSONArray();
            for (Course course : new ArrayList<>(students.get(i).getAllCourses().keySet())) {
                JSONArray courses = new JSONArray();
                courses.put(course.getId() + "");
                courses.put(students.get(i).getAllCourses().get(course));
                courseTaken.put(courses);
            }
            student.put("CourseTaken", courseTaken);
            try (FileWriter file = new FileWriter("./JSON/Student/" + students.get(i).getId() + ".json")) {
                file.write(student.toString(4));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAdvisorJSON() {
        List<Advisor> advisors = new ArrayList<>(advisorMap.values());
        for (int i = 0; i < advisors.size(); i++) {
            JSONObject advisor = new JSONObject();
            advisor.put("id", advisors.get(i).getId());
            advisor.put("name", advisors.get(i).getName());
            advisor.put("eMail", advisors.get(i).getEMail());
            advisor.put("password", advisors.get(i).getPassword());

            try (FileWriter file = new FileWriter("./JSON/Advisor/" + advisors.get(i).getId() + ".json")) {
                file.write(advisor.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
