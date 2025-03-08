
import logging
from CourseSection import CourseSection
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module

class Classroom:
    def __init__(self, classRoomID: str, classRoomSeatingCapacity: int, controllerInstance):
        self.__class_room_id: str = classRoomID
        self.__class_room_seating_capacity: int = classRoomSeatingCapacity
        self.__controller_instance = controllerInstance
        self.__timeTableDictionary: dict[int, str] = {}
        
    def add_course_section_to_time_table_map(self, time: int, course_section: CourseSection) -> None:
        self.__timeTableDictionary[time] = course_section

    def get_classroom_id(self) -> str:
        return self.__class_room_id

    def get_classroom_seating_capacity(self) -> int:
        return self.__class_room_seating_capacity

    def display_time_table(self) -> None:
        self.__controller_instance.menu_operation(f"\n\n\n     << {self.__class_room_id} >>")
        schedule_by_day = {day: [] for day in range(1, 8)}
        
        for lecture_hour, course in self.__timeTableDictionary.items():
            day = lecture_hour // 10 + 1
            schedule_by_day[day].append((lecture_hour % 10, course.get_id()))

        for day, courses in schedule_by_day.items():
            self.__print_courses_of_the_day(day, courses)

    def __print_courses_of_the_day(self, day_number: int, courses: list) -> None:
        days = {
            1: "\tMONDAY", 2: "\tTUESDAY", 3: "\tWEDNESDAY", 
            4: "\tTHURSDAY", 5: "\tFRIDAY", 6: "\tSATURDAY", 7: "\tSUNDAY"
        }
        hours = [
            f"{" 8.30 -  9.20":^15}", f"{" 9.30 - 10.20":^15}", f"{"10.30 - 11.20":^15}", 
            f"{"11.30 - 12.20":^15}", f"{"13.00 - 13.50":^15}", f"{"14.00 - 14.50":^15}", 
            f"{"15.00 - 15.50":^15}", f"{"16.00 - 16.50":^15}", f"{"17.00 - 17.50":^15}", 
            f"{"18.00 - 18.50":^15}"
        ]

        daily_schedule = ["[ empty ]\n"] * 10
        for hour, course in courses:
            daily_schedule[hour] = f"[{course:^7}]\n"

        schedule_text = f"\n{days[day_number]}\n"
        schedule_text += "".join(f"{hours[i]} {daily_schedule[i]}" for i in range(10))
        self.__controller_instance.menu_operation(schedule_text)


    def is_classroom_available(self, lecture_hour: int) -> bool:
        return lecture_hour not in self.__timeTableDictionary

    def add_lecture_hour(self, course_section: CourseSection, lecture_hour: int) -> None: #is this really supposed to be int
        """add lecture hour to classroom if it is available

        Args:
            course_section (CourseSection): _description_
            lecture_hour (int): _description_

        Raises:
            ValueError: if classroom is not available
        """
        if self.is_classroom_available(lecture_hour):
            self.__timeTableDictionary[lecture_hour] = course_section
        else:
            raise ValueError("Classroom is not available for the specified lecture hour.")
