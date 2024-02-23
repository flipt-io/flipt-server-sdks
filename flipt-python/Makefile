format:
	poetry run ruff flipt tests --fix
	poetry run black flipt tests

lint:
	poetry run ruff flipt tests
	poetry run black flipt tests --check
	poetry run mypy flipt
	poetry run pytest --dead-fixtures

test:
	poetry run pytest --cov

testcov:
	poetry run pytest --cov --cov-report html

check: format lint test

.PHONY: format lint test check testcov