from typing import List
from abc import ABC,abstractmethod

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class Course(ABC):

#Constructor should be named  __init__
#don't need to declare or initialize attributes before the constructor, Attributes are dynamically created and initialized within the constructor

    def __init__(self, course_id: int, course_name: str, course_credits: int):
        self.__course_id = course_id
        self.__course_name = course_name
        self.__course_credits = course_credits
        self.__course_sections: List = []
        self.__prerequisites: List[Course] = []
        self.__student_list: List = []
        self.__waiting_student_list: List = []
        self.__course_capacity: int = 0  # TODO: Implement as per course sections
        self.number_of_lecture_hours: int = 0 #in the old java they were not private

    @abstractmethod
    def check_prerequisites_met(self, student) -> bool: #abstract
        pass
    
    def add_student_to_wait_list(self):
        pass

    def get_id(self) -> int:
        return self.__course_id

    def set_id(self, course_id: int):
        self.__course_id =course_id

    def get_credits(self) -> int:
        return self.__course_credits

    def set_credits(self, course_credits: int):
        self.__course_credits = course_credits

    def get_name(self) -> str:
        return self.__course_name

    def get_course_sections(self) -> List:
        return self.__course_sections

    def set_course_sections(self, course_sections: List):
        self.__course_sections = course_sections

    def add_course_section(self, section):
        self.__course_sections.append(section)

    def get_prerequisites(self) -> List:
        return self.__prerequisites

    def add_prerequisite(self, course):
        self.__prerequisites.append(course)

    def get_course_capacity(self) -> int:
        return sum(course_section.get_course_capacity() for course_section in self.get_course_sections())
