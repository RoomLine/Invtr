import os
import subprocess
import sys

# 1. Add your microservice folder names to this list as you build them
SERVICES = [
    "auth-service",
    # "equipment-service",  <-- Just uncomment and add new ones later!
    # "request-service"
]

def run_all():
    print("Booting up Invtr Microservices...")

    for service in SERVICES:
        # Check if the folder actually exists before trying to run it
        service_path = os.path.join(os.getcwd(), service)

        if os.path.exists(service_path):
            print(f"Starting {service}...")

            # If on Windows, pop open a new CMD window for the service
            if sys.platform == "win32":
                subprocess.Popen(f'start cmd /k "cd {service} && mvn spring-boot:run"', shell=True)

            # Fallback for Mac/Linux teammates (runs in the background)
            else:
                subprocess.Popen(["mvn", "spring-boot:run"], cwd=service_path)
        else:
            print(f"Warning: Could not find folder for '{service}'. Skipping.")

if __name__ == "__main__":
    run_all()