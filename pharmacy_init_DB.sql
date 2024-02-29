-- SELECT * FROM V$VERSION;

-- *** DELETING DB STUFF *** --
-- tables
DROP TABLE products;
DROP TABLE clients;
DROP TABLE shippers;
DROP TABLE suppliers;
DROP TABLE inbound_shipments;
DROP TABLE outbound_shipments;
-- sequences
DROP SEQUENCE product_seq;
DROP SEQUENCE client_seq;
DROP SEQUENCE shipper_seq;
DROP SEQUENCE supplier_seq;
DROP SEQUENCE inbound_seq;
DROP SEQUENCE outbound_seq;
-- types
DROP TYPE medicine_t;
DROP TYPE supplement_t;
DROP TYPE other_t;
DROP TYPE hospital_t;
DROP TYPE pharmacy_t;
DROP TYPE drugstore_t;
DROP TYPE inbound_shipment_t;
DROP TYPE outbound_shipment_t;
DROP TYPE shipment_t;
DROP TYPE shipper_t;
DROP TYPE supplier_t;
DROP TYPE fulfillment_rate_t;
DROP TYPE product_t;
DROP TYPE client_t;

-- ***** Creating the classes *****
-- stoka --
CREATE OR REPLACE TYPE product_t AS OBJECT (
product_id NUMBER,
product_name NVARCHAR2(30),
product_quantity NUMBER(4),
product_price NUMBER(9,2),
product_accesslevel NUMBER(1)
) NOT FINAL;

CREATE OR REPLACE TYPE medicine_t UNDER product_t(
medicine_formula NVARCHAR2(100));

CREATE OR REPLACE TYPE supplement_t UNDER product_t(
supplement_content NVARCHAR2(100));

CREATE OR REPLACE TYPE other_t UNDER product_t(
other_description NVARCHAR2(100));

-- klient --
CREATE OR REPLACE TYPE client_t AS OBJECT (
client_id NUMBER,
client_name NVARCHAR2(30),
client_address NVARCHAR2(50),
client_accessLevel NUMBER(1)
) NOT FINAL;

CREATE OR REPLACE TYPE hospital_t UNDER client_t();

CREATE OR REPLACE TYPE pharmacy_t UNDER client_t();

CREATE OR REPLACE TYPE drugstore_t UNDER client_t();

-- snabditel --
CREATE OR REPLACE TYPE supplier_t AS OBJECT (
supplier_id NUMBER,
supplier_name NVARCHAR2(30)
) FINAL;

-- dostavchik --
CREATE OR REPLACE TYPE fulfillment_rate_t AS OBJECT (
time_amount NUMBER(2),
time_scale NVARCHAR2(10));

CREATE OR REPLACE TYPE shipper_t AS OBJECT (
shipper_id NUMBER,
shipper_name NVARCHAR2(30),
shipper_speed fulfillment_rate_t,
shipper_price NUMBER(9,2)
) FINAL;

-- dostavki --
CREATE OR REPLACE TYPE shipment_t AS OBJECT (
shipment_status NVARCHAR2(20),
shipment_quantity NUMBER(4),
shipment_price NUMBER(9,2)
) NOT FINAL;

-- vhodqshti
CREATE OR REPLACE TYPE inbound_shipment_t UNDER shipment_t (
inbound_id NUMBER,
inbound_supplier REF supplier_t,
inbound_product REF product_t,
inbound_arrive_date DATE);

-- izhodqshti
CREATE OR REPLACE TYPE outbound_shipment_t UNDER shipment_t (
outbound_id NUMBER,
outbound_client REF client_t,
outbound_shipper REF shipper_t,
outbound_product REF product_t,
outbound_requested_date DATE,
outbound_sent_date DATE);

-- ***** Creating and configuring the database tables  *****
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE client_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE supplier_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE shipper_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE inbound_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
CREATE SEQUENCE outbound_seq START WITH 1 INCREMENT BY 1 NOMAXVALUE;
-- stoki --
CREATE TABLE products OF product_t;
ALTER TABLE products
ADD PRIMARY KEY (product_id);
-- klienti --
CREATE TABLE clients OF client_t;
ALTER TABLE clients
ADD PRIMARY KEY (client_id);
-- snabditeli --
CREATE TABLE suppliers OF supplier_t;
ALTER TABLE suppliers
ADD PRIMARY KEY (supplier_id);
-- dostavchici --
CREATE TABLE shippers OF shipper_t;
ALTER TABLE shippers
ADD PRIMARY KEY (shipper_id);
-- vhodqshti dostavki --
CREATE TABLE inbound_shipments OF inbound_shipment_t;
ALTER TABLE inbound_shipments
ADD PRIMARY KEY (inbound_id);
-- izhodqshti dostavki --
CREATE TABLE outbound_shipments OF outbound_shipment_t;
ALTER TABLE outbound_shipments
ADD PRIMARY KEY (outbound_id);

-- ***** Seeding with initial data *****
-- stoki --
-- lekarstva
INSERT INTO products VALUES(medicine_t(product_seq.NEXTVAL, 'Medicine 1', 100, 19.99, 1, 'Formula 1'));
INSERT INTO products VALUES(medicine_t(product_seq.NEXTVAL, 'Medicine 2', 50, 29.99, 1, 'Formula 2'));
-- dobavki
INSERT INTO products VALUES(supplement_t(product_seq.NEXTVAL, 'Supplement 1', 200, 9.99, 2, 'Content 1'));
INSERT INTO products VALUES(supplement_t(product_seq.NEXTVAL, 'Supplement 2', 75, 14.99, 2, 'Content 2'));
-- drugi
INSERT INTO products VALUES(other_t(product_seq.NEXTVAL, 'Other 1', 300, 4.99, 3, 'Description 1'));
INSERT INTO products VALUES(other_t(product_seq.NEXTVAL, 'Other 2', 120, 7.99, 3, 'Description 2'));

-- klienti --
-- bolnici
INSERT INTO clients VALUES(hospital_t(client_seq.NEXTVAL, 'Hospital 1', 'Address 1', 1));
INSERT INTO clients VALUES(hospital_t(client_seq.NEXTVAL, 'Hospital 2', 'Address 2', 1));
-- apteki
INSERT INTO clients VALUES(pharmacy_t(client_seq.NEXTVAL, 'Pharmacy 1', 'Address 3', 2));
INSERT INTO clients VALUES(pharmacy_t(client_seq.NEXTVAL, 'Pharmacy 2', 'Address 4', 2));
-- drogerii
INSERT INTO clients VALUES(drugstore_t(client_seq.NEXTVAL, 'Drugstore 1', 'Address 5', 3));
INSERT INTO clients VALUES(drugstore_t(client_seq.NEXTVAL, 'Drugstore 2', 'Address 6', 3));

-- snabditeli --
INSERT INTO suppliers VALUES(supplier_t(supplier_seq.NEXTVAL, 'Supplier 1'));
INSERT INTO suppliers VALUES(supplier_t(supplier_seq.NEXTVAL, 'Supplier 2'));

-- dostavchici --
INSERT INTO shippers VALUES(shipper_t(shipper_seq.NEXTVAL, 'Shipper 1', fulfillment_rate_t(2, 'days'), 15.99));
INSERT INTO shippers VALUES(shipper_t(shipper_seq.NEXTVAL, 'Shipper 2', fulfillment_rate_t(3, 'days'), 20.99));

-- vhodqshti dostavki --
INSERT INTO inbound_shipments VALUES(inbound_shipment_t(
'Enroute', 10, 10.10,
inbound_seq.NEXTVAL, 
(SELECT REF(s) FROM suppliers s WHERE s.supplier_id = 1), 
(SELECT REF(p) FROM products p WHERE p.product_id = 1), 
TO_DATE('2024-02-22', 'YYYY-MM-DD')
));
INSERT INTO inbound_shipments VALUES(inbound_shipment_t(
'Arrived', 2, 222,
inbound_seq.NEXTVAL, 
(SELECT REF(s) FROM suppliers s WHERE s.supplier_id = 2), 
(SELECT REF(p) FROM products p WHERE p.product_id = 3), 
TO_DATE('2024-02-23', 'YYYY-MM-DD')
));

-- izhodqshti dostavki --
INSERT INTO outbound_shipments VALUES(outbound_shipment_t(
'Awaiting pickup', 1, 11.01,
outbound_seq.NEXTVAL, 
(SELECT REF(c) FROM clients c WHERE c.client_id = 3), 
(SELECT REF(s) FROM shippers s WHERE s.shipper_id = 1), 
(SELECT REF(p) FROM products p WHERE p.product_id = 2), 
TO_DATE('2024-02-24', 'YYYY-MM-DD'), 
TO_DATE('2024-02-25', 'YYYY-MM-DD')
));
INSERT INTO outbound_shipments VALUES(outbound_shipment_t(
'Delivered', 20, 20.22,
outbound_seq.NEXTVAL, 
(SELECT REF(c) FROM clients c WHERE c.client_id = 4),
(SELECT REF(s) FROM shippers s WHERE s.shipper_id = 2),
(SELECT REF(p) FROM products p WHERE p.product_id = 4),
TO_DATE('2024-02-26', 'YYYY-MM-DD'),
TO_DATE('2024-02-27', 'YYYY-MM-DD')
));
