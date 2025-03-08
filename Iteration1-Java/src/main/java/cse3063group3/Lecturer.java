
package cse3063group3;
import org.json.JSONObject;

public class Lecturer extends Staff{
    //private List<Course> coursesGiven; // will be updated in the later iterations

    // add to the uml
    public Lecturer(int id, String name, String eMail, String password){
        super(id, name, eMail, password); 
    }

    // will be omitted in this iteration
   /* public void addCourse(Course course){
        this.coursesGiven.add(course);
    }
        public List<Course> getCoursesGiven(){
        return (this.coursesGiven);
    }*/
    public  JSONObject toJSON(){return null;};
    
    //TODO: Actually implement this.
    public void mainMenu(){}
}
