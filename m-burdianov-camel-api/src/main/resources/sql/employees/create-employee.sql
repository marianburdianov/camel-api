INSERT INTO employees(first_name, last_name, gender, birth_date, hire_date, role, salary)
VALUES (
        :#${exchangeProperty.firstName},
        :#${exchangeProperty.lastName},
        :#${exchangeProperty.gender},
        :#${exchangeProperty.birthDate},
        :#${exchangeProperty.hireDate},
        :#${exchangeProperty.role},
        :#${exchangeProperty.salary}
        );