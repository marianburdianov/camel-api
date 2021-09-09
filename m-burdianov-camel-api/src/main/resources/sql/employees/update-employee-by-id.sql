UPDATE employees
SET `first_name` = :#${exchangeProperty.firstName},
    `last_name`  = :#${exchangeProperty.lastName},
    `gender`     = :#${exchangeProperty.gender},
    `birth_date` = :#${exchangeProperty.birthDate},
    `hire_date`  = :#${exchangeProperty.hireDate},
    `role`       = :#${exchangeProperty.role},
    `salary`     = :#${exchangeProperty.salary}
WHERE employee_id = :#employeeId