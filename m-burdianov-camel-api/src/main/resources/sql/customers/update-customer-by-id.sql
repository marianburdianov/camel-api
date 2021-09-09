UPDATE customers
SET `first_name`   = :#${exchangeProperty.firstName},
    `last_name`    = :#${exchangeProperty.lastName},
    `phone_number` = :#${exchangeProperty.phoneNumber},
    `address`      = :#${exchangeProperty.address}
WHERE customer_id = :#customerId