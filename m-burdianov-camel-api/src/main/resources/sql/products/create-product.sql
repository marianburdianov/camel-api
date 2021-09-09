INSERT INTO products(name, price, quantity)
VALUES (
           :#${exchangeProperty.name},
           :#${exchangeProperty.price},
           :#${exchangeProperty.quantity}
       );