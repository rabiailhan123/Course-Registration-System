
from Course import Course
from Student import Student

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class TECourse(Course):
    def __init__(self, course_Id, course_name, course_credits):
            super().__init__( course_Id, course_name, course_credits)
            from CourseRegistrationSystem import CourseRegistrationSystem
            self.__controller_instance = CourseRegistrationSystem.get_controller_instance()
            #self.__credits_needed = credits_needed
            # removed feom the constructor self.credits_needed = credits_needed
            

    def check_prerequisites_met(self , student : Student) -> bool:
        student_semester = student.get_semester()

        if student_semester < 7:
            self.__controller_instance.menu_operation("Did not meet the semester requirements to take " + self.get_name() + ".")
            return False
        
        if not all(prerequisite in student.get_finished_courses() for prerequisite in super().get_prerequisites()):
            self.__controller_instance.menu_operation(f"Did not finish all required courses to take {self.get_name()}.")
            return False
        
        return True
        


    def get_credits_needed(self):
        return self.__credits_needed
