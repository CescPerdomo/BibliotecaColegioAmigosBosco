-- Create database
CREATE DATABASE IF NOT EXISTS biblioteca_db;
USE biblioteca_db;

-- Create usuarios table
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    tipo_usuario ENUM('Admin', 'Profesor', 'Alumno') NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create materiales table
CREATE TABLE IF NOT EXISTS materiales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    autor VARCHAR(100),
    categoria VARCHAR(50),
    estado ENUM('Disponible', 'Prestado', 'En Mantenimiento') DEFAULT 'Disponible',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create prestamos table
CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    material_id INT,
    usuario_id INT,
    fecha_prestamo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado ENUM('Activo', 'Devuelto', 'Atrasado') DEFAULT 'Activo',
    FOREIGN KEY (material_id) REFERENCES materiales(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Create mora table
CREATE TABLE IF NOT EXISTS mora (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id INT,
    monto DECIMAL(10,2) NOT NULL,
    estado ENUM('Pendiente', 'Pagado') DEFAULT 'Pendiente',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prestamo_id) REFERENCES prestamos(id)
);

-- Insert default admin user
INSERT INTO usuarios (usuario, contrasena, nombre, correo, tipo_usuario) 
VALUES ('admin', 'admin123', 'Administrador', 'admin@donbosco.edu', 'Admin');

-- Insert some test materials
INSERT INTO materiales (titulo, autor, categoria) VALUES
('Matemáticas Básicas', 'Juan Pérez', 'Matemáticas'),
('Historia Universal', 'María García', 'Historia'),
('Física Moderna', 'Pedro López', 'Ciencias'),
('Literatura Española', 'Ana Martínez', 'Literatura');
