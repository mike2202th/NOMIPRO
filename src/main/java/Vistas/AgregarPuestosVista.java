/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vistas;

import Main.Menu;
import Main.conexionMariaDB;
import Modelos.AgregarPuestos;
import Modelos.AgregarPuestosDAO;
import Modelos.DepartamentoItem;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author maico
 */
public class AgregarPuestosVista extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AgregarPuestos.class.getName());
    private Menu menu;
    
    public AgregarPuestosVista(Menu menu) {
        initComponents();
        
        this.menu = menu;       
        cargarDepartamentos();
        
        setLocationRelativeTo(this);
    }

    private Map<String, Integer> departamentosMap = new HashMap<>();
    private void cargarDepartamentos() {

    cmbDepartamentoPuesto.removeAllItems();

    departamentosMap.clear();

    String sql = "SELECT id_departamento, nombre_departamento "
               + "FROM DEPARTAMENTO "
               + "WHERE estado = 'Activo' "
               + "ORDER BY nombre_departamento";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            int id = rs.getInt("id_departamento");

            String nombre =
                    rs.getString("nombre_departamento");

            cmbDepartamentoPuesto.addItem(nombre);

            departamentosMap.put(nombre, id);
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar los departamentos: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
private void agregarPuesto() {

    String departamento =
            (String) cmbDepartamentoPuesto.getSelectedItem();

    String nombre =
            txtNombrePuesto.getText().trim();

    String descripcion =
            txtDescripcionPuesto.getText().trim();

    String salarioMinimoTexto =
            txtSalarioMinimo.getText().trim();

    String salarioMaximoTexto =
            txtSalarioMaximo.getText().trim();

    String estado = "ACTIVO";

    // Validar departamento
    if (departamento == null || departamento.isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar un departamento.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    // Obtener ID del departamento
    Integer idDepartamento =
            departamentosMap.get(departamento);

    if (idDepartamento == null) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo identificar el departamento.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    // Validar nombre
    if (nombre.isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe ingresar el nombre del puesto.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        txtNombrePuesto.requestFocus();
        return;
    }

    BigDecimal salarioMinimo = null;
    BigDecimal salarioMaximo = null;

    try {

        if (!salarioMinimoTexto.isEmpty()) {
            salarioMinimo =
                    new BigDecimal(salarioMinimoTexto);
        }

        if (!salarioMaximoTexto.isEmpty()) {
            salarioMaximo =
                    new BigDecimal(salarioMaximoTexto);
        }

    } catch (NumberFormatException e) {

        JOptionPane.showMessageDialog(
            this,
            "Los salarios deben ser valores numéricos.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    // Validar rango salarial
    if (salarioMinimo != null &&
        salarioMaximo != null &&
        salarioMaximo.compareTo(salarioMinimo) < 0) {

        JOptionPane.showMessageDialog(
            this,
            "El salario máximo no puede ser menor "
          + "que el salario mínimo.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    String sql = "INSERT INTO PUESTOS "
               + "(id_departamento, nombre_puesto, descripcion, "
               + "salario_minimo, salario_maximo, estado) "
               + "VALUES (?, ?, ?, ?, ?, ?)";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idDepartamento);
        ps.setString(2, nombre);
        ps.setString(3, descripcion);

        if (salarioMinimo != null) {
            ps.setBigDecimal(4, salarioMinimo);
        } else {
            ps.setNull(4, java.sql.Types.DECIMAL);
        }

        if (salarioMaximo != null) {
            ps.setBigDecimal(5, salarioMaximo);
        } else {
            ps.setNull(5, java.sql.Types.DECIMAL);
        }

        ps.setString(6, estado);

        int filas = ps.executeUpdate();

        if (filas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Puesto agregado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );

            // ⭐ ACTUALIZAR LA TABLA DEL MENU
            menu.cargarPuestos();

            // Limpiar los campos
            limpiarCampos();

            // Cerrar el JFrame
            dispose();
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al agregar el puesto: "
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void limpiarCampos() {

    txtNombrePuesto.setText("");
    txtDescripcionPuesto.setText("");
    txtSalarioMinimo.setText("");
    txtSalarioMaximo.setText("");
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbDepartamentoPuesto = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombrePuesto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSalarioMinimo = new javax.swing.JTextField();
        txtSalarioMaximo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcionPuesto = new javax.swing.JTextArea();
        btnAgregar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(520, 470));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Roboto", 3, 36)); // NOI18N
        jLabel1.setText("Agregar Puestos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 80));

        cmbDepartamentoPuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contabilidad", "2- Produccion y Operaciones", "3- Marketing ", "4- Ventas", "5- Tecnologia" }));
        cmbDepartamentoPuesto.addActionListener(this::cmbDepartamentoPuestoActionPerformed);
        jPanel1.add(cmbDepartamentoPuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 160, -1));

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel2.setText("Departamentos:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel3.setText("Nombre del Puesto:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, -1));
        jPanel1.add(txtNombrePuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, 200, 40));

        jLabel4.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel4.setText("Salario Minimo:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel5.setText("Salario Maximo:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, -1, -1));
        jPanel1.add(txtSalarioMinimo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 180, 40));
        jPanel1.add(txtSalarioMaximo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 200, 40));

        jLabel6.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel6.setText("Descripcion del Puesto:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, -1, -1));

        txtDescripcionPuesto.setColumns(20);
        txtDescripcionPuesto.setLineWrap(true);
        txtDescripcionPuesto.setRows(5);
        txtDescripcionPuesto.setAutoscrolls(false);
        txtDescripcionPuesto.setMaximumSize(new java.awt.Dimension(232, 92));
        jScrollPane2.setViewportView(txtDescripcionPuesto);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 310, 310, -1));

        btnAgregar.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);
        jPanel1.add(btnAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 430, -1, -1));

        btnCerrar.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);
        jPanel1.add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 430, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 470));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbDepartamentoPuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDepartamentoPuestoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDepartamentoPuestoActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregarPuesto();
        
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JComboBox<String> cmbDepartamentoPuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtDescripcionPuesto;
    private javax.swing.JTextField txtNombrePuesto;
    private javax.swing.JTextField txtSalarioMaximo;
    private javax.swing.JTextField txtSalarioMinimo;
    // End of variables declaration//GEN-END:variables
}
