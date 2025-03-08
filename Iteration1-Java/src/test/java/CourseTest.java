/*
 import cse3063group3.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class CourseTest {
    private Course course;
    private Course prerequisiteCourse;
    private Student student;

    @BeforeEach
    void setUp(){
        course= new Course(5476231,"Software Engineering",3);
        prerequisiteCourse=new Course(1098764,"Cloud Computing",3);

        student=new Student(4564645, "Joe", "dhskfjsdhfjk", "dhfkjsdhks", (Map<Course, String>)null , (Advisor) null);
    }
    @Test
    void testConstructor(){
        assertEquals(1098764,course.getId());
        assertEquals("Cloud Computing",course.getName());
        assertEquals(3,course.getCredits());
        assertNotNull(course.registered_Students());
        assertNotNull(course.getCourseSection());
        assertNotNull(course.getPrerequisites());

    }
    @Test 
    void testSettersandGetters(){
        course.setId(5476231);
        course.setName("Software Engineering");
        course.setCredits(3);

        assertEquals(5476231,course.getId());
        assertEquals("Software Engineering",course.getName());
        assertEquals(3,course.getCredits());
    }
    @Test
    void testCheckPrerequisitesMet_EnoughCredits(){
        List<Course> completedCourses=new ArrayList<>();
        completedCourses.add(new Course (1012345,"Data Structures and Algorithms",3));
        completedCourses.add(new Course(5476231,"Software Engineering",3));
        student.setCourses(completedCourses.get(1),"taken");
        boolean result=course.CheckPrerequisitesMet(student,prerequisiteCourse);
        assertTrue(result,"Student meets the prerequisite credits requirements.");
    }
    @Test
    void testCheckPrerequisitesMet_NotEnoughCredits(){
        //Student that takes the courses but doesn't meet the total credits needed.
        List<Course> completedCourses=new ArrayList<>();
        completedCourses.add(new Course (2014578,"Database Management Systems",3));
        completedCourses.add(new Course(6574218,"Artificial Intelligence",4));
        student.setCourses(completedCourses.get(0),"taken");
        boolean result=course.CheckPrerequisitesMet(student,prerequisiteCourse);
        assertFalse(result, "Student should not meet the prerequisite credits requirement.");
    }
    @Test
    void testCheckPrerequisitesMet_NoCompletedCourses(){
        List<Course> completedCourses=new ArrayList<>();
        completedCourses.add(new Course (2014578,"Database Management Systems",3));
        completedCourses.add(new Course(6574218,"Artificial Intelligence",4));
        student.setCourses(completedCourses.get(0),"approved");
        student.setCourses(completedCourses.get(1),"pending");
        boolean result= course.CheckPrerequisitesMet(student,prerequisiteCourse);
        assertFalse(result, "Student with no completed courses should not meet the prerequisite credits requirement.");
    }

}

 */