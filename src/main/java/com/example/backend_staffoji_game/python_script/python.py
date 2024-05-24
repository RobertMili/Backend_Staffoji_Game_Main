import sys
import os
import pandas as pd
import importlib.util
import re

# Function to check if a module is installed
def is_module_installed(name):
    spec = importlib.util.find_spec(name)
    return spec is not None

print(is_module_installed('pandas'))  # This will print True if pandas is installed, False otherwise


if len(sys.argv) > 1:
    output = sys.argv[1]
    print(output)

    # Parse the string representation of the list of users into a list of dictionaries
    user_pattern = r"User\(id=(\d+), username=(.*?), password=(.*?), email=(.*?), isPremium=(.*?), isAdmin=(.*?)\)"
    users = re.findall(user_pattern, output)
    users = [dict(zip(['id', 'username', 'password', 'email', 'isPremium', 'isAdmin'], user)) for user in users]

    # Convert the list of dictionaries into a pandas DataFrame
    df = pd.DataFrame(users)

    # Create a new directory named 'test' in the current directory
    os.makedirs('/home/robert/IdeaProjects/Backend_staffoji_game/src/main/java/com/example/backend_staffoji_game/python_script/Backup', exist_ok=True)

    # Save the DataFrame to an Excel file in the 'test' directory
    df.to_excel('/home/robert/IdeaProjects/Backend_staffoji_game/src/main/java/com/example/backend_staffoji_game/python_script/Backup/backup_file.xlsx', index=False)

