import unittest
from Admin import Admin
from CourseRegistrationSystem import CourseRegistrationSystem
import logging

logger = logging.getLogger(__name__)  # to ensure that the logger is named after the module

class TestAdmin(unittest.TestCase):


    def test_initialize_admin(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        admin = Admin("99", "Admin Name", "admin@mail", "password1", c_instance)
        """Test the initialization of an Admin instance."""
        self.assertEqual(admin.get_name(), "Admin Name")
        self.assertEqual(admin.get_id(), "99")
        self.assertEqual(admin.get_e_mail(), "admin@mail")
        self.assertTrue(admin.get_password(), "password")
        logger.info("Passed test_initialize_admin.")

    def test_create_new_course(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        admin = Admin("99", "Admin Name", "admin@mail", "password1", c_instance)
        # You can simulate user input through mock or set specific attributes to bypass user prompts
        admin.create_new_course()  # Simulating user input in the function
        # Assertions for expected file creation or in-memory changes could be added here
        logger.info("Passed test_create_new_course.")

    def test_create_new_user(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        admin = Admin("99", "Admin Name", "admin@mail", "password1", c_instance)
        admin.create_new_user()  # Simulating user input in the function
        # Assertions to check if user data is correctly serialized could be added here
        logger.info("Passed test_create_new_user.")

    def test_admin_main_menu(self):
        c_instance = CourseRegistrationSystem.get_course_registration_system_instance()
        admin = Admin("99", "Admin Name", "admin@mail", "password1", c_instance)
        # Simulating user choices
        admin.main_menu()
        # You would add assertions to ensure correct flow after a user selects an option
        logger.info("Passed test_admin_main_menu.")
    
    

if __name__ == '__main__':
    unittest.main()
