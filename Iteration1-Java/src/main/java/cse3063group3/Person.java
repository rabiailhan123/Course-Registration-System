package cse3063group3;


public abstract class Person {
    private int id;
    private String name;
    private String eMail;
    private String password;
    private CourseRegistrationSystem controllerInstance;

    
    /**
     * @param id ID of person.
     * @param name Name of person.
     * @param eMail E-Mail address of person.
     * @param password Password of person.
     * @param controllerInstance Referance to the controller instance.
     */
    public Person(int id, String name, String eMail, String password) {
        this.id = id;
        this.name = name;
        this.eMail = eMail;
        this.password = password;
        this.controllerInstance = CourseRegistrationSystem.getCourseRegistrationSystemInstance();
    }

    public boolean checkPassword(String password){
        return password.equals(this.password);
    }
    public String getPassword(){
        return this.password;
    }
    public String getEMail(){
        return this.eMail;
    }

    public int getId(){
        return (this.id);
    }

    public String getName(){
        return (this.name);
    }

    /**
     * <pre>
     * Constructs the logic and main text body for a Person.
     * Should generally be called right after successful login.
     * </pre>
     */
    public abstract void mainMenu();

    /**
     * @return Referance to the controller instance.
     */
    public CourseRegistrationSystem getControllerInstance(){
        return this.controllerInstance;
    }
    
}
    