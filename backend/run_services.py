import os
import subprocess
import sys

SERVICES = [
    "auth-service",
    "api-gateway",
    "equipment-service",
    # "request-service"
]

def run_all():
    print("Booting up Invtr Microservices...")
    parent_path = os.getcwd()

    for service in SERVICES:
        service_path = os.path.join(parent_path, service)

        if os.path.exists(service_path):
            print(f"Starting {service}...")

            if sys.platform == "win32":
                subprocess.Popen(
                    f'start cmd /k "cd /d {parent_path} && mvn spring-boot:run -pl {service}"',
                    shell=True
                )
            else:
                subprocess.Popen(
                    ["mvn", "spring-boot:run", "-pl", service],
                    cwd=parent_path
                )
        else:
            print(f"Warning: Could not find folder for '{service}'. Skipping.")

if __name__ == "__main__":
    run_all()