
from typing import List, Dict
import math
from Person import Person
from Advisor import Advisor
from LectureHour import LectureHour
from CourseSection import CourseSection

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class Student(Person):
    MAX_COURSES = 5

    def __init__(self, id : int, name : str, e_mail : str, password : str, semester : int, controller_instance):
        super(Student, self).__init__(id, name, e_mail,password, controller_instance)
        logger.info(f"Super type' 'Person' constructor is called. {__name__} type object is created.")
        # set parameters initialized inside of the super type constructor as private
        self.__id : int = id
        self.__name : str = name
        self.__e_mail : str = e_mail
        self.__password : str = password
        self.__controller_instance = controller_instance

        self.__advisor : Advisor = None
        self.__semester : int = semester
        self.__course_sections_of_student : Dict = {}
        self.__course_sections_of_student_with_letter_grades : Dict = {}    # course section(CourseSection) - letter grade(str)

        from Transcript import Transcript
        self.__transcript : Transcript = Transcript(semester, self.__course_sections_of_student_with_letter_grades)
    
    
    def get_student_course_sections_with_status(self, status : str = None) -> List:
        if (self.__course_sections_of_student is None):
            logger.warning(f"Student with the id of {self.get_id()} does not have any course section.")
            return None
        
        if(status is None ):
            course_section_list = []
            for course_section in self.__course_sections_of_student.keys():
                course_section_list.append(course_section)
            return course_section_list
        else:
            course_section_list = []
            for course_section , stat in self.__course_sections_of_student.items():
                if status == stat:
                    course_section_list.append(course_section)
            return course_section_list


    def add_course_section(self ,course_section) -> None:
        previously_chosen_course_count = len(self.get_student_course_sections_with_status('pending')) 
        + len(self.get_student_course_sections_with_status('approved'))
        + len(self.get_student_course_sections_with_status('waiting'))
        + len(self.get_student_course_sections_with_status('selected'))
        
        logger.debug(f"Checking previously chosen course count, result = {previously_chosen_course_count}")
        if previously_chosen_course_count <= self.MAX_COURSES:
            if self.check_schedule_available(course_section):
                self.__course_sections_of_student[course_section] = 'selected'


    def change_status(self, course_section, status) -> None:
        if(status == 'selected'):
            logger.info(f"Course section{course_section.get_id()} is added to student' {self.get_id()} course sections.")
            self.add_course_section(course_section)
        logger.info(f"Updating course' status to {status}.")
        self.__course_sections_of_student[course_section] = status


    def list_student_courses_with_status(self, status = None) -> None:
        prefix_text : List = []

        course_section_with_status_list = self.get_student_course_sections_with_status(status)
        if status is None:
            prefix_text.append("All courses taken by " + self.get_name() + ".\n")
        else:
            prefix_text.append(self.get_name() + "'s " + status + " courses.\n")
        
        if(len(course_section_with_status_list) == 0):
            prefix_text.append("Student: " + self.get_name() + " with ID: " + str(self.get_id())
                    + " currently doesn't have any " + status + " courses.\n")
        else:
            i = 1
            for course_section in course_section_with_status_list:
                prefix_text.append(f"{i:^3}| "+ f"{course_section.get_course().get_name():<40}| " + f"{course_section.get_id()}"
                                + ", Status: " + self.__course_sections_of_student[course_section]
                                + ", Credits: " + str(course_section.get_course().get_credits())
                                + ", Course ID: " + str(course_section.get_course().get_id())
                                + "\n")
                i += 1
        self.get_controller_instance().menu_operation(prefix_text)


    def main_menu(self) -> None:
        prefix_text = ["\n\nStudent system.\n",
                        "Available options:\n\n"]
        options_text = ["List offered courses in this semester.",
                "List pending courses.",
                "List approved courses.",
                "List finished courses.",
                "List selected courses.",
                "Select Courses.",
                "Send selected courses for advisor approval.",
                "Show weekly schedule",
                "Show transcript information.",
                "Exit.\n"]
        suffix_text = ["Please choose an option: \n"]

        while(True):
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text ,suffix_text)

            if choice == len(options_text) -1:
                self.get_controller_instance().menu_operation(["You are leaving the system...\n"])
                break
            match choice:
                case 0:
                    self.get_controller_instance().print_all_department_courses(self.__course_sections_of_student)
                case 1:
                    self.list_student_courses_with_status("pending")
                case 2:
                    self.list_student_courses_with_status("approved")
                case 3:
                    self.list_student_courses_with_status("finished")
                case 4:
                    self.list_student_courses_with_status("selected")
                case 5:
                    self.__select_course_sections_for_semester()
                case 6:
                    self.get_controller_instance().menu_operation(["All selected courses are sended to advisor approval.\n"])
                    for course_section, status in self.__course_sections_of_student.items():
                        if status == 'selected':
                            self.__course_sections_of_student[course_section] = 'pending'
                case 7:
                    self.print_weekly_schedule()
                case 8:
                    self.print_transcript()
                case _:
                    logger.info(f"User entered an option:{choice} which is out of range 1 - {len(options_text)}.")
                    self.get_controller_instance().menu_operation(f"Please enter an integer between 1 and {len(options_text)}\n")
            logger.info(f"Student selected {choice + 1} from the main menu.")


    def __select_course_sections_for_semester(self) -> None:
        previously_chosen_course_count  = len(self.get_student_course_sections_with_status("pending"))
        previously_chosen_course_count += len(self.get_student_course_sections_with_status("selected"))
        previously_chosen_course_count += len(self.get_student_course_sections_with_status("approved"))
        previously_chosen_course_count += len(self.get_student_course_sections_with_status("waiting"))

        while(True):
            section_selection = 1
            chosen_course = self.get_controller_instance().select_a_course_from_all_department_courses(self.get_all_courses())

            if chosen_course is None:
                break

            if self.MAX_COURSES <= previously_chosen_course_count :
                self.get_controller_instance().menu_operation(["Course is not selected because at most " + str(self.MAX_COURSES) + " courses can be selected in a semester."])
                break
            if chosen_course in self.get_all_courses():
                logger.info(f"User tried to take the same course {chosen_course.get_id()} again but system did not allowed it.")
                self.get_controller_instance().menu_operation([chosen_course.get_name() + " is already in your courses. Please choose another course.\n"])
            else:
                if chosen_course.check_prerequisites_met(self):
                    if len(chosen_course.get_course_sections()) > 1:
                        self.get_controller_instance().menu_operation(["Please select a section.\n"])
                        for  i in range(len(chosen_course.get_course_sections())):
                            self.get_controller_instance().menu_operation([str(i + 1) + "- " + chosen_course.get_name() + "."+str(i + 1)+"\n"])
                        
                        while True:
                            try:
                                section_selection = self.get_controller_instance().get_next_int()
                                break
                            except ValueError as e:
                                logging.warning(f"Selection has to be an integer.\n")
                                self.get_controller_instance().menu_operation(f"Selection has to be an integer.\n")
                        
                        
                        self.__course_sections_of_student[chosen_course.get_course_sections()[section_selection - 1]] = 'selected'
                        self.get_controller_instance().menu_operation(f"{chosen_course.get_id()}.{section_selection} is selected successfully.\n")
                        previously_chosen_course_count += 1
                    elif len(chosen_course.get_course_sections()) == 1:
                        self.__course_sections_of_student[chosen_course.get_course_sections()[0]] = 'selected'
                        self.get_controller_instance().menu_operation(f"{chosen_course.get_id()}.{1} is selected successfully.\n")
                        previously_chosen_course_count += 1
                    else:
                        pass
                else: 
                    pass


    def set_course_section_status_to_approved_or_rejected(self) -> None:
        while True:
            pending_course_sections = self.get_student_course_sections_with_status("pending")
            self.get_controller_instance().menu_operation(["\n\n"])
            self.list_student_courses_with_status("pending") # This isn't really meant as an input method so it doesn't print the exit option.
            self.get_controller_instance().menu_operation([str(len(pending_course_sections) + 1) + "- Exit.\n\n", "Please select a course section: "])

            
            while True:
                try:
                    result = self.get_controller_instance().get_next_int() - 1 
                    break
                except ValueError as e:
                    logging.warning(f"Selection has to be an integer.\n")
                    self.get_controller_instance().menu_operation(f"Selection has to be an integer.\n")
            
            if result >= 0 and result <  len(pending_course_sections):
                prefix_text = ["\n\nApprove - Reject " + pending_course_sections[result].get_name() + "\n"]
                options_text = ["Approve.",
                            "Reject",
                            "Skip\n"]
                suffix_text = ["Please choose an option: \n"]

                while True:
                    option = self.get_controller_instance().menu_operation(prefix_text,options_text,suffix_text)

                    if option == 0:
                        self.change_status(pending_course_sections[result], "approved")
                        pending_course_sections[result].add_student(self)
                        break
                    if option == 1:
                        self.change_status(pending_course_sections[result], "rejected")
                        break
                    if option == 2:
                        break

                    # Exhausted all possibilities. The choice is invalid.
                    self.get_controller_instance().menu_operation(["Please choose a valid option.\n"])
            else:
                if result < 0:
                    self.get_controller_instance().menu_operation(["Please choose a valid option.\n"])
                if result == len(pending_course_sections):
                    break


    def check_schedule_available(self, course_section_to_check_against : CourseSection) -> bool:
        taken_course_section_list = []

        taken_course_section_list.extend(self.get_student_course_sections_with_status("selected"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("pending"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("approved"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("waiting"))

        schedule = []

        for course_section in taken_course_section_list:
            lecture_hours = course_section.get_lecture_hours()
            for lecture_hour in lecture_hours:
                schedule.append(lecture_hour.get_lecture_time_table_number())

        new_course_hours  = []
        for lecture_hour in course_section_to_check_against.get_lecture_hours():
            new_course_hours.append(lecture_hour.get_lecture_time_table_number())
        
        for x in schedule:
            for y in new_course_hours:
                if x == y:
                    return False

        return True


    def print_weekly_schedule(self) -> None:
        taken_course_section_list = []
        taken_course_section_list.extend(self.get_student_course_sections_with_status("selected"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("pending"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("approved"))
        taken_course_section_list.extend(self.get_student_course_sections_with_status("waiting"))

        schedule = [[None] * 10 for i in range(7)]
        size = len(taken_course_section_list)

        for taken_course_section in taken_course_section_list:
            lecture_hours = taken_course_section.get_lecture_hours()
            for lecture_hour in lecture_hours:
                schedule[lecture_hour.get_lecture_day()][lecture_hour.get_lecture_hour_number()]= str(taken_course_section.get_course().get_id()) + '.'+ str(taken_course_section.get_course_section_number()) 

        schedule_text_list = []

        for i in range(10):
            string_builder = []
            for j in range(7):
                if schedule[j][i] is None:
                    string_builder.append("     -     ")
                    continue
                else:
                    string_builder.append(f"{schedule[j][i]:^11}")
            string_builder.append("\n")
            schedule_text_list.append(' '.join(string_builder))
        
        self.get_controller_instance().menu_operation(schedule_text_list)


    def get_all_course_sections_with_letter_grades(self) -> None:
        return self.__course_sections_of_student_with_letter_grades

    def get_all_course_sections(self):
        return self.__course_sections_of_student

    def get_all_courses(self) -> List:
        student_courses = {}
        for key,value in self.__course_sections_of_student.items():
            student_courses[key]=value
        return student_courses


    def get_advisor(self) -> Advisor:
        return self.__advisor


    def get_id(self) -> int:
        return self.__id


    def get_name(self) -> str:
        return self.__name


    def get_email(self) -> str:
        return self.__e_mail


    def set_advisor(self, advisor:Advisor) -> None:
        self.__advisor = advisor


    def set_course_section_status(self, course_section, status) -> None:
        self.__course_sections_of_student[course_section]= status
        

    def set_course_section_letter_grade(self, course_section, letter_grade : str) -> None:
            self.__course_sections_of_student_with_letter_grades[course_section]= letter_grade


    def has_pending_course(self) -> bool:
        if 'pending' in self.__course_sections_of_student.values():
            logger.info(f"Student {self.get_id()} has pending courses.")
            return True
        else:
            logger.info(f"Student {self.get_id()} does not have any pending courses.")
            return False

    def get_semester(self)-> int:
        return self.__semester 

    def get_finished_course_credits(self)-> int:
        return sum(course.get_credits() for course in self.get_student_course_sections_with_status("finished"))

    def get_finished_courses(self): # TODO: Create a List for the finished courses 
        return [course_seciton.get_course() for course_seciton in self.get_student_course_sections_with_status("finished") ]


    def print_transcript(self) -> None:
        self.get_controller_instance().menu_operation(f"\n\t\t\tTranscript of {self.__name}\n")
        self.get_controller_instance().menu_operation(f"Year : {math.ceil(self.__semester / 2)}")
        self.get_controller_instance().menu_operation(f"Semester : {self.__semester}")
        self.get_controller_instance().menu_operation(f"\n-----------------------------------------------------------------------------------------------")
        self.get_controller_instance().menu_operation(f"-------------------------------------------------------------------------------------------------\n")
        self.get_controller_instance().menu_operation(self.__transcript.__str__())


    def get_json(self):
        student_data = {
            'AdvisorId': self.get_advisor().get_id(),
            'password': self.get_password(),
            'name': self.get_name(),
            'semester' : self.get_semester(),
            'id': self.get_id(),
            'eMail': self.get_email(),
        }

        course_sections_taken = []
        course_section : CourseSection
        for course_section, status in self.get_all_course_sections().items():
            course_section_data = [
                str(course_section.get_course().get_id()),
                status,
                str(course_section.get_course_section_number()),
                self.get_all_course_sections_with_letter_grades().get(course_section)
            ]
            course_sections_taken.append(course_section_data)
        student_data['CourseSectionsTaken']=course_sections_taken

        return student_data
    