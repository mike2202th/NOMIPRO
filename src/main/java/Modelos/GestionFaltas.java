package Modelos;

import ConexionDB.Falta;
import ConexionDB.FaltaDAO;
import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana para el registro y consulta de faltas de empleados.
 * Sigue el mismo diseño visual y estructura que las demás ventanas del sistema.
 */
public class GestionFaltas extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GestionFaltas.class.getName());

    private FaltaDAO faltaDAO = new FaltaDAO();
    private conexionMariaDB conexion = new conexionMariaDB();
    private int idFaltaSeleccionada = -1;

    public GestionFaltas() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("NOMIPRO - Registro de Faltas");
        cargarEmpleados();
        cargarTabla();
    }

    /** Carga los empleados en el ComboBox desde la base de datos. */
    private void cargarEmpleados() {
        cboEmpleado.removeAllItems();
        cboEmpleado.addItem("-- Seleccione un empleado --");
        String sql = "SELECT id_empleado, CONCAT(primer_nombre, ' ', primer_apellido) AS nombre "
                   + "FROM EMPLEADOS WHERE estado = 'Activo' ORDER BY primer_nombre";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cboEmpleado.addItem(rs.getInt("id_empleado") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
        }
    }

    /** Carga todos los registros de faltas en la tabla. */
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tblFaltas.getModel();
        modelo.setRowCount(0);
        for (Falta f : faltaDAO.listarTodas()) {
            modelo.addRow(new Object[]{
                f.getIdFalta(),
                f.getNombreEmpleado(),
                f.getFecha(),
                f.getTipoFalta(),
                f.isJustificada() ? "Justificada" : "No Justificada",
                f.getObservaciones()
            });
        }
    }

    /** Limpia todos los campos del formulario. */
    private void limpiarFormulario() {
        idFaltaSeleccionada = -1;
        cboEmpleado.setSelectedIndex(0);
        txtFecha.setText("");
        cboTipoFalta.setSelectedIndex(0);
        rdoJustificada.setSelected(false);
        rdoNoJustificada.setSelected(true);
        txtObservaciones.setText("");
    }

    /** Obtiene el ID del empleado seleccionado en el combo. */
    private int getIdEmpleadoSeleccionado() {
        String item = (String) cboEmpleado.getSelectedItem();
        if (item == null || item.startsWith("--")) return -1;
        try {
            return Integer.parseInt(item.split(" - ")[0].trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgJustificada = new javax.swing.ButtonGroup();
        pnlHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        pnlFormulario = new javax.swing.JPanel();
        lblEmpleado = new javax.swing.JLabel();
        cboEmpleado = new javax.swing.JComboBox<>();
        lblFecha = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        lblFechaHint = new javax.swing.JLabel();
        lblTipoFalta = new javax.swing.JLabel();
        cboTipoFalta = new javax.swing.JComboBox<>();
        lblJustificacion = new javax.swing.JLabel();
        rdoJustificada = new javax.swing.JRadioButton();
        rdoNoJustificada = new javax.swing.JRadioButton();
        lblObservaciones = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        btnRegistrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        lblTablaTitle = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFaltas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Faltas");
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1100, 750));

        // ---- Header ----
        pnlHeader.setBackground(new java.awt.Color(9, 144, 120));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 28));
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Registro de Faltas");
        pnlHeader.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 15, -1, -1));

        lblSubtitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        lblSubtitulo.setForeground(new java.awt.Color(200, 255, 245));
        lblSubtitulo.setText("Control y seguimiento de ausencias del personal");
        pnlHeader.add(lblSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        btnCerrar.setBackground(new java.awt.Color(220, 53, 69));
        btnCerrar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 12));
        btnCerrar.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrar.setText("✕ Cerrar");
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(evt -> dispose());
        pnlHeader.add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 20, 100, 35));

        // ---- Formulario ----
        pnlFormulario.setBackground(new java.awt.Color(245, 247, 250));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(9, 144, 120), 1),
            "Formulario de Registro",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("sansserif", java.awt.Font.BOLD, 13),
            new java.awt.Color(9, 144, 120)));
        pnlFormulario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEmpleado.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblEmpleado.setText("Empleado:");
        pnlFormulario.add(lblEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 120, -1));

        cboEmpleado.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        cboEmpleado.setModel(new DefaultComboBoxModel<>(new String[]{"-- Seleccione un empleado --"}));
        pnlFormulario.add(cboEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 26, 350, 30));

        lblFecha.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblFecha.setText("Fecha:");
        pnlFormulario.add(lblFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 120, -1));

        txtFecha.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtFecha.setToolTipText("Formato: yyyy-MM-dd (ej: 2025-01-15)");
        pnlFormulario.add(txtFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 71, 200, 30));

        lblFechaHint.setFont(new java.awt.Font("sansserif", java.awt.Font.ITALIC, 11));
        lblFechaHint.setForeground(new java.awt.Color(120, 120, 120));
        lblFechaHint.setText("Formato: yyyy-MM-dd");
        pnlFormulario.add(lblFechaHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 104, 200, -1));

        lblTipoFalta.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblTipoFalta.setText("Tipo de Falta:");
        pnlFormulario.add(lblTipoFalta, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 75, 120, -1));

        cboTipoFalta.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        cboTipoFalta.setModel(new DefaultComboBoxModel<>(new String[]{
            "Inasistencia", "Abandono de puesto", "Tardanza excesiva",
            "Licencia no autorizada", "Causa médica", "Causa personal", "Otro"
        }));
        pnlFormulario.add(cboTipoFalta, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 71, 260, 30));

        lblJustificacion.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblJustificacion.setText("Justificación:");
        pnlFormulario.add(lblJustificacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 120, -1));

        bgJustificada.add(rdoJustificada);
        rdoJustificada.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        rdoJustificada.setText("Justificada");
        rdoJustificada.setBackground(new java.awt.Color(245, 247, 250));
        pnlFormulario.add(rdoJustificada, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 126, 130, 30));

        bgJustificada.add(rdoNoJustificada);
        rdoNoJustificada.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        rdoNoJustificada.setText("No Justificada");
        rdoNoJustificada.setSelected(true);
        rdoNoJustificada.setBackground(new java.awt.Color(245, 247, 250));
        pnlFormulario.add(rdoNoJustificada, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 126, 140, 30));

        lblObservaciones.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblObservaciones.setText("Observaciones:");
        pnlFormulario.add(lblObservaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 175, 120, -1));

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(3);
        txtObservaciones.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtObservaciones);
        pnlFormulario.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, 700, 70));

        // Botones
        btnRegistrar.setBackground(new java.awt.Color(9, 144, 120));
        btnRegistrar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(evt -> registrarFalta());
        pnlFormulario.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 130, 36));

        btnActualizar.setBackground(new java.awt.Color(15, 69, 141));
        btnActualizar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(evt -> actualizarFalta());
        pnlFormulario.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 260, 130, 36));

        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(evt -> eliminarFalta());
        pnlFormulario.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 260, 130, 36));

        btnLimpiar.setBackground(new java.awt.Color(108, 117, 125));
        btnLimpiar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(evt -> limpiarFormulario());
        pnlFormulario.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(575, 260, 130, 36));

        // ---- Tabla ----
        pnlTabla.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabla.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(9, 144, 120), 1),
            "Faltas Registradas",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("sansserif", java.awt.Font.BOLD, 13),
            new java.awt.Color(9, 144, 120)));
        pnlTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTablaTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 12));
        lblTablaTitle.setForeground(new java.awt.Color(100, 100, 100));
        lblTablaTitle.setText("Haga clic en una fila para seleccionar y editar");
        pnlTabla.add(lblTablaTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 400, -1));

        tblFaltas.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Empleado", "Fecha", "Tipo de Falta", "Justificación", "Observaciones"}
        ) {
            boolean[] canEdit = {false, false, false, false, false, false};
            @Override public boolean isCellEditable(int row, int col) { return canEdit[col]; }
        });
        tblFaltas.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        tblFaltas.setRowHeight(26);
        tblFaltas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblFaltas.getTableHeader().setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        tblFaltas.getTableHeader().setBackground(new java.awt.Color(9, 144, 120));
        tblFaltas.getTableHeader().setForeground(new java.awt.Color(255, 255, 255));
        tblFaltas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });
        tblFaltas.getColumnModel().getColumn(0).setMaxWidth(50);
        tblFaltas.getColumnModel().getColumn(4).setMaxWidth(120);

        jScrollPane2.setViewportView(tblFaltas);
        pnlTabla.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 42, 1060, 270));

        // ---- Layout principal ----
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 1100, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, 1088, Short.MAX_VALUE)
                    .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 1088, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registrarFalta() {
        if (!validarCampos()) return;
        Falta f = construirFalta();
        if (faltaDAO.insertar(f)) {
            JOptionPane.showMessageDialog(this, "Falta registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la falta. Verifique las tablas de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarFalta() {
        if (idFaltaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una falta de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        Falta f = construirFalta();
        f.setIdFalta(idFaltaSeleccionada);
        if (faltaDAO.actualizar(f)) {
            JOptionPane.showMessageDialog(this, "Falta actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la falta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFalta() {
        if (idFaltaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una falta de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar este registro de falta?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (faltaDAO.eliminar(idFaltaSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Falta eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la falta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void seleccionarFila() {
        int fila = tblFaltas.getSelectedRow();
        if (fila == -1) return;
        DefaultTableModel modelo = (DefaultTableModel) tblFaltas.getModel();
        idFaltaSeleccionada = (int) modelo.getValueAt(fila, 0);
        String empleadoStr = (String) modelo.getValueAt(fila, 1);
        Object fecha = modelo.getValueAt(fila, 2);
        String tipo = (String) modelo.getValueAt(fila, 3);
        String justStr = (String) modelo.getValueAt(fila, 4);
        String obs = (String) modelo.getValueAt(fila, 5);

        for (int i = 0; i < cboEmpleado.getItemCount(); i++) {
            String item = cboEmpleado.getItemAt(i);
            if (item != null && item.contains(empleadoStr)) {
                cboEmpleado.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(fecha != null ? fecha.toString() : "");
        for (int i = 0; i < cboTipoFalta.getItemCount(); i++) {
            if (cboTipoFalta.getItemAt(i).equals(tipo)) {
                cboTipoFalta.setSelectedIndex(i);
                break;
            }
        }
        boolean esJustificada = "Justificada".equals(justStr);
        rdoJustificada.setSelected(esJustificada);
        rdoNoJustificada.setSelected(!esJustificada);
        txtObservaciones.setText(obs != null ? obs : "");
    }

    private boolean validarCampos() {
        if (getIdEmpleadoSeleccionado() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(txtFecha.getText().trim(), fmt);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Falta construirFalta() {
        Falta f = new Falta();
        f.setIdEmpleado(getIdEmpleadoSeleccionado());
        f.setFecha(Date.valueOf(txtFecha.getText().trim()));
        f.setTipoFalta((String) cboTipoFalta.getSelectedItem());
        f.setJustificada(rdoJustificada.isSelected());
        f.setObservaciones(txtObservaciones.getText().trim());
        return f;
    }

    // Variables declaration//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgJustificada;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cboEmpleado;
    private javax.swing.JComboBox<String> cboTipoFalta;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblEmpleado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFechaHint;
    private javax.swing.JLabel lblJustificacion;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTablaTitle;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTipoFalta;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JRadioButton rdoJustificada;
    private javax.swing.JRadioButton rdoNoJustificada;
    private javax.swing.JTable tblFaltas;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
