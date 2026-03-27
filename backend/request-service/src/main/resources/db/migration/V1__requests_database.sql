CREATE TABLE requests (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          request_date DATE DEFAULT CURRENT_DATE,
                          start_date_time TIMESTAMP NOT NULL,
                          end_date_time TIMESTAMP NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'TAKEN', 'RETURNED', 'LATE')),
                          approved_by BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE request_items (
                               request_id BIGINT NOT NULL,
                               equipment_id BIGINT NOT NULL,
                               PRIMARY KEY (request_id, equipment_id),
                               CONSTRAINT fk_request_items_requests FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE returns (
                         id BIGSERIAL PRIMARY KEY,
                         request_id BIGINT NOT NULL,
                         equipment_id BIGINT NOT NULL,
                         return_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         condition VARCHAR(50) NOT NULL DEFAULT 'EXCELLENT' CHECK (condition IN ('EXCELLENT', 'GOOD', 'DAMAGED', 'BROKEN')),
                         CONSTRAINT fk_returns_requests FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);