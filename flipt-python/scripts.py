import subprocess


def test():
    """
    Run all tests. Equivalent to:
    `poetry run pytest tests`
    """
    subprocess.run(["python", "-m", "pytest", "tests"])
