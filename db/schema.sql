DROP TABLE IF EXISTS LogAuditoria CASCADE;
DROP TABLE IF EXISTS Mascota CASCADE;
DROP TABLE IF EXISTS Veterinario CASCADE;
DROP TABLE IF EXISTS Adoptante CASCADE;
DROP TABLE IF EXISTS Personal CASCADE;
DROP TABLE IF EXISTS CentroAdopcion CASCADE;

CREATE TABLE CentroAdopcion (
    ID_CENTRO SERIAL,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(250) NOT NULL,
    capacidadMaxima INTEGER NOT NULL CHECK (capacidadMaxima >= 0),
    capacidadActual INTEGER DEFAULT 0 NOT NULL CHECK (capacidadActual >= 0),
    estado_registro VARCHAR(3) DEFAULT 'ACT' NOT NULL CHECK (estado_registro IN ('ACT', 'INA')),
    CONSTRAINT PK_CENTRO_ADOPCION PRIMARY KEY (ID_CENTRO),
    CONSTRAINT CHK_CAPACIDAD_SOBRECUPO CHECK (capacidadActual <= capacidadMaxima)
);

CREATE TABLE Personal (
    ID_PERSONAL SERIAL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(150) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    cargo VARCHAR(50) NOT NULL CHECK (cargo IN ('ADMINISTRADOR', 'VOLUNTARIO', 'VETERINARIO_JEFE')),
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    clave VARCHAR(250) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    estado_registro VARCHAR(3) DEFAULT 'ACT' NOT NULL CHECK (estado_registro IN ('ACT', 'INA')),
    CONSTRAINT PK_PERSONAL PRIMARY KEY (ID_PERSONAL)
);

CREATE TABLE Adoptante (
    ID_ADOPTANTE SERIAL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    direccion VARCHAR(250) NOT NULL,
    ocupacion VARCHAR(150) NOT NULL,
    estado_registro VARCHAR(3) DEFAULT 'ACT' NOT NULL CHECK (estado_registro IN ('ACT', 'INA')),
    CONSTRAINT PK_ADOPTANTE PRIMARY KEY (ID_ADOPTANTE)
);

CREATE TABLE Veterinario (
    ID_VETERINARIO SERIAL,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(150) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    licencia VARCHAR(50) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    estado_registro VARCHAR(3) DEFAULT 'ACT' NOT NULL CHECK (estado_registro IN ('ACT', 'INA')),
    CONSTRAINT PK_VETERINARIO PRIMARY KEY (ID_VETERINARIO)
);

CREATE TABLE Mascota (
    ID_MASCOTA SERIAL,
    ID_CENTRO INTEGER NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    especie VARCHAR(50) NOT NULL CHECK (especie IN ('PERRO', 'GATO', 'OTRO')),
    fechaNacimiento DATE NOT NULL,
    raza VARCHAR(100) NOT NULL,
    estado VARCHAR(50) DEFAULT 'DISPONIBLE' NOT NULL CHECK (estado IN ('DISPONIBLE', 'ADOPTADA', 'EN_TRATAMIENTO')),
    estado_registro VARCHAR(3) DEFAULT 'ACT' NOT NULL CHECK (estado_registro IN ('ACT', 'INA')),
    CONSTRAINT PK_MASCOTA PRIMARY KEY (ID_MASCOTA),
    CONSTRAINT UQ_MASCOTA_IDENTIDAD UNIQUE (nombre, especie, raza, fechaNacimiento),
    CONSTRAINT FK_MASCOTA_CENTRO FOREIGN KEY (ID_CENTRO) REFERENCES CentroAdopcion(ID_CENTRO) ON DELETE RESTRICT
);

CREATE TABLE LogAuditoria (
    ID_LOG SERIAL,
    usuario_identificacion VARCHAR(20) DEFAULT 'SISTEMA',
    rol_usuario VARCHAR(50) DEFAULT 'N/A',
    tabla_afectada VARCHAR(50) NOT NULL,
    accion VARCHAR(20) NOT NULL,
    detalle TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_LOG_AUDITORIA PRIMARY KEY (ID_LOG)
);



CREATE OR REPLACE FUNCTION trg_actualizar_capacidad_centro() RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE CentroAdopcion SET capacidadActual = capacidadActual + 1 WHERE ID_CENTRO = NEW.ID_CENTRO;
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE CentroAdopcion SET capacidadActual = capacidadActual - 1 WHERE ID_CENTRO = OLD.ID_CENTRO;
    ELSIF (TG_OP = 'UPDATE') THEN
        IF (NEW.ID_CENTRO != OLD.ID_CENTRO) THEN
            UPDATE CentroAdopcion SET capacidadActual = capacidadActual - 1 WHERE ID_CENTRO = OLD.ID_CENTRO;
            UPDATE CentroAdopcion SET capacidadActual = capacidadActual + 1 WHERE ID_CENTRO = NEW.ID_CENTRO;
        END IF;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_capacidad AFTER INSERT OR UPDATE OR DELETE ON Mascota FOR EACH ROW EXECUTE FUNCTION trg_actualizar_capacidad_centro();

INSERT INTO CentroAdopcion (nombre, direccion, capacidadMaxima, capacidadActual) VALUES
('Sede Quito Norte (Ponceano)', 'Av. Real Audiencia N50-22 y Galo Plaza Lasso', 5, 0),
('Sede Cumbayá (Valle)', 'Av. Francisco de Orellana y Pampite', 3, 0),
('Sede Quito Sur (Villa Flora)', 'Av. Rodrigo de Chávez y Maldonado', 10, 0),
('Sede Lleno Total (Prueba)', 'Calle Central N1', 2, 0);

INSERT INTO Personal (nombres, apellidos, cedula, cargo, telefono, correo, clave, estado) VALUES
('Admin', 'Sistema', '1700000001', 'ADMINISTRADOR', '0999999999', 'admin@sistema.com', 'admin123', 'ACTIVO'),
('Juan', 'Perez', '1700000002', 'VOLUNTARIO', '0988888888', 'juan@sistema.com', 'voluntario123', 'ACTIVO'),
('Joselyn', 'Cadena', '1700000003', 'VETERINARIO_JEFE', '0977777777', 'vet@sistema.com', 'vet123', 'ACTIVO');

INSERT INTO Adoptante (nombre, cedula, fechaNacimiento, direccion, ocupacion) VALUES
('Juan Pérez', '1711112222', '1990-05-15', 'Av. 10 de Agosto N30-10', 'Ingeniero Comercial'),
('María Gómez', '1722223333', '1985-08-22', 'Calle de las Rosas 123', 'Profesora');

INSERT INTO Veterinario (nombres, apellidos, cedula, especialidad, licencia, telefono) VALUES
('Fabián', 'de la Cruz', '1700009999', 'Cirugía y Traumatología Canina', 'LIC-001', '0990001111'),
('Ana', 'Miranda', '1799990000', 'Medicina General Felina', 'LIC-002', '0991112222');

INSERT INTO Mascota (ID_CENTRO, nombre, especie, fechaNacimiento, raza, estado) VALUES
(1, 'Toby', 'PERRO', '2022-05-10', 'Mestizo', 'DISPONIBLE'),
(1, 'Luna', 'GATO', '2023-01-15', 'Siamés', 'DISPONIBLE'),
(2, 'Rocky', 'PERRO', '2020-08-20', 'Pastor Alemán', 'DISPONIBLE'),
(3, 'Bella', 'PERRO', '2018-03-10', 'Labrador', 'DISPONIBLE'),
(4, 'Max', 'PERRO', '2021-07-22', 'Bulldog', 'DISPONIBLE'),
(4, 'Coco', 'GATO', '2022-11-05', 'Persa', 'DISPONIBLE');
