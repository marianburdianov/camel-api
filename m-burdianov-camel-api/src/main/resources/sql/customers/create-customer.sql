INSERT INTO customers(first_name, last_name, phone_number, address)
VALUES (
           :#${exchangeProperty.firstName},
           :#${exchangeProperty.lastName},
           :#${exchangeProperty.phoneNumber},
           :#${exchangeProperty.address}
       );