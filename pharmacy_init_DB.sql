
-- SELECT * FROM V$VERSION;
INSERT INTO products VALUES (medicine_t(NULL,"test",10,11.2,3));
-- ***** Creating the classes *****
-- stoka --
CREATE TYPE product_t AS OBJECT (
product_id NUMBER,
product_name NVARCHAR2(30),
product_quantity NUMBER(4),
product_price NUMBER(9,2),
product_accesslevel NUMBER(1)
)
NOT FINAL;
-- lekarstva
CREATE TYPE medicine_t UNDER product_t(
medicine_formula NVARCHAR2(100)
);
-- hranitelni dobavki
CREATE TYPE supplement_t UNDER product_t(
supplement_content NVARCHAR2(100)
);
-- drugi
CREATE TYPE other_t UNDER product_t(
other_description NVARCHAR2(100)
);

-- klient --
CREATE TYPE client_t AS OBJECT (
client_id NUMBER,
client_name NVARCHAR2(30),
client_address NVARCHAR2(50),
client_accessLevel NUMBER(1)
)
NOT FINAL;
-- bolnici
CREATE TYPE hospital_t UNDER client_t();
-- apteki
CREATE TYPE pharmacy_t UNDER client_t();
-- drogerii
CREATE TYPE drugstore_t UNDER client_t();

-- snabditel --
CREATE TYPE supplier_t AS OBJECT (
supplier_id NUMBER,
supplier_name NVARCHAR2(30)
)
FINAL;

-- dostavchik --
CREATE TYPE fulfillment_rate_t AS OBJECT (
time_amount NUMBER(2),
time_scale NVARCHAR2(10)
);
CREATE TYPE shipper_t AS OBJECT (
shipper_id NUMBER,
shipper_name NVARCHAR2(30),
shipper_speed fulfillment_rate_t,
shipper_price NUMBER(9,2)
)
FINAL;

-- dostavki --
CREATE TYPE shipment_t AS OBJECT (
shipment_status NVARCHAR2(20),
shipment_quantity NUMBER(4),
shipment_price NUMBER(9,2)
)
NOT FINAL;
-- vhodqshti
CREATE TYPE inbound_shipment_t UNDER shipment_t (
inbound_id NUMBER,
inbound_supplier REF supplier_t,
inbound_product REF product_t,
inbound_arrive_date DATE
);
-- izhodqshti
CREATE TYPE outbound_shipment_t UNDER shipment_t (
outbound_id NUMBER,
outbound_client REF client_t,
outbound_shipper REF shipper_t,
outbound_product REF product_t,
outbound_requested_date DATE,
outbound_sent_date DATE
);

-- ***** Creating and configuring the data tables  *****
-- stoki --
CREATE TABLE products OF product_t;
ALTER TABLE products
ADD PRIMARY KEY (product_id);
ALTER TABLE products
ADD IDENTITY product_id;
MODIFY product_id GENERATED ALWAYS AS IDENTITY PRIMARY KEY;
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
INSERT ALL
-- lekarstva
INTO products VALUES(medicine_t(1, 'Medicine 1', 100, 19.99, 1, 'Formula 1'))
INTO products VALUES(medicine_t(2, 'Medicine 2', 50, 29.99, 1, 'Formula 2'))
-- dobavki
INTO products VALUES(supplement_t(3, 'Supplement 1', 200, 9.99, 2, 'Content 1'))
INTO products VALUES(supplement_t(4, 'Supplement 2', 75, 14.99, 2, 'Content 2'))
-- drugi
INTO products VALUES(other_t(5, 'Other 1', 300, 4.99, 3, 'Description 1'))
INTO products VALUES(other_t(6, 'Other 2', 120, 7.99, 3, 'Description 2'))
SELECT * FROM DUAL;

-- klienti --
INSERT ALL
-- bolnici
INTO clients VALUES(hospital_t(1, 'Hospital 1', 'Address 1', 1))
INTO clients VALUES(hospital_t(2, 'Hospital 2', 'Address 2', 1))
-- apteki
INTO clients VALUES(pharmacy_t(3, 'Pharmacy 1', 'Address 3', 2))
INTO clients VALUES(pharmacy_t(4, 'Pharmacy 2', 'Address 4', 2))
-- drogerii
INTO clients VALUES(drugstore_t(5, 'Drugstore 1', 'Address 5', 3))
INTO clients VALUES(drugstore_t(6, 'Drugstore 2', 'Address 6', 3))
SELECT * FROM DUAL;

-- snabditeli --
INSERT ALL
INTO suppliers VALUES(supplier_t(1, 'Supplier 1'))
INTO suppliers VALUES(supplier_t(2, 'Supplier 2'))
SELECT * FROM DUAL;

-- dostavchici --
INSERT ALL
INTO shippers VALUES(shipper_t(1, 'Shipper 1', fulfillment_rate_t(2, 'days'), 15.99))
INTO shippers VALUES(shipper_t(2, 'Shipper 2', fulfillment_rate_t(3, 'days'), 20.99))
SELECT * FROM DUAL;

-- vhodqshti dostavki --
INSERT ALL
INTO inbound_shipments VALUES(inbound_shipment_t(
'Enroute',
10,
10.10,
1, 
(SELECT REF(s) FROM suppliers s WHERE s.supplier_id = 1), 
(SELECT REF(p) FROM products p WHERE p.product_id = 1), 
TO_DATE('2024-02-22', 'YYYY-MM-DD')
))
INTO inbound_shipments VALUES(inbound_shipment_t(
'Arrived',
2,
222,
2, 
(SELECT REF(s) FROM suppliers s WHERE s.supplier_id = 2), 
(SELECT REF(p) FROM products p WHERE p.product_id = 3), 
TO_DATE('2024-02-23', 'YYYY-MM-DD')
))
SELECT * FROM DUAL;

-- izhodqshti dostavki --
INSERT ALL
INTO outbound_shipments VALUES(outbound_shipment_t(
'Awaiting pickup',
1,
11.01,
1, 
(SELECT REF(c) FROM clients c WHERE c.client_id = 3), 
(SELECT REF(s) FROM shippers s WHERE s.shipper_id = 1), 
(SELECT REF(p) FROM products p WHERE p.product_id = 2), 
TO_DATE('2024-02-24', 'YYYY-MM-DD'), 
TO_DATE('2024-02-25', 'YYYY-MM-DD')
))
INTO outbound_shipments VALUES(outbound_shipment_t(
'Delivered',
20,
20.22,
2, 
(SELECT REF(c) FROM clients c WHERE c.client_id = 4),
(SELECT REF(s) FROM shippers s WHERE s.shipper_id = 2),
(SELECT REF(p) FROM products p WHERE p.product_id = 4),
TO_DATE('2024-02-26', 'YYYY-MM-DD'),
TO_DATE('2024-02-27', 'YYYY-MM-DD')
))
SELECT * FROM DUAL;



