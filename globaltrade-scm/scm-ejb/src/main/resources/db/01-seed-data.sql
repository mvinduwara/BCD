INSERT INTO vendor (name, contact_email, country, performance_score, status) VALUES
                                                                                 ('Pacific Rim Freight', 'ops@pacificrimfreight.com', 'SG', 4.6, 'ACTIVE'),
                                                                                 ('Meridian Cargo Partners', 'contact@meridiancargo.com', 'IN', 3.9, 'ACTIVE');

INSERT INTO shipment (tracking_number, origin, destination, status, estimated_departure, estimated_arrival, vendor_id, carrier_id) VALUES
                                                                                                                                       ('GT-100234', 'Shenzhen, CN', 'Rotterdam, NL', 'IN_TRANSIT', NOW() - INTERVAL 3 DAY, NOW() + INTERVAL 4 DAY, 1, 1),
                                                                                                                                       ('GT-100235', 'Mumbai, IN', 'Hamburg, DE', 'CUSTOMS_HOLD', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 9 DAY, 2, 1);

INSERT INTO inventory_item (sku, description, quantity_on_hand, reorder_threshold, warehouse_location) VALUES
                                                                                                           ('SKU-4021', 'Marine-grade cargo straps', 340, 100, 'WH-ROTTERDAM-A'),
                                                                                                           ('SKU-7788', 'Reefer container seals', 42, 150, 'WH-HAMBURG-B');

INSERT INTO customs_document (shipment_id, document_type, status, submission_deadline, country_code) VALUES
                                                                                                         (1, 'BILL_OF_LADING', 'SUBMITTED', CURDATE() + INTERVAL 2 DAY, 'NL'),
                                                                                                         (2, 'IMPORT_DECLARATION', 'PENDING', CURDATE() + INTERVAL 1 DAY, 'DE');