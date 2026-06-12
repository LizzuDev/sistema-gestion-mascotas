-- =============================================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS Y PERSISTENCIA (POSTGRESQL)
-- Proyecto: Sistema de Gestión de Adopciones para Refugios Multi-sede
-- Metodología: TSPi - Grupo 4
-- Estándar de Base de Datos: Tablas en PascalCase, Atributos en camelCase
-- =============================================================================

-- Eliminar tablas si existen para garantizar limpieza
DROP TABLE IF EXISTS Auditoria CASCADE;
DROP TABLE IF EXISTS CitaVeterinaria CASCADE;
DROP TABLE IF EXISTS Adopcion CASCADE;
DROP TABLE IF EXISTS Mascota CASCADE;
DROP TABLE IF EXISTS Veterinario CASCADE;
DROP TABLE IF EXISTS Adoptante CASCADE;
DROP TABLE IF EXISTS Personal CASCADE;
DROP TABLE IF EXISTS CentroAdopcion CASCADE;

-- 1. TABLA: CentroAdopcion
CREATE TABLE CentroAdopcion (
    idCentro SERIAL,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(250) NOT NULL,
    capacidadMaxima INTEGER NOT NULL CHECK (capacidadMaxima >= 0),
    capacidadActual INTEGER DEFAULT 0 NOT NULL CHECK (capacidadActual >= 0),
    CONSTRAINT PK_CentroAdopcion PRIMARY KEY (idCentro),
    CONSTRAINT CHK_CAPACIDAD_SOBRECUPO CHECK (capacidadActual <= capacidadMaxima)
);

-- 2. TABLA: Personal
CREATE TABLE Personal (
    idPersonal SERIAL,
    nombre VARCHAR(150) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    rol VARCHAR(50) NOT NULL CHECK (rol IN ('LIDER', 'PLANIFICACION_CALIDAD', 'DESARROLLO', 'PERSONAL_APOYO')),
    estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT PK_Personal PRIMARY KEY (idPersonal)
);

-- 3. TABLA: Adoptante
CREATE TABLE Adoptante (
    idAdoptante SERIAL,
    nombre VARCHAR(150) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    CONSTRAINT PK_Adoptante PRIMARY KEY (idAdoptante)
);

-- 4. TABLA: Veterinario
CREATE TABLE Veterinario (
    idVeterinario SERIAL,
    nombre VARCHAR(150) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    especialidad VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    CONSTRAINT PK_Veterinario PRIMARY KEY (idVeterinario)
);

-- 5. TABLA: Mascota
CREATE TABLE Mascota (
    idMascota SERIAL,
    idCentro INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL CHECK (especie IN ('PERRO', 'GATO', 'OTRO')),
    edad INTEGER NOT NULL CHECK (edad >= 0),
    estado VARCHAR(30) DEFAULT 'DISPONIBLE' NOT NULL CHECK (estado IN ('DISPONIBLE', 'ADOPTADA', 'EN_TRATAMIENTO')),
    CONSTRAINT PK_Mascota PRIMARY KEY (idMascota),
    CONSTRAINT FK_Mascota_Centro FOREIGN KEY (idCentro) REFERENCES CentroAdopcion(idCentro) ON DELETE RESTRICT
);

-- 6. TABLA: Adopcion (Proceso Crítico 2 - Cierre de Adopción)
CREATE TABLE Adopcion (
    idAdopcion SERIAL,
    idMascota INTEGER NOT NULL UNIQUE, -- Una mascota solo puede ser adoptada una vez
    idAdoptante INTEGER NOT NULL,
    idPersonal INTEGER NOT NULL,
    fechaAdopcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_Adopcion PRIMARY KEY (idAdopcion),
    CONSTRAINT FK_Adopcion_Mascota FOREIGN KEY (idMascota) REFERENCES Mascota(idMascota) ON DELETE RESTRICT,
    CONSTRAINT FK_Adopcion_Adoptante FOREIGN KEY (idAdoptante) REFERENCES Adoptante(idAdoptante) ON DELETE RESTRICT,
    CONSTRAINT FK_Adopcion_Personal FOREIGN KEY (idPersonal) REFERENCES Personal(idPersonal) ON DELETE RESTRICT
);

-- 7. TABLA: CitaVeterinaria (Proceso Crítico 3 - Citas)
CREATE TABLE CitaVeterinaria (
    idCita SERIAL,
    idMascota INTEGER NOT NULL,
    idVeterinario INTEGER NOT NULL,
    idPersonal INTEGER NOT NULL,
    fechaCita TIMESTAMP NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    estado VARCHAR(30) DEFAULT 'PROGRAMADA' NOT NULL CHECK (estado IN ('PROGRAMADA', 'COMPLETADA', 'CANCELADA')),
    CONSTRAINT PK_CitaVeterinaria PRIMARY KEY (idCita),
    CONSTRAINT FK_Cita_Mascota FOREIGN KEY (idMascota) REFERENCES Mascota(idMascota) ON DELETE CASCADE,
    CONSTRAINT FK_Cita_Veterinario FOREIGN KEY (idVeterinario) REFERENCES Veterinario(idVeterinario) ON DELETE RESTRICT,
    CONSTRAINT FK_Cita_Personal FOREIGN KEY (idPersonal) REFERENCES Personal(idPersonal) ON DELETE RESTRICT
);

-- 8. TABLA: Auditoria (STD-06 - Tabla Transversal de Auditoría Obligatoria)
CREATE TABLE Auditoria (
    idAuditoria SERIAL,
    idPersonal INTEGER NOT NULL,
    accion VARCHAR(50) NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
    detalle TEXT NOT NULL,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_Auditoria PRIMARY KEY (idAuditoria),
    CONSTRAINT FK_Auditoria_Personal FOREIGN KEY (idPersonal) REFERENCES Personal(idPersonal) ON DELETE RESTRICT
);


-- =============================================================================
-- INSERCIÓN DE DATOS SEMILLA (MOCK DATA PARA PRUEBAS)
-- =============================================================================

-- Inserción de Centros de Adopción (Sedes)
INSERT INTO CentroAdopcion (nombre, direccion, capacidadMaxima, capacidadActual) VALUES
('Sede Quito Norte (Ponceano)', 'Av. Real Audiencia N50-22 y Galo Plaza Lasso', 5, 0),
('Sede Cumbayá (Valle)', 'Av. Francisco de Orellana y Pampite', 3, 0),
('Sede Quito Sur (Villa Flora)', 'Av. Rodrigo de Chávez y Maldonado', 10, 0),
('Sede Lleno Total (Prueba)', 'Calle Central N1', 2, 2); -- Sede saturada para pruebas de cupo = 0

-- Inserción de Personal (Miembros del Equipo e Integrantes)
INSERT INTO Personal (nombre, identificacion, rol, estado) VALUES
('Paúl Rosero', '1712345678', 'LIDER', 'ACTIVO'),
('Gabriel Aguinaga', '1787654321', 'PLANIFICACION_CALIDAD', 'ACTIVO'),
('Joselyn Cadena', '1755554444', 'DESARROLLO', 'ACTIVO');

-- Inserción de Adoptantes
INSERT INTO Adoptante (nombre, identificacion, correo, telefono) VALUES
('Juan Pérez', '1711112222', 'juan.perez@email.com', '0999888777'),
('María Gómez', '1722223333', 'maria.gomez@email.com', '0987654321');

-- Inserción de Veterinarios
INSERT INTO Veterinario (nombre, identificacion, especialidad, telefono) VALUES
('Dr. Fabián de la Cruz', '1700009999', 'Cirugía y Traumatología Canina', '0990001111'),
('Dra. Ana Miranda', '1799990000', 'Medicina General Felina', '0991112222');

-- Inserción de Mascotas iniciales en sedes
INSERT INTO Mascota (idCentro, nombre, especie, edad, estado) VALUES
(1, 'Toby', 'PERRO', 2, 'DISPONIBLE'),
(1, 'Luna', 'GATO', 1, 'DISPONIBLE'),
(2, 'Rocky', 'PERRO', 4, 'DISPONIBLE'),
(3, 'Bella', 'PERRO', 6, 'DISPONIBLE'),
(4, 'Max', 'PERRO', 3, 'DISPONIBLE'),
(4, 'Coco', 'GATO', 2, 'DISPONIBLE');

-- Ajustar la capacidadActual de los centros para reflejar la cantidad de mascotas ingresadas inicialmente
UPDATE CentroAdopcion SET capacidadActual = 2 WHERE idCentro = 1;
UPDATE CentroAdopcion SET capacidadActual = 1 WHERE idCentro = 2;
UPDATE CentroAdopcion SET capacidadActual = 1 WHERE idCentro = 3;
