
from Staff import Staff

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class Lecturer(Staff):
    def __init__(self , id,name,eMail,password,controller_instance):
        super(Lecturer,self).__init__(id,name,eMail,password,controller_instance)
        
    def main_menu(self):
        pass
