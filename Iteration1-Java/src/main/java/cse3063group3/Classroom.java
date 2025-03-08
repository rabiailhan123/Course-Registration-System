package cse3063group3;

import java.util.ArrayList;
import java.util.HashMap;

public class Classroom {

    final private String classroomID;
    final private int classroomSeatingCapacity;
    // TODO: Use HashMap<Integer, CourseSection> on CourseSection.lectureHours
    private HashMap<Integer, CourseSection> timeTableMap = new HashMap<>();
    private CourseRegistrationSystem controllerInstance;

    public Classroom(String classRoomID, int classRoomSeatingCapacity, CourseRegistrationSystem controllerInstance){
        this.classroomID = classRoomID;
        this.classroomSeatingCapacity = classRoomSeatingCapacity;
        this.controllerInstance = controllerInstance;
    }

    public void setTimeTableMap(Integer time, CourseSection courseSection){
        timeTableMap.put(time, courseSection);
    }

    //public void registerCourse

    public String getclassroomID(){
        return this.classroomID;
    }

    public int getclassroomSeatingCapacity(){
        return this.classroomSeatingCapacity;
    }

    public void displayTimeTableOfTheClassroom(){
        ArrayList<String> hourAndCoursesListText = new ArrayList<>();
        hourAndCoursesListText.add("\nClassroom " + this.classroomID + "\n");
        ArrayList<Integer> lectureHours = new ArrayList<>(timeTableMap.keySet());
        ArrayList<CourseSection> coursesOfTheDay1 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay2 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay3 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay4 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay5 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay6 = new ArrayList<>();
        ArrayList<CourseSection> coursesOfTheDay7 = new ArrayList<>();


        int size = 0;
        int day = 0;
        //Collections.sort(lectureHours);
        for(Integer lectureHour : lectureHours){
            switch (lectureHour / 10){
                case 0 -> coursesOfTheDay1.add(this.timeTableMap.get(lectureHour));
                case 1 -> coursesOfTheDay2.add(this.timeTableMap.get(lectureHour));
                case 2 -> coursesOfTheDay3.add(this.timeTableMap.get(lectureHour));
                case 3 -> coursesOfTheDay4.add(this.timeTableMap.get(lectureHour));
                case 4 -> coursesOfTheDay5.add(this.timeTableMap.get(lectureHour));
                case 5 -> coursesOfTheDay6.add(this.timeTableMap.get(lectureHour));
                case 6 -> coursesOfTheDay7.add(this.timeTableMap.get(lectureHour));
            }
        }      
        this.controllerInstance.menuOperation(hourAndCoursesListText);
        printCoursesOfTheDay(1, coursesOfTheDay1);
        printCoursesOfTheDay(2, coursesOfTheDay2);
        printCoursesOfTheDay(3, coursesOfTheDay3);
        printCoursesOfTheDay(4, coursesOfTheDay4);
        printCoursesOfTheDay(5, coursesOfTheDay5);
        printCoursesOfTheDay(6, coursesOfTheDay6);
        printCoursesOfTheDay(7, coursesOfTheDay7);

        //ArrayList<Integer> lectureHours = new ArrayList<>(timeTableMap.keySet());
        //ArrayList<CourseSection> coursesOfTheDay = new ArrayList<>();
        //int size = 0;
        //int day = 0;
        //Collections.sort(lectureHours);
        //for(Integer lectureHour : lectureHours){
        //    CourseSection courseSec = this.timeTableMap.get(lectureHour);
        //    coursesOfTheDay.add(courseSec);
        //    if(++size == 10)
        //    {
        //        day++;
        //        this.printCoursesOfTheDay(day, coursesOfTheDay);
        //        coursesOfTheDay = new ArrayList<>();
        //    }
        //}      
    }

    //TODO: Optionally add TimeSlot to Classroom class to easily retrieve time table for current Classroom instance.

    private void printCoursesOfTheDay(int dayNumber, ArrayList<CourseSection> courses){
        
        ArrayList<String> hourAndCoursesListText = new ArrayList<>();
        String day = "";

        switch(dayNumber){
            case 1 -> day = "MONDAY";
            case 2 -> day = "TUESDAY";
            case 3 -> day = "WEDNESDAY";
            case 4 -> day = "THURSDAY";
            case 5 -> day = "FRIDAY";
            case 6 -> day = "SATURDAY";
            case 7 -> day = "SUNDAY";
        }
        String[] dailySchedule = new String[10];
        String[] hours = new String[10];

        for(int i = 0; i < 10; i++){
            dailySchedule[i] = "[empty]\n";
        }
        for(CourseSection courseSection : courses){
            for(LectureHour lectureHour : courseSection.getLectureHours()){
                dailySchedule[lectureHour.getLectureHourNumber()] = "[" + courseSection.getName() + "]" + "\n";
            }
        }
        hourAndCoursesListText.add("\n" + day + "\n");
        hours[0] = "8.30 - 9.20  ";
        hours[1] = "9.30 - 10.20 ";
        hours[2] = "10.30 - 11.20";
        hours[3] = "11.30 - 12.20";
        hours[4] = "13.00 - 13.50";
        hours[5] = "14.00 - 14.50";
        hours[6] = "15.00 - 15.50";
        hours[7] = "16.00 - 16.50";
        hours[8] = "17.00 - 17.50";
        hours[9] = "18.00 - 18.50";
        for(int i = 0; i < 10; i++){
            hourAndCoursesListText.add(hours[i] + " " + dailySchedule[i]);
        }

        this.controllerInstance.menuOperation(hourAndCoursesListText);
    }
  
  
    public boolean isClassroomAvailable(LectureHour lecture){
        
        if(!this.timeTableMap.containsKey(lecture.getLectureTimeTableNumber()))
            return false;
        else
            return true;
    }

    public void addLectureHour( CourseSection coursesSection, LectureHour lecture){
        
        this.timeTableMap.put(lecture.getLectureTimeTableNumber(), coursesSection);
        
    }
}

    