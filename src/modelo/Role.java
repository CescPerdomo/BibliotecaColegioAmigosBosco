

// src/modelo/Role.java

//  Define los roles disponibles para los usuarios del sistema
//  Utilizado para aplicar restricciones de acceso y logica diferenciada

package modelo;

// ADMIN : Acceso completo al sistema. Puede ver moras, gestionar usuarios, materiales
// PROFESOR : Puede prestar hasta 10 ejemplares y acceder a la mayoría de funciones básicas
// ALUMNO : Puede prestar hasta 5 ejemplares, acceso limitado a funciones administrativas

public enum Role {
    ADMIN, PROFESOR, ALUMNO
    
}

