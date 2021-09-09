INSERT INTO purchaseinfos_products (product_id, purchaseinfo_id )
VALUES (
           :#productId,
--            :#purchaseInfoId
           :#${exchangeProperty.lastPurchaseInfoId}
       );