package cse3063group3;
import org.json.JSONObject;

public abstract class Staff extends Person {
    // write this contructor the uml
    public Staff (int id, String name, String eMail, String password){
        super(id, name, eMail, password); 
    }
    public  JSONObject toJSON(){return null;};
}
