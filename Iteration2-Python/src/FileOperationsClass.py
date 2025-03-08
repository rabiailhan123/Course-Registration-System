
from DepartmentScheduler import DepartmentScheduler
from Classroom import Classroom
from Course import Course
from TECourse import TECourse
from LectureHour import LectureHour
from CourseSection import CourseSection
from Advisor import Advisor
from Student import Student
from Admin import Admin

import os
import json

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class FileOperationsClass():
    
    def __init__(self, controller_instance):
        from CourseRegistrationSystem import CourseRegistrationSystem
        self.course_map = {}
        self.advisor_map = {}
        self.student_map = {}
        self.department_head = []
        self.department_schedular= []
        self.admin : Admin
        self.people = []
        self.classrooms = {}
        self.controller_instance : CourseRegistrationSystem.instance = controller_instance
    
    def login_check(self, user_id, password):
        for person in self.people:
            if person.get_id() == user_id and person.get_password() == password:
                return person
        return None

    def read_json(self, dir_path):
        self.course_map = {}
        self.advisor_map = {}
        self.student_map = {}
        self.department_head = []
        self.department_schedular= []
        self.admin : Admin 
        self.people = []
        self.classrooms = {}
        
        
        logger.info(f"Json file will be read from {dir_path}")
        #self.controller_instance.menu_operation(f"dir_path is {dir_path}")
        student_dir_path = os.path.join(dir_path, "Student")
        classroom_dir_path = os.path.join(dir_path, "Classroom")
        course_dir_path = os.path.join(dir_path, "Course")
        advisor_dir_path = os.path.join(dir_path, "Advisor")
        admin_dir_path = os.path.join(dir_path , "Admin")
        department_scheduler_dir_path = os.path.join(dir_path, "DepartmentScheduler")
        department_head_dir_path = os.path.join(dir_path, "DepartmentHead")

        try:
            logger.debug("Courses are read.")
            self.read_classroom_json(classroom_dir_path)
            self.read_course_json(course_dir_path) # read course must be called after classroom in order to assign classrooms tu lecture hours
            self.read_student_json(student_dir_path)
            self.read_advisor_json(advisor_dir_path)
            self.read_admin_json(admin_dir_path)
            self.read_department_scheduler(department_scheduler_dir_path)
            self.read_department_head(department_head_dir_path)


            self.map_courses_and_advisor_to_student(student_dir_path)
            self.map_prerequisite_to_course(course_dir_path)
        except FileNotFoundError as e:
            logger.critical(f"Fatal error, program may not work: {e}")
        except Exception as e:
            logger.critical(f"Unexpected error, program may fail: {e}")
            
        self.people.extend(self.advisor_map.values())
        self.people.extend(self.student_map.values())
        
        # update list in the CourseRegistrationSystem
        self.controller_instance.all_classrooms = list(self.classrooms.values())
        self.controller_instance.all_department_courses_list = list(self.course_map.values()) 


    def map_courses_and_advisor_to_student(self, student_dir_path):
        student_files = self.take_json_files(student_dir_path)
        
        if student_files is None:
            logger.error(f"No file is returned from directory {student_dir_path}")
            raise FileNotFoundError(f"No file is found in {student_dir_path}.")

        for file in student_files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)

                    student_id = data['id']
                    student = self.student_map.get(student_id)
                    course_sections_taken = data['CourseSectionsTaken']

                    # row stands for a single course_section
                    for row in course_sections_taken:
                        course_id = row[0]
                        course_status = row[1]
                        
                        from Course import Course
                        course : Course = self.course_map[int(course_id)]    # TODO: course_id is str make sure it only contains int number 
                        course_sections = course.get_course_sections()
                        course_section = course_sections[int(row[2]) - 1]
                        student.set_course_section_status(course_section, course_status)

                        letter_grade : str = row[3]
                        student.set_course_section_letter_grade(course_section, letter_grade)
                        
                        if course_status == "approved":
                            course_section.add_student(student)

                    advisor = self.advisor_map[data['AdvisorId']]

                    student.set_advisor(advisor)
                    advisor.add_advisee(student)

            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")


    def read_department_scheduler(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.")

        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    from DepartmentScheduler import DepartmentScheduler
                    department_scheduler = DepartmentScheduler(
                        data['id'], data['name'], data['eMail'], data['password'], data['departmentName']
                    ,self.controller_instance)
                    self.people.append(department_scheduler)
            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")


    def read_department_head(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.")
            
        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    from DepartmentHead import DepartmentHead
                    department_head = DepartmentHead(
                        data['id'], data['name'], data['eMail'], data['password'], data['departmentName']
                    ,self.controller_instance)
                    self.people.append(department_head)
                    logger.info(f"Department Head {file} is created and added to user list.")
            except FileNotFoundError as e:
                logger.error(f"While opening file {file}: {e}")
                self.controller_instance.menu_operation(f"Error: {e}")


    def read_admin_json(self, admin_dir_path):
        files = self.take_json_files(admin_dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {admin_dir_path}")
            raise FileNotFoundError(f"No file is found in {admin_dir_path}.")

        
        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    #from Admin import Admin 
                    self.__admin = Admin(
                        data['id'], data['name'], data['eMail'], data['password'],self.controller_instance)
                    self.people.append(self.__admin)
            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")


    def map_prerequisite_to_course(self, course_dir_path):
        course_files = self.take_json_files(course_dir_path)
        
        if course_files is None:
            logger.error(f"No file is returned from directory {course_dir_path}")
            raise FileNotFoundError(f"No file is found in {course_dir_path}.")

        for file in course_files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)

                    course_id_of_course = data['id']
                    course = self.course_map.get(course_id_of_course)
                    prerequisites = data['prerequisite']

                    for course_id in prerequisites:
                        prerequisite = self.course_map.get(course_id)
                        course.add_prerequisite(prerequisite)

            except FileNotFoundError as e:
                logger.error(f"Error: {e}")

    def read_classroom_json(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.")

        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    from Classroom import Classroom
                    classroom = Classroom(data['classroomID'], data['classroomSeatingCapacity'], self.controller_instance)
                    self.classrooms[classroom.get_classroom_id()] = classroom
            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")


    def read_course_json(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.") 
    
        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    course_sections = data['courseSection']
                    if(data['CourseType'] == "Mandatory"):
                        from MandatoryCourse import MandatoryCourse
                        course = MandatoryCourse(data['id'], data['name'], data['credits'])
                    elif(data['CourseType'] =="TECourse"):
                        from TECourse import TECourse
                        course = TECourse(data['id'], data['name'], data['credits'])
                    elif(data['CourseType'] == "EngineeringProjectCourse"):
                        from EngineeringProjectCourse import EngineeringProjectCourse
                        course = EngineeringProjectCourse(data['id'], data['name'], data['credits'], data['creditsNeeded'])
                    else:
                        logger.warning(f"{data} has wrong course type")
                        continue
                    for section in course_sections:
                        lecture_hours = []
                        for hour in section['lectureHour']:
                            from LectureHour import LectureHour 
                            lecture_hour = LectureHour(hour['lectureDay'], hour['lectureHourNumber'], self.classrooms.get(hour['classroomID']))

                            lecture_hours.append(lecture_hour)
                        from CourseSection import CourseSection
                        course_section = CourseSection(
                            lecture_hours, section['lecturer'], section['courseSectionNumber'], section['courseCapacity'], course, self.controller_instance
                        )
                        course.add_course_section(course_section)
                    number_of_lecture_hours : int = data.get("numberOfLectureHour")
                    if number_of_lecture_hours is None:
                        logger.critical(f"{course.get_id()} has no number of lecture hours it is set to 0 ")
                        course.number_of_lecture_hours = 0
                    else:
                        course.number_of_lecture_hours = number_of_lecture_hours 

                    self.course_map[course.get_id()] = course
            except FileNotFoundError as e:
                logger.error(f"{file} is not found. {e}")


    def read_advisor_json(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.")
        
        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    from Advisor import Advisor
                    advisor = Advisor(
                        data['id'], data['name'], data['eMail'], data['password']
                    , self.controller_instance)
                    self.advisor_map[advisor.get_id()] = advisor

            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")


    def read_student_json(self, dir_path):
        files = self.take_json_files(dir_path)
        
        if files is None:
            logger.error(f"No file is returned from directory {dir_path}")
            raise FileNotFoundError(f"No file is found in {dir_path}.")
        
        for file in files:
            try:
                with open(file, 'r') as f:
                    file_content = f.read()
                    data = json.loads(file_content)
                    student = Student(data['id'], data['name'], data['eMail'], data['password'], data["semester"], self.controller_instance)
                    self.student_map[student.get_id()] = student
            except FileNotFoundError as e:
                self.controller_instance.menu_operation(f"Error: {e}")

    def take_json_files(self, dir_path):
        """get all the files in a directory

        Args:
            dir_path (_type_): directory which includes json files

        Returns:
            list of files: returns the list of the files inside of a directory
            error: may throw FileNotFoundError if directory does not exist
        """
        try:
            return [os.path.join(dir_path, f) for f in os.listdir(dir_path) if f.endswith('.json')]
        except FileNotFoundError as e:
            logger.error(f"While opening directory {dir_path}: {e} : FileNotFoundError")


    def update_json(self):
        #self.update_student_json() these two are wrong implementation every user json must be updated using person list
        #self.update_advisor_json() no need anymore update_person_json does this three  and also more update
        #self.update_admin_json()
        self.update_person_json()
        self.update_course_json()
        self.update_classroom_json()


    def update_student_json(self):
        from Student import Student
        student : Student

        for student in self.student_map.values():
            student_data = {
                'AdvisorId': student.get_advisor().get_id(),
                'password': student.get_password(),
                'name': student.get_name(),
                'semester' : student.get_semester(),
                'id': student.get_id(),
                'eMail': student.get_email(),
            }

            course_sections_taken = []
            course_section : CourseSection
            for course_section, status in student.get_all_course_sections().items():
                course_section_data = [
                    str(course_section.get_course().get_id()),
                    status,
                    str(course_section.get_course_section_number()),
                    student.get_all_course_sections_with_letter_grades().get(course_section)
                ]
                course_sections_taken.append(course_section_data)
            student_data['CourseSectionsTaken']=course_sections_taken
            self.save_file("./JSON/Student/",f"{student.get_id()}.json",student_data)


    def update_advisor_json(self):
        from Advisor import Advisor
        advisor : Advisor
        for advisor in self.advisor_map.values():
            advisor_data = {
                'id': advisor.get_id(),
                'name': advisor.get_name(),
                'eMail': advisor.get_e_mail(),
                'password': advisor.get_password(),
            }

            self.save_file("./JSON/Advisor/",f"{advisor.get_id()}.json",advisor_data)


    def update_admin_json(self):
            admin_data = {
                'id': self.__admin.get_id(),
                'name':self.__admin.get_name(),
                'eMail':self.__admin.get_e_mail(),
                'password': self.__admin.get_password(),
            }
            self.save_file("./JSON/Admin/",f"{self.__admin.get_id()}.json",admin_data)


    def update_course_json(self):
        from MandatoryCourse import MandatoryCourse
        from EngineeringProjectCourse import EngineeringProjectCourse
        course: Course
        
        for course in self.controller_instance.all_department_courses_list: # self.course_map.values(): 
            course_data = {
                "id": course.get_id(),
                "name": course.get_name(),
                "credits": course.get_credits(),
                "numberOfLectureHour": course.number_of_lecture_hours,

            }
            if type(course) is TECourse:
                course_data["CourseType"]="TECourse"
            elif type(course) is MandatoryCourse:
                course_data["CourseType"]="Mandatory"
            elif type(course) is EngineeringProjectCourse:
                course_data['creditsNeeded'] = course.get_credits_needed()
                course_data["CourseType"] ="EngineeringProjectCourse"
            else:
                logging.warning(f"{course} object is wrong type or none")

            prerequisite_data = []
            for prerequisite_course in course.get_prerequisites():
                if prerequisite_course is None:
                    logger.warning(f"course:{course.get_id()} has {prerequisite_course} prerequisite this message should'nt happen it must n have none in a list")
                    continue
                prerequisite_data.append(prerequisite_course.get_id())
            course_data["prerequisite"]= prerequisite_data

            course_section_data = []
            course_section : CourseSection
            for course_section in course.get_course_sections():
                logger.debug(f"New course section capacity of {course_section.get_id} is {course_section.get_course_capacity()}")
                course_section_json = { 
                    "lecturer" : course_section.get_lecturer(),
                    "courseCapacity" : course_section.get_course_capacity(),
                    "courseSectionNumber" : course_section.get_course_section_number(),  
                }

                lecture_hour_data = []
                lecture_hour : LectureHour
                for lecture_hour in course_section.get_lecture_hours():
                    lecture_hour_json = {
                        "lectureDay" : lecture_hour.get_lecture_day(),
                        "lectureHourNumber" : lecture_hour.get_lecture_hour_number(),
                        "classroomID" : lecture_hour.get_classroom_id()
                    }
                    lecture_hour_data.append(lecture_hour_json)
                course_section_json["lectureHour"]= lecture_hour_data

                course_section_data.append(course_section_json)

            course_data["courseSection"]= course_section_data

            self.save_file("./JSON/Course/",f"{course.get_id()}.json",course_data)


    def update_classroom_json(self):
        classroom :Classroom
        for classroom in self.classrooms.values():
            classroom_json = {
                "classroomID" : classroom.get_classroom_id() ,
                "classroomSeatingCapacity" : classroom.get_classroom_seating_capacity()
            }
            self.save_file("./JSON/Classroom/",f"{classroom.get_classroom_id()}.json",classroom_json)


    def update_department_head_json(self):
        pass


    def update_department_scheduler_json(self):
        pass

    def update_person_json(self):
        from Person import Person
        from DepartmentHead import DepartmentHead
        from StudentAffairs import StudentAffairs
        person : Person
        for person in self.people:
            person_json = person.get_json()

            if type(person) is Student:
                self.save_file("./JSON/Student/",f"{person.get_id()}.json",person_json)
            elif type(person) is Advisor:
                self.save_file("./JSON/Advisor/",f"{person.get_id()}.json",person_json)
            elif type(person) is Admin:
                self.save_file("./JSON/Admin/",f"{person.get_id()}.json", person_json)
            elif type(person) is DepartmentScheduler:
                self.save_file("./JSON/DepartmentScheduler/",f"{person.get_id()}.json", person_json)
            elif type(person) is DepartmentHead:
                self.save_file("./JSON/DepartmentHead/",f"{person.get_id()}.json", person_json)
            elif type(person) is StudentAffairs:
                self.save_file("./JSON/StudentAffairs/",f"{person.get_id()}.json", person_json)
            else:
                logger.warning(f"{person.get_name()} has incorrect type:{type(person)}")
                return


    def save_file(self,dir_path: str,file_name:str, json_data):
        file_path = dir_path + file_name
        try:
            with open(file_path, 'w') as file:
                        json.dump(json_data, file, indent=4)
        except FileNotFoundError:
            os.mkdir(dir_path)
            with open(file_path, 'w') as file:
                        json.dump(json_data, file, indent=4)
