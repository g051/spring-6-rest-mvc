DROP TABLE IF EXISTS beer_order_shipment;

CREATE TABLE beer_order_shipment (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    beer_order_id VARCHAR(36) UNIQUE,
    tracking_number VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP(6) DEFAULT NULL,
    version BIGINT DEFAULT NULL,
    CONSTRAINT bos_pk FOREIGN KEY (beer_order_id) REFERENCES beer_order (id)
);

--ALTER TABLE beer_order
--    ADD COLUMN beer_order_shipment_id VARCHAR(36);
--
--ALTER TABLE beer_order
--    ADD CONSTRAINT bos_shipment_fk
--        FOREIGN KEY (beer_order_shipment_id) REFERENCES beer_order_shipment (id);

ALTER TABLE beer_order
    ADD COLUMN beer_order_shipment_id VARCHAR(36)
      CONSTRAINT bos_shipment_fk REFERENCES beer_order_shipment (id);