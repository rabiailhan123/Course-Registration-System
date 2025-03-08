
from Person import Person

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class Staff(Person):
    def __init__(self,id,name, eMail, password, controller_instance):
        super().__init__(id, name, eMail, password, controller_instance)
        logger.info(f"Super type' 'Person' constructor is called. {__name__} type object is created.")
