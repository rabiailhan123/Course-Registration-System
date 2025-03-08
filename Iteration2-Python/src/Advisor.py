#import json
from typing import List
from Lecturer import Lecturer

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class Advisor(Lecturer):
    def __init__(self, id, name, e_mail, password, controller_instance):
        super(Advisor,self).__init__(id, name, e_mail, password, controller_instance)
        
        # set parameters initialized inside of the super type constructor as private
        self.__id : int = id
        self.__name : str = name
        self.__e_mail : str = e_mail
        self.__password : str = password
        self.__controller_instance = controller_instance
        self.__advisee_list = []
        
        logger.debug(f"Initialized Advisor object. ID: {self.__id}, NAME: {self.__name}, EMAIL: {self.__e_mail}, PASSWORD: {self.__password}, ADVISEE_LIST {self.__advisee_list}.")
        logger.debug("Leaving __init__.")


    def add_advisee(self, advisee):
        logger.debug(f"Trying to append Advisee: {advisee}, Named: {advisee.get_name()} to Advisor: {self}, Named: {self.__name}.")
        self.__advisee_list.append(advisee)
        logger.debug(f"Successfully appended.")
        logger.debug("Leaving add_advisee.")

    def get_advisee_list(self):
        logger.debug(f"Getting ADVISEE_LIST: {self.__advisee_list} of Advisor: {self}, Named: {self.__name}.")
        logger.debug("Leaving get_advisee_list.")
        return self.__advisee_list

    def main_menu(self):
        logger.info(f"Entering main_menu for Advisor.")
        logger.debug(f"Entered main_menu for Advisor: {self}, Named: {self.__name}.")
        
        prefix_text = [f"\n\nAdvisor system. Hello {super().get_name()}\n", "Available options:\n\n"]
        options_text = [
            "List students who selected courses and sended to be approved.",
            "View weekly schedule of a student.",
            "Exit.\n"
        ]
        suffix_text = ["Please choose an option: "]

        while True:
            logger.info(f"Choose an option prompt for main_menu of advisor.")
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text, suffix_text)
            logger.info(f"Chosen value: {choice}.")
            

            if choice == len(options_text) - 1:
                logger.info(f"Chosen 'Exit' ({choice}).")
                break

            if choice == 0:
                logger.info(f"Chosen '{options_text[0]}'.")
                self.list_students_with_pending_courses_operation()
            elif choice == 1:
                logger.info(f"Chosen '{options_text[1]}'.")
                self.view_student_weekly_schedule()
        logger.debug("Leaving main_menu.")
        


    def list_students_with_pending_courses_operation(self):
        logger.info(f"Trying to print students that still have pending courses.")
        logger.debug(f"Entered 'List students who selected courses and sended to be approved.' for Advisor: {self}, Named: {self.__name}.")
        students_with_pending_courses_list = [] #TODO: use fixed typing

        title_text = ["\n\nStudents waiting for registration approval:\n"]
        suffix_text = ["Please select a student: "]

        while True:
            options_text = []
            students_with_pending_courses_list = []
            for student in self.__advisee_list:
                if student.has_pending_course():
                    students_with_pending_courses_list.append(student)
                    logger.debug(f"Added Student: {student}, Named: {student.get_name()} to 'students_with_pending_courses_list'.")
                    options_text.append(student.get_name())
                    logger.debug(f"Added Student: {student}, Named: {student.get_name()} to 'options_text'.")
                    

            if not students_with_pending_courses_list:
                logger.info(f"None of the advisees of advisor Named: {self.__name} have pending courses.")
                title_text.append("There are currently no students who require course registration approval.\n")

            options_text.append("Exit.\n")

            chosen_option = self.get_controller_instance().menu_operation(title_text, options_text, suffix_text)
            logger.info(f"Chosen option {chosen_option}.")
            

            if chosen_option >= 0:
                if chosen_option == len(students_with_pending_courses_list):
                    logger.info(f"Chosen option 'Exit' ({chosen_option}).")
                    break

                students_with_pending_courses_list[chosen_option].set_course_section_status_to_approved_or_rejected()
                logger.info(f"Chosen student Named: ({students_with_pending_courses_list[chosen_option].get_name()}).")
                
            else:
                self.get_controller_instance().menu_operation(["Please choose a valid option.\n"])
                logger.info(f"Chosen option ({chosen_option}) is not valid for current selection list.")
                
        logger.debug("Leaving list_students_with_pending_courses_operation.")
                


    def view_student_weekly_schedule(self):
        logger.info(f"Trying to print students weekly schedule.")
        logger.debug(f"Entered 'View weekly schedule of a student.' for Advisor: {self}, Named: {self.__name}.")
        
        title_text = ["\n\nAdvisee List:\n"]
        options_text = []
        suffix_text = ["Please select a student: "]

        for student in self.__advisee_list:
            options_text.append(student.get_name())

        if not options_text:
            logger.info(f"Advisor Named: {self.__name} doesn't have any advisee's.")
            title_text.append("You have no advisee's.\n")

        options_text.append("Exit.\n")

        while True:
            chosen_option = self.get_controller_instance().menu_operation(title_text, options_text, suffix_text)
            logger.info(f"Chosen option {chosen_option}.")

            if chosen_option >= 0:
                if chosen_option == len(options_text) - 1:
                    logger.info(f"Chosen option 'Exit' ({chosen_option}).")
                    break

                self.__advisee_list[chosen_option].print_weekly_schedule()
                logger.info(f"Chosen student Named: ({self.__advisee_list[chosen_option].get_name()}).")
            else:
                self.get_controller_instance().menu_operation(["Please choose a valid option.\n"])
                logger.info(f"Chosen option ({chosen_option}) is not valid for current selection list.")

        logger.debug("Leaving view_student_weekly_schedule.")
        
        
    def get_json(self):
        logger.debug(f"Entered 'get_json()' for Advisor: {self}, Named: {self.__name}.")
        logger.debug("Leaving get_json.")
        return  {
                'id': self.get_id(),
                'name': self.get_name(),
                'eMail': self.get_e_mail(),
                'password': self.get_password(),
            }
