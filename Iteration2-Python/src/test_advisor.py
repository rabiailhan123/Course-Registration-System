import unittest

from Student import Student
from Advisor import Advisor
from CourseRegistrationSystem import CourseRegistrationSystem

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class TestAdvisor(unittest.TestCase):
    
    def test_initialize_advisor(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        advisor = Advisor("999", "Advisor Name", "advisor@mail", "password1", [], c_instance)
        self.assertEqual(advisor.get_name(), "Advisor Name")
        self.assertEqual(advisor.get_id(), "999")
        self.assertEqual(advisor.get_e_mail(), "advisor@mail")
        self.assertTrue(advisor.get_password(),"df")
        self.assertTrue(advisor.get_advisee_list() is not None)
        logger.info("Passed test_initialize_advisor.")
        
        
    def test_add_advisee(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        advisor = Advisor("999", "Advisor Name", "advisor@mail", "password1", [], c_instance)
        student = Student("888", "Advisee Name", "advisee@mail", "password2", 99, c_instance)
        before_len = len(advisor.get_advisee_list())
        advisor.add_advisee(student)
        after_len = len(advisor.get_advisee_list())
        self.assertEqual(before_len + 1, after_len)
        self.assertEqual(advisor.get_advisee_list()[before_len], student)
        logger.info("Passed test_add_advisee.")
    
    
    def test_get_advisee_list(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        advisor = Advisor("999", "Advisor Name", "advisor@mail", "password1", [], c_instance)
        student = Student("888", "Advisee Name", "advisee@mail", "password2", 99, c_instance)
        
        advisor.advisee_list = []
        advisor.advisee_list.append(student)
        
        self.assertEqual(len(advisor.get_advisee_list()), 1)
        self.assertEqual(advisor.get_advisee_list()[0], student)
        logger.info("Passed test_get_advisee_list.")
        
        

if __name__ == '__main__':
    unittest.main()