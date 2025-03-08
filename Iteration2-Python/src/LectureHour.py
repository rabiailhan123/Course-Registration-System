
from Classroom import Classroom

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class LectureHour():
    
    def __init__(self, lectureDay, lectureHourNumber, classroom):
        self.__lecture_day : int = lectureDay
        self.__lecture_hour_number : int = lectureHourNumber
        self.__classroom  :Classroom = classroom

    def  get_lecture_day(self):
        return self.__lecture_day
    
    def set_lecture_day(self,lectureDay):
        self.__lecture_day = lectureDay
        
    def get_lecture_hour_number(self):
        return self.__lecture_hour_number
    
    def set_lecture_hour_number(self,lectureHourNumber):
        self.__lecture_hour_number = lectureHourNumber
        
    def get_classroom(self):
        return self.__classroom

    def get_classroom_id(self):
        return self.__classroom.get_classroom_id()
    
    def set_classroom(self,classroom):
        if type(classroom)is str:
            raise TypeError
        self.__classroom = classroom
        
    def get_lecture_time_table_number(self):
        return self.__lecture_day * 10 +self.__lecture_hour_number
