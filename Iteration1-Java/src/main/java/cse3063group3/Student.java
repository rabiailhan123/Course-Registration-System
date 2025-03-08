package cse3063group3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.*;
import java.util.Arrays;

public class Student extends Person {

    final int MAX_COURSES = 5; // add this to the uml
    private HashMap<CourseSection, String> courseSectionsOfStudent; // this will be map
    //private HashMap<Course, Integer> selectedSections = new HashMap<>();    // TODO: Depriciated delete later.
    private Advisor advisor;


    /**
     * 
     * @param id                 ID of the student.
     * @param name               Name of the student.
     * @param eMail              E-Mail address of the student.
     * @param password           Password of the student.
     * @param controllerInstance Referance to the controller instance.
     */
    public Student(int id, String name, String eMail, String password) {
        super(id, name, eMail, password);
        this.courseSectionsOfStudent = new HashMap<CourseSection, String>();
        this.advisor = null;
    }

    // add the course and set it's status to selected
    // called by the
    public void addCourseSection(CourseSection courseSec) {
        int previouslyChosenCourseCount = this.getStudentCoursesWithStatus("pending").size()
                + this.getStudentCoursesWithStatus("selected").size()
                + this.getStudentCoursesWithStatus("approved").size()
                + this.getStudentCoursesWithStatus("waiting").size();

        if (previouslyChosenCourseCount < MAX_COURSES) {
            if (this.CheckScheduleAvailable(courseSec)){
                this.courseSectionsOfStudent.put(courseSec, "selected");
                // TODO: Also add student to coursesections students list.
            }
        }
    }

    public void changeStatus(CourseSection courseSection, String status) {
        if (status.equals("selected"))
            addCourseSection(courseSection);
        this.courseSectionsOfStudent.replace(courseSection, status);
    }


    /**
     * <b>Probably Depriciated IDK!</b> <p>
     * Finds all courses from the current student instance that match the given
     * argument status and returns it as an ArrayList.
     * 
     * @param status String to match course status against.
     * @return A list of courses that match with the given status string.
     *         <p>
     *         If "status" is null or left blank, returns all courses.
     */
    private ArrayList<Course> getStudentCoursesWithStatus(String status) {
        ArrayList<CourseSection> courseSectionsList = new ArrayList<>();
        ArrayList<Course> coursesList = new ArrayList<>();

        if (status == null || status.isBlank()) {
            courseSectionsList.addAll(this.courseSectionsOfStudent.keySet());
            courseSectionsList.forEach((courseSection) -> {coursesList.add(courseSection.getCourse());});
            return coursesList;
        }

        for (Map.Entry<CourseSection, String> entry : this.courseSectionsOfStudent.entrySet()) {
            if (entry.getValue().equals(status)){
                coursesList.add(entry.getKey().getCourse());
            }
        }
        return coursesList;
    }

    /**
     * Finds all course sections from the current student instance that match the given
     * argument status and returns it as an ArrayList.
     * 
     * @param status String to match course section status against.
     * @return A list of course sections that match with the given status string.
     *         <p>
     *         If "status" is null or left blank, returns all course sections.
     */
    private ArrayList<CourseSection> getStudentCourseSectionsWithStatus(String status) {
        ArrayList<CourseSection> courseSectionsList = new ArrayList<>();

        if (status == null || status.isBlank()) {
            courseSectionsList.addAll(this.courseSectionsOfStudent.keySet());
            return courseSectionsList;
        }

        for (Map.Entry<CourseSection, String> entry : this.courseSectionsOfStudent.entrySet()) {
            if (entry.getValue().equals(status)){
                courseSectionsList.add(entry.getKey());
            }
        }
        return courseSectionsList;
    }

    /**
     * <pre>
     * Tries to print a numbered list of courses taken by the student that match given status.
     * </pre>
     * 
     * @param status String to match course status against.
     *               <p>
     *               If left empty prints all courses taken by the student without
     *               matching any status.
     */
    public void listStudentCoursesWithStatus(String status) {
        ArrayList<String> prefixText = new ArrayList<>();
        if (status == null) {
            status = "";
        }

        ArrayList<CourseSection> courseSectionsWithStatusList = getStudentCourseSectionsWithStatus(status);

        if (status.isBlank()) {
            prefixText.add("All courses taken by " + this.getName() + ".\n");
        } else {
            // Ex: Barış Sedefoğlu's finished courses.
            prefixText.add(this.getName() + "'s " + status + " courses.\n");
        }

        if (courseSectionsWithStatusList == null || courseSectionsWithStatusList.isEmpty()) {
            // Ex: Student: Barış Sedefoğlu with ID: 150120002 currently doesn't have any
            // courses.
            prefixText.add("Student: " + this.getName() + " with ID: " + this.getId()
                    + " currently doesn't have any " + status + " courses.\n");
        } else {
            for (int i = 0; i < courseSectionsWithStatusList.size(); i++) {
                // Ex: 3- Object Oriented Software Design, Status: taken, Credits: 5, Course ID: 105
                prefixText.add((i + 1) + "- " + courseSectionsWithStatusList.get(i).getCourse().getName() + "." + courseSectionsWithStatusList.get(i).getCourseSectionNumber() 
                                + ", Status: " + this.courseSectionsOfStudent.get(courseSectionsWithStatusList.get(i))
                                + ", Credits: " + courseSectionsWithStatusList.get(i).getCourse().getCredits()
                                + ", Course ID: " + courseSectionsWithStatusList.get(i).getCourse().getId()
                                + "\n");
            }
        }
        this.getControllerInstance().menuOperation(prefixText);
    }

    @Override
    public void mainMenu() {
        ArrayList<String> prefixText = new ArrayList<>(Arrays.asList("\n\nStudent system.\n",
                "Available options:\n"));

        ArrayList<String> optionsText = new ArrayList<>(Arrays.asList("List offered courses in this semester.",
                "List pending courses.",
                "List approved courses.",
                "List finished courses.",
                "List selected courses.",
                "Select Courses.",
                "Send selected courses for advisor approval.",
                "Show weekly schedule",
                "Exit.\n"));

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));
        // Loop inside the student menu until exit is chosen.
        while (true) {
            // Send the main menu body to be printed and get an option from user.
            int choice = this.getControllerInstance().menuOperation(prefixText, optionsText, suffixText);

            if (choice == optionsText.size() - 1) {
                this.getControllerInstance()
                .menuOperation(new ArrayList<>(Arrays.asList("You are leaving the system...\n")));
                break;
                // student exit the system
            }

            switch (choice) {
                case 0 -> // "List offered courses in this semester."
                    // Prints with course status information for current student.
                    this.getControllerInstance().printAllDepartmentCourses(this.courseSectionsOfStudent);
                case 1 -> // "List pending courses."
                    listStudentCoursesWithStatus("pending");
                case 2 -> // "List approved courses."
                    listStudentCoursesWithStatus("approved");
                case 3 -> // "List finished courses."
                    listStudentCoursesWithStatus("finished");
                case 4 -> // "List selected courses."
                    listStudentCoursesWithStatus("selected");
                case 5 -> // "Select Courses."
                    selectCourseSectionsForSemester();
                case 6 -> {
                    // "Send selected courses to advisor approval."
                    this.getControllerInstance()
                            .menuOperation(new ArrayList<>(Arrays.asList("All selected courses are sended to advisor approval.\n")));
                    this.courseSectionsOfStudent.replaceAll((course, status) -> status.equals("selected") ? "pending" : status);
                }
                case 7 -> this.printWeeklySchedule();
                default ->//TODO: burada try-catch kullanılmalı, mesela faladan enter basarsa kişi bu mesajı görmemeli
                    this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("Error: This shouldn't happen. StudentSystem.studentSystem()")));
            }
        }
    }

    /**
     * <pre>
     * Numbers and prints all of the available courses in the department for course selection.
     * Upon selection(s) does course prerequisite checks and tries to add each selected course to student's courses HashMap.
     * </pre>
     */
    private void selectCourseSectionsForSemester() {
        int previouslyChosenCourseCount = this.getStudentCoursesWithStatus("pending").size()
                + this.getStudentCoursesWithStatus("selected").size()
                + this.getStudentCoursesWithStatus("approved").size()
                + this.getStudentCoursesWithStatus("waiting").size();
                
        while (true) {
            int sectionSelection = 1;
            Course chosenCourse = this.getControllerInstance().selectCourseFromAllDepartmentCourses(/*this.getName(),*/ this.getAllCourses());
            //Chose exit.
            if (chosenCourse == null) {
                break;
            }

            if (this.MAX_COURSES <= previouslyChosenCourseCount + 1) {
                this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("Course is not selected because at most " + MAX_COURSES + " courses can be selected in a semester.")));
                break;
            }

            if (getAllCourses().containsKey(chosenCourse)) {
                // TODO: statüye göre mesaj yazdırılabilir
                this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList(chosenCourse.getName() + " is already in your courses. Please choose another course.\n")));
            } else {
                // check if prerequisites are met
                if (chosenCourse.CheckPrerequisitesMet(this.getStudentCoursesWithStatus("finished"), this)) {
                    if (chosenCourse.getCourseSections().size() > 1) {
                        this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("Please select a section.")));
                        for (int j = 0; j < chosenCourse.getCourseSections().size() - 1; j++) {
                            this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList((j + 1) + "- " + chosenCourse.getName() + "." + (j + 1))));
                        }
                        sectionSelection = this.getControllerInstance().getNextInt();
                    }

                    courseSectionsOfStudent.put(chosenCourse.getCourseSections().get(sectionSelection - 1), "selected");
                    previouslyChosenCourseCount++;
                } else {
                    // TODO: buraya gerek yok. Course classının içinde yazdırıyoruz zaten
                    // System.out
                    // .println(chosenCourse.getName() + " is not selected beacuse not all
                    // prereguisites are met");
                }
            }
        }
    }

    public void setCourseSectionStatusToApprovedOrRejected() {
        ArrayList<CourseSection> pendingCourseSections;

        while (true) {
            pendingCourseSections = this.getStudentCourseSectionsWithStatus("pending");
            this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("\n\n")));
            this.listStudentCoursesWithStatus("pending");  // This isn't really meant as an input method so it doesn't print the exit option.
            this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList((pendingCourseSections.size() + 1) + "- Exit.\n\n", "Please select a course section: ")));
            
            int result = this.getControllerInstance().getNextInt() - 1;
            if (result >= 0 && result < pendingCourseSections.size()) {
                ArrayList<String> prefixText = new ArrayList<>(
                        Arrays.asList("\n\nApprove - Reject " + pendingCourseSections.get(result).getName() + "\n"));

                ArrayList<String> optionsText = new ArrayList<>(Arrays.asList("Approve.",
                        "Reject",
                        "Skip\n"));

                ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));
                while (true) {
                    int option = this.getControllerInstance().menuOperation(prefixText, optionsText, suffixText);

                    if (option == 0) {
                        this.changeStatus(pendingCourseSections.get(result), "approved");
                        // TODO: Add student object to course / coursesection.
                        break;
                    }
                    if (option == 1) {
                        this.changeStatus(pendingCourseSections.get(result), "rejected");
                        break;
                    } 
                    if (option == 2) {
                        break;
                    }

                    //Exhausted all possibilities. The choice is invalid.
                    this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("Please choose a valid option.\n")));

                }
            } else {
                if (result < 0)
                    this.getControllerInstance().menuOperation(new ArrayList<>(Arrays.asList("Please choose a valid option.\n")));
                if (result == pendingCourseSections.size()) {
                    break;
                }
            }
        }
    }

    /**
     * 
     * @param courseSection chekcs against this
     * @return
     */
    public boolean CheckScheduleAvailable(CourseSection courseSectionToCheckAgainst) {

        ArrayList<CourseSection> takenCourseSectionList = new ArrayList<>();
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("selected"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("pending"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("approved"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("waiting"));
        // TODO: course status will change to waiting after selection when course is full.
        ArrayList<Integer> schedule = new ArrayList<Integer>();

        for (CourseSection courseSection : takenCourseSectionList) {
            ArrayList<LectureHour> lectureHours = courseSection.getLectureHours();
            for (LectureHour lectureHour : lectureHours) {
                schedule.add(lectureHour.getLectureTimeTableNumber());
            }
        }
        
        ArrayList<Integer> newCourseHours = new ArrayList<Integer>();
        for(LectureHour lectureHour : courseSectionToCheckAgainst.getLectureHours()){
            newCourseHours.add(lectureHour.getLectureTimeTableNumber());
        }

        if(schedule.stream().anyMatch(newCourseHours::contains)){
            return false;
        }


        return true;
    }
    
    public void printWeeklySchedule() {

        ArrayList<CourseSection> takenCourseSectionList = new ArrayList<>();
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("selected"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("pending"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("approved"));
        takenCourseSectionList.addAll(getStudentCourseSectionsWithStatus("waiting"));
        // TODO: course status will change to waiting after selection when course is full.
        String[][] schedule = new String[7][10];
        int size = takenCourseSectionList.size();
        for (int i = 0; i < size; i++) {
            ArrayList<LectureHour> lectureHours = takenCourseSectionList.get(i).getLectureHours();
            for (int j = 0; j < lectureHours.size(); j++) {
                // first dimansion of array takes lecture day as index, second takes lecture
                // hour
                schedule[lectureHours.get(j).getLectureDay()][lectureHours.get(j).getLectureHourNumber()] = 
                    /*takenCourse.get(j).getName();*/ takenCourseSectionList.get(i).getCourse().getId() + "." + takenCourseSectionList.get(i).getCourseSectionNumber() + "";
                // TODO: ^ I highly doubt you can fit the course name in the schedule.
                // 'Data Structures and Algorithms' will easily mess up the entire formatting.
                // Changing to getID. If getName was intentional. Just uncomment. Barış
            }
        }

        ArrayList<String> scheduleTextList = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                if (schedule[i][j] == null) {
                    stringBuilder.append("     -     ");
                    // Adding continue here otherwise later on schedule[i][j] will print "null" in the schedule.
                    // I don't think this is intentional. If it was, just delete this continue. Barış
                    continue;
                }
                stringBuilder.append(" " + schedule[i][j] + " ");   // The last spacer was 3 whitespaces, changed to 1 otherwise it wont match the above spacer.
                                                                    // "         " has nine; courseId has 7 so it can be padded 1 space on each side.
                                                                    // Undo if it was intentional. Barış.
            }
            stringBuilder.append("\n");
            scheduleTextList.add(stringBuilder.toString());
        }
        this.getControllerInstance().menuOperation(scheduleTextList);
    }






    /**
     * <b>Probably Depriciated IDK!</b> <p>
     * @return Returns a HashMap<Course, String> containing the Course - Course Status relations.
     */
    public HashMap<Course, String> getAllCourses() {
        var coursesMap = new HashMap<Course, String>();
        this.courseSectionsOfStudent.forEach((a, b) -> {coursesMap.put(a.getCourse(), b); });

        return coursesMap;
    }

    /**
     * @return Returns the Student.courseSectionsofStudent field which contains the CourseSection - CourseSection Status relations.
     */
    public HashMap<CourseSection, String> getAllCourseSections() {
        return this.courseSectionsOfStudent;
    }

    public Advisor getAdvisor() {
        return (this.advisor);
    }

    @Override
    public int getId() {
        return (super.getId());
    }

    @Override
    public String getName() {
        return (super.getName());
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }

    public JSONObject toJSON() {
        return null;
    };

    public void setCourseSectionStatus(CourseSection courseSection, String status) {
        this.courseSectionsOfStudent.put(courseSection, status);
    }

    public boolean hasPendingCourse() {
        return this.courseSectionsOfStudent.containsValue("pending");
    }
}