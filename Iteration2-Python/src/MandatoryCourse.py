
from typing import List
from Course import Course
from Student import Student

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class MandatoryCourse(Course):
    

    def __init__(self, course_id: int, course_name: str, course_credits: int):
        super().__init__(course_id = course_id, course_name = course_name, course_credits = course_credits)
        from CourseRegistrationSystem import CourseRegistrationSystem
        self.__controller_instance = CourseRegistrationSystem.get_controller_instance()

    def check_prerequisites_met(self, student: 'Student') -> bool: #abstract
        if not all(prerequisite in student.get_finished_courses() for prerequisite in super().get_prerequisites()):
            self.__controller_instance.menu_operation(f"Did not finish all required courses to take {self.get_name()}.")
            return False
        return True
