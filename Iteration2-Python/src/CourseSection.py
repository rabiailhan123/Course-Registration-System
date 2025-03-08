
from typing import List


import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class CourseSection:

    #Constructor should be named  __init__
    #don't need to declare or initialize attributes before the constructor, Attributes are dynamically created and initialized within the constructor
    def __init__(self, lecture_hours: List, lecturer_name: str, course_section_number: int, course_capacity: int, course,instance):

        
        self.__controller_instance = instance
        from LectureHour import LectureHour
        self.__lecture_hours: List[LectureHour] = lecture_hours
        self.__lecturer_name: str = lecturer_name
        self.__course_section_number: int = course_section_number #it was not private in java
        self.__course_capacity: int = course_capacity
        from Course import Course
        self.__course: Course = course
        from Student import Student
        self.__student_list: List[Student] = []
        self.__waiting_student_list: List[Student] = []


    def get_lecture_hours(self) -> List:
        return self.__lecture_hours


    def set_lecture_hours(self, lecture_hours: List):
        self.__lecture_hours = lecture_hours


    def get_lecturer(self) -> str:
        return self.__lecturer_name


    def get_course_section_number(self) -> int:
        return self.__course_section_number


    def get_name(self) -> str:
        return f"{self.__course.get_name()}"


    def get_id(self) -> str:
            return f"{self.__course.get_id()}.{self.__course_section_number}"


    def lecture_hour_dont_overlap(self, new_lecture_hour) -> bool:
        for lecture_of_course_section in self.__lecture_hours:
            if lecture_of_course_section.get_lecture_time_table_number() == new_lecture_hour.get_lecture_time_table_number():
                return False
        return True


    def add_lecture_hour(self, new_lecture):
        self.__lecture_hours.append(new_lecture)


    def increment_capacity(self, new_capacity: int) -> bool:
        # If the new capacity is less than or equal to the current capacity, return False
        if new_capacity <= self.__course_capacity:
            self.__controller_instance.menu_operation(["New course capacity cannot be less than the previous."])
            return False
        # Check if the new capacity exceeds the seating capacity of any classroom
        for lecture_hour in self.get_lecture_hours():
            # check if lecture hour has a classroom or not
            if lecture_hour.get_classroom() and lecture_hour.get_classroom().get_classroom_seating_capacity() < new_capacity:
                self.__controller_instance.menu_operation(["New course capacity doesn't fit to classroom."])
                return False
        # Calculate the difference and update the capacity    
        difference = new_capacity - self.__course_capacity
        self.__course_capacity = new_capacity
        # Move students from the waiting list to the student list
        for _ in range(difference):
            if not self.__waiting_student_list:
                break
            waiting_student = self.__waiting_student_list.pop(0)
            #TODO: gereksiz sil
            # waiting_student.change_status(self, "approved")
            self.__student_list.append(waiting_student)
            
            from StudentAffairs import StudentAffairs
            student_affairs : StudentAffairs = self.__controller_instance.student_affairs
            student_affairs.add_notification_to_user(waiting_student, "removed_from_wait_list", self.get_name())
            waiting_student.change_status(self,"approved")
        self.__controller_instance.menu_operation(f"Capacity of course section: {self.get_id()} is increased to {new_capacity}.")
        return True


    def add_student(self, student):
        # If the student list is at full capacity, add to the waiting list
        if len(self.__student_list) >= self.__course_capacity:  # If the capacity is full
            #add_notification_to_user(self, user, notification_type : str, course_name : str):
            self.__waiting_student_list.append(student)
            from StudentAffairs import StudentAffairs
            student_affairs : StudentAffairs = self.__controller_instance.student_affairs
            student_affairs.add_notification_to_user(student, "added_to_wait_list", self.get_name())
            student.change_status(self,"waiting")
            #print("Course's capacity is full. You are placed on the waiting list.")
        else:
            # Else, add the student to the main student list
            self.__student_list.append(student)
            #self.__controller_instance.menu_operation(f"{self.get_id()} is selected successfully.")     #TODO: sil 


    def get_course(self):
        return self.__course


    def get_course_capacity(self) -> int:
        return self.__course_capacity


    def get_credits(self):
        return self.__course.get_credits()
