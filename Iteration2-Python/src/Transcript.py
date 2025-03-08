
import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class Transcript(object):

    def __init__(self, semester, taken_courses_with_letter_grades):
        """
        Constructor: Will be called inside of student's constructor

        Args:
            semester (int): student's current semester
            taken_courses_with_grades (dict): Course -> str(letter grade)
        """
        self.__semester : int = semester
        self.__taken_courses_with_letter_grades : dict = taken_courses_with_letter_grades
        self.__taken_courses_with_numeric_grades : dict = {} 
        # credits_taken and credits_finished are
        # initialized inside of the calculate_gpa method
        self.__credits_taken : int = 0
        self.__credits_finished : int = 0
        self.__gpa : float = self.__calculate_gpa()


    def __calculate_gpa(self):
        """
        This method calculates the gpa via
        iterating through taken_courses_with_letter_grades dict and call  
        __get_the_multiplier_of_the_grade function of each course.
        Then, returns the calculated GPA.
        
        Args:
            taken_courses_with_letter_grades (dict): Course -> str (letter_grade)

        Returns:
            float: total grades of all finished courses divided by total credits of finished courses
        """        
        num_grade : float = 0.0
        total_grade : float = 0.0
        total_finished_credits : int = 0
        total_taken_credits : int = 0
        
        for course, letter_grade in self.__taken_courses_with_letter_grades.items():
            num_grade = self.__get_the_multiplier_of_the_grade(letter_grade)
            
            # num_grade can be either a value between 0 and 4 or
            # (-1) if no grade is provided for the course
            if num_grade is not None and num_grade is not -1: 
                total_grade += num_grade * course.get_credits()
                total_finished_credits += course.get_credits()            
            else:
                total_taken_credits += course.get_credits()
            
            self.__taken_courses_with_numeric_grades[course] = num_grade  
            
        self.__credits_taken = total_finished_credits + total_taken_credits
        self.__credits_finished = total_finished_credits
        
        if total_finished_credits == 0:
            return 0.0

        return (total_grade / total_finished_credits)
    
    
    def __get_the_multiplier_of_the_grade(self, letter_grade):
        """
        This method returns numeric value of the letter grade
        it takes as a parameter 

        Args:
            letter_grade (str)

        Returns:
            float
        """        
        # since python does not support switch statements unlike many languages
        # we use dictionaries to simulate the same behaviour 
        switcher : dict = { "FF": 0.0, "FD": 0.5, "DD": 1.0,
                            "DC": 1.5, "CC": 2.0, "CB": 2.5,
                            "BB": 3.0, "BA": 3.5, "AA": 4.0
                        }
        
        # get the value from dict switcher whose key is letter_grade
        # returns -1 as default, if letter grade is not a valid value
        num_grade = switcher.get(letter_grade, -1)

        # TODO: will be deleted. Checking for invalid grades
        #if num_grade == -1 and letter_grade not in switcher:
        #    raise ValueError(f"Invalid letter grade: {letter_grade}")

        return num_grade
        

    def __str__(self):
        """
        This method overwrites the __str__ method of the object class.
        It returns a string filled with the information of the transcript
        Returns:
            str: info of transcript
        """        
        
        self.__gpa = self.__calculate_gpa()
        
        output : str
        output = "\t\t\t<< Courses >>\n\n"
        for i, (course, letter_grade) in enumerate(self.__taken_courses_with_letter_grades.items()):
            status = "F "
            num_grade = self.__get_the_multiplier_of_the_grade(letter_grade)
            
            if num_grade < 0 :
                status = "NF"
                num_grade = " - "
                
            if letter_grade is not None:
                letter_grade += "  "
            
            output += f"| {i + 1} | {status} | {course.get_id()} | {num_grade} | {letter_grade} | {course.get_name()}\n"
            
        output += f"\n\n-------------------------------------------------------------------------------------------------"
        output += f"\n-------------------------------------------------------------------------------------------------\n\n"
        output += f"Total credits taken = {self.__credits_taken}\n"
        output += f"Total credits finished = {self.__credits_finished}\n\n" 
        output += f"Current GPA is {self.__gpa : .2f}\n"
        
        return (output)


    def get_credits_taken(self):
        return self.__credits_taken
        

    def get_credits_finished(self):
        return self.__credits_finished
    
    def get_gpa(self):
        return self.__gpa
    
    def get_semester(self):
        return self.__semester
