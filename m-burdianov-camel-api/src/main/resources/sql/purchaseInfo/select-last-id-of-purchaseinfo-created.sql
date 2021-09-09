SELECT MAX(purchaseinfo_id) FROM purchaseinfos;
-- SELECT purchaseinfo_id FROM purchaseinfos WHERE purchaseinfo_id=(select last_insert_id())