package cse3063group3;

import java.util.ArrayList;
import java.util.Arrays;

public class CourseSection {

    private String lecturerName;
    private ArrayList<LectureHour> lectureHours;
    int courseSectionNumber;
    private ArrayList<Student> studentList;
    private int courseCapacity;
    private CourseRegistrationSystem controllerInstance;
  
    private ArrayList<Student> waitingStudentList = new ArrayList<>();
    private Course course; //TODO: bunu assign etmeliyiz courseSection oluştururken

    public CourseSection(ArrayList<LectureHour> lectureHours, String lecturerName,  int courseSectionNumber , int courseCapacity,Course course) {
        this.controllerInstance = CourseRegistrationSystem.getCourseRegistrationSystemInstance();
        this.lectureHours = lectureHours;
        this.lecturerName = lecturerName;
        this.courseSectionNumber = courseSectionNumber;
        this.courseCapacity = courseCapacity;
        this.course = course;
        this.studentList = new ArrayList<>();
    }
    public CourseSection( String lecturerName,  int courseSectionNumber , int courseCapacity, Course course) {
        this.controllerInstance = CourseRegistrationSystem.getCourseRegistrationSystemInstance();
        this.lectureHours = new ArrayList<>();
        this.lecturerName = lecturerName;
        this.courseSectionNumber = courseSectionNumber;
        this.courseCapacity = courseCapacity;
        this.course = course;
        this.studentList = new ArrayList<>();
    }
  
//TODO: bu contructor neden var?
    public CourseSection(ArrayList<LectureHour> lectureHours) {
        this.lectureHours = lectureHours;
    }

    public ArrayList<LectureHour> getLectureHours() {
        return this.lectureHours;
    }
  
    public void setLectureHours(ArrayList<LectureHour> lectureHours) {
        this.lectureHours = lectureHours;
    }

    public String getLecturer() {
        return this.lecturerName;
    }

    //public void setLecturer(String lecturerName) {
    //    this.lecturerName = lecturerName;
    //}
  

    public int getCourseSectionNumber() {
        return this.courseSectionNumber;
    }

    public String getName(){
        return (((this.course.getName() != null) ? course.getName() : "xx") + "." + this.courseSectionNumber);
    }

    public boolean LectureHourDontOverlap( LectureHour newLectureHour){
        for(LectureHour lectureOfCourseSection : lectureHours){
            if(lectureOfCourseSection.getLectureTimeTableNumber() == newLectureHour.getLectureTimeTableNumber())
                return false;
        }
        return true;
    }
    public void addLectureHour( LectureHour newLecture){
            this.lectureHours.add(newLecture);
    }

    public boolean incrementCapacity(int newCapacity){
        if(newCapacity <= courseCapacity)
            return false;
        else
        {
            for(LectureHour lectureHour : this.getLectureHours()){
                if(lectureHour.getClassroom().getclassroomSeatingCapacity() < newCapacity){
                    this.controllerInstance.menuOperation(new ArrayList<>(Arrays.asList("New course capacity doesn't fit to classroom.")));
                    return false;
                }
            }
            int difference = newCapacity-courseCapacity;
            this.courseCapacity = newCapacity;
            for(int i = 0 ; i < difference; i++)
            {   
                if(waitingStudentList.isEmpty()){
                    break;
                }
                
                Student waitingStudent = waitingStudentList.get(0);
                waitingStudentList.remove(0);
                waitingStudent.changeStatus(this, "approved");
                // TODO: course status will change to waiting after selection when course is full.
                studentList.add(waitingStudent);
            }
        }
        //TODO: return statment yoktu buraya yazdım   
        return true;
    }

    public void addStudent(Student student) {
        // add student to studentList
        // TODO: this check method checks capacity for course not coursesection.!!!
        if (studentList.size() >= this.courseCapacity){ // if the capacity is full
            this.waitingStudentList.add(student);
            System.out.println("Course's capacity is full. You are placed to the waiting list.");
        } else {
            this.studentList.add(student);
            System.out.println(this.getName() + " is selected successfully."); // TODO: this message can be printed inside of the student
        }  
    }


    public Course getCourse(){
        return this.course;
    }
    public int getCourseCapacity(){
        return this.courseCapacity;
    }
}
