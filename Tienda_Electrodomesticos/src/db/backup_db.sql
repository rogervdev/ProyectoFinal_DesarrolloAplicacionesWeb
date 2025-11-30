-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS tienda_electrodomesticos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE tienda_electrodomesticos;

-- Tabla: categorias
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    imagen_nombre VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Tabla: usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    profile_image VARCHAR(150) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT TRUE,
    cuenta_no_bloqueada BOOLEAN DEFAULT TRUE,
    intento_fallido INT DEFAULT 0,
    lock_time DATETIME,
    reset_token VARCHAR(255)
) ENGINE=InnoDB;

-- Tabla: productos
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(500) NOT NULL,
    descripcion TEXT NOT NULL, -- TEXT para soportar hasta 65K caracteres (más que suficiente para 5000)
    categoria_id INT NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(255),
    descuento INT NOT NULL DEFAULT 0,
    precio_con_descuento DOUBLE,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Clave foránea
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Tabla: carrito
CREATE TABLE IF NOT EXISTS carrito (
    id INT AUTO_INCREMENT PRIMARY KEY,
    
    usuario_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 1),
    
    -- Claves foráneas
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
        
    FOREIGN KEY (producto_id) REFERENCES productos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Insertar 5 categorías de computación
INSERT INTO categorias (nombre, imagen_nombre, is_active) VALUES
('Computadoras de Escritorio', 'computadoras_escritorio.webp', TRUE),
('All-in-One', 'all_in_one.webp', TRUE),
('Laptops', 'laptops.webp', TRUE),
('Tablets', 'tablets.webp', TRUE),
('Monitores', 'monitores.webp', TRUE);

-- Insertar 5 productos de computación
INSERT INTO productos (titulo, descripcion, categoria_id, precio, stock, imagen, descuento, precio_con_descuento, is_active) VALUES
('Computadora de Escritorio Gamer', 'PC de escritorio con procesador Intel i7, 16GB RAM, SSD 1TB y tarjeta gráfica RTX 3060.', 1, 1800.00, 8, 'pc_gamer.webp', 10, 1620.00, TRUE),
('All-in-One 27"', 'All-in-One de 27 pulgadas con pantalla 4K, procesador Intel i5, 16GB RAM y 512GB SSD.', 2, 1500.00, 5, 'aio27.webp', 5, 1425.00, TRUE),
('Laptop Ultrabook i7', 'Laptop ultraligera con procesador Intel i7, 16GB RAM, SSD 512GB, ideal para profesionales.', 3, 2200.00, 10, 'laptop_ultrabook.webp', 10, 1980.00, TRUE),
('Tablet Pro 12.9"', 'Tablet de 12.9 pulgadas con pantalla Retina, 256GB almacenamiento y soporte para lápiz digital.', 4, 1200.00, 12, 'tablet_pro.webp', 0, 1200.00, TRUE),
('Monitor 27" 4K', 'Monitor 27 pulgadas 4K UHD, con alta frecuencia de actualización y soporte ajustable.', 5, 450.00, 15, 'monitor_27.webp', 0, 450.00, TRUE);

