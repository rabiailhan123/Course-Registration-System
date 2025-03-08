from Staff import Staff
from typing import List

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class DepartmentScheduler(Staff):

    def __init__(self, id: str, name: str, email: str, password: str, department_name: str, controller_instance):
        super().__init__(id, name, email, password, controller_instance)
        self.__administered_department_name: str = department_name

    def main_menu(self) -> None: 
        prefix_text = [
            f"\n\n\t\t{self.__administered_department_name}\n  << Department Scheduler System >>\n\n",
            "Available options:\n\n"
        ]

        options_text = [
            "List given courses in this semester.",
            "List classrooms with their capacities.",
            "Review a classroom's weekly schedule.",
            "Review all classrooms' weekly schedules.",
            "List lecture hours not assigned to any classroom.",
            "Assign lecture hours to a classroom and course section.",
            "Exit.\n"
        ]

        suffix_text = ["Please choose an option: "]

        while True:
            # Display the menu and get the user's choice.
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text, suffix_text)

            if choice == len(options_text) - 1:  # Exit option
                break

            match choice:
                case 0:  # List given courses in this semester 
                    self.get_controller_instance().print_all_given_course_sections_with_their_capacities()
                    #self.get_controller_instance().print_all_given_courses_with_their_capacities()
                case 1:  # List classrooms with their capacities
                    self.get_controller_instance().print_all_classrooms_with_capacities()
                case 2:  # Review a classroom's weekly schedule
                    self.get_controller_instance().review_classroom_schedule()
                case 3:  # Review all classrooms' weekly schedules
                    self.get_controller_instance().review_all_classroom_schedules()
                case 4:  # Print lecture hours without classrooms
                    self.get_controller_instance().print_all_classes_lecture_hours()
                case 5:  # Assign new lecture hour to course section
                    self.__assign_new_lecture_hour_to_course_section()
                case _:
                    logger.info(f"User entered an option:{choice} which is out of range 1 - {len(options_text)}.")
                    self.get_controller_instance.menu_operation(f"Please enter an integer between 1 and {len(options_text)}")


    def __assign_lecture_hours_to_classroom_and_course_section(self, classroom, course_section, new_lecture_hour) -> bool:
        if self.__check_time_conflict_of_classroom_and_course_section(classroom, course_section, new_lecture_hour):
            if course_section.get_course_capacity() > classroom.get_classroom_seating_capacity():
                self.get_controller_instance().menu_operation("The classroom capacity is insufficient for the course section.")
                return False
            try:
                classroom.add_lecture_hour(course_section, new_lecture_hour.get_lecture_time_table_number())
                course_section.add_lecture_hour(new_lecture_hour)
                new_lecture_hour.set_classroom(classroom)
                logger.info(f"Lecture hour in day {new_lecture_hour.get_lecture_day()} : section {new_lecture_hour.get_lecture_hour_number()} is assigned successfully to the classroom {classroom.get_classroom_id()}.")
                self.get_controller_instance().menu_operation("Lecture hour successfully assigned.")
                return True
            except ValueError as e:
                logger.warning("Lecture hour is not assigned to the classroom because time conflict detected.")
        self.get_controller_instance().menu_operation("Time conflict detected. Assignment failed.")
        return False

    def __check_time_conflict_of_classroom_and_course_section(self, classroom, course_section, new_lecture_hour) -> bool:
        classroom_is_available = classroom.is_classroom_available(new_lecture_hour.get_lecture_time_table_number())
        course_section_doesnt_overlap = course_section.lecture_hour_dont_overlap(new_lecture_hour)
        if classroom_is_available is False:
            logger.warning(f"clasroom:{classroom.get_classroom_id()} is not available at day:{new_lecture_hour.get_lecture_day()} hournumber:{new_lecture_hour.get_lecture_hour_number()}")
        if course_section_doesnt_overlap is False:
            logger.warning(f"course section:{course_section.get_course().get_id()}.{course_section.get_course_section_number()} is not available at day:{new_lecture_hour.get_lecture_day()} hournumber:{new_lecture_hour.get_lecture_hour_number()}")
        logger.info(f"course section:{course_section.get_course()}.{course_section.get_course_section_number()}and clasroom:{classroom.get_classroom_id()} is  available at day:{new_lecture_hour.get_lecture_day()} hournumber:{new_lecture_hour.get_lecture_hour_number()}")
        return classroom_is_available and course_section_doesnt_overlap

    def __assign_new_lecture_hour_to_course_section(self) -> None:
        from LectureHour import LectureHour
        from Classroom import Classroom
        selected_course = self.get_controller_instance().select_course_to_assign_new_lecture_hour()
        if selected_course is None:
            return  # Exit to the previous menu

        selected_course_section = self.get_controller_instance().select_courses_section_from_course(selected_course)
        if selected_course_section is None:
            return  # Exit to the previous menu

        selected_classroom  :Classroom = self.get_controller_instance().select_classroom_from_all_classrooms()
        if selected_classroom is None:
            return  # Exit to the previous menu

        selected_lecture_hour : LectureHour = self.get_controller_instance().select_lecture_hour_for_classroom(selected_classroom)
        if selected_lecture_hour is None:
            return  # Exit to the previous menu

        success = self.__assign_lecture_hours_to_classroom_and_course_section(
            selected_classroom, selected_course_section, selected_lecture_hour
        )

    def get_json(self):
        return  {
            'id': self.get_id(),
            'name':self.get_name(),
            'eMail':self.get_e_mail(),
            'password': self.get_password(),
            "departmentName" : self.__administered_department_name
        }
