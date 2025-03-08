from Staff import Staff
from typing import List

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class DepartmentHead(Staff):

    def __init__(self, id: int, name: str, email: str, password: str, department_name: str, controller_instance):
        super().__init__(id, name, email, password, controller_instance)
        self.__administered_department_name: str = department_name

    def main_menu(self) -> None: 
        prefix_text = [
            f"\n\n\t\t{self.__administered_department_name}\nDepartment Scheduler System.\n",
            "Available options:\n\n"
        ]

        options_text = [
            "List given course sections in this semester.",
            "Increase course section capacity.",
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
                    # TODO: print course section information 
                    self.get_controller_instance().print_all_given_course_sections_with_their_capacities()
                case 1:  # Increase course section capacity
                    self.get_controller_instance().select_and_increase_course_section_capacity()
                case _:
                    logger.info(f"User entered an option:{choice} which is out of range 1 - {len(options_text)}.")
                    self.get_controller_instance().menu_operation(f"Please enter an integer between 1 and {len(options_text)}\n")


    def get_json(self):
        return  {
            'id': self.get_id(),
            'name':self.get_name(),
            'eMail':self.get_e_mail(),
            'password': self.get_password(),
            "departmentName" : self.__administered_department_name
        }
