# Sistema de Gestión de Adopciones PAE - TSP Grupo 4

Este proyecto ha sido estructurado y configurado siguiendo rigurosamente las especificaciones del **Informe de Lanzamiento** y el **Informe de Estrategia** para la cátedra de Ingeniería de Software.

---

## 🚀 Integrantes y Roles (Grupo 4)
* **Paúl Rosero**: Líder del Equipo
* **Gabriel Aguinaga**: Administrador de Planificación y Calidad
* **Joselyn Cadena**: Administradora de Desarrollo

---

## 🛠️ Arquitectura y Estructura
El sistema se implementa bajo una **Arquitectura de Tres Capas (Capa Gráfica, Capa de Dominio del Problema y Capa de Manejo de Datos)** utilizando **Java** y base de datos relacional **PostgreSQL** mediante conectividad **JDBC**:

```text
sistema-gestion-mascotas/
├── .vscode/                 # Configuraciones de classpath y depuración para VS Code
├── db/
│   └── schema.sql           # DDL y DML semilla de la base de datos (STD-03)
├── docs/
│   └── tsp/                 # Copia digital de Informes de Lanzamiento y Estrategia
├── lib/
│   └── postgresql-42.7.3.jar# Controlador JDBC oficial de PostgreSQL
└── src/
    └── gestionmascotas/
        ├── Main.java        # Punto de entrada de la interfaz gráfica
        ├── gui/             # Capa de Presentación (Paneles Modernos)
        ├── dp/
        │   ├── controllers/ # Controladores de la capa de dominio (Sesión de Usuario)
        │   └── models/      # Modelos de la capa de dominio (Sufijo *DP)
        └── md/              # Capa de Manejo de Datos (Persistencia, JDBC y Auditoría)
```

---

## 📋 Verificación de Estándares Implementados

* **STD-02 (Estilo)**: Todas las clases usan `PascalCase` (ejemplo: `MascotaDP`) y los métodos/variables usan `camelCase` (ejemplo: `obtenerTodos`). Las clases de la capa gráfica utilizan la nomenclatura `VentanaFuncion` (ejemplo: `VentanaMascota`).
* **STD-03 (Esquema de Base de Datos)**: Definido en `db/schema.sql`. Las tablas inician con mayúscula y el resto en minúscula (`Mascota`, `Adoptante`), y los atributos utilizan `camelCase` (`idMascota`, `nombre`, etc.).
* **STD-04 (Integridad Transaccional)**: Implementado en la capa de Manejo de Datos (ejemplo: `MascotaMD.java`) mediante bloques transaccionales controlados con `conn.setAutoCommit(false)` y reversión automática (`rollback`) ante cualquier error.
* **STD-05 (Seguridad en la Interfaz)**: Implementado de forma nativa en la UI usando `setEnabled(false)` en los botones y campos de entrada. Se puede simular el cambio de rol en la barra superior en tiempo real y observar cómo se inhabilitan los componentes según el rol asignado.
* **STD-06 (Auditoría Obligatoria)**: Cada escritura en el sistema llama a `AuditoriaMD.registrarAuditoria` para insertar en la tabla `Auditoria` el ID del personal ejecutor y la marca de tiempo.
* **STD-09 (Manejo de Errores)**: El sistema captura `SQLException` controladas, escribe los detalles en los logs y muestra alertas limpias (`JOptionPane`) sin revelar datos técnicos sensibles. Además, si la base de datos está desconectada, la aplicación entra automáticamente en **Modo Fuera de Línea de Contingencia** para permitir navegar en la interfaz.

---

## ⚙️ Configuración y Ejecución

### 1. Base de Datos (PostgreSQL)
1. Instale PostgreSQL en su máquina local (puerto por defecto `5432`).
2. Cree una base de datos llamada `sistema_mascotas`.
3. Ejecute el script `db/schema.sql` para crear las tablas e insertar los datos semilla (usuarios, adoptantes iniciales).

> [!NOTE]  
> Las credenciales por defecto son usuario `postgres` y contraseña `postgres`. Si utiliza credenciales diferentes, puede cambiarlas directamente en la clase `ConexionBD.java` o definir las siguientes variables de entorno:
> `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`.

### 2. Compilación y Ejecución Manual
Para compilar y correr la aplicación desde la consola (dentro del directorio raíz del proyecto):

**Compilar:**
```powershell
javac -cp "lib/postgresql-42.7.3.jar" -d bin src/gestionmascotas/*.java src/gestionmascotas/dp/models/*.java src/gestionmascotas/dp/controllers/*.java src/gestionmascotas/md/*.java src/gestionmascotas/gui/*.java
```

**Ejecutar:**
```powershell
java -cp "bin;lib/postgresql-42.7.3.jar" gestionmascotas.Main
```

### 3. Ejecución desde VS Code
1. Abra la carpeta raíz del proyecto en VS Code.
2. Asegúrese de tener instalada la extensión oficial para soporte de Java.
3. Presione `F5` para correr la aplicación mediante la configuración del archivo `.vscode/launch.json`.
