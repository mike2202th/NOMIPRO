# Sistema de Nómina y Gestión de Empleados (NOMIPRO)

## Descripción

<img width="1024" height="559" alt="logo sistema" src="https://github.com/user-attachments/assets/e50e8c90-ad80-4e97-b53f-b957398f15d1" />


*NOMIPRO* es una aplicación de escritorio desarrollada para facilitar la administración de empleados, usuarios, departamentos y procesos relacionados con la gestión de nómina de una empresa.

El sistema permite centralizar la información de los empleados, controlar el acceso de los usuarios según sus roles y gestionar diferentes procesos administrativos relacionados con recursos humanos y nómina.

La aplicación fue desarrollada utilizando **Java Swing** como tecnología para la interfaz gráfica y **MariaDB** como sistema gestor de base de datos.

---

## Objetivo del sistema

El objetivo principal es proporcionar una herramienta que permita a una empresa administrar de manera organizada y segura la información de sus empleados y los procesos relacionados con el cálculo y gestión de nóminas.

Entre sus objetivos específicos se encuentran:

* Registrar y administrar empleados.
* Gestionar departamentos y puestos de trabajo.
* Administrar usuarios del sistema.
* Controlar el acceso mediante roles y permisos.
* Gestionar información de AFP y ARS.
* Registrar asistencia, horas extras, licencias y vacaciones.
* Gestionar períodos de nómina.
* Generar y administrar nóminas.
* Registrar las deducciones correspondientes.
* Gestionar diferentes métodos de pago.
* Generar reportes relacionados con la nómina.
* Mantener un registro de auditoría de las operaciones realizadas.

---

##  Tecnologías utilizadas

| Tecnología        | Uso                                 |
| ----------------- | ----------------------------------- |
| **Java**          | Lenguaje principal                  |
| **Java Swing**    | Interfaz gráfica                    |
| **MariaDB**       | Base de datos                       |
| **JDBC**          | Conexión entre Java y MariaDB       |
| **Maven**         | Gestión del proyecto y dependencias |
| **JasperReports** | Generación de reportes              |
| **NetBeans**      | Entorno de desarrollo               |

---

## Arquitectura del proyecto

El sistema utiliza una estructura organizada por responsabilidades, separando la interfaz gráfica, la lógica de negocio y el acceso a datos.

Una estructura aproximada del proyecto es:

```text
SistemaDeNomina/
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── Main/
│       │   │   └── conexionMariaDB.java
│       │   │
│       │   ├── Modelos/
│       │   │   ├── Empleado.java
│       │   │   ├── Usuario.java
│       │   │   ├── Departamento.java
│       │   │   └── ...
│       │   │
│       │   ├── DAO/
│       │   │   ├── EmpleadoDAO.java
│       │   │   ├── GestorUsuariosDAO.java
│       │   │   └── ...
│       │   │
│       │   ├── Controladores/
│       │   │   └── ControladorLogin.java
│       │   │
│       │   └── Vistas/
│       │       ├── Login.java
│       │       ├── Menu.java
│       │       ├── Empleados.java
│       │       ├── Departamentos.java
│       │       └── ...
│       │
│       └── resources/
│           ├── reportes/
│           ├── imagenes/
│           └── logos/
│
├── pom.xml
└── README.md
```

---

# Sistema de autenticación

El sistema cuenta con un módulo de autenticación para controlar el acceso a la aplicación.

El usuario debe proporcionar sus credenciales para poder acceder al menú principal.

Las contraseñas no se almacenan directamente como texto plano. Se utiliza **BCrypt** para generar un hash seguro de las contraseñas.

El proceso de autenticación funciona de la siguiente manera:

```text
Usuario
   │
   ▼
Ingresa usuario y contraseña
   │
   ▼
Sistema busca usuario en BD
   │
   ▼
Verificación del estado del usuario
   │
   ▼
Validación del rol y permisos
   │
   ▼
Acceso al sistema
```

---

#  Roles y permisos

El sistema implementa un mecanismo de **roles y permisos** para controlar las funciones disponibles para cada usuario.

Entre los roles utilizados se encuentra:

* **Administrador**
* **RRHH**

Los permisos determinan qué módulos puede utilizar cada usuario.

Por ejemplo, las funciones relacionadas con la gestión de empleados pueden estar restringidas a usuarios que posean los permisos correspondientes.

Esto evita que cualquier usuario pueda modificar información sensible del sistema.

---

#  Gestión de empleados

El módulo de empleados permite administrar la información relacionada con el personal de la empresa.

Entre los datos manejados se encuentran:

* Información personal.
* Información laboral.
* Departamento.
* Puesto.
* Tipo de contrato.
* Salario.
* AFP.
* ARS.
* Fotografía.
* Estado del empleado.

El sistema también permite realizar búsquedas y consultar información registrada.

---

#  Gestión de departamentos

El sistema permite administrar los departamentos de la empresa.

Para cada departamento se puede registrar información como:

* ID del departamento.
* Nombre.
* Funciones.
* Estado.

También se implementó un buscador para facilitar la localización de departamentos dentro de la tabla.

---

#  Gestión de puestos y contratos

El sistema contempla la administración de los puestos disponibles en la empresa y los tipos de contratos.

Los tipos de contrato contemplados incluyen:

* **Indefinido**
* **Temporal**
* **Pasantía**

Esto permite relacionar correctamente a cada empleado con su situación laboral.

---

#  Gestión de nómina

El sistema incorpora un módulo dedicado a la administración de nóminas.

La estructura permite trabajar con:

* Períodos de nómina.
* Nóminas.
* Detalle de nómina.
* Salario base.
* Bonificaciones.
* Deducciones.
* AFP.
* ARS.
* ISR.
* Otras deducciones.
* Salario neto.

El detalle de la nómina permite consultar la información correspondiente a cada empleado.

---

#  Deducciones

El sistema contempla diferentes tipos de deducciones que pueden afectar el salario del empleado.

Entre ellas se encuentran:

* AFP.
* ARS.
* ISR.
* Otras deducciones.

La información correspondiente a las deducciones se almacena en la base de datos para mantener un registro organizado.

---

#  Métodos de pago

El sistema permite registrar diferentes métodos mediante los cuales puede realizarse el pago de la nómina.

Entre los métodos contemplados están:

* Transferencia bancaria.
* Depósito bancario.
* Cheque.
* Efectivo.

También se contempla la gestión de cuentas bancarias relacionadas con los empleados.

---
#  Base de datos

La aplicación utiliza **MariaDB** como sistema gestor de base de datos.

Entre las principales tablas utilizadas se encuentran:

```text
EMPRESA
CUENTA_BANCARIA
METODO_PAGO
PAGOS
DEPARTAMENTO
PUESTOS
TIPOS_CONTRATO
ROLES
PERMISO
ROL_PERMISO
USUARIO
EMPLEADO
AFP
ARS
EMPLEADO_AFP
EMPLEADO_ARS
TIPO_DEDUCCION
EMPLEADO_DEDUCCION
ASISTENCIA
HORAS_EXTRAS
TIPOS_LICENCIAS
LICENCIAS
VACACIONES
PERIODOS_NOMINA
NOMINAS
DETALLE_NOMINA
AUDITORIA
```

La aplicación mostrará inicialmente la pantalla de inicio de sesión.

---

# Flujo general del sistema

                 ┌─────────────────┐
                 │     LOGIN       │
                 └────────┬────────┘
                          │
                          ▼
      
                 ┌─────────────────┐
                 │ Roles/Permisos  │
                 └────────┬────────┘
                          │
                          ▼
                 ┌─────────────────┐
                 │ Menú Principal  │
                 └────────┬────────┘
                          │
          ┌───────────────┼────────────────┐
          ▼               ▼                ▼
     Empleados       Departamentos      Nómina
          │               │                │
          ▼               ▼                ▼
       Gestión         Gestión          Procesamiento
          │                                │
          └───────────────┬────────────────┘
                          ▼
                    ┌───────────┐
                    │ Reportes  │
                    └───────────┘

