SELECT purchaseinfos.purchaseinfo_id, employee_id, customer_id, cost, amount, date, product_id
FROM purchaseinfos
    INNER JOIN purchaseinfos_products
ON purchaseinfos.purchaseinfo_id = purchaseinfos_products.purchaseinfo_id
WHERE purchaseinfos.purchaseinfo_id = :#purchaseInfoId;