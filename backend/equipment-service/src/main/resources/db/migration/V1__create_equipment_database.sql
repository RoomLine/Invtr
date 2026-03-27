CREATE TABLE equipment (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           type VARCHAR(50) NOT NULL CHECK (type IN ('ELECTRICAL', 'FURNITURE', 'UTILITY')),
                           serial_no VARCHAR(255) NOT NULL,
                           qr_code_url VARCHAR(255) NOT NULL,
                           status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'CHECKED_OUT', 'UNDER_REPAIR', 'RETIRED')),
                           condition VARCHAR(50) NOT NULL DEFAULT 'EXCELLENT' CHECK (condition IN ('EXCELLENT', 'GOOD', 'DAMAGED', 'BROKEN')),
                           location VARCHAR(255) NOT NULL,
                           is_sensitive BOOLEAN DEFAULT false,
                           photo_url VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE condition_logs (
                                id BIGSERIAL PRIMARY KEY,
                                equipment_id BIGINT NOT NULL,
                                condition VARCHAR(50) NOT NULL CHECK (condition IN ('EXCELLENT', 'GOOD', 'DAMAGED', 'BROKEN')),
                                logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_condition_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);