import unittest

from Student import Student

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class TestCoursePrerequisite(unittest.TestCase):

   
    def test_tecourse_prerequisite(self):
        from CourseRegistrationSystem import CourseRegistrationSystem
        from TECourse import TECourse
        S1 = Student(1,"rookie","rookie@example.com","a",1,CourseRegistrationSystem.get_course_registration_system_instance())
        S2 = Student(2,"rookie2","rookie2@example.com","a",2,CourseRegistrationSystem.get_course_registration_system_instance())
        S3 = Student(3,"rookie3","rookie3@example.com","a",3,CourseRegistrationSystem.get_course_registration_system_instance())
        S4 = Student(4,"rookie4","rookie4@example.com","a",4,CourseRegistrationSystem.get_course_registration_system_instance())
        S5 = Student(5,"rookie5","rookie5@example.com","a",5,CourseRegistrationSystem.get_course_registration_system_instance())
        S6 = Student(6,"rookie6","rookie6@example.com","a",6,CourseRegistrationSystem.get_course_registration_system_instance())
        S7 = Student(7,"rookie7","rookie7@example.com","a",7,CourseRegistrationSystem.get_course_registration_system_instance())
        S8 = Student(8,"rookie8","rookie8@example.com","a",8,CourseRegistrationSystem.get_course_registration_system_instance())
        course = TECourse(10,"Critical Thinking",5)

        self.assertFalse(course.check_prerequisites_met(S1))
        self.assertFalse(course.check_prerequisites_met(S2))
        self.assertFalse(course.check_prerequisites_met(S3))
        self.assertFalse(course.check_prerequisites_met(S4))
        self.assertFalse(course.check_prerequisites_met(S5))
        self.assertFalse(course.check_prerequisites_met(S6))
        self.assertTrue(course.check_prerequisites_met(S7))
        self.assertTrue(course.check_prerequisites_met(S8))
        
    def test_engineering_project_course_prerequisite(self):
        from CourseRegistrationSystem import CourseRegistrationSystem
        from EngineeringProjectCourse import EngineeringProjectCourse
        from MandatoryCourse import MandatoryCourse
        from CourseSection import CourseSection
        finished_course = MandatoryCourse(2,"Calculus1",20)
        course_section = CourseSection([],"Gandalf",1,2,finished_course)

        S1 = Student(1,"rookie","rookie@example.com","a",1,CourseRegistrationSystem.get_course_registration_system_instance())
        S1.change_status(course_section,"finished")
        S2 = Student(2,"rookie2","rookie@example.com","a",2,CourseRegistrationSystem.get_course_registration_system_instance())

        engineering_project = EngineeringProjectCourse(1,"Engineering Project Team1",5,15)

        self.assertTrue(engineering_project.check_prerequisites_met(S1))
        self.assertFalse(engineering_project.check_prerequisites_met(S2))

    def test_mandatory_course(self):
        from CourseRegistrationSystem import CourseRegistrationSystem
        from MandatoryCourse import MandatoryCourse
        from CourseSection import CourseSection

        course1 = MandatoryCourse(1,"Calculus 1",5)
        course2 = MandatoryCourse(2,"Calculus 2",5)
        course2.add_prerequisite(course1)

        S1 = Student(1,"rookie","rookie@example.com","a",1,CourseRegistrationSystem.get_course_registration_system_instance())
        S2 = Student(2,"rookie1","rookie@example.com1","a",1,CourseRegistrationSystem.get_course_registration_system_instance())
        course_section1 = CourseSection([],"Dumbledore",1,2,course1)
        course_section2 = CourseSection([],"Dumbledore",1,2,course2)
        
        S2.change_status(course_section1,"finished")

        self.assertTrue(course1.check_prerequisites_met(S1))
        self.assertTrue(course1.check_prerequisites_met(S2))

        self.assertFalse(course2.check_prerequisites_met(S1))
        self.assertTrue(course2.check_prerequisites_met(S2))






if __name__ == '__main__':
    unittest.main()