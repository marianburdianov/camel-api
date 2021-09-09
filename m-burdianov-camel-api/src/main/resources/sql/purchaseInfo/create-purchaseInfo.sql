INSERT INTO purchaseinfos(employee_id, customer_id, cost, amount, date)
VALUES (
           :#${exchangeProperty.employeeId},
           :#${exchangeProperty.customerId},
           :#${exchangeProperty.cost},
           :#${exchangeProperty.amount},
           :#${exchangeProperty.date}
       );