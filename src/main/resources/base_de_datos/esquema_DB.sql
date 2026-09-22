DROP TABLE IF EXISTS productos;

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio NUMERIC(12, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    
    -- Restricciones
    CONSTRAINT chk_precio_positivo CHECK (precio > 0),
    CONSTRAINT chk_stock_no_negativo CHECK (stock >= 0),
    CONSTRAINT chk_estado_valido CHECK (estado IN ('Activo', 'Inactivo'))
);

-- Indices
CREATE INDEX idx_productos_codigo ON productos(codigo);
CREATE INDEX idx_productos_nombre ON productos(nombre);