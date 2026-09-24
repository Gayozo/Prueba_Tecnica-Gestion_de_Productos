# Prueba Tecnica Gestion de Productos - Samuel Gayozo

## Instrucciones breves para configurar y ejecutar la aplicación

### 1. Requisitos previos
* **Java Development Kit (JDK):** Versión 17 o superior.
* **PostgreSQL:** Servidor local activo en el puerto `5432`.
* **Herramienta SQL:** DBeaver, pgAdmin o terminal `psql`.

### 2. Configuración de la Base de Datos
1. En PostgreSQL, cree la base de datos:
   ```sql
   CREATE DATABASE gestion_productos;

### 3. Al crear la DB
1. Ejecute el siguiente Script:
    ```bash
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
    ```
### 3. Configuración de credenciales
Validar el archivo src/main/java/gestionproductos/persistencia_y_JDBC/ConexionDB.java y verifique o ajuste su contraseña local de PostgreSQL:
```bash
    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_productos";
    private static final String USER = "postgres";
    private static final String PASSWORD = "su_password_local"; // Ajustar según su entorno
```
### 4. Ejecutar el proyecto
1. Abrir el proyecto en VS Code
2. Navegue al archivo src/main/java/gestionproductos/Main.java.
3. Presione F5 o haga clic en el botón Run Java.

### Opción B
Desde la raíz del proyecto (donde está pom.xml), ejecute:
```bash
    mvn compile exec:java -Dexec.mainClass="gestionproductos.Main"
```