# Sistema de Biblioteca Colegio Amigos de Don Bosco

## Resumen del Proyecto
El **Sistema de Biblioteca Colegio Amigos de Don Bosco** es una aplicación de escritorio diseñada para gestionar libros, revistas y materiales audiovisuales. Este sistema permite realizar operaciones de préstamo, devolución, cálculo de mora, renovación de préstamos y gestión de usuarios. Además, aplica lógica condicional basada en roles: ADMIN, PROFESOR y ALUMNO, asegurando que cada usuario tenga acceso a las funcionalidades adecuadas según su rol.

## Tecnologías Utilizadas
- **Java 8**
- **Swing**
- **MySQL**
- **JDBC**
- **Log4J**

## Características Principales
- **Inicio de sesión y validación de credenciales**: Implementado en `VistaLoginCustom.java` y `AuthDAO.java`.
- **Interfaz dinámica controlada por roles**: Navegación y acceso a funcionalidades en `VistaPrincipal.java`.
- **CRUD completo de materiales**: Gestión de libros, revistas y materiales audiovisuales en `Material.java`, `MaterialDAO.java` y `PanelMateriales.java`.
- **Préstamos con restricciones por usuario**: Control de préstamos en `PanelPrestamos.java` y `PrestamoDAO.java`.
- **Devoluciones y cálculo automático de mora**: Implementado en `PanelDevoluciones.java`, `PanelMora.java` y `MoraDAO.java`.
- **Simulación de mora con lógica de cálculo**: Permite simular moras para usuarios.
- **Gestión de usuarios por ADMIN**: CRUD de usuarios en `PanelUsuarios.java` y `UsuarioDAO.java`.
- **Enum `Role.java`**: Utilizado para aplicar lógica de permisos y roles.

## Lógica de Negocio Destacada
- **Límite de préstamos**: 5 para ALUMNOS, 10 para PROFESORES.
- **Cálculo de mora**: $0.25 por día + $0.25 por ejemplar.
- **Renovación**: Solo permitida si el préstamo no ha vencido.
- **Visibilidad condicional**: Botones y paneles se muestran u ocultan según el rol del usuario.

## Estructura de Archivos Clave
- `Main.java`: Punto de entrada de la aplicación.
- `VistaLoginCustom.java`: Pantalla de inicio de sesión con listener para mostrar `VistaPrincipal`.
- `VistaPrincipal.java`: Gestiona la navegación entre paneles utilizando `CardLayout`.
- `PanelMateriales.java`: Formulario para alta y consulta de materiales.
- `Material.java`, `MaterialDAO.java`: Modelo unificado para libros, revistas y materiales audiovisuales.
- `Prestamo.java`, `PrestamoDAO.java`: Entidad y lógica para gestionar préstamos.
- `PanelPrestamos.java`, `PanelDevoluciones.java`: Acciones relacionadas con préstamos y devoluciones.
- `Mora.java`, `MoraDAO.java`: Cálculo y registro de moras.
- `PanelMora.java`: Visualización y simulación de moras.
- `Usuario.java`, `UsuarioDAO.java`, `PanelUsuarios.java`: Gestión CRUD de usuarios.
- `Role.java`: Enum que define los roles y permisos del sistema.

## Cómo Ejecutarlo
1. Abrir el proyecto en **NetBeans 8.2**.
2. Configurar la base de datos **MySQL** con las tablas `usuarios`, `material`, `prestamo`, `mora`, etc.
3. Ejecutar `Main.java`.

## Autor
- **Cesar Ernesto Perdomo Guerrero PG241690**


