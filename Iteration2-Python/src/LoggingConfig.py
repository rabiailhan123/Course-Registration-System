import logging

def setup_logging(log_file: str = "course_registration_system.log") -> None:
    logging.basicConfig(
        level = logging.INFO,  # Log all INFO, WARNING, ERROR, CRITICAL
        format = "%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        filename = log_file,
        filemode = "w",  # Overwrite the log file each run. should we make it append
    )
    
    """
    Log Levels

    DEBUG: Detailed information, typically useful for diagnosing problems.
    INFO: General events in the program.
    WARNING: Something unexpected, but the program continues to work.
    ERROR: A more serious problem, the program may not work properly.
    CRITICAL: A severe error, the program might stop.
    
    """