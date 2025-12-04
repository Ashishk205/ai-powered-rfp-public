-- 1. Create Business Tables
-- vendors
CREATE TABLE IF NOT EXISTS vendors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- rfps table store the requirement of the users
CREATE TABLE IF NOT EXISTS rfps (
    id BIGSERIAL PRIMARY KEY,
    description_raw TEXT, -- user prompt
    parsed_json_data jsonb DEFAULT NULL, -- user prompt in structured format
    rfp_email_format TEXT, -- we send this email format to vendors
    status VARCHAR(20) DEFAULT 'OPEN', -- when vendor replies we close this status
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- proposals (we store the vendors responses in structured format jsonb format)
CREATE TABLE IF NOT EXISTS proposals (
    id BIGSERIAL PRIMARY KEY,
    rfp_id BIGSERIAL REFERENCES rfps(id), -- which rpf this response belongs to
    vendor_id BIGSERIAL REFERENCES vendors(id), -- vendor
    email_content_raw TEXT, -- email data
    parsed_vendor_res jsonb DEFAULT NULL, -- parsed vendor response
    ai_score BIGINT, -- optional 
    ai_analysis TEXT, -- optional 
    status VARCHAR(20), -- 'SENT, RECEIVED, REJECTED, ACCEPTED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- we treat this like 'sent_at'
);

-- 3. Insert Dummy Data (The "Fake Money")
INSERT INTO vendors (name, email, description) VALUES
('Dell Technologies', 'business@dell.com', 'Dell sell world class laptops/desktops'),
('Lenovo', 'lenovo@lenovo.com', 'Lenovo sell world class laptops/desktops'),
('Hp', 'hp@hp.com', 'hp'),
('Test Computers And Security Systems', 'test@planetlearning.org','For testing email replies');