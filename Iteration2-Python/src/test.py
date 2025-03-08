import unittest
from typing import List
from Classroom import Classroom
from LectureHour import LectureHour
from MandatoryCourse import MandatoryCourse
from CourseSection import CourseSection
from Student import Student
from CourseRegistrationSystem import CourseRegistrationSystem

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class TestClassroom(unittest.TestCase):
    classroom_id = 15
    classroom_seating_capacity = 40
    controller_instance = None
    classroom:Classroom

    lecutre_day = 5
    lecture_hour_number = 6
    lecture_hour:LectureHour

    course_id = 1 
    course_name = "CSE123"
    course_credits = 5
    coruse: MandatoryCourse

    lecture_hour_list : List['LectureHour'] = list()
    lecturer_name = "mujdat"
    course_section_number = 1
    course_section_capacity = 15
    course_section:CourseSection

    student_id = 5 
    student_name = "emul"
    student_email = "example@example.com"
    student_password = "a"
    student :Student

    def setUp(self):
        self.classroom = Classroom(self.classroom_id,self.classroom_seating_capacity,self.controller_instance)
        self.lecture_hour = LectureHour(self.lecutre_day,self.lecture_hour_number,self.classroom)
        self.course = MandatoryCourse(self.course_id,self.course_name,self.course_credits)
        self.course_section = CourseSection(self.lecture_hour_list,self.lecturer_name,self.course_section_number,self.course_section_capacity,self.course)
        self.course_section.add_lecture_hour(self.lecture_hour)
        self.course.add_course_section(self.course_section)
        self.student = Student(self.student_id,self.student_name,self.student_email,self.student_password,1,CourseRegistrationSystem.get_course_registration_system_instance())

    def test_lecture_hour_time_table_nubmer(self):
        new_lecture_hour = LectureHour(2, 5,self.classroom)
        self.assertTrue(new_lecture_hour.get_lecture_time_table_number() == 2*10 + 5)



    def test_student_add_course_section_(self):
        self.student.add_course_section(self.course_section)
        course_sections = self.student.get_all_course_sections()
        self.assertTrue(self.course_section in course_sections)
       
        
    def test_is_clasroom_available(self):
        self.assertTrue(self.classroom.is_classroom_available(self.lecture_hour))
        self.classroom.add_lecture_hour(self.course_section,self.lecture_hour)
        self.assertFalse(self.classroom.is_classroom_available(self.lecture_hour))
    
  


if __name__ == '__main__':
    unittest.main()