package cse3063group3;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

public class Advisor extends Lecturer {
    private ArrayList<Student> adviseeList;
    // ArrayList<Student> pendingStudents;

    Advisor(int id, String name, String eMail, String password, ArrayList<Student> advisee) {
        super(id, name, eMail, password);
        this.adviseeList = advisee;
    }

    public void addAdvisee(Student advisee) {
        this.adviseeList.add(advisee);
    }

    public JSONObject toJSON() {
        return null;
    };

    // getName, id, <advisee> etc
    public ArrayList<Student> getAdviseeList() {
        return adviseeList;
    }

    public void mainMenu() {
        ArrayList<String> prefixText = new ArrayList<>(
                Arrays.asList("\n\nAdvisor system. Hello " + super.getName() + "\n",
                        "Available options:\n"));

        ArrayList<String> optionsText = new ArrayList<>(Arrays.asList(  "List students who selected courses and sended to be approved.",
                                                                        "View weekly schedule of a student",
                                                                        "Exit.\n"));

        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please choose an option: "));
        // Loop inside the student menu until exit is chosen.
        while (true) {
            // Send the main menu body to be printed and get an option from user.
            int choice = this.getControllerInstance().menuOperation(prefixText, optionsText, suffixText);

            if (choice == optionsText.size() - 1) {
                break;
            }

            switch (choice) {
                case 0 -> listStudentsWithPendingCoursesOperation();
                case 1 -> viewStudentWeeklySchedule();
                
                default -> // TODO Send this to a logger.
                    System.err.println("Error: This shoudn't happen. Advisor.Advisor()");
            }
        }
    }

    public void listStudentsWithPendingCoursesOperation() {
        ArrayList<Student> studentsWithPendingCoursesList;

        ArrayList<String> titleText = new ArrayList<>(Arrays.asList("\n\nStudents waiting for registration approval:\n"));
        ArrayList<String> optionsText;
        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please select a student: "));


        while (true) {
            optionsText = new ArrayList<>();
            studentsWithPendingCoursesList = new ArrayList<>();
            //Only list students who still have pending courses to be approved/rejected.
            for (Student student : this.adviseeList) {
                if (student.hasPendingCourse()) {
                    studentsWithPendingCoursesList.add(student);
                    optionsText.add(student.getName());
                }
            };

            if(studentsWithPendingCoursesList.isEmpty()){
                titleText.add("There are currently no students who require course registration approval.\n");
            }

            // TODO: newLine should be added between students
            // Fixed documentation. numberedOptionsText of menuOperation actually does print newlines since they need to be numbered/visible. This comment will be deleted later.
        
            optionsText.add("Exit.\n");



            int chosenOption = this.getControllerInstance().menuOperation(titleText, optionsText, suffixText);

            if(chosenOption >= 0){
                //Exit selected.
                if (chosenOption == studentsWithPendingCoursesList.size() /* or can use optionsText - 1 same thing */) {
                    break;
                }

                studentsWithPendingCoursesList.get(chosenOption).setCourseSectionStatusToApprovedOrRejected();
            } else {
                //Invalid input.
                this.getControllerInstance().menuOperation(new ArrayList<String>(Arrays.asList("Please choose a valid option.\n")));
            }
        }
    }
    
    public void viewStudentWeeklySchedule() {
        ArrayList<String> titleText = new ArrayList<>(Arrays.asList("\n\nAdvisee List:\n"));
        ArrayList<String> optionsText = new ArrayList<String>();
        ArrayList<String> suffixText = new ArrayList<>(Arrays.asList("Please select a student: "));
        
        //Only list students who still have pending courses to be approved/rejected.
        for (Student student : this.adviseeList) {
                optionsText.add(student.getName());
        }
        if(optionsText.isEmpty()){
            titleText.add("You have no advisee's.\n");
        }
        optionsText.add("Exit.\n");
        while (true) {
            int chosenOption = this.getControllerInstance().menuOperation(titleText, optionsText, suffixText);
            if(chosenOption >= 0){
                //Exit selected.
                if (chosenOption == optionsText.size() - 1) {
                    break;
                }
                this.adviseeList.get(chosenOption).printWeeklySchedule();
            } else {
                //Invalid input.
                this.getControllerInstance().menuOperation(new ArrayList<String>(Arrays.asList("Please choose a valid option.\n")));
            }
        }
    }

}
