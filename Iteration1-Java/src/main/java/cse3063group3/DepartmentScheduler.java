package cse3063group3;

import java.util.ArrayList;
import java.util.Arrays;

public class DepartmentScheduler extends Staff{

    String administeredDepartmantName;

    public DepartmentScheduler(int id, String name, String eMail, String password, String departmentName){
        super(id, name, eMail, password); 
        this.administeredDepartmantName = departmentName;
    }

    @Override
    public void mainMenu() {
    // TODO Auto-generated method stub
        ArrayList<String> prefixText = new ArrayList<>(Arrays.asList("\n\nDepartment Scheduler system.\n",
                "Available options:\n"));

        ArrayList<String> optionsText = new ArrayList<>(Arrays.asList("List given courses in this semester.",
                "List classrooms with their capacities.",
                "Review a classroom's weekly schedule.",
                "Review all classroom's weekly schedules.",
                "List lecture hours which are not assigned to any classroom yet.",
                "Assign lecture hours to classroom and course sections.",
                "Increase Course Section capacity.",
                "Exit.\n"));

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));
        

        while (true) {
            // Send the main menu body to be printed and get an option from user.
            int choice = this.getControllerInstance().menuOperation(prefixText, optionsText, suffixText);

            if (choice == optionsText.size() - 1) {
                break;
            }

            switch (choice) {
                case 0 -> //List given courses in this semester
                    this.getControllerInstance().printAllGivenCoursesWithTheirCapacity();
                case 1 -> //List classrooms with their capacities
                    this.getControllerInstance().printAllClassroomsWithCapacities();
                case 2 -> // TODO // Review a classroom's weekly schedule
                    this.getControllerInstance().reviewClassroomSchedule();                
                case 3 -> // Review all classroom's weekly schedule
                    this.getControllerInstance().reviewAllClassroomSchedules();
                case 4 -> //Print Lecture hours without classrooms.
                    this.getControllerInstance().printAllClasslessLectureHours();
                case 5 -> assignNewLectureHourToCourseSection();
                case 6 -> this.getControllerInstance().selectAndIncreaseCourseSectionCapacity();
            }
        }
    }

  
    public boolean assignLectureHoursToClasroomAndCourseSection(Classroom clasroom, CourseSection coursesSection,LectureHour newLectureHour) {
        if (this.checkTimeConflictOfClasroomAndCourseSection(clasroom, coursesSection,newLectureHour)){ 
            if(coursesSection.getCourseCapacity() > clasroom.getclassroomSeatingCapacity()){
                return false;
            }
            clasroom.addLectureHour(coursesSection,newLectureHour);
            coursesSection.addLectureHour(newLectureHour);
            
            return true ;
        }else 
            return false;
    }

    public boolean checkTimeConflictOfClasroomAndCourseSection(Classroom clasroom,CourseSection coursesSection, LectureHour newLectureHour ){
        boolean classroomIsAvailable = clasroom.isClassroomAvailable(newLectureHour) ;
        boolean courseSectionDontOverlap = coursesSection.LectureHourDontOverlap(newLectureHour) ;
        return classroomIsAvailable && courseSectionDontOverlap;
    }
    

    public void assignNewLectureHourToCourseSection(){

        Course selectedCourse = this.getControllerInstance().selectCourseFromAllDepartmentCourses();
        if(selectedCourse == null){
            return;// go to upper menu dont do anything
        }
        CourseSection selectedCourseSection = this.getControllerInstance().selectCoursesSectionFromCourse(selectedCourse);
        if(selectedCourseSection == null){
            return;// go to upper menu dont do anything
        }
        Classroom selectedClassroom = this.getControllerInstance().selectClassroomFromAllClassrooms();
        if(selectedClassroom == null){
            return;// go to upper menu dont do anything
        }

        LectureHour selectedLectureHour = this.getControllerInstance().selectLectureHourForClassroom(selectedClassroom);
        if(selectedLectureHour == null){
            return;
        }
        
        boolean res = assignLectureHoursToClasroomAndCourseSection(selectedClassroom, selectedCourseSection, selectedLectureHour);
        if(res == false){
            System.out.println("Fail."); // TODO
        }else{
            System.out.println("lecture hour is assigned successfully"); //TODO
            selectedClassroom.setTimeTableMap(selectedLectureHour.getLectureTimeTableNumber(), selectedCourseSection);
        }
   }
}
