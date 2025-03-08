
from abc import ABC,abstractmethod

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class Person(ABC):

   def __init__(self, id, name, e_mail, Password, controller_instance):
         self.__id : int = id
         self.__name : str = name
         self.__e_mail : str = e_mail
         self.__password : str = Password
         self.__controller_instance = controller_instance


   #def check_password(self, password):
   #   return password.__eq__(self.__password)

   def get_password(self) -> str:
      return self.__password
   
   def get_e_mail(self) -> str:
      return self.__e_mail
   
   def get_id(self) -> int:
      return self.__id
   
   def get_name(self) -> str:
      return self.__name
            
   def get_controller_instance(self):
      return  self.__controller_instance
      
   @abstractmethod
   def get_json(self):
      pass
   
   @abstractmethod
   def main_menu(self) -> None:
      pass
