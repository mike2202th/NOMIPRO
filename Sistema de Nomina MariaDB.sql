CREATE DATABASE SISTEMA_DE_NOMINA

USE SISTEMA_DE_NOMINA;

/*Configuracion Empresarial*/ 

CREATE TABLE EMPRESA (
    id_empresa          INT AUTO_INCREMENT PRIMARY KEY,
    rnc                 VARCHAR(15) NOT NULL UNIQUE,
    nombre_comercial    VARCHAR(150),
    razon_social        VARCHAR(250),
    direccion           VARCHAR(255),
    telefono            VARCHAR(20),
    email               VARCHAR(100),
    descripcion         TEXT NULL,
    representante_legal VARCHAR(150),
    logo                VARCHAR(250),
    fecha_registro      DATE NOT NULL,
    estado              ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    creado_en           DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE CUENTA_BANCARIA (
    id_cuenta      INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa     INT NOT NULL,
    banco          VARCHAR(60) NOT NULL,
    numero_cuenta  VARCHAR(30) NOT NULL,
    tipo_cuenta    ENUM('AHORRO', 'CORRIENTE') NOT NULL,
    moneda         VARCHAR(10) DEFAULT 'DOP',
    titular        VARCHAR(150) NOT NULL,
    estado         ENUM('ACTIVA', 'INACTIVA') DEFAULT 'ACTIVA',

    CONSTRAINT fk_cuenta_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES EMPRESA(id_empresa)
) ENGINE=InnoDB;


CREATE TABLE METODO_PAGO (
    id_metodo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(50) NOT NULL UNIQUE,
    descripcion    VARCHAR(150),
    estado         ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


INSERT INTO METODO_PAGO (nombre, descripcion) VALUES
('Transferencia bancaria', 'Pago realizado mediante transferencia bancaria'),
('Depósito bancario', 'Pago realizado mediante depósito bancario'),
('Cheque', 'Pago realizado mediante cheque'),
('Efectivo', 'Pago realizado en efectivo');


CREATE TABLE DEPARTAMENTO (
    id_departamento     INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa          INT NOT NULL,
    nombre_departamento VARCHAR(100) NOT NULL,
    descripcion         TEXT NULL,
    estado              ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT fk_depto_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES EMPRESA(id_empresa)
) ENGINE=InnoDB;


CREATE TABLE PUESTOS (
    id_puesto        INT AUTO_INCREMENT PRIMARY KEY,
    id_departamento  INT NOT NULL,
    nombre_puesto    VARCHAR(100) NOT NULL,
    descripcion      VARCHAR(1000),
    salario_minimo   DECIMAL(12,2),
    salario_maximo   DECIMAL(12,2),
    estado           ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT fk_puesto_depto
        FOREIGN KEY (id_departamento)
        REFERENCES DEPARTAMENTO(id_departamento),

    CONSTRAINT chk_puesto_salarios
        CHECK (
            salario_maximo IS NULL
            OR salario_minimo IS NULL
            OR salario_maximo >= salario_minimo
        )
) ENGINE=InnoDB;


CREATE TABLE TIPOS_CONTRATO (
    id_tipo_contrato INT AUTO_INCREMENT PRIMARY KEY,
    nombre           ENUM('INDEFINIDO','TEMPORAL','PASANTIA') NOT NULL UNIQUE,
    descripcion      VARCHAR(255),
    duracion_meses   INT NULL,
    estado           ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


-- Seguridad

CREATE TABLE ROLES (
    id_rol       INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol   VARCHAR(60) NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    estado       ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE PERMISO (
    id_permiso     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_permiso VARCHAR(80) NOT NULL UNIQUE,
    modulo         VARCHAR(60) NOT NULL,
    descripcion    VARCHAR(255)
) ENGINE=InnoDB;


CREATE TABLE ROL_PERMISO (
    id_rol     INT NOT NULL,
    id_permiso INT NOT NULL,

    PRIMARY KEY (id_rol, id_permiso),

    CONSTRAINT fk_rolperm_rol
        FOREIGN KEY (id_rol)
        REFERENCES ROLES(id_rol)
        ON DELETE CASCADE,

    CONSTRAINT fk_rolperm_permiso
        FOREIGN KEY (id_permiso)
        REFERENCES PERMISO(id_permiso)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- Empleado

CREATE TABLE EMPLEADO (
    id_empleado       INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa        INT NOT NULL,
    id_departamento   INT NOT NULL,
    id_puesto         INT NOT NULL,
    id_tipo_contrato  INT NOT NULL,

    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    cedula            VARCHAR(15) NOT NULL UNIQUE,
    fecha_nacimiento  DATE,
    genero            ENUM('M','F','OTRO'),
    estado_civil      ENUM('SOLTERO','CASADO'),

    direccion         VARCHAR(255),
    telefono          VARCHAR(20),
    email             VARCHAR(100),
    foto              VARCHAR(500) NULL,

    fecha_ingreso     DATE NOT NULL,
    fecha_salida      DATE NULL,

    salario_base      DECIMAL(12,2) NOT NULL,

    tipo_pago         ENUM(
        'MENSUAL',
        'QUINCENAL',
        'SEMANAL'
    ) NOT NULL DEFAULT 'MENSUAL',

    banco             VARCHAR(80),
    cuenta_bancaria   VARCHAR(30),

    estado            ENUM(
        'ACTIVO',
        'INACTIVO',
        'SUSPENDIDO',
        'DESVINCULADO'
    ) NOT NULL DEFAULT 'ACTIVO',

    creado_en         DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_emp_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES EMPRESA(id_empresa),

    CONSTRAINT fk_emp_depto
        FOREIGN KEY (id_departamento)
        REFERENCES DEPARTAMENTO(id_departamento),

    CONSTRAINT fk_emp_puesto
        FOREIGN KEY (id_puesto)
        REFERENCES PUESTOS(id_puesto),

    CONSTRAINT fk_emp_contrato
        FOREIGN KEY (id_tipo_contrato)
        REFERENCES TIPOS_CONTRATO(id_tipo_contrato)
) ENGINE=InnoDB;


CREATE TABLE DOCUMENTO_EMPLEADO (
    id_documento      INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado       INT NOT NULL,
    tipo_documento   VARCHAR(60) NOT NULL,
    nombre_archivo   VARCHAR(150) NOT NULL,
    ruta_archivo     VARCHAR(255) NOT NULL,
    fecha_subida     DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NULL,

    CONSTRAINT fk_doc_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado)
        ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE USUARIO (
    id_usuario        INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado       INT NULL,
    id_rol            INT NOT NULL,
    usuario           VARCHAR(50) NOT NULL UNIQUE,
    contrasena        VARCHAR(255) NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    ultimo_acceso     DATETIME DEFAULT NULL,
    intentos_fallidos INT NOT NULL DEFAULT 0,

    estado ENUM(
        'ACTIVO',
        'INACTIVO',
        'BLOQUEADO'
    ) NOT NULL DEFAULT 'ACTIVO',

    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES ROLES(id_rol)
) ENGINE=InnoDB;


-- Seguridad Social

CREATE TABLE AFP (
    id_afp      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_afp  VARCHAR(100) NOT NULL UNIQUE,
    rnc_afp     VARCHAR(15) NOT NULL UNIQUE,
    estado      ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE ARS (
    id_ars      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_ars  VARCHAR(100) NOT NULL UNIQUE,
    rnc_ars     VARCHAR(15) NOT NULL UNIQUE,
    estado      ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE TSS (
    id_tss                    INT AUTO_INCREMENT PRIMARY KEY,
    porcentaje_afp_empleado   DECIMAL(5,2) NOT NULL,
    porcentaje_afp_empleador  DECIMAL(5,2) NOT NULL,
    porcentaje_ars_empleado   DECIMAL(5,2) NOT NULL,
    porcentaje_ars_empleador  DECIMAL(5,2) NOT NULL,
    porcentaje_infotep        DECIMAL(5,2) NOT NULL,

    tope_salud                DECIMAL(12,2) NOT NULL,
    tope_pension              DECIMAL(12,2) NOT NULL,
    tope_riesgo_laboral       DECIMAL(12,2) NOT NULL,
    salario_minimo            DECIMAL(12,2) NOT NULL,

    fecha_desde               DATE NOT NULL,
    fecha_hasta               DATE DEFAULT NULL,

    estado                    ENUM(
        'ACTIVO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE ISR (
    id_isr           INT AUTO_INCREMENT PRIMARY KEY,
    limite_inferior  DECIMAL(12,2) NOT NULL,
    limite_superior  DECIMAL(12,2) DEFAULT NULL,
    porcentaje       DECIMAL(5,2) NOT NULL,
    monto_fijo       DECIMAL(12,2) NOT NULL DEFAULT 0,
    exceso_sobre     DECIMAL(12,2) NOT NULL DEFAULT 0,
    fecha_desde      DATE NOT NULL,
    fecha_hasta      DATE DEFAULT NULL
) ENGINE=InnoDB;


CREATE TABLE EMPLEADO_AFP (
    id_empleado_afp INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado     INT NOT NULL,
    id_afp          INT NOT NULL,
    numero_afiliado VARCHAR(30) NOT NULL,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE DEFAULT NULL,

    estado ENUM(
        'ACTIVO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT fk_emp_afp_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_emp_afp_afp
        FOREIGN KEY (id_afp)
        REFERENCES AFP(id_afp)
) ENGINE=InnoDB;


CREATE TABLE EMPLEADO_ARS (
    id_empleado_ars INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado     INT NOT NULL,
    id_ars          INT NOT NULL,
    numero_afiliado VARCHAR(30) NOT NULL,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE DEFAULT NULL,

    estado ENUM(
        'ACTIVO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT fk_emp_ars_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_emp_ars_ars
        FOREIGN KEY (id_ars)
        REFERENCES ARS(id_ars)
) ENGINE=InnoDB;

-- Asistencia

CREATE TABLE ASISTENCIA (
    id_asistencia     INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado       INT NOT NULL,
    fecha             DATE NOT NULL,
    hora_entrada      TIME,
    hora_salida       TIME,
    horas_trabajadas  DECIMAL(5,2),

    tipo ENUM(
        'NORMAL',
        'TARDANZA',
        'FALTA',
        'PERMISO'
    ) NOT NULL DEFAULT 'NORMAL',

    observaciones VARCHAR(255),

    CONSTRAINT fk_asist_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT uq_asist_emp_fecha
        UNIQUE (id_empleado, fecha)
) ENGINE=InnoDB;


CREATE TABLE HORAS_EXTRAS (
    id_hora_extra     INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado       INT NOT NULL,
    id_asistencia     INT NULL,
    fecha             DATE NOT NULL,
    cantidad_horas    DECIMAL(5,2) NOT NULL,

    tipo_hora_extra ENUM(
        'DIURNA',
        'NOCTURNA',
        'FERIADO'
    ) NOT NULL,

    tarifa_aplicada  DECIMAL(6,2) NOT NULL,
    monto_calculado  DECIMAL(12,2) NOT NULL,

    estado_aprobacion ENUM(
        'PENDIENTE',
        'APROBADA',
        'RECHAZADA'
    ) NOT NULL DEFAULT 'PENDIENTE',

    aprobado_por INT NULL,

    CONSTRAINT fk_hextra_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_hextra_asistencia
        FOREIGN KEY (id_asistencia)
        REFERENCES ASISTENCIA(id_asistencia),

    CONSTRAINT fk_hextra_aprobado
        FOREIGN KEY (aprobado_por)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


-- Aucensias y Vacaciones

CREATE TABLE TIPOS_LICENCIAS (
    id_tipo_licencia INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(80) NOT NULL UNIQUE,
    descripcion      VARCHAR(255),
    licencia_pago    BOOLEAN NOT NULL DEFAULT TRUE,
    dias_maximos     INT
) ENGINE=InnoDB;


CREATE TABLE LICENCIAS (
    id_licencia      INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado      INT NOT NULL,
    id_tipo_licencia INT NOT NULL,
    fecha_inicio     DATE NOT NULL,
    fecha_fin        DATE NOT NULL,
    dias_solicitados INT NOT NULL,
    motivo           VARCHAR(255),

    estado_aprobacion ENUM(
        'PENDIENTE',
        'APROBADA',
        'RECHAZADA'
    ) NOT NULL DEFAULT 'PENDIENTE',

    aprobado_por INT NULL,

    CONSTRAINT fk_licencia_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_licencia_tipo
        FOREIGN KEY (id_tipo_licencia)
        REFERENCES TIPOS_LICENCIAS(id_tipo_licencia),

    CONSTRAINT fk_licencia_aprobador
        FOREIGN KEY (aprobado_por)
        REFERENCES USUARIO(id_usuario),

    CONSTRAINT chk_licencia_fechas
        CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;


CREATE TABLE VACACIONES (
    id_vacacion      INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado      INT NOT NULL,
    periodo_anual    YEAR NOT NULL,
    dias_disponibles INT NOT NULL DEFAULT 14,
    dias_tomados     INT NOT NULL DEFAULT 0,
    fecha_inicio     DATE,
    fecha_fin        DATE,

    estado_aprobacion ENUM(
        'PENDIENTE',
        'APROBADA',
        'RECHAZADA',
        'DISFRUTADA'
    ) NOT NULL DEFAULT 'PENDIENTE',

    aprobado_por INT NULL,

    CONSTRAINT fk_vac_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_vac_aprobado
        FOREIGN KEY (aprobado_por)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


-- Nomina

CREATE TABLE PERIODOS_NOMINA (
    id_periodo     INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa     INT NOT NULL,
    nombre_periodo VARCHAR(60) NOT NULL,
    fecha_inicio   DATE NOT NULL,
    fecha_fin      DATE NOT NULL,
    fecha_pago     DATE NOT NULL,

    tipo_periodo ENUM(
        'MENSUAL',
        'QUINCENAL',
        'SEMANAL'
    ) NOT NULL,

    estado ENUM(
        'ABIERTO',
        'PROCESADO',
        'CERRADO'
    ) NOT NULL DEFAULT 'ABIERTO',

    CONSTRAINT fk_periodo_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES EMPRESA(id_empresa),

    CONSTRAINT chk_periodo_fechas
        CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;


CREATE TABLE NOMINAS (
    id_nomina         INT AUTO_INCREMENT PRIMARY KEY,
    id_periodo        INT NOT NULL,
    fecha_generacion  DATETIME DEFAULT CURRENT_TIMESTAMP,
    generado_por      INT NOT NULL,

    total_devengado   DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_deducciones DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_neto        DECIMAL(14,2) NOT NULL DEFAULT 0,

    estado ENUM(
        'BORRADOR',
        'APROBADA',
        'PAGADA',
        'ANULADA'
    ) NOT NULL DEFAULT 'BORRADOR',

    CONSTRAINT fk_nomina_periodo
        FOREIGN KEY (id_periodo)
        REFERENCES PERIODOS_NOMINA(id_periodo),

    CONSTRAINT fk_nomina_usuario
        FOREIGN KEY (generado_por)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


CREATE TABLE DETALLE_NOMINA (
    id_detalle          INT AUTO_INCREMENT PRIMARY KEY,
    id_nomina           INT NOT NULL,
    id_empleado         INT NOT NULL,

    salario_base        DECIMAL(12,2) NOT NULL,
    dias_trabajados     DECIMAL(5,2) NOT NULL,
    total_horas_extras  DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_bonificaciones DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_deducciones   DECIMAL(12,2) NOT NULL DEFAULT 0,

    salario_bruto       DECIMAL(12,2) NOT NULL,
    salario_neto        DECIMAL(12,2) NOT NULL,

    estado ENUM(
        'PENDIENTE',
        'PAGADO',
        'ANULADO'
    ) NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT fk_det_nomina
        FOREIGN KEY (id_nomina)
        REFERENCES NOMINAS(id_nomina)
        ON DELETE CASCADE,

    CONSTRAINT fk_det_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT uq_det_nomina_emp
        UNIQUE (id_nomina, id_empleado)
) ENGINE=InnoDB;


-- Pagos

CREATE TABLE PAGOS (
    id_pago        INT AUTO_INCREMENT PRIMARY KEY,
    id_nomina      INT NOT NULL,
    id_cuenta      INT NULL,
    id_metodo_pago INT NOT NULL,

    fecha_pago     DATE NOT NULL,
    referencia     VARCHAR(100),
    monto          DECIMAL(12,2) NOT NULL,
    observaciones  VARCHAR(255),

    estado ENUM(
        'PAGADO',
        'ANULADO'
    ) NOT NULL DEFAULT 'PAGADO',

    CONSTRAINT fk_pago_nomina
        FOREIGN KEY (id_nomina)
        REFERENCES NOMINAS(id_nomina),

    CONSTRAINT fk_pago_cuenta
        FOREIGN KEY (id_cuenta)
        REFERENCES CUENTA_BANCARIA(id_cuenta),

    CONSTRAINT fk_pago_metodo
        FOREIGN KEY (id_metodo_pago)
        REFERENCES METODO_PAGO(id_metodo_pago)
) ENGINE=InnoDB;


-- ============================================================================
-- MODULO 9: DEDUCCIONES
-- ============================================================================

CREATE TABLE TIPOS_DEDUCCION (
    id_tipo_deduccion INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(80) NOT NULL UNIQUE,
    descripcion       VARCHAR(255),

    es_legal          BOOLEAN NOT NULL DEFAULT FALSE,
    porcentaje        DECIMAL(5,2) NULL,
    monto_fijo        DECIMAL(12,2) NULL,

    estado ENUM(
        'ACTIVO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE DEDUCCIONES_EMPLEADO (
    id_deduccion      INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado       INT NOT NULL,
    id_tipo_deduccion INT NOT NULL,
    id_detalle_nomina INT NULL,

    monto             DECIMAL(12,2) NOT NULL,
    fecha_aplicacion  DATE NOT NULL,

    estado ENUM(
        'ACTIVA',
        'APLICADA',
        'ANULADA'
    ) NOT NULL DEFAULT 'ACTIVA',

    CONSTRAINT fk_dedemp_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_dedemp_tipo
        FOREIGN KEY (id_tipo_deduccion)
        REFERENCES TIPOS_DEDUCCION(id_tipo_deduccion),

    CONSTRAINT fk_dedemp_detnom
        FOREIGN KEY (id_detalle_nomina)
        REFERENCES DETALLE_NOMINA(id_detalle)
) ENGINE=InnoDB;


-- Bonificaciones

CREATE TABLE TIPOS_BONIFICACION (
    id_tipo_bonificacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre               VARCHAR(80) NOT NULL UNIQUE,
    descripcion          VARCHAR(255),
    es_fijo              BOOLEAN NOT NULL DEFAULT TRUE,
    monto_fijo           DECIMAL(12,2) NULL,

    estado ENUM(
        'ACTIVO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;


CREATE TABLE BONIFICACIONES_EMPLEADO (
    id_bonificacion       INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado           INT NOT NULL,
    id_tipo_bonificacion  INT NOT NULL,
    id_detalle_nomina     INT NULL,

    monto                 DECIMAL(12,2) NOT NULL,
    fecha_aplicacion      DATE NOT NULL,

    estado ENUM(
        'ACTIVA',
        'APLICADA',
        'ANULADA'
    ) NOT NULL DEFAULT 'ACTIVA',

    CONSTRAINT fk_bonemp_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_bonemp_tipo
        FOREIGN KEY (id_tipo_bonificacion)
        REFERENCES TIPOS_BONIFICACION(id_tipo_bonificacion),

    CONSTRAINT fk_bonemp_detnom
        FOREIGN KEY (id_detalle_nomina)
        REFERENCES DETALLE_NOMINA(id_detalle)
) ENGINE=InnoDB;


-- Historial y Auditoria

CREATE TABLE HISTORIAL_SALARIAL (
    id_historial     INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado      INT NOT NULL,
    salario_anterior DECIMAL(12,2) NOT NULL,
    salario_nuevo    DECIMAL(12,2) NOT NULL,
    fecha_cambio     DATE NOT NULL,

    motivo ENUM(
        'ASCENSO',
        'EVALUACION_MERITO',
        'AJUSTE_INFLACION',
        'AUMENTO_SALARIO_MINIMO',
        'CAMBIO_PUESTO',
        'CORRECCION_SISTEMA'
    ) NOT NULL DEFAULT 'EVALUACION_MERITO',

    aprobado_por INT NULL,

    CONSTRAINT fk_histsal_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO(id_empleado),

    CONSTRAINT fk_histsal_aprobador
        FOREIGN KEY (aprobado_por)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


CREATE TABLE AUDITORIA (
    id_auditoria      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario        INT NULL,
    tabla_afectada    VARCHAR(60) NOT NULL,

    accion ENUM(
        'INSERT',
        'UPDATE',
        'DELETE'
    ) NOT NULL,

    registro_id       VARCHAR(50) NOT NULL,
    fecha_hora        DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address        VARCHAR(45),

    CONSTRAINT fk_audit_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


-- Reportes

CREATE TABLE CONFIGURACION_REPORTE (
    id_config_reporte  INT AUTO_INCREMENT PRIMARY KEY,
    nombre_reporte     VARCHAR(100) NOT NULL,
    tipo_reporte       VARCHAR(60) NOT NULL,
    id_usuario_creador INT NOT NULL,
    fecha_creacion     DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_confrep_usuario
        FOREIGN KEY (id_usuario_creador)
        REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;


/*INSERT DE PRUEBAS*/

INSERT INTO EMPRESA
(rnc, nombre_comercial, razon_social, direccion, telefono, email,
 descripcion, representante_legal, logo, fecha_registro, estado)
VALUES
('101234567', 'Tecnología Caribe', 'Tecnología Caribe SRL',
 'Av. 27 de Febrero #125, Santo Domingo',
 '8095551001', 'info@tecnologiacaribe.com',
 'Empresa dedicada al desarrollo de soluciones tecnológicas.',
 'Carlos Rodríguez', 'logo_tecnologia.png', '2026-01-10', 'ACTIVO');
 
 UPDATE `EMPRESA` 
SET 
    `rnc` = '131234567',
    `nombre_comercial` = 'Acroma',
    `razon_social` = 'Acroma Soluciones Industriales S.R.L.',
    `direccion` = 'Av. Industrial #45, Zona Industrial, Santo Domingo',
    `telefono` = '809-555-0199',
    `email` = 'contacto@acroma.com',
    `descripcion` = 'Soluciones de producción industrial y eficiencia',
    `representante_legal` = 'Carlos Rodríguez',
    `logo` = 'logo_acroma.png',
    `estado` = 'ACTIVO'
WHERE `id_empresa` = 1;

-- CUENTA BANCARIA

INSERT INTO CUENTA_BANCARIA
(id_empresa, banco, numero_cuenta, tipo_cuenta, moneda, titular, estado)
VALUES
(1, 'Banco Popular Dominicano', '1234567890',
 'CORRIENTE', 'DOP', 'Tecnología Caribe SRL', 'ACTIVA'),

(1, 'Banco de Reservas', '9876543210',
 'AHORRO', 'DOP', 'Tecnología Caribe SRL', 'ACTIVA');
 
 -- METODO PAGO

INSERT INTO METODO_PAGO
(nombre, descripcion)
VALUES
('Transferencia bancaria',
 'Pago realizado mediante transferencia bancaria'),

('Depósito bancario',
 'Pago realizado mediante depósito en una cuenta bancaria'),

('Cheque',
 'Pago realizado mediante cheque'),

('Efectivo',
 'Pago realizado en efectivo');
 
 -- DEPARTAMENTOS

INSERT INTO DEPARTAMENTO
(id_empresa, nombre_departamento, descripcion, estado)
VALUES
(1, 'Recursos Humanos',
 'Departamento encargado de la gestión del personal.', 'ACTIVO'),

(1, 'Tecnología',
 'Departamento encargado del desarrollo y mantenimiento tecnológico.', 'ACTIVO'),

(1, 'Contabilidad',
 'Departamento encargado de las operaciones contables.', 'ACTIVO'),

(1, 'Administración',
 'Departamento encargado de la gestión administrativa.', 'ACTIVO');
 
 -- PUESTOS

INSERT INTO PUESTOS
(id_departamento, nombre_puesto, descripcion,
 salario_minimo, salario_maximo, estado)
VALUES
(1, 'Analista de Recursos Humanos',
 'Responsable de procesos de gestión humana.',
 35000.00, 60000.00, 'ACTIVO'),

(2, 'Desarrollador de Software',
 'Responsable del desarrollo y mantenimiento de sistemas.',
 45000.00, 90000.00, 'ACTIVO'),

(2, 'Administrador de Sistemas',
 'Responsable de servidores y sistemas informáticos.',
 40000.00, 80000.00, 'ACTIVO'),

(3, 'Contador',
 'Responsable de los procesos contables.',
 40000.00, 75000.00, 'ACTIVO'),

(4, 'Asistente Administrativo',
 'Apoyo en las operaciones administrativas.',
 25000.00, 45000.00, 'ACTIVO');
 
 -- ROLES

INSERT INTO ROLES
(nombre_rol, descripcion, estado)
VALUES
('Administrador',
 'Acceso completo al sistema.', 'ACTIVO'),

('Recursos Humanos',
 'Gestión de empleados y procesos de recursos humanos.', 'ACTIVO'),

('Contabilidad',
 'Gestión de nóminas, pagos y deducciones.', 'ACTIVO'),

('Supervisor',
 'Consulta y supervisión de información.', 'ACTIVO');
 
 -- EMPLEADOS
 
 INSERT INTO EMPLEADO
(id_empresa, id_departamento, id_puesto, id_tipo_contrato,
 nombres, apellidos, cedula, fecha_nacimiento, genero,
 estado_civil, direccion, telefono, email, foto,
 fecha_ingreso, fecha_salida, salario_base, tipo_pago,
 banco, cuenta_bancaria, estado)
VALUES

(1, 1, 1, 1,
 'Ana', 'Martínez',
 '00112345678',
 '1992-04-15', 'F',
 'CASADO',
 'Santo Domingo Este',
 '8095552001',
 'ana.martinez@tecnologiacaribe.com',
 NULL,
 '2023-01-10', NULL,
 55000.00, 'MENSUAL',
 'Banco Popular Dominicano',
 '1111111111',
 'ACTIVO'),

(1, 2, 2, 1,
 'Michael', 'Cabrera',
 '00123456789',
 '1998-07-20', 'M',
 'SOLTERO',
 'Santo Domingo Este',
 '8095552002',
 'michael.cabrera@tecnologiacaribe.com',
 NULL,
 '2024-02-01', NULL,
 65000.00, 'MENSUAL',
 'Banco de Reservas',
 '2222222222',
 'ACTIVO'),

(1, 2, 3, 1,
 'Luis', 'Ramírez',
 '00134567890',
 '1990-11-05', 'M',
 'CASADO',
 'Santo Domingo Norte',
 '8095552003',
 'luis.ramirez@tecnologiacaribe.com',
 NULL,
 '2022-08-15', NULL,
 60000.00, 'MENSUAL',
 'Banco Popular Dominicano',
 '3333333333',
 'ACTIVO'),

(1, 3, 4, 1,
 'Laura', 'Gómez',
 '00145678901',
 '1995-03-12', 'F',
 'SOLTERO',
 'Santo Domingo Oeste',
 '8095552004',
 'laura.gomez@tecnologiacaribe.com',
 NULL,
 '2024-05-20', NULL,
 52000.00, 'MENSUAL',
 'Banco de Reservas',
 '4444444444',
 'ACTIVO'),

(1, 4, 5, 2,
 'Pedro', 'Hernández',
 '00156789012',
 '2000-09-25', 'M',
 'SOLTERO',
 'Santo Domingo Este',
 '8095552005',
 'pedro.hernandez@tecnologiacaribe.com',
 NULL,
 '2025-01-15', NULL,
 32000.00, 'MENSUAL',
 'Banco Popular Dominicano',
 '5555555555',
 'ACTIVO');