package cse3063group3;

import java.util.ArrayList;

public class TECourse extends Course{
    final private int creditsNeeded;

    public TECourse(int course_Id, String course_Name, int course_Credits, int creditsNeeded){
        super(course_Id, course_Name, course_Credits);
        this.creditsNeeded = creditsNeeded;
    }

    // TODO do different checks. For pOlYmOrPhisM
    /**
     * Checks if credits needed for the current course are met by comparing argument studentFinishedCourseList to creditsNeeded.
     * @param studentFinishedCourseList An ArrayList containing a list of courses to compare against.
     * @return True if Course.creditsNeeded is met.<p>False otherwise.
     */
    @Override
    public boolean CheckPrerequisitesMet(ArrayList<Course> studentFinishedCourseList, Student student){
        int totalCredits = 0;
        
        for(int i = 0; i < studentFinishedCourseList.size(); i++){
            totalCredits += studentFinishedCourseList.get(i).getCredits();
        }

        if(totalCredits < this.creditsNeeded){
            //TODO: Send to controller.
            System.out.println("Do not have enough credits to take " + this.getName() + ".");
           return false; 
        }

        return true;
    }

    public int getCreditsNeeded() {
        return this.creditsNeeded;
    }
}