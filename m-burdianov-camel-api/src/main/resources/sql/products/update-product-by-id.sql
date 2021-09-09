UPDATE products
SET `name`     = :#${exchangeProperty.name},
    `price`    = :#${exchangeProperty.price},
    `quantity` = :#${exchangeProperty.quantity}
WHERE product_id = :#productId