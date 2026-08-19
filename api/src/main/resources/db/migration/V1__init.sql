CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE,
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE stores (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo VARCHAR(255),
    address_number VARCHAR(255),
    address_street VARCHAR(255),
    address_area VARCHAR(255),
    address_lga VARCHAR(255),
    address_state VARCHAR(255),
    address_country VARCHAR(255),
    phone_number VARCHAR(255),
    contact_info VARCHAR(255),
    operating_hours VARCHAR(255),
    tax_number VARCHAR(255),
    currency VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    owner_id UUID NOT NULL UNIQUE REFERENCES users(id)
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    store_id UUID NOT NULL REFERENCES stores(id)
);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) UNIQUE,
    email VARCHAR(255),
    address VARCHAR(255),
    outstanding_balance NUMERIC(15,2),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    store_id UUID NOT NULL REFERENCES stores(id)
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    selling_price NUMERIC(10,2) NOT NULL,
    cost_price NUMERIC(10,2) NOT NULL,
    wholesale_price NUMERIC(10,2) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    barcode VARCHAR(255),
    active BOOLEAN NOT NULL,
    archived BOOLEAN NOT NULL,
    min_stock_level INTEGER NOT NULL,
    max_stock_level INTEGER,
    image VARCHAR(255),
    category_id UUID REFERENCES categories(id),
    store_id UUID NOT NULL REFERENCES stores(id),
    supplier_id UUID REFERENCES suppliers(id),
    batch_number VARCHAR(255),
    expiry_date DATE
);

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) UNIQUE,
    email VARCHAR(255),
    address VARCHAR(255),
    credit_limit NUMERIC(15,2),
    outstanding_balance NUMERIC(15,2),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    store_id UUID NOT NULL REFERENCES stores(id)
);

CREATE TABLE branches (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(255),
    manager VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    store_id UUID NOT NULL REFERENCES stores(id)
);

CREATE TABLE stocks (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE REFERENCES products(id),
    quantity INTEGER NOT NULL,
    low_stock_threshold INTEGER NOT NULL,
    unit VARCHAR(255) NOT NULL,
    track_inventory BOOLEAN NOT NULL,
    batch_number VARCHAR(255),
    expiry_date DATE,
    min_stock_level INTEGER NOT NULL,
    max_stock_level INTEGER
);

CREATE TABLE product_locations (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    location_type VARCHAR(255) NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL
);

CREATE TABLE wholesale_price_rules (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    min_quantity INTEGER NOT NULL,
    max_quantity INTEGER,
    price NUMERIC(10,2) NOT NULL
);

CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    url VARCHAR(255) NOT NULL,
    alt_text VARCHAR(255),
    sort_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    price NUMERIC(10,2) NOT NULL,
    billing_interval VARCHAR(255) NOT NULL,
    max_products INTEGER NOT NULL,
    max_users INTEGER NOT NULL,
    max_branches INTEGER NOT NULL,
    trial_days INTEGER NOT NULL,
    annual_price NUMERIC(10,2),
    whatsapp_enabled BOOLEAN NOT NULL,
    whatsapp_commerce_enabled BOOLEAN NOT NULL,
    whatsapp_commerce_commission_percent NUMERIC(5,2) NOT NULL,
    advanced_reports_enabled BOOLEAN NOT NULL,
    api_enabled BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL,
    features TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL UNIQUE REFERENCES stores(id),
    plan_id UUID NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    paystack_subscription_code VARCHAR(255),
    paystack_authorization_code VARCHAR(255),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL,
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(255)
);

CREATE TABLE sales (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES stores(id),
    customer_name VARCHAR(255),
    customer_phone VARCHAR(255),
    total_amount NUMERIC(10,2) NOT NULL,
    total_cost NUMERIC(10,2) NOT NULL,
    profit NUMERIC(10,2) NOT NULL,
    sale_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(255),
    notes TEXT,
    payment_status VARCHAR(255) NOT NULL,
    discount NUMERIC,
    customer_id UUID REFERENCES customers(id)
);

CREATE TABLE sale_items (
    id UUID PRIMARY KEY,
    sale_id UUID NOT NULL REFERENCES sales(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    cost_price NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL
);

CREATE TABLE purchases (
    id UUID PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    store_id UUID NOT NULL REFERENCES stores(id),
    supplier_id UUID REFERENCES suppliers(id),
    total_amount NUMERIC(12,2) NOT NULL,
    total_cost NUMERIC(12,2) NOT NULL,
    amount_paid NUMERIC(12,2) NOT NULL,
    outstanding NUMERIC(12,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    notes TEXT
);

CREATE TABLE purchase_items (
    id UUID PRIMARY KEY,
    purchase_id UUID NOT NULL REFERENCES purchases(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    cost_price NUMERIC(10,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL
);

CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES stores(id),
    category VARCHAR(255) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    description VARCHAR(255),
    receipt VARCHAR(255),
    expense_date TIMESTAMP NOT NULL,
    created_by VARCHAR(255)
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    store_id UUID NOT NULL REFERENCES stores(id),
    quantity INTEGER NOT NULL,
    movement_type VARCHAR(255) NOT NULL,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    reference VARCHAR(255),
    reason VARCHAR(255),
    user_id UUID,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE stock_transfers (
    id UUID PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    from_store_id UUID NOT NULL REFERENCES stores(id),
    to_store_id UUID NOT NULL REFERENCES stores(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP
);

CREATE TABLE returns (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES stores(id),
    sale_id UUID REFERENCES sales(id),
    reference VARCHAR(255) NOT NULL UNIQUE,
    reason VARCHAR(255) NOT NULL,
    refund_amount NUMERIC(10,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    refund_method VARCHAR(255),
    approved_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE return_items (
    id UUID PRIMARY KEY,
    return_id UUID NOT NULL REFERENCES returns(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    restock BOOLEAN NOT NULL
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES stores(id),
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    channel VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    target VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    store_id UUID NOT NULL REFERENCES stores(id),
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);