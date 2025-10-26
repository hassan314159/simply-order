
INSERT INTO inventory_item
  (sku, name, description, in_stock_qty, reserved_qty, updated_at)
VALUES
  ('SKU-001', 'Laptop ABC', 'Laptop for development', 10, 0, NOW()),
  ('SKU-002', 'PC XYZ', 'PC for Gaming', 5,  0, NOW()),
  ('SKU-003', 'USB-C cable', '1m cable, black', 20, 0, NOW()),
  ('SKU-004', 'HDMI cable', '2m cable black HDMI 2.1 Cable', 12, 0, NOW())
ON CONFLICT (sku) DO UPDATE
SET
  name         = EXCLUDED.name,
  -- Keep an existing non-null description if the new one is NULL
  description  = COALESCE(EXCLUDED.description, inventory_item.description),
  in_stock_qty = EXCLUDED.in_stock_qty,
  reserved_qty = EXCLUDED.reserved_qty,
  -- Always refresh the last-update timestamp on upsert
  updated_at   = NOW();
