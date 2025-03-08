from Staff import Staff
from typing import List
import os, json

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class Admin(Staff):

    def __init__(self, id: int, name: str, email: str, password: str, controller_instance):
        super().__init__(id, name, email, password, controller_instance)
        logger.debug(f"Initialized Advisor object. ID: {self.get_id()}, NAME: {self.get_name()}, EMAIL: {self.get_e_mail()}, PASSWORD: {self.get_password()}.")
        logger.debug("Leaving __init__.")


    def main_menu(self) -> None: 
        logger.info(f"Entering main_menu for Admin.")
        logger.debug(f"Entered main_menu for Admin: {self}, Named: {self.get_name()}.")
        prefix_text = [
            "\n\nAdmin System.\n",
            "Available options:\n\n"
        ]

        options_text = [
            "Create a new course.",
            "Delete a course.",
            "Create a new user.",
            "Delete a user.",
            "Create a new classroom.",
            "Delete a classroom.",
            "Exit.\n"
        ]

        suffix_text = ["Please choose an option: "]

        while True:
            # Display the menu and get the user's choice.
            logger.info(f"Choose an option prompt for main_menu of advisor.")
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text, suffix_text)
            logger.info(f"Chosen value: {choice}.")

            if choice == len(options_text) - 1:  # Exit option
                logger.info(f"Chosen 'Exit' ({choice}).")
                break

            match choice:
                case 0:  
                    logger.info(f"Chosen '{options_text[0]}'.")
                    self.create_new_course()
                case 1:  
                    logger.info(f"Chosen '{options_text[1]}'.")
                    self.delete_course()
                case 2: 
                    logger.info(f"Chosen '{options_text[2]}'.")
                    self.create_new_user()
                case 3:  
                    logger.info(f"Chosen '{options_text[3]}'.")
                    self.delete_user()
                case 4:  
                    logger.info(f"Chosen '{options_text[4]}'.")
                    self.create_new_classroom()
                case 5:  
                    logger.info(f"Chosen '{options_text[5]}'.")
                    self.delete_classroom()
                case _:
                    logger.info(f"Chosen option ({choice}) is not valid for current selection list.")
                    self.get_controller_instance().menu_operation(f"Please select an option between 1 and {len(options_text)}\n")
        logger.debug("Leaving main_menu.")


    def create_new_course(self) -> None:
        logger.info(f"Entering 'Create new course' for Admin.")
        logger.debug(f"Entered 'create_new_course' for Admin: {self}, Named: {self.get_name()}.")
        prefix_text = [
            "\n\nCreate a Course.\n",
            "Available options:\n\n"
        ]

        options_text = [
            "Create a new mandatory course.",
            "Create a new technical elective course.",
            "Create a new engineering project course.",
            "Exit.\n"
        ]

        suffix_text = ["Please choose an option: "]

        while True:
            # Display the menu and get the user's choice.
            logger.info(f"Choose course type prompt.")
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text, suffix_text)
            logger.info(f"Chosen value: {choice}.")

            if choice == len(options_text) - 1:  # Exit option
                logger.info(f"Chosen 'Exit' ({choice}).")
                break
            
            directory : str = "./JSON/Course"

            match choice:
                case 0:  
                    logger.info(f"Chosen 'Mandatory'.")
                    course_type : str = "Mandatory"
                case 1:  
                    logger.info(f"Chosen 'TECourse'.")
                    course_type : str = "TECourse"
                case 2: 
                    logger.info(f"Chosen 'EngineeringProjectCourse'.")
                    course_type : str = "EngineeringProjectCourse"
                case _:
                    logger.info(f"Chosen option ({choice}) is not valid for current selection list.")
                    self.get_controller_instance().menu_operation(f"Please select an option between 1 and {len(options_text)}\n")
                    
            # get course information
            while True:
                try:
                    id : int =  self.get_controller_instance().get_next_int("Please enter course id.")
                    break
                except ValueError as e:
                    logger.warning(f"Course id must be an integer.")
                    self.get_controller_instance().menu_operation(f"Course id has to be an integer.\n")
                    
            
            name : str = self.get_controller_instance().get_next_line("Please enter course name.")
            
            while True:
                try:
                    credits : int =  self.get_controller_instance().get_next_int("Please enter course credits.")
                    break
                except ValueError as e:
                    logger.warning(f"Course credits must be an integer.")
                    self.get_controller_instance().menu_operation(f"Course credits has to be an integer.\n")
            
            
            logger.info(f"Chosen ID: {id}, NAME: {name}, COURSE_CREDITS: {credits} for course.")
            
            if course_type == "EngineeringProjectCourse":
                while True:
                    try:
                        credits_required : int =  self.get_controller_instance().get_next_int("Please enter credits required to take this course.")
                        break
                    except ValueError as e:
                        logger.warning(f"Credits required has to be an integer.\n")
                        self.get_controller_instance().menu_operation(f"Credits required has to be an integer.\n")
                    
                logger.info(f"Credits required for EngineeringProject: {credits_required}.")
                #semester_required : int =  self.get_controller_instance().get_next_int("Please enter minimum semester to take this course.")
            else:
                input_stream : str = self.get_controller_instance().get_next_line("Please enter prerequisite courses' ids separated with blank space.")
                logger.info(f"Prerequisites for Mandatory/TE course: {input_stream}.")
                prerequisites : List = list(map(int, input_stream.split()))
            
                
            while True:
                try:
                    number_of_lecture_hours : int =  self.get_controller_instance().get_next_int("Please enter number of lecture hours.")
                    break
                except ValueError as e:
                    logger.warning(f"Number of lecture hours has to be an integer.\n")
                    self.get_controller_instance().menu_operation(f"Number of lecture hours has to be an integer.\n")
            
            logger.info(f"Number of lecture hours for course: {number_of_lecture_hours}.")
            lecturer_name : str = self.get_controller_instance().get_next_line("Please enter lecturer name.")
            logger.info(f"Lecturer name for course: {lecturer_name}.")
            
            while True:
                try:
                    course_capacity : int =  self.get_controller_instance().get_next_int("Please enter course capacity.")
                    break
                except ValueError as e:
                    logger.warning(f"Course capacity has to be an integer.\n")
                    self.get_controller_instance().menu_operation(f"Course capacity has to be an integer.\n")
            
            logger.info(f"Course capacity for course: {course_capacity}.")
            
            if course_type != "EngineeringProjectCourse":
                data = {
                    "id": id,
                    "name": name,
                    "credits": credits,
                    "numberOfLectureHour" : number_of_lecture_hours,
                    "CourseType": course_type,
                    "prerequisite" : prerequisites, 
                    "courseSection" : [
                    {
                        "lecturer" : lecturer_name,
                        "courseCapacity" : course_capacity,
                        "courseSectionNumber" : 1,  
                        "lectureHour" : []
                    }
                    ]  
                }
            else:
                data = {
                    "id": id,
                    "name": name,
                    "credits": credits,
                    "creditsRequired" : credits_required,
                    "numberOfLectureHour" : number_of_lecture_hours,
                    "CourseType": course_type,
                    "prerequisite" : prerequisites, 
                    "courseSection" : [
                    {
                        "lecturer" : lecturer_name,
                        "courseCapacity" : course_capacity,
                        "courseSectionNumber" : 1,  
                        "lectureHour" : []
                    }
                    ]  
                }

            file_name = f"{id}.json"
            file_path = os.path.join(directory, file_name)

            # creating the directory in create mode
            os.makedirs(directory, exist_ok=True)

            # Write the data to the JSON file
            with open(file_path, 'w') as json_file:
                json.dump(data, json_file, indent=4)   

            logger.info(f"Serialized and written data for course.")
        logger.debug("Leaving create_new_course.")


    def create_new_user(self) -> None:
        logger.info(f"Entering create new user for Admin.")
        logger.debug(f"Entered create new user for Admin: {self}, Named: {self.get_name()}.")
        prefix_text = [
            "\n\nCreate a User.\n",
            "Available options:\n\n"
        ]

        options_text = [
            "Create a new student.",
            "Create a new department scheduler.",
            "Create a new advisor.",
            "Create a new department head.",
            "Exit.\n"
        ]

        suffix_text = ["Please choose an option: "]

        while True:
            # Display the menu and get the user's choice.
            logger.info(f"Choose an option prompt for main_menu of advisor.")
            choice = self.get_controller_instance().menu_operation(prefix_text, options_text, suffix_text)
            logger.info(f"Chosen value: {choice}.")

            if choice == len(options_text) - 1:  # Exit option
                logger.info(f"Chosen 'Exit' ({choice}).")
                break
            
            directory : str = "./JSON/"

            match choice:
                case 0:  
                    logger.info(f"Chosen '{options_text[0]}'.")
                    directory += "Student"
                case 1:  
                    logger.info(f"Chosen '{options_text[1]}'.")
                    directory += "DepartmentScheduler"
                case 2: 
                    logger.info(f"Chosen '{options_text[2]}'.")
                    directory += "Advisor"
                case 3:
                    logger.info(f"Chosen '{options_text[3]}'.")
                    directory += "DepartmentHead"
                case _:
                    logger.info(f"User entered an option:{choice} which is out of range 1 - {len(options_text)}.")
                    #logger.info(f"Chosen option ({choice}) is not valid for current selection list.")
                    self.get_controller_instance().menu_operation(f"Please enter an integer between 1 and {len(options_text)}\n")

            while True:
                try:
                    id : int =  self.get_controller_instance().get_next_int("Please enter user id.")
                    break
                except ValueError as e:
                    logger.warning(f"Course id has to be an integer.\n")
                    self.get_controller_instance().menu_operation(f"Course id has to be an integer.\n")
            
            name : str = self.get_controller_instance().get_next_line("Please enter user name.")
            password : str = self.get_controller_instance().get_next_line("Please enter password.")
            e_mail : str = self.get_controller_instance().get_next_line("Please enter e mail.")
            logger.info(f"Chosen ID: {id}, NAME: {name}, PASSWORD: {password}, E_MAIL: {e_mail} for user.")
            
            data : str = ""
            if directory == "./JSON/DepartmentScheduler" or directory == "./JSON/DepartmentHead":
                department_name : str = self.get_controller_instance().get_next_line("Please enter department name.")
                logger.info(f"Chosen DEPARTMENT_NAME: {department_name} for user.")
                data = {
                    "password": password,
                    "name": name,
                    "id": id,
                    "eMail": e_mail,
                    "departmentName" : department_name
                }
                logger.info(f"Serialized data for user.")
                
            elif directory == "./JSON/Student":   
                while True:
                    try:
                        advisor_id : int =  self.get_controller_instance().get_next_int("Please enter advisor id.")
                        break
                    except ValueError as e:
                        logger.warning(f"Advisor id has to be an integer.\n")
                        self.get_controller_instance().menu_operation(f"Advisor id has to be an integer.\n")
                
                while True:
                    try:
                        semester_number : int =  self.get_controller_instance().get_next_int("Please enter semester number.")
                        break
                    except ValueError as e:
                        logger.warning(f"Semester number has to be an integer.\n")
                        self.get_controller_instance().menu_operation(f"Semester number has to be an integer.\n")
                
                logger.info(f"Chosen ADVISOR_ID: {advisor_id}, SEMESTER_NUMBER: {semester_number} for user.")
                
                data = {
                    "AdvisorId": advisor_id,
                    "password": password,
                    "name": name,
                    "semester": semester_number,
                    "id": id,
                    "eMail": e_mail,
                    "CourseSectionsTaken" : []
                }
                logger.info(f"Serialized data for user.")

            elif directory == "./JSON/Advisor": 
                data = {
                    "password": password,
                    "name": name,
                    "id": id,
                    "eMail": e_mail
                }
                logger.info(f"Serialized data for user.")
                
            else:
                logger.error(f"Chosen option ({choice}) is not valid for current selection list.")
                logger.error(f"How this is possible is beyond me. You are on your own.")
                self.get_controller_instance().menu_operation(f"Path is not found.{directory}")

            file_name = f"{id}.json"
            file_path = os.path.join(directory, file_name)
            # creating the directory in create mode
            os.makedirs(directory, exist_ok=True)
            # Write the data to the JSON file
            with open(file_path, 'w') as json_file:
                json.dump(data, json_file, indent=4)  
            
            logger.info(f"Written data for user.")
        logger.debug("Leaving create_new_user.")


    def create_new_classroom(self) -> None:
        logger.info(f"Entering create new classroom for Admin.")
        logger.debug(f"Entered create_new_classroom for Admin: {self}, Named: {self.get_name()}.")
        
        classroom_name : str = self.get_controller_instance().get_next_line("Please enter classroom name.")
        
        logger.info(f"Chosen CLASSROOM_NAME: {classroom_name}")
        
        while True:
            try:
                classroom_seating_capacity : int =  self.get_controller_instance().get_next_int("Please enter classroom' seating capacity.")
                break
            except ValueError as e:
                logger.warning(f"Classroom's seating capacity has to be an integer.\n")
                self.get_controller_instance().menu_operation(f"Classroom's seating capacity has to be an integer.\n")
        
        logger.info(f"Chosen SEATING_CAPACITY: {classroom_seating_capacity}")
    
        data = {
            "classroomID" : classroom_name,
            "classroomSeatingCapacity" : classroom_seating_capacity
        }
        logger.info(f"Serialized data for classroom.")
        
        directory = "./JSON/Classroom"
        file_name = f"{classroom_name}.json"
        file_path = os.path.join(directory, file_name)
        # creating the directory in create mode
        os.makedirs(directory, exist_ok=True)
        # Write the data to the JSON file
        with open(file_path, 'w') as json_file:
            json.dump(data, json_file, indent=4)
        logger.info(f"Written data for classroom.")
        logger.debug("Leaving create_new_classroom.")







    #TODO: These 3 can be bunched up into a single delete_file method by printing the necessary messages in main_menu match-case statement.
    def delete_course(self) -> None:
        logger.info(f"Entering delete course for Admin.")
        logger.debug(f"Entered delete_course for Admin: {self}, Named: {self.get_name()}.")
        self.get_controller_instance().menu_operation("\n\nDelete a Course.\n")
        
        while True:
            try:
                course_id : int =  self.get_controller_instance().get_next_int("Please enter course id that you want to delete, enter 0 if you do not want to delete any course:")
                break
            except ValueError as e:
                logger.warning(f"Course id has to be an integer.\n")
                self.get_controller_instance().menu_operation(f"Course id has to be an integer.\n")
        
        logger.info(f"Chosen course id to be deleted, COURSE_ID: {course_id}.")
        
        if course_id == 0:
            logger.info(f"Chosen course id is '0'.")
            logger.debug("Leaving delete_course.")
            return
        
        self.delete_file(str(course_id))
        logger.debug("Leaving delete_course.")

    #TODO: These 3 can be bunched up into a single delete_file method by printing the necessary messages in main_menu match-case statement.
    def delete_classroom(self) -> None:
        logger.info(f"Entering delete classroom for Admin.")
        logger.debug(f"Entered delete_classroom for Admin: {self}, Named: {self.get_name()}.")
        self.get_controller_instance().menu_operation("\n\nDelete a Classroom.\n")
        
        classroom_name : str =  self.get_controller_instance().get_next_line("Please enter classroom id that you want to delete enter 0 if you do not want to delete any classroom:")
        logger.info(f"Chosen classroom name to be deleted, CLASSROOM_NAME: {classroom_name}.")
        
        if classroom_name is None:
            logger.info(f"Chosen classroom name is '0'.")
            logger.debug("Leaving delete_classroom.")
            return
        
        self.delete_file(classroom_name)
        logger.debug("Leaving delete_classroom.")

    #TODO: These 3 can be bunched up into a single delete_file method by printing the necessary messages in main_menu match-case statement.
    def delete_user(self) -> None:
        logger.info(f"Entering delete user for Admin.")
        logger.debug(f"Entered delete_user for Admin: {self}, Named: {self.get_name()}.")
        self.get_controller_instance().menu_operation("\n\nDelete a User.\n")
        
        while True:
            try:
                user_id : int =  self.get_controller_instance().get_next_int("Please enter user id that you want to delete enter 0 if you do not want to delete any user:")
                break
            except ValueError as e:
                logger.warning(f"User id has to be an integer.\n")
                self.get_controller_instance().menu_operation(f"User id has to be an integer.\n")
        
        logger.info(f"Chosen user id to be deleted, USER_ID: {user_id}.")
        
        if user_id == 0:
            logger.info(f"Chosen user id is '0'.")
            logger.debug("Leaving delete_user.")
            return
        
        self.delete_file(str(user_id))
        logger.debug("Leaving delete_user.")



    #TODO: delete operations are bugged. if i have student with id 1. course with id 1. both will be deleted.
    #TODO: or just make sure no two thing never ever get the same id.
    def delete_file(self, file_name : str):
        logger.info(f"Entering delete file for Admin.")
        logger.debug(f"Entered delete_file for Admin: {self}, Named: {self.get_name()}.")
        file_name += ".json"
        for root, _, files in os.walk("./JSON"):
            for file in files:
                logger.debug(f"Found FILE: {file}.")
                if file == file_name:
                    logger.info(f"Matched FILE: {file}.")
                    file_path = os.path.join(root, file)
                    os.remove(file_path)
                    logger.info((f"Deleted: {file} from {root}"))
                    self.get_controller_instance().menu_operation(f"Deleted: {file} from {root}")
                    logger.debug("Leaving delete_file.")
                    return
                logger.debug(f"Miss.")
        self.get_controller_instance().menu_operation(f"There is not any file name matched with given name {file_name}.\nNo file is deleted.")
        logger.info(f"User tried to delete a file {file_name} which does not exist, deletion failed.")
        logger.debug("No matches.")
        logger.debug("Leaving delete_file.")

    def get_json(self):
        logger.debug(f"Entered get_json for Admin: {self}, Named: {self.get_name()}.")
        logger.debug("Leaving get_json.")
        return  {
            'id': self.get_id(),
            'name':self.get_name(),
            'eMail':self.get_e_mail(),
            'password': self.get_password(),
        }