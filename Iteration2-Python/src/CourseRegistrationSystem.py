
from typing import List


from typing import List
from LoggingConfig import setup_logging     # LoggingConfig class will be used to handle logging operations more centrally

import logging
logger = logging.getLogger(__name__)

class CourseRegistrationSystem(object):
    # class attributes
    # none indicates that there is not any instance of the class
    instance = None

    # constructor of the singleton
    def __init__(self):
        from StudentAffairs import StudentAffairs
        self.input_scanner = input # TODO buna gerek olmayabilir
        self.all_department_courses_list : List = []
        self.all_classrooms : List = []
        self.student_affairs : StudentAffairs =  StudentAffairs(1111111, "Ali Veli", "ali.veli@marmara.edu.tr", "a",self)


    # checks if the singleton is created once or not,
    # if not calls the constructor
    @staticmethod
    def get_controller_instance():
        if CourseRegistrationSystem.instance is None:
            CourseRegistrationSystem.instance = CourseRegistrationSystem()
        return CourseRegistrationSystem.instance
    

    @staticmethod
    def main():

        controller_instance = CourseRegistrationSystem.get_controller_instance()
        from FileOperationsClass import FileOperationsClass
        fo = FileOperationsClass(controller_instance)
        fo.read_json("./JSON")
        #controller_instance.all_classrooms = list(fo.classrooms.values()) # TODO: classrooms u private yap ve getter ile al  
        #controller_instance.all_department_courses_list = list(fo.course_map.values()) # i have moved these into FileOperatios
        fo.people.append(controller_instance.student_affairs)
        
        while True:
            fo.read_json("./JSON")
            controller_instance.all_classrooms = list(fo.classrooms.values()) # TODO: classrooms u private yap ve getter ile al 
            controller_instance.all_department_courses_list = list(fo.course_map.values())

            user_id : int = 0
            password : str = ""
            
            controller_instance.update_all_classrooms_schedules()
            print("Course Registration System Login Menu\n")

            try:
                user_id = controller_instance.get_next_int("Please enter your username:\n")
            except ValueError:
                print("Your user name cannot include any character except integers!")
                logger.warning("A user name includes a character rather than integers is entered.")

            password = input("Please enter your password:\n")

            from Person import Person
            user : Person = fo.login_check(user_id, password)
            # A user is found with the given user_name and password
            if user is not None:
                logger.info(f"A user found with id: {user.get_id()}, password : {user.get_password()}")
                logger.info(f"System will check if user has notifications. If yes messages will be printed.")
                controller_instance.student_affairs.does_user_have_notification(user)
                logger.info(f"User entered the system with id: {user.get_id()}, password : {user.get_password()}")
                logger.info(f"Menu of the user with the name of {user.get_name()} will be called.")
                user.main_menu()
                try: 
                    fo.update_json()
                except:
                    logger.critical("cant update json files after user logout ")
            # login failed because of wrong input
            else:
                logger.info("User entered wrong user name or password")
                print("Username or password is wrong press any key to continue or",
                    "enter 'exit' to exit the system.")
                input_value = input()

                # leave the system, before that update all the json files
                if input_value.endswith("exit"):
                    logger.info("User exit the system.")
                    try:
                        fo.update_json()
                        logger.info("Json files are updated.")
                        logger.debug("User entered the exit.")
                        break
                    except Exception as e:
                        logger.critical(f"Exception in update_json method. Program will fail. Error message is: {e}")
                    logger.info("Program is terminating.")
                    break
            print("\n\n\n\n\n\n\n\n\n\n")


    def select_and_increase_course_section_capacity(self):
        prefix_text = ["Course Sections: \n"]
        suffix_text = ["Choose a course section: "]
        course_section_list_text = []
        course_sections_list = []

        for course in self.all_department_courses_list:
            for course_section in course.get_course_sections():
                course_section_list_text.append(course_section.get_name())
                course_sections_list.append(course_section)

        course_section_list_text.append("Exit.\n")
        choice = self.menu_operation(prefix_text, course_section_list_text, suffix_text)
        
        # user selected the last option which is always exit 
        if choice == len(course_section_list_text) - 1:
            return

        print("Current course section capacity: " + str(course_sections_list[choice].get_course_capacity()) + "\n")
        
        while True:
            try:
                new_capacity = int(input("Increase to: "))
                break
            except ValueError as e:
                logger.warning(f"Course section capacity has to be an integer.\n")
                self.menu_operation(f"Course section capacity has to be an integer.\n")
    
        course_sections_list[choice].increment_capacity(new_capacity)


    def update_classroom_schedule(self, classroom):
        for course in self.all_department_courses_list:
            for course_section in course.get_course_sections():
                for lecture_hour in course_section.get_lecture_hours():
                    if classroom == lecture_hour.get_classroom():
                        classroom.add_course_section_to_time_table_map(lecture_hour.get_lecture_time_table_number(), course_section)
                        # TODO: bu satır debug için konmuş olmalı sil. print(lecture_hour.get_lecture_time_table_number())


    def update_all_classrooms_schedules(self):
        for classroom in self.all_classrooms:
            self.update_classroom_schedule(classroom)


    def __print_all_department_courses_with_status(self, student_course_sections_map):
        course_list_text = []
        for course in self.all_department_courses_list:
            course_sections_of_department_courses = course.get_course_sections()
            status = "Not Taken"    # default

            for course_section in course_sections_of_department_courses:
                # get method returns None object if given key is not found
                if student_course_sections_map.get(course_section) is not None:
                    status = student_course_sections_map.get(course_section)
                    break
            course_list_text.append(f"{course.get_name()}, Status: {status}, Credits: {course.get_credits()}, Course ID: {course.get_id()}\n")
        self.menu_operation(course_list_text)


    def __print_all_department_courses(self):
        course_list_text = []

        for course in self.all_department_courses_list:
            course_list_text.append(f"{course.get_name()}, Credits: {course.get_credits()}, Course ID: {course.get_id()}\n")

        self.menu_operation(course_list_text)

    def print_all_department_courses(self, *args):
        if len(args) == 0:
            self.__print_all_department_courses()
        elif len(args) == 1:
            self.__print_all_department_courses_with_status(args[0])
        else:
            return


    def print_all_classrooms_with_capacities(self):
        classroom_list_text = []

        """
        TODO: numarali yazdirmayi kaldirdim, gerekliyse bu kullanilmali 
        for i, classroom in enumerate(self.all_classrooms, start=1):
            classroom_list_text.append(f"{i}- {classroom.get_classroom_id()}, {classroom.get_classroom_seating_capacity()}\n")
        """
        for i, classroom in enumerate(self.all_classrooms):
            classroom_list_text.append(f"{i + 1 :^4} | {classroom.get_classroom_id()}, {classroom.get_classroom_seating_capacity()}\n")

        self.menu_operation(classroom_list_text)


# TODO: bu method cagrilmiyor olabilir
    def print_all_classes_lecture_hours(self):
        for course in self.all_department_courses_list:
            for course_section in course.get_course_sections():
                for lecture_hour in course_section.get_lecture_hours():
                    if lecture_hour.get_classroom() is None:
                        print(f"Course Section: {course_section.get_course().get_id()}.{course_section.get_course_section_number()}, Day: {lecture_hour.get_lecture_day()}, Hour: {lecture_hour.get_lecture_hour_number()}")


    def review_classroom_schedule(self):
        self.print_all_classrooms_with_capacities()
        print(f"{len(self.all_classrooms) + 1 :^4} | Exit.\n")

        while True:
            try:
                classroom_choice = int(input("Please select a classroom:")) - 1
            except ValueError:
                logger.warning(f"Selection has to be an integer.\n")
                self.menu_operation(f"Selection has to be an integer.\n")

            # if the last option(Exit) is chosen exit the menu
            if classroom_choice == len(self.all_classrooms):
                    return
            # if classroom_choice is a non-negative value between 0 and # of classrooms 
            if 0 <= classroom_choice < len(self.all_classrooms):
                classroom = self.all_classrooms[classroom_choice]
                classroom.display_time_table()
                break


    def review_all_classroom_schedules(self):
        for classroom in self.all_classrooms:
            classroom.display_time_table()

    # called by student' menu
    def select_a_course_from_all_department_courses(self, all_student_courses):
        prefix_text = []
        course_list_text = []
        suffix_text = ["Choose an option: "]
        selected_course = None
        #i = 1
        """
        TODO: delete this bunu degistirdim
        for course in self.all_department_courses_list:
            status = "Not Taken" if course not in self.all_student_courses else self.all_student_courses[course]
            course_list_text.append(f"{i}- {course.get_name()},\t Status: {status},\t Credits: {course.get_credits()},\t Course ID: {course.get_id()}\n")
            i += 1
        course_list_text.append(f"{i}- Exit\n")

        self.menu_operation(prefix_text)
        self.menu_operation(course_list_text)
        self.menu_operation(suffix_text)
        """

        # bunun yerine list_all_department_courses cagrilabilir
        for course in self.all_department_courses_list:
            status = "Not Taken" if course not in all_student_courses else all_student_courses[course]
            course_list_text.append(f"{course.get_name():<40}| Status: {status:<9}| Credits: {course.get_credits():<3}| Course ID: {course.get_id():<8}|")
        course_list_text.append(f"Exit\n")

        while True:
            try:
                course_choice = self.menu_operation(prefix_text, course_list_text, suffix_text)
            except ValueError:
                print("Invalid choice! Please try again.")
                continue
            
            if course_choice == len(self.all_department_courses_list):
                    return None
            elif 0 <= course_choice < len(self.all_department_courses_list):
                selected_course = self.all_department_courses_list[course_choice]
                return selected_course
            else:
                print("Invalid choice! Please try again.")
                continue


    # TODO: bu neden eklenmis bilmiyorum,
    # Oguz gerek duymustu galiba sonra bunu kullandık mı kontrol et
    # departmentScheduler kullanıyor
    def select_course_to_assign_new_lecture_hour(self):
        i = 1
        prefix_text = []
        suffix_text = ["Choose an option: "]
        course_list_text = []
        selected_course = None
        print("-" * 82)
        for course in self.all_department_courses_list:
            course_list_text.append(f"{i :^4}|  {course.get_name() :<40} | Credits: {course.get_credits(): <3}| Course ID: {course.get_id():<6}|\n")
            i += 1
        course_list_text.append(f"{i :^4}|  {"Exit\n" :<40}\n")

        self.menu_operation(prefix_text)
        self.menu_operation(course_list_text)
        self.menu_operation(suffix_text)

        while True:
            while True:
                try:
                    choice = self.get_next_int() - 1
                    break
                except ValueError as e:
                    logger.warning(f"Selection has to be an integer.\n")
                    self.menu_operation(f"Selection has to be an integer.\n")
            

            if choice == len(self.all_department_courses_list):
                return None

            if choice < 0:
                print("Invalid choice!")
                continue

            selected_course = self.all_department_courses_list[choice]
            return selected_course


    def select_courses_section_from_course(self, course):
        prefix_text = ["\n\nSection Selection Menu.\n", "Available options:\n\n"]
        options_text = []
        suffix_text = ["Please choose an option: "]

        if len(course.get_course_sections()) > 1:
            while True:
                for i in range(len(course.get_course_sections()) - 1):
                    options_text.append(f"{course.get_name()}.{i + 1}")
                options_text.append("Exit.\n")
                # TODO: menu_operation a try-except koymayi unutma
                section_selection = self.menu_operation(prefix_text, options_text, suffix_text)

                # Exit
                if section_selection == len(course.get_course_sections()):
                    return None
                elif 0 <= section_selection < len(course.get_course_sections()):
                    return course.get_course_sections()[section_selection]
                else:
                    print("Invalid choice! Please try again.")
        else:
            return course.get_course_sections()[0]
        

    def select_classroom_from_all_classrooms(self):
        prefix_text = ["\n\nClassroom Selection Menu.\n", "Available options:\n\n"]
        options_text = []
        suffix_text = ["Please choose an option: "]

        while True:
            print("Please select a classroom.")

            for classroom in self.all_classrooms:
                options_text.append(f"{classroom.get_classroom_id()}.")

            classroom_selection = self.menu_operation(prefix_text, options_text, suffix_text)
            
            # Exit
            if classroom_selection == len(self.all_classrooms):
                return None
            elif 0 <= classroom_selection < len(self.all_classrooms):
                return self.all_classrooms[classroom_selection]
            else:
                return None
            

    def select_lecture_hour_for_classroom(self, classroom):
        classroom.display_time_table()

        prefix_text = ["\n\nDay Selection Menu.\n", "Available options:\n\n"]
        options_text = ["MONDAY", "TUESDAY", "WEDNESDAY",
                        "THURSDAY", "FRIDAY", "SATURDAY", 
                        "SUNDAY"]
        suffix_text = ["Please choose an option: "]
        options_text.append("Exit.")

        prefix_text1 = ["\n\nSlot Selection Menu.\n", "Available options:\n\n"]
        options_text1 =[" 8.30 -  9.20", " 9.30 - 10.20",
                        "10.30 - 11.20", "11.30 - 12.20", 
                        "13.00 - 13.50", "14.00 - 14.50",
                        "15.00 - 15.50", "16.00 - 16.50", 
                        "17.00 - 17.50", "18.00 - 18.50"]
        suffix_text1 = ["Please choose an option: "]
        options_text1.append("Exit.\n")

        while True:
            lecture_day_selection = self.menu_operation(prefix_text, options_text, suffix_text)

            # Exit
            if lecture_day_selection == len(options_text):
                return None
            elif 0 <= lecture_day_selection < len(options_text):
                while True:
                    lecture_hour_selection = self.menu_operation(prefix_text1, options_text1, suffix_text1)
                    # Exit
                    if lecture_hour_selection == len(options_text1):
                        return None
                    elif 0 <= lecture_hour_selection < len(options_text1):
                        from LectureHour import LectureHour
                        return LectureHour(lecture_day_selection, lecture_hour_selection, None)
                    else:
                        print("Please enter a valid integer value")
                        continue
            else:
                logger.info(f"User entered an option:{lecture_day_selection} which is out of range 1 - {len(options_text)}.")
                self.menu_operation(f"Please enter an integer between 1 and {len(options_text)}\n")
                continue


    # 1: prefixText
    # 2: numberedOptionsText, suffixText
    # 3: prefix_text, numbered_options_text, suffix_text
    def menu_operation(self, *args):
        
        prefix_text = None
        numbered_options_text = None
        suffix_text = None

        # method overloading is simulated here
        if len(args) == 1:
            if type(args[0]) is str:
                print(args[0])
                return
            prefix_text = args[0]
        elif len(args) == 2:
            numbered_options_text = args[0]
            suffix_text = args[1]
        elif len(args) == 3:
            prefix_text = args[0]
            numbered_options_text = args[1]
            suffix_text = args[2]

        result = -1
        while True:
            if prefix_text is not None:
                for prefix_string in prefix_text:
                    print(prefix_string, end='') # do not put new line character at the end

            if numbered_options_text is not None:
                for i, options_string in enumerate(numbered_options_text, start=1):
                    print(f"{i:<3}| {options_string}")

            if suffix_text is not None:
                for suffix_string in suffix_text:
                    print(suffix_string, end='')
                try:
                    result = int(input())
                    break
                except ValueError:
                    print("Please enter a valid integer value. Try again.")
                    continue
            else:
                return

        if 0 < result <= len(numbered_options_text):
            return result - 1
        return -1
    
    def get_next_line(self, *args):
        if(len(args) == 0):
            return input()
        for string in args:
            print(string)
        return input()

    def get_next_int(self, *args):
        try:
            for string in args:
                print(string)
            return int(self.get_next_line())
        except ValueError:
            logger.warning(f"Invalid input. Expected integer value.") 
            raise ValueError

    #def print_all_given_courses_with_their_capacities(self):
    #    # course_list_text is created by using list comprehension
    #    course_list_text = [
    #        f"{i + 1 :^4}| {course.get_name()}, Course ID: {course.get_id()}, Capacity: {course.get_course_capacity()}\n"
    #        for i, course in enumerate(self.all_department_courses_list)
    #    ]
    #    self.menu_operation(course_list_text)

    # department 
    def print_all_given_course_sections_with_their_capacities(self):
        # course_list_text is created by using list comprehension
        course_section_list_text = []
        i = 1
        for  course in self.all_department_courses_list:
            for course_section in course.get_course_sections():
                course_section_list_text.append(f"{i  :^4}| {f"{course_section.get_name():<45}"}| Course ID: {f"{course.get_id()}.{course_section.get_course_section_number()}":<10}| Capacity: {f"{course_section.get_course_capacity():^4}"}|\n")
                i=i+1
        self.menu_operation(course_section_list_text)

# Initialization of logging at the beginning so that every class can use it
if __name__ == "__main__":
    # Initialization of logging at the beginning so that every class can use it
    setup_logging()
    instance = CourseRegistrationSystem.get_controller_instance()
    instance.main()