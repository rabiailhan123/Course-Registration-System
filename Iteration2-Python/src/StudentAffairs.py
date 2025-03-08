
from Staff import Staff

import logging
logger = logging.getLogger(__name__)    # to ensure that the logger is named after the module
class StudentAffairs(Staff):
    def __init__(self,id,  name, eMail, password,controller_instance):
        super().__init__(id, name, eMail, password, controller_instance)
        from Person import Person
        self.notification_list : {int, str} = {} #Dictionary to store notifications for users

    def main_menu(self) -> None:
        pass
        

    def does_user_have_notification(self, user):
        user_id = user.get_id()
        if user_id in self.notification_list:
            notification = self.notification_list[user_id]
            if notification:
                self.get_controller_instance().menu_operation(f"Notifications for user {user.get_name()}:")
                self.get_controller_instance().menu_operation(f"-{notification}")

                # Clear notifications after displaying
                self.notification_list[user_id] = None
            else:
                self.get_controller_instance().menu_operation(f"User {user_id} has no notifications.")
        else:
            self.get_controller_instance().menu_operation(f"You do not have any notification.")


    def add_notification_to_user(self, user, notification_type : str, course_name : str):
        notifications_messages = {
            "added_to_wait_list": f"You have been added to the wait list of the course section you selected {course_name}.",
            "removed_from_wait_list": f"You are removed from the wait list of the course section {course_name}."
        }
        # Retrieve the notification message
        #notification_template = notifications_messages.get(notification_type, notifications_messages[""])
        notification_message = notifications_messages[notification_type]
        # Format the message with dynamic data
        #try:
        #    notification_message = notification_template.format(**kwargs)
        #except KeyError as e:
        #    raise ValueError(f"Missing placeholder value for: {e}")
        user_id = user.get_id()
        if self.notification_list.get(user_id) is None:
            self.notification_list[user_id] = notification_message
        else:
            (str)(self.notification_list[user_id]).append("\n" + notification_message)
            #self.notification_list.get(user, []) + [notification_message]
        #print(f"Notification added for {user.get_name()}")

    def get_json(self):
        return  {
            'id': self.get_id(),
            'name':self.get_name(),
            'eMail':self.get_e_mail(),
            'password': self.get_password(),
        }
        