package cse3063group3;

import java.util.ArrayList;


public class Course {

    private int id;
    private String name;
    private int credits;
//    private int creditsNeeded;
    private ArrayList<Course> prerequisites;
    private ArrayList<Student> studentList;     // TODO: Send this and related methods into CourseSections.
    private ArrayList<CourseSection> courseSections;
    private ArrayList<Student> waitingStudentList = new ArrayList<>();
    int courseCapacity;                         // TODO: ^^^ Same as above.
    int numberOfLectureHours;

    public Course(int course_Id, String course_Name, int course_Credits) {
        this.id = course_Id;
        this.name = course_Name;
        this.credits = course_Credits;
        this.courseSections = new ArrayList<CourseSection>();
        this.prerequisites = new ArrayList<Course>();
    }

    /**
     * Checks if prerequisets for the current course are met or not by comparing argument studentFinishedCourseList to prerequisites.
     * @param studentFinishedCourseList An ArrayList containing a list of courses to compare against.
     * @return True if Course.ArrayList<Course> prerequisites is mett.<p>False otherwise.
     */
    public boolean CheckPrerequisitesMet(ArrayList<Course> studentFinishedCourseList, Student student){
        if (!studentFinishedCourseList.containsAll(this.prerequisites)) {
            //TODO: Send to controller.
            System.out.println("Did not finish all required courses to take " + this.getName() + ".");
            return false;
        }
        return true;
    }

    public void addStudentToWaitList() {
        // TODO: implement it
    }

    public int getId() {
        return this.id;
    }

    public void setId(int course_Id) {
        this.id = course_Id;
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int course_Credits) {
        this.credits = course_Credits;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<CourseSection> getCourseSections() {
        return this.courseSections;
    }

    public void setCourseSection(ArrayList<CourseSection> course_Section_T) {
        this.courseSections = course_Section_T;
    }
    public void addCourseSection(CourseSection section){
        this.courseSections.add(section);
    }

    public ArrayList<Course> getPrerequisites() {
        return this.prerequisites;
    }

    public void setPrerequisites(Course course) {
        this.prerequisites.add(course);
    }

    public int getCourseCapacity() {
        int sum = 0;
        for(CourseSection courseSection : this.getCourseSections()){
            sum += courseSection.getCourseCapacity();
        }
        return sum;
    }
}
