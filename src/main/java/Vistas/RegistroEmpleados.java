
package Vistas;

import Main.Menu;
import Main.conexionMariaDB;
import java.awt.Image;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RegistroEmpleados extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegistroEmpleados.class.getName());
    private Map<String, Integer> departamentosMap = new HashMap<>();
    private Map<String, Integer> puestosMap = new HashMap<>();
    private Map<String, Integer> contratosMap = new HashMap<>();

    private Menu menu;

    private String rutaFoto = null;
    public RegistroEmpleados(Menu menu) {
        initComponents();
        
        this.menu = menu;

        configurarCombos();

        cargarDepartamentos();

        cargarTiposContrato();
        
        cmbEstadoEmpleado.setSelectedItem("ACTIVO");
        
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void configurarCombos() {

    cmbGeneroEmpleado.removeAllItems();

    cmbGeneroEmpleado.addItem("M");
    cmbGeneroEmpleado.addItem("F");
    cmbGeneroEmpleado.addItem("OTRO");


    cmbEstadoCivilEmpleado.removeAllItems();

    cmbEstadoCivilEmpleado.addItem("SOLTERO");
    cmbEstadoCivilEmpleado.addItem("CASADO");


    cmbTipoPagoEmpleado.removeAllItems();

    cmbTipoPagoEmpleado.addItem("MENSUAL");
    cmbTipoPagoEmpleado.addItem("QUINCENAL");
    cmbTipoPagoEmpleado.addItem("SEMANAL");


    cmbEstadoEmpleado.removeAllItems();

    cmbEstadoEmpleado.addItem("ACTIVO");
    cmbEstadoEmpleado.addItem("INACTIVO");
    cmbEstadoEmpleado.addItem("SUSPENDIDO");
    cmbEstadoEmpleado.addItem("DESVINCULADO");
}
    
    private void cargarDepartamentos() {

    cmbDepartamentoEmpleado.removeAllItems();

    departamentosMap.clear();

    String sql =
        "SELECT id_departamento, nombre_departamento "
      + "FROM DEPARTAMENTO "
      + "WHERE estado = 'Activo' "
      + "ORDER BY nombre_departamento";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            int id =
                rs.getInt("id_departamento");

            String nombre =
                rs.getString("nombre_departamento");

            cmbDepartamentoEmpleado.addItem(nombre);

            departamentosMap.put(nombre, id);
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar departamentos: "
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void cargarPuestos() {

    cmbPuestoEmpleado.removeAllItems();

    puestosMap.clear();

    String departamento =
        (String) cmbDepartamentoEmpleado.getSelectedItem();

    if (departamento == null) {
        return;
    }

    Integer idDepartamento =
        departamentosMap.get(departamento);

    if (idDepartamento == null) {
        return;
    }

    String sql =
        "SELECT id_puesto, nombre_puesto "
      + "FROM PUESTOS "
      + "WHERE id_departamento = ? "
      + "AND estado = 'ACTIVO' "
      + "ORDER BY nombre_puesto";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idDepartamento);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id =
                    rs.getInt("id_puesto");

                String nombre =
                    rs.getString("nombre_puesto");

                cmbPuestoEmpleado.addItem(nombre);

                puestosMap.put(nombre, id);
            }
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar puestos: "
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void cargarTiposContrato() {

    cmbTipoContratoEmpleado.removeAllItems();

    contratosMap.clear();

    String sql =
        "SELECT id_tipo_contrato, nombre "
      + "FROM TIPOS_CONTRATO "
      + "WHERE estado = 'ACTIVO' "
      + "ORDER BY nombre";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            int id =
                rs.getInt("id_tipo_contrato");

            String nombre =
                rs.getString("nombre");

            cmbTipoContratoEmpleado.addItem(nombre);

            contratosMap.put(nombre, id);
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar tipos de contrato: "
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void agregarEmpleado() {

    String departamento =
        (String) cmbDepartamentoEmpleado.getSelectedItem();

    String puesto =
        (String) cmbPuestoEmpleado.getSelectedItem();

    String contrato =
        (String) cmbTipoContratoEmpleado.getSelectedItem();

    String nombres =
        txtNombresEmpleado.getText().trim();

    String apellidos =
        txtApellidosEmpleado.getText().trim();

    String cedula =
        txtCedulaEmpleado.getText().trim();

    String direccion =
        txtDireccionEmpleado.getText().trim();

    String telefono =
        txtTelefonoEmpleado.getText().trim();

    String email =
        txtEmailEmpleado.getText().trim();

    String salarioTexto =
        txtSalarioBaseEmpleado.getText().trim();

    String banco =
        txtBancoEmpleado.getText().trim();

    String cuenta =
        txtCuentaBancariaEmpleado.getText().trim();

    String genero =
        cmbGeneroEmpleado.getSelectedItem().toString();

    String estadoCivil =
        cmbEstadoCivilEmpleado.getSelectedItem().toString();

    String tipoPago =
        cmbTipoPagoEmpleado.getSelectedItem().toString();

    String estado =
        cmbEstadoEmpleado.getSelectedItem().toString();


    // =========================
    // VALIDACIONES
    // =========================

    if (departamento == null ||
        puesto == null ||
        contrato == null ||
        nombres.isEmpty() ||
        apellidos.isEmpty() ||
        cedula.isEmpty() ||
        salarioTexto.isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe completar los campos obligatorios.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }


    Integer idDepartamento =
        departamentosMap.get(departamento);

    Integer idPuesto =
        puestosMap.get(puesto);

    Integer idContrato =
        contratosMap.get(contrato);


    if (idDepartamento == null ||
        idPuesto == null ||
        idContrato == null) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo identificar el departamento, puesto o contrato.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }


    // =========================
    // SALARIO
    // =========================

    BigDecimal salario;

    try {

        salario = new BigDecimal(salarioTexto);

        if (salario.compareTo(BigDecimal.ZERO) <= 0) {

            JOptionPane.showMessageDialog(
                this,
                "El salario debe ser mayor que cero.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

    } catch (NumberFormatException e) {

        JOptionPane.showMessageDialog(
            this,
            "El salario debe ser un valor numérico.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }


    // =========================
    // EMPRESA
    // =========================

    int idEmpresa = 1;


    // =========================
    // FECHAS
    // =========================

    Date fechaNacimiento = null;
    Date fechaIngreso;


    try {

        /*
         * Si utilizas JTextField:
         *
         * formato:
         * yyyy-MM-dd
         */

        String fechaNacimientoTexto =
            txtFechaNacimientoEmpleado.getText().trim();

        String fechaIngresoTexto =
            txtFechaIngresoEmpleado.getText().trim();


        if (!fechaNacimientoTexto.isEmpty()) {

            fechaNacimiento =
                Date.valueOf(fechaNacimientoTexto);
        }


        if (fechaIngresoTexto.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Debe ingresar la fecha de ingreso.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        fechaIngreso =
            Date.valueOf(fechaIngresoTexto);


    } catch (IllegalArgumentException e) {

        JOptionPane.showMessageDialog(
            this,
            "Las fechas deben tener el formato:\n"
          + "yyyy-MM-dd",
            "Fecha inválida",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }


    // =========================
    // INSERT
    // =========================

    String sql =
        "INSERT INTO EMPLEADO ("
      + "id_empresa, "
      + "id_departamento, "
      + "id_puesto, "
      + "id_tipo_contrato, "
      + "nombres, "
      + "apellidos, "
      + "cedula, "
      + "fecha_nacimiento, "
      + "genero, "
      + "estado_civil, "
      + "direccion, "
      + "telefono, "
      + "email, "
      + "foto, "
      + "fecha_ingreso, "
      + "salario_base, "
      + "tipo_pago, "
      + "banco, "
      + "cuenta_bancaria, "
      + "estado"
      + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    conexionMariaDB conectar =
        new conexionMariaDB();


    try (java.sql.Connection cn =
            conectar.conectar();
         PreparedStatement ps =
            cn.prepareStatement(sql)) {


        ps.setInt(1, idEmpresa);

        ps.setInt(2, idDepartamento);

        ps.setInt(3, idPuesto);

        ps.setInt(4, idContrato);

        ps.setString(5, nombres);

        ps.setString(6, apellidos);

        ps.setString(7, cedula);


        if (fechaNacimiento != null) {

            ps.setDate(8, fechaNacimiento);

        } else {

            ps.setNull(
                8,
                java.sql.Types.DATE
            );
        }


        ps.setString(9, genero);

        ps.setString(10, estadoCivil);

        ps.setString(11, direccion);

        ps.setString(12, telefono);

        ps.setString(13, email);


        if (rutaFoto != null &&
            !rutaFoto.isEmpty()) {

            ps.setString(14, rutaFoto);

        } else {

            ps.setNull(
                14,
                java.sql.Types.VARCHAR
            );
        }


        ps.setDate(15, fechaIngreso);

        ps.setBigDecimal(16, salario);

        ps.setString(17, tipoPago);

        ps.setString(18, banco);

        ps.setString(19, cuenta);

        ps.setString(20, estado);


        int filas =
            ps.executeUpdate();


        if (filas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Empleado registrado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );


            // Actualizar tabla del Menu
            if (menu != null) {
                menu.cargarEmpleados();
            }


            limpiarCampos();

            dispose();
        }


    } catch (SQLException e) {

        if (e.getErrorCode() == 1062) {

            JOptionPane.showMessageDialog(
                this,
                "La cédula ingresada ya está registrada.",
                "Cédula duplicada",
                JOptionPane.WARNING_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Error al registrar empleado:\n"
              + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
    
    private void seleccionarFoto() {

    JFileChooser selector =
        new JFileChooser();

    selector.setDialogTitle(
        "Seleccionar foto del empleado"
    );

    int resultado =
        selector.showOpenDialog(this);

    if (resultado ==
        JFileChooser.APPROVE_OPTION) {

        File archivo =
            selector.getSelectedFile();

        rutaFoto =
            archivo.getAbsolutePath();


        ImageIcon icono =
            new ImageIcon(rutaFoto);

        Image imagen =
            icono.getImage()
                 .getScaledInstance(
                     lblFotoEmpleado.getWidth(),
                     lblFotoEmpleado.getHeight(),
                     Image.SCALE_SMOOTH
                 );

        lblFotoEmpleado.setIcon(
            new ImageIcon(imagen)
        );
    }
}
    
    private void limpiarCampos() {

    txtNombresEmpleado.setText("");
    txtApellidosEmpleado.setText("");
    txtCedulaEmpleado.setText("");

    txtFechaNacimientoEmpleado.setText("");
    txtFechaIngresoEmpleado.setText("");

    txtDireccionEmpleado.setText("");
    txtTelefonoEmpleado.setText("");
    txtEmailEmpleado.setText("");

    txtSalarioBaseEmpleado.setText("");

    txtBancoEmpleado.setText("");
    txtCuentaBancariaEmpleado.setText("");

    rutaFoto = null;

    lblFotoEmpleado.setIcon(null);

    if (cmbGeneroEmpleado.getItemCount() > 0) {
        cmbGeneroEmpleado.setSelectedIndex(0);
    }

    if (cmbEstadoCivilEmpleado.getItemCount() > 0) {
        cmbEstadoCivilEmpleado.setSelectedIndex(0);
    }

    if (cmbTipoPagoEmpleado.getItemCount() > 0) {
        cmbTipoPagoEmpleado.setSelectedItem("MENSUAL");
    }

    if (cmbEstadoEmpleado.getItemCount() > 0) {
        cmbEstadoEmpleado.setSelectedItem("ACTIVO");
    }
}
   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbDepartamentoEmpleado = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmbPuestoEmpleado = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbTipoContratoEmpleado = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtNombresEmpleado = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtApellidosEmpleado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCedulaEmpleado = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtFechaNacimientoEmpleado = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cmbGeneroEmpleado = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cmbEstadoCivilEmpleado = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtDireccionEmpleado = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtTelefonoEmpleado = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtFechaIngresoEmpleado = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtEmailEmpleado = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtSalarioBaseEmpleado = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtBancoEmpleado = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cmbTipoPagoEmpleado = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        txtCuentaBancariaEmpleado = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        cmbEstadoEmpleado = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblFotoEmpleado = new javax.swing.JLabel();
        btnSubirFoto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 700));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));

        jLabel2.setFont(new java.awt.Font("sansserif", 3, 36)); // NOI18N
        jLabel2.setText("Formulario de Empleados");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(jLabel2)
                .addContainerGap(227, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel2)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 880, -1));

        jPanel3.setBackground(new java.awt.Color(236, 236, 236));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel1.setText("Departamentos");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        cmbDepartamentoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contabilidad", "2- Produccion y Operaciones", "3- Marketing ", "4- Ventas", "5- Tecnologia" }));
        cmbDepartamentoEmpleado.addActionListener(this::cmbDepartamentoEmpleadoActionPerformed);
        jPanel3.add(cmbDepartamentoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 160, -1));

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel3.setText("Cargo");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, -1, -1));

        cmbPuestoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contador", "2- Auxiliar Contable", "3- Analista Financiero", "4- Supervisor de Produccion", "5- Operario", "6- Encargado de Operaciones", "7- Gerente de Marketing", "8- Analista de Marketing", "9- Gerente de Ventas", "10- Vendedor", "11- Soporte Tecnico" }));
        cmbPuestoEmpleado.addActionListener(this::cmbPuestoEmpleadoActionPerformed);
        jPanel3.add(cmbPuestoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, 170, -1));

        jLabel4.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel4.setText("Tipo de Contrato");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, -1, -1));

        cmbTipoContratoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Indefinido", "2- Temporal", "3- Pasantia" }));
        jPanel3.add(cmbTipoContratoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 80, 160, -1));

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel5.setText("Nombres");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));

        txtNombresEmpleado.addActionListener(this::txtNombresEmpleadoActionPerformed);
        jPanel3.add(txtNombresEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 170, -1));

        jLabel6.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel6.setText("Apellidos");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, -1, -1));

        txtApellidosEmpleado.addActionListener(this::txtApellidosEmpleadoActionPerformed);
        jPanel3.add(txtApellidosEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 180, 160, -1));

        jLabel7.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel7.setText("Cedula");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 160, -1, -1));

        txtCedulaEmpleado.addActionListener(this::txtCedulaEmpleadoActionPerformed);
        jPanel3.add(txtCedulaEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 180, 140, -1));

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel8.setText("Fecha de Nacimiento");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, -1, -1));

        txtFechaNacimientoEmpleado.addActionListener(this::txtFechaNacimientoEmpleadoActionPerformed);
        jPanel3.add(txtFechaNacimientoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 250, 160, -1));

        jLabel9.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel9.setText("Genero");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));

        cmbGeneroEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Otro" }));
        cmbGeneroEmpleado.addActionListener(this::cmbGeneroEmpleadoActionPerformed);
        jPanel3.add(cmbGeneroEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 110, -1));

        jLabel10.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel10.setText("Estado Civil");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 230, -1, -1));

        cmbEstadoCivilEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Soltero", "Casado" }));
        jPanel3.add(cmbEstadoCivilEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, 100, -1));

        jLabel11.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel11.setText("Direccion");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, -1, -1));
        jPanel3.add(txtDireccionEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 170, -1));

        jLabel12.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel12.setText("Telefono");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 300, -1, 20));

        txtTelefonoEmpleado.addActionListener(this::txtTelefonoEmpleadoActionPerformed);
        jPanel3.add(txtTelefonoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 320, 160, -1));

        jLabel13.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel13.setText("Fecha de Ingreso");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 470, -1, -1));
        jPanel3.add(txtFechaIngresoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 490, 130, -1));

        jLabel14.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel14.setText("Fecha de Salida");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 470, -1, -1));
        jPanel3.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 490, 140, -1));

        jLabel15.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel15.setText("Email");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 300, -1, -1));

        txtEmailEmpleado.addActionListener(this::txtEmailEmpleadoActionPerformed);
        jPanel3.add(txtEmailEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 320, 140, -1));

        jLabel16.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel16.setText("Salario Base");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, -1, -1));
        jPanel3.add(txtSalarioBaseEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 160, -1));

        jLabel17.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel17.setText("Banco");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 400, -1, -1));
        jPanel3.add(txtBancoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 420, 140, -1));

        jLabel18.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel18.setText("Tipo de Pago");
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 400, -1, -1));

        cmbTipoPagoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mensual", "Quincenal", "Semanal" }));
        jPanel3.add(cmbTipoPagoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 420, 110, -1));

        jLabel19.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel19.setText("Cuenta Bancaria");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 400, -1, -1));
        jPanel3.add(txtCuentaBancariaEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 420, 170, -1));

        jLabel20.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel20.setText("Estado");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 470, -1, -1));

        cmbEstadoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "Activo", "Inactivo", "Suspendido", "Desvinculado" }));
        jPanel3.add(cmbEstadoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 490, 120, -1));

        jLabel21.setFont(new java.awt.Font("Roboto", 3, 24)); // NOI18N
        jLabel21.setText("Informacion de la Empresa");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 300, -1));

        jLabel22.setFont(new java.awt.Font("Roboto", 3, 24)); // NOI18N
        jLabel22.setText("Informacion Empleado");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 260, -1));

        jLabel23.setFont(new java.awt.Font("Roboto", 3, 24)); // NOI18N
        jLabel23.setText("Informacion Contractual");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 270, -1));

        jPanel4.setBackground(new java.awt.Color(242, 241, 241));

        jButton1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton1.setText("Listo");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton2.setText("Cerrar");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(27, 27, 27))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 520, 140, 40));

        lblFotoEmpleado.setBackground(new java.awt.Color(255, 255, 255));
        lblFotoEmpleado.setForeground(new java.awt.Color(255, 255, 255));
        lblFotoEmpleado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(lblFotoEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 180, 170));

        btnSubirFoto.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        btnSubirFoto.setText("Subir Foto");
        btnSubirFoto.addActionListener(this::btnSubirFotoActionPerformed);
        jPanel3.add(btnSubirFoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 340, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 880, 560));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbDepartamentoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDepartamentoEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDepartamentoEmpleadoActionPerformed

    private void txtNombresEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombresEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombresEmpleadoActionPerformed

    private void txtApellidosEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApellidosEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApellidosEmpleadoActionPerformed

    private void txtCedulaEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCedulaEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaEmpleadoActionPerformed

    private void txtFechaNacimientoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaNacimientoEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaNacimientoEmpleadoActionPerformed

    private void txtTelefonoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoEmpleadoActionPerformed

    private void txtEmailEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailEmpleadoActionPerformed

    private void cmbGeneroEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGeneroEmpleadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbGeneroEmpleadoActionPerformed

    private void btnSubirFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubirFotoActionPerformed
        seleccionarFoto();
    }//GEN-LAST:event_btnSubirFotoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void cmbPuestoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPuestoEmpleadoActionPerformed
        cargarPuestos();
    }//GEN-LAST:event_cmbPuestoEmpleadoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        agregarEmpleado();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSubirFoto;
    private javax.swing.JComboBox<String> cmbDepartamentoEmpleado;
    private javax.swing.JComboBox<String> cmbEstadoCivilEmpleado;
    private javax.swing.JComboBox<String> cmbEstadoEmpleado;
    private javax.swing.JComboBox<String> cmbGeneroEmpleado;
    private javax.swing.JComboBox<String> cmbPuestoEmpleado;
    private javax.swing.JComboBox<String> cmbTipoContratoEmpleado;
    private javax.swing.JComboBox<String> cmbTipoPagoEmpleado;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JLabel lblFotoEmpleado;
    private javax.swing.JTextField txtApellidosEmpleado;
    private javax.swing.JTextField txtBancoEmpleado;
    private javax.swing.JTextField txtCedulaEmpleado;
    private javax.swing.JTextField txtCuentaBancariaEmpleado;
    private javax.swing.JTextField txtDireccionEmpleado;
    private javax.swing.JTextField txtEmailEmpleado;
    private javax.swing.JTextField txtFechaIngresoEmpleado;
    private javax.swing.JTextField txtFechaNacimientoEmpleado;
    private javax.swing.JTextField txtNombresEmpleado;
    private javax.swing.JTextField txtSalarioBaseEmpleado;
    private javax.swing.JTextField txtTelefonoEmpleado;
    // End of variables declaration//GEN-END:variables
}
