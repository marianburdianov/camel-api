UPDATE purchaseinfos
SET `employee_id` = :#${exchangeProperty.employeeId},
    `customer_id`  = :#${exchangeProperty.customerId},
    `cost`        = :#${exchangeProperty.cost},
    `amount`      = :#${exchangeProperty.amount},
    `date`        = :#${exchangeProperty.date}
WHERE purchaseinfo_id = :#purchaseInfoId