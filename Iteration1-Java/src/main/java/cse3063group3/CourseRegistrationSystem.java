package cse3063group3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CourseRegistrationSystem {
    // TODO: actually make CourseRegistrationSystem singleton and don't pass
    // CourseRegistrationSystem instance, instead call new CourseRegistrationSystem

    final private Scanner inputScanner;
    private static CourseRegistrationSystem instance = null;
    private ArrayList<Course> allDepartmentCoursesList;
    private ArrayList<Classroom> allClassrooms;

    private CourseRegistrationSystem() {
        this.inputScanner = new Scanner(System.in);
    }

    public static CourseRegistrationSystem getCourseRegistrationSystemInstance() {
        if (CourseRegistrationSystem.instance == null) {
            CourseRegistrationSystem.instance = new CourseRegistrationSystem();
        }
        return CourseRegistrationSystem.instance;
    }

    public static void main(String[] args) {
        CourseRegistrationSystem controllerInstance = CourseRegistrationSystem.getCourseRegistrationSystemInstance();

        FileOperationsClass fo = new FileOperationsClass();
        fo.readJson("JSON");
        controllerInstance.allClassrooms = fo.classrooms;
        controllerInstance.allDepartmentCoursesList = new ArrayList<>(fo.courseMap.values());
        int userId = 0;
        String password = "";

        while (true) {
            controllerInstance.updateAllClassroomsSchedules();
            System.out.println("Course Registration System Login Menu");

            System.out.println("Please enter username.");
            userId = controllerInstance.getNextInt();
            System.out.println("Please enter password.");
            password = controllerInstance.getNextLine();

            Person user = fo.loginCheck(userId, password);

            if (user != null) {
                // --------------------------------------------
                // Currently only implemented for Student class. And not fully at that.
                // Use that as an example when implementing to other classes.
                user.mainMenu();

            } else {
                System.out.println("Username or password is wrong press any key to continue or");
                System.out.println("please enter \'exit\' to exit the system.");
                String input = controllerInstance.getNextLine();
                if (input.endsWith("exit")) {
                    fo.updateJSON();
                    break;
                }
            }
            System.out.println("\n\n\n\n\n\n\n\n\n\n");

        }

    }

    public void selectAndIncreaseCourseSectionCapacity(){
        ArrayList<String> prefixText = new ArrayList<>();
        ArrayList<String> suffixText = new ArrayList<>();
        ArrayList<String> courseSectionListText = new ArrayList<>();
        ArrayList<CourseSection> courseSectionsList = new ArrayList<>();

        prefixText.add("Course Sections: \n");
        suffixText.add("Choose a course section: ");
        for(Course course : this.allDepartmentCoursesList){
            for(CourseSection courseSection : course.getCourseSections()){
                courseSectionListText.add(courseSection.getName());
                courseSectionsList.add(courseSection);
            }
        }
        courseSectionListText.add("Exit.");
        int choice = this.menuOperation(prefixText, courseSectionListText, suffixText);
        if(choice == courseSectionListText.size()){
            return;
        }

        System.out.println("Current course section capacity: " + courseSectionsList.get(choice).getCourseCapacity() + "\n");
        System.out.println("Increase to: ");
        
        int newCapacity = this.getNextInt();

        courseSectionsList.get(choice).incrementCapacity(newCapacity);

    }



    public void updateAllClassroomsSchedules(){
        for(Classroom classroom : this.allClassrooms){
            this.updateClassroomSchedule(classroom);
        }
    }
    public void updateClassroomSchedule(Classroom classroom){
        for(Course course : this.allDepartmentCoursesList){
            for (CourseSection courseSection : course.getCourseSections()){
                for(LectureHour lectureHour : courseSection.getLectureHours()){
                    if(classroom == lectureHour.getClassroom()){
                        classroom.setTimeTableMap(lectureHour.getLectureTimeTableNumber(), courseSection);
                        System.out.println(lectureHour.getLectureTimeTableNumber());
                    }
                }
            }
        }
        System.out.println("");
    }
    /**
     * <pre>
     * Prints all courses in the department with the courses from studentCoursesMap.
     * </pre>
     * 
     * @param studentCoursesMap HashMap to pull the course statuses from.
     */
    public void printAllDepartmentCourses(HashMap<CourseSection, String> studentCourseSectionsMap) {
        ArrayList<String> courseListText = new ArrayList<>();

        for (Course course : this.allDepartmentCoursesList) {
            ArrayList<CourseSection> courseSectionsOfDepartmentCourses = course.getCourseSections();
            String status = "Not Taken";
            for (CourseSection cs : courseSectionsOfDepartmentCourses) {
                if (studentCourseSectionsMap.get(cs) != null) {
                    status = studentCourseSectionsMap.get(cs);
                    break;
                }
            }
            courseListText.add(course.getName() + ", Status: " + status + ", Credits: " + course.getCredits()
                    + ", Course ID: " + course.getId() + "\n");
        }
        menuOperation(courseListText);
    }

    /**
     * <pre>
     * Prints all courses in the department.
     * </pre>
     */
    public void printAllDepartmentCourses() {
        ArrayList<String> courseListText = new ArrayList<>();

        for (Course course : allDepartmentCoursesList) {
            courseListText.add(
                    course.getName() + ", Credits: " + course.getCredits() + ", Course ID: " + course.getId() + "\n");
        }
        menuOperation(courseListText);
    }

    public void printAllClassroomsWithCapacities() {
        ArrayList<String> classroomListText = new ArrayList<>();

        int i = 0;
        for (Classroom classroom : this.allClassrooms) {
            classroomListText.add(
                    ++i + "- " + classroom.getclassroomID() + ", " + classroom.getclassroomSeatingCapacity() + "\n");
        }
        menuOperation(classroomListText);
    }

    public void printAllClasslessLectureHours(){
        for(Course course : this.allDepartmentCoursesList){
            for(CourseSection courseSection : course.getCourseSections()){
                for(LectureHour lectureHour : courseSection.getLectureHours()){
                    if(lectureHour.getClassroom() == null){
                        System.out.println("Course Section: " + courseSection.getCourse().getId() + "." + courseSection.getCourseSectionNumber() + ", Day: " + lectureHour.getLectureDay() + ", Hour: " + lectureHour.getLectureHourNumber());
                    }
                }
            }
        }
    }

    public void reviewClassroomSchedule() {

        this.printAllClassroomsWithCapacities();

        while (true) {
            System.out.println("Please select a classroom:");
            int choice = this.getNextInt() - 1;

            if (choice >= 0 || choice <= this.allClassrooms.size()) {
                // Exit option.
                if (choice == this.allClassrooms.size()) {
                    return;// TODO: return null
                }
                Classroom classroom = this.allClassrooms.get(choice);
                classroom.displayTimeTableOfTheClassroom();
                return;
            } else {
                System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public void reviewAllClassroomSchedules() {

        for (Classroom classroom : this.allClassrooms) {
            classroom.displayTimeTableOfTheClassroom();
        }
    }

    /**
     * <pre>
     * Numbers and prints all of the available courses in the department for course selection.
     * Upon selection returns a Course object matching the selected course.
     * </pre>
     * 
     * @param studentName       The name of the student.
     * @param allStudentCourses HashMap of Courses to Strings to pull course status
     *                          from.
     * @return A Course object that matches the chosen course.
     */
    public Course selectCourseFromAllDepartmentCourses(
            /* String studentName, */ HashMap<Course, String> allStudentCourses) {
        int i = 1;
        ArrayList<String> prefixText = new ArrayList<>();
        ArrayList<String> suffixText = new ArrayList<>();
        ArrayList<String> courseListText = new ArrayList<>();
        Course selectedCourse = null;

        suffixText.add("Choose an option: ");
        // TODO: courseSection seçimi de yapılmalı
        // TODO: Seçimi direk first available şeklinde yapıp geçebiliriz.
        for (Course course : allDepartmentCoursesList) {
            // course.getCourseSection().getCourseSectionNumber()
            String status = allStudentCourses.get(course) == null ? "Not Taken" : allStudentCourses.get(course);
            courseListText.add(i + "- " + course.getName() + ",\t Status: " + status + ",\t Credits: "
                    + course.getCredits() + ",\t Course ID: " + course.getId() + "\n");
            i++;
        }
        courseListText.add(i + "- Exit\n");

        menuOperation(prefixText);
        menuOperation(courseListText);
        menuOperation(suffixText);

        int choice = -1;
        while (true) {
            choice = this.getNextInt() - 1;

            if (choice == allDepartmentCoursesList.size()) {
                return null;
            }

            if (choice < 0) {
                System.out.println("Invalid choice!");
                continue;
            }

            selectedCourse = this.allDepartmentCoursesList.get(choice);
            return selectedCourse;
        }

    }

    // TODO: oğuz update.
    public Course selectCourseFromAllDepartmentCourses() {
        int i = 1;
        ArrayList<String> prefixText = new ArrayList<>();
        ArrayList<String> suffixText = new ArrayList<>();
        ArrayList<String> courseListText = new ArrayList<>();
        Course selectedCourse = null;

        suffixText.add("Choose an option: ");
        // TODO: courseSection seçimi de yapılmalı
        // TODO: Seçimi direk first available şeklinde yapıp geçebiliriz.
        for (Course course : allDepartmentCoursesList) {
            // course.getCourseSection().getCourseSectionNumber()
            // String status = allStudentCourses.get(course) == null ? "Not Taken" :
            // allStudentCourses.get(course);
            courseListText.add(i + "- " + course.getName() + ",\t Credits: "
                    + course.getCredits() + ",\t Course ID: " + course.getId() + "\n");
            i++;
        }
        courseListText.add(i + "- Exit\n");

        menuOperation(prefixText);
        menuOperation(courseListText);
        menuOperation(suffixText);

        int choice = -1;
        while (true) {
            choice = this.getNextInt() - 1;

            if (choice == allDepartmentCoursesList.size()) {
                return null;
            }

            if (choice < 0) {
                System.out.println("Invalid choice!");
                continue;
            }

            selectedCourse = this.allDepartmentCoursesList.get(choice);
            return selectedCourse;
        }

    }

    public CourseSection selectCoursesSectionFromCourse(Course course) {

        ArrayList<String> prefixText = new ArrayList<>(Arrays.asList("\n\nSection Selection Menu.\n",
                "Available options:\n"));

        ArrayList<String> optionsText = new ArrayList<>();

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));

        if (course.getCourseSections().size() > 1) {// There must be at least one CourseSection in the course.
            while (true) {
                int i = 0;
                for (i = 0; i < course.getCourseSections().size() - 1; i++) {
                    optionsText.add(course.getName() + "." + (i + 1));
                }
                optionsText.add("Exit.");
                int sectionSelection = this.menuOperation(prefixText, optionsText, suffixText);// TODO: try-catch needed
                if (sectionSelection == course.getCourseSections().size())
                    return null; // Exit
                else
                    return course.getCourseSections().get(sectionSelection);
            }
        } else {
            return course.getCourseSections().get(0);
        }

    }

    public Classroom selectClassroomFromAllClassrooms() {
        ArrayList<String> prefixText = new ArrayList<>(Arrays.asList("\n\nClassroom Selection Menu.\n",
                "Available options:\n"));

        ArrayList<String> optionsText;

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));

        while (true) {
            optionsText = new ArrayList<>();
            System.out.println("Please select a clasroom.");

            for (Classroom classroom : this.allClassrooms) {
                optionsText.add(classroom.getclassroomID() + ".");
            }

            optionsText.add("Exit.");

            int classroomSelection = this.menuOperation(prefixText, optionsText, suffixText);// TODO: try-catch needed
            if (classroomSelection == this.allClassrooms.size())
                return null;
            else if (classroomSelection >= 0 && classroomSelection < this.allClassrooms.size())
                return this.allClassrooms.get(classroomSelection);
            else{
                return null;
            }
        }
    }

    public LectureHour selectLectureHourForClassroom(Classroom classroom) {
        classroom.displayTimeTableOfTheClassroom();// Day Selection Menu

        ArrayList<String> prefixText = new ArrayList<>(Arrays.asList("\n\nDay Selection Menu.\n",
                "Available options:\n"));

        ArrayList<String> optionsText = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY",
                "SUNDAY"));
        optionsText.add("Exit.");

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));
        int lectureDaySelection = this.menuOperation(prefixText, optionsText, suffixText);

        ArrayList<String> prefixText1 = new ArrayList<>(Arrays.asList("\n\n Slot Selection Menu.\n",
                "Available options:\n"));

        ArrayList<String> optionsText1 = new ArrayList<>(Arrays.asList("8.30 - 9.20  ",
                "9.30 - 10.20 ",
                "10.30 - 11.20",
                "11.30 - 12.20",
                "13.00 - 13.50",
                "14.00 - 14.50",
                "15.00 - 15.50",
                "16.00 - 16.50",
                "17.00 - 17.50",
                "18.00 - 18.50"));
        optionsText1.add("Exit.");

        ArrayList<String> suffixText1 = new ArrayList<>(Arrays.asList("Please choose an option: "));

        if (lectureDaySelection == optionsText.size()) {
            return null;
        } else {
            if (lectureDaySelection >= 0 && lectureDaySelection < optionsText.size()) {
                int lectureHourSelection = this.menuOperation(prefixText1, optionsText1, suffixText1);
                if (lectureHourSelection == optionsText1.size()) {
                    return null;
                } else {
                    if (lectureHourSelection >= 0 && lectureHourSelection < optionsText1.size()) {
                        return new LectureHour(lectureDaySelection, lectureHourSelection, classroom);
                    } else {
                        System.out.println("Please enter a valid operation");
                    }
                }
            } else {
                System.out.println("Please enter a valid operation");
            }
        }
        return null;
    }

    /**
     * <pre>
     * 
     * Takes in 3 ArrayList<String> arguments and prints them in order.
     * After which it takes user input if and only if the provided suffixText argument is not empty.
     * Arguments prefixText and suffixText are printed as is with no modification / parsing.
     * Each line in argument numberedOptionsText is printed with a numbered prefix at the start of the line.
     * Ex: "1- numberedOptionsText[0]", ..., "n- optionsString[n - 1]"
     * New lines '\n' are added only on numberedOptionsText as it might be undesireable to do so on any other arguments.
     *
     * Return Value: On success returns a zero based integer index given by user.
     * Ex: User input 9, return val = 8.
     * On failure returns -1 as that is not a valid index.
     * </pre>
     * 
     * <br>
     * 
     * @param prefixText          Lines of text to be printed before options.
     * @param numberedOptionsText Lines of text that are numbered and printed.
     * @param suffixText          Lines of text to be printed after options.
     * @return Returns index of the selected line.
     */
    public int menuOperation(ArrayList<String> prefixText, ArrayList<String> numberedOptionsText,
            ArrayList<String> suffixText) {
        int i = 1;
        int result = -1;

        for (String prefixString : prefixText) {
            System.out.print(prefixString);
        }

        for (String optionsString : numberedOptionsText) {
            System.out.println(i + "- " + optionsString);
            i++;
        }

        for (String suffixString : suffixText) {
            System.out.print(suffixString);
        }

        if (suffixText.isEmpty() == false) {
            result = this.getNextInt();
        }

        if (result > 0 && result <= numberedOptionsText.size()) {
            return result - 1;
        }
        return -1;
    }

    /**
     * <pre>
     * 
     * Takes in 2 ArrayList<String> arguments and prints them in order.
     * After which it takes user input if and only if the provided suffixText argument is not empty.
     * Argument suffixText is printed as is with no modification / parsing.
     * Each line in argument numberedOptionsText is printed with a numbered prefix at the start of the line.
     * Ex: "1- numberedOptionsText[0]", ..., "n- optionsString[n - 1]"
     * New lines '\n' are added only on numberedOptionsText as it might be undesireable to do so on any other arguments
     *
     * Return Value: On success returns a zero based integer index given by user.
     * Ex: User input 9, return val = 8.
     * On failure returns -1 as that is not a valid index.
     * </pre>
     * 
     * <br>
     * 
     * @param numberedOptionsText Lines of text that are numbered and printed.
     * @param suffixText          Lines of text to be printed after options.
     * @return Returns index of the selected line.
     */
    public int menuOperation(ArrayList<String> numberedOptionsText, ArrayList<String> suffixText) {
        return menuOperation(new ArrayList<String>(), numberedOptionsText, suffixText);
    }

    /**
     * <pre>
     * 
     * Takes in ArrayList<String> as an argument and prints the strings contained in order.
     * Argument prefixText is printed as is with no modification / parsing.
     * New lines '\n' aren't added as it might be undesireable.
     *
     * Return Value: This method doesn't have a return value as it doesn't get any input from the user.
     * </pre>
     * 
     * <br>
     * 
     * @param prefixText Lines of text to be printed.
     * @return No return value.
     */
    public void menuOperation(ArrayList<String> prefixText) {
        menuOperation(prefixText, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Reads a line from stdin.
     * 
     * @return The line read.
     */
    public String getNextLine() {
        String input;

        try {
            input = inputScanner.nextLine();

        } catch (Exception e) {

            if (e instanceof NoSuchElementException) {
                return "";

            } else if (e instanceof IllegalStateException) {
                System.err.println("\n\n");
                System.err.println("######################");
                System.err.println("The input scanner is closed. InputStream System.in is static. Do not close it!");
                System.err.println("######################");
                System.err.println("\n\n");
                return "";

            } else {
                throw e;
            }
        }

        return input;
    }

    /**
     * Reads an entire line and tries to parse from line start as an integer. Only
     * works for non-negative numbers.
     * 
     * @return On success the parsed integer.
     *         <p>
     *         On failure -1.
     */
    public int getNextInt() {
        int i;
        int inputNumber;

        String inputString = getNextLine();

        String[] characters = inputString.split("");
        StringBuilder stringBuilder = new StringBuilder();

        for (i = 0; i < characters.length; i++) {
            if ((characters[i].matches("[0-9]+"))) {
                // Digits, append them to stringBuilder.
                stringBuilder.append(characters[i]);
            } else {
                // First non digit character. Stop appending.
                break;

            }
        }

        // If input is empty/has no digits at the beginning of line.
        if (i == 0) {
            return -1;
        }

        try {
            inputNumber = Integer.parseInt(stringBuilder.toString());
        } catch (NumberFormatException e) {
            // Number is larger than INT_MAX 2147483647.
            return -1;
        }

        return inputNumber;
    }

    public void printAllGivenCoursesWithTheirCapacity() {
        ArrayList<String> courseListText = new ArrayList<>();

        for (Course course : allDepartmentCoursesList) {
            courseListText.add(
                    course.getName() + ", Course ID: " + course.getId() + ", Capacity" + course.getCourseCapacity()
                            + "\n");
        }
        menuOperation(courseListText);
    }
}