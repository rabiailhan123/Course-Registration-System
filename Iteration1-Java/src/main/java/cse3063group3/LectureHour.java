package cse3063group3;

public class LectureHour {
    private int lectureDay;
    private int lectureHourNumber;
    private Classroom classroom;

    public LectureHour(int lectureDay,int lectureHourNumber,Classroom classroom){
        this.lectureDay = lectureDay;
        this.lectureHourNumber=lectureHourNumber;
        this.classroom=classroom;
    }
    public LectureHour(int lectureDay,int lectureHourNumber){
        this.lectureDay = lectureDay;
        this.lectureHourNumber=lectureHourNumber;
    }

    public int getLectureDay(){
        return this.lectureDay;
    }

    public void setLectureDay(int lectureDay){
        this.lectureDay = lectureDay;
    }
  
    public int getLectureHourNumber(){
        return this.lectureHourNumber;
    }

    public void setLectureHourNumber(int lectureHourNumber){
        this.lectureHourNumber=lectureHourNumber;
    }

    public Classroom getClassroom(){
        return this.classroom;
    }
  
    
    public void setClassroom(Classroom classroom){
        this.classroom = classroom;
    }
    
    public int getLectureTimeTableNumber(){
        return this.lectureDay * 10 +this.lectureHourNumber;
    }
}
