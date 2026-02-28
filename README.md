# Comedor Ciens UCV - Sistema de Gestión de Comedor Universitario (SGCU)

    Funcionalidades Principales
* **Gestión de Usuarios:** Registro y login de estudiantes y personal.
* **Monedero Virtual:** Sistema de recarga y cobro de bandejas mediante saldo.
* **Control de Menú e Ingredientes:** Administración de los platos del día y lo que hay en almacén.
* **Persistencia en JSON:** Los datos se guardan en archivos JSON dentro de la carpeta `Database`.

##  Tecnologías utilizadas
* **Lenguaje:** Java 17
* **Interfaz Gráfica:** Java Swing
* **Gestor de Proyectos:** Maven
* **Librerías:** Jackson (manejo de JSON)

- **Nombre:**  Comedor UCV (SGCU)
- **Tipo:** Aplicación de escritorio Java (Swing)
- **Propósito:** Gestionar usuarios, menús, ingredientes y cobros mediante un monedero virtual y reglas de tarifa.

**Stack técnico**

- **Lenguaje:** Java 17 (configurado en [pom.xml](pom.xml)).
- **Construcción:** Apache Maven ([pom.xml](pom.xml)).
- **UI:** Java Swing (vistas en `View/`).
- **Serialización JSON:** Jackson (`jackson-databind` en [pom.xml](pom.xml)).
- **Tests:** JUnit (dependencia en [pom.xml](pom.xml)).

**Arquitectura y estructura del repositorio**

- **Entrada:** [src/Main.java](src/Main.java) — levanta la interfaz de login y el `LoginController`.
- **Controladores:** `src/Controllers/` — lógica que conecta vistas y servicios.
- **Model / Services:** `src/Model/` — modelos y servicios por dominio (Food, Menu, User, Wallet, Ingredient).
- **DTOs:** `src/DTO/` — objetos de transferencia.
- **Database (persistencia simple):** `src/Database/` — ficheros JSON que actúan como almacenamiento (ej.: `Database/Food/foods.json`).
- **Utils / View / Testing** — utilidades, componentes de interfaz y pruebas de ejemplo.

Cómo funciona 

1. Al iniciar (`mvn exec:java` o ejecutar `Main`), se abre la pantalla de login (`LoginView`).
2. `LoginController` valida credenciales contra los ficheros en `Database/User/` y crea una `UserSession`.
3. Según el rol, se carga el dashboard correspondiente y se habilitan acciones (gestionar menú, recargar monedero, registrar costos).
4. Los datos persistentes se escriben y leen desde ficheros JSON en `src/Database/` mediante servicios en `Model/*Service`.

**Comandos para compilar y ejecutar**

```bash
mvn clean install
mvn exec:java
# o, para lanzar explícitamente la clase principal
mvn exec:java -Dexec.mainClass="Main"
```
.

**Puntos clave detectados en el análisis**

- Proyecto modularizado en capas (View / Controllers / Model / DTO / Database).  
- Persistencia ligera basada en JSON — útil para prototipos, no para producción concurrente.  
- Interfaz Swing clásica — fácil de ejecutar en escritorio, pero limitada para acceso remoto.  
- Dependencias mínimas: Jackson + JUnit; compatibilidad con JDK 17.

**Roadmap para replicar y desplegar el proyecto**

1. Requisitos locales
  - Java 17 (JDK 17) instalado.
  - Maven 3.8+.

 Clonar y preparar
  - `git clone <repo>`
  - `cd <repo>`
  - Revisar `pom.xml` y `src` para confirmar rutas.

 Construir y ejecutar
  - `mvn clean install`
  - `mvn exec:java` (o `mvn exec:java -Dexec.mainClass="Main"` si es necesario)

 Verificación básica
  - Iniciar sesión con datos de `Database/User/users.json` (crear si no existen).
  - Probar flujo: login → recarga monedero → consumir bandeja → ver actualización de JSON.


-  Tener JDK 17 + Maven instalados.
- Verificar que `Database/` contiene ficheros JSON (usuarios, menus, wallets).
-  Ejecutar `mvn exec:java` y comprobar que la GUI arranca.

Caso de prueba para administrador:

usuario: admin
contraseña: admin123

Caso de prueba para estudiante:

usuario: estudiante
contraseña: estudiante123