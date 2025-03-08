from Course import Course
from Student import Student

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class EngineeringProjectCourse(Course):

    def __init__(self, course_id: int, course_name: str, course_credits: int, credits_needed):
        super().__init__(course_id = course_id, course_name = course_name, course_credits=course_credits)
        self.__credits_needed = credits_needed
        from CourseRegistrationSystem import CourseRegistrationSystem
        self.__controller_instance = CourseRegistrationSystem.get_controller_instance()
        #self.__semester = semester


    def check_prerequisites_met(self, student: Student) -> bool: #abstract
        #if student.get_semester() < self.__semester :
        #    self.get_controller_instance().menu_operation("Did not meet the semester requirements to take " + self.get_name() + ".")
        #    return False
        if student.get_finished_course_credits() < self.__credits_needed:
            self.__controller_instance.menu_operation("Do not have enough credits to take " + self.get_name() + ".")
            return False
        if not all(prerequisite in student.get_finished_courses() for prerequisite in super().get_prerequisites()):
            self.__controller_instance.menu_operation(f"Did not finish all required courses to take {self.get_name()}.")
            return False

        return True 

    def get_credits_needed(self):
        return self.__credits_needed
        
