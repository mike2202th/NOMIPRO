package Modelos;

import ConexionDB.Asistencia;
import ConexionDB.AsistenciaDAO;
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
 * Ventana para el registro y consulta de asistencia de empleados.
 * Sigue el mismo diseño visual y estructura que las demás ventanas del sistema.
 */
public class GestionAsistencia extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GestionAsistencia.class.getName());

    private AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private conexionMariaDB conexion = new conexionMariaDB();
    private int idAsistenciaSeleccionada = -1;

    public GestionAsistencia() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("NOMIPRO - Registro de Asistencia");
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

    /** Carga todos los registros de asistencia en la tabla. */
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tblAsistencia.getModel();
        modelo.setRowCount(0);
        for (Asistencia a : asistenciaDAO.listarTodas()) {
            modelo.addRow(new Object[]{
                a.getIdAsistencia(),
                a.getNombreEmpleado(),
                a.getFecha(),
                a.getHoraEntrada(),
                a.getHoraSalida(),
                a.getEstado(),
                a.getObservaciones()
            });
        }
    }

    /** Limpia todos los campos del formulario. */
    private void limpiarFormulario() {
        idAsistenciaSeleccionada = -1;
        cboEmpleado.setSelectedIndex(0);
        txtFecha.setText("");
        txtHoraEntrada.setText("");
        txtHoraSalida.setText("");
        cboEstado.setSelectedIndex(0);
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

        pnlHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        pnlFormulario = new javax.swing.JPanel();
        lblEmpleado = new javax.swing.JLabel();
        cboEmpleado = new javax.swing.JComboBox<>();
        lblFecha = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        lblHoraEntrada = new javax.swing.JLabel();
        txtHoraEntrada = new javax.swing.JTextField();
        lblHoraSalida = new javax.swing.JLabel();
        txtHoraSalida = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        cboEstado = new javax.swing.JComboBox<>();
        lblObservaciones = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        btnRegistrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblFechaHint = new javax.swing.JLabel();
        lblHoraHint = new javax.swing.JLabel();
        pnlTabla = new javax.swing.JPanel();
        lblTablaTitle = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAsistencia = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Asistencia");
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1100, 750));

        // ---- Header ----
        pnlHeader.setBackground(new java.awt.Color(9, 144, 120));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 28));
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Registro de Asistencia");
        pnlHeader.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 15, -1, -1));

        lblSubtitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        lblSubtitulo.setForeground(new java.awt.Color(200, 255, 245));
        lblSubtitulo.setText("Control de entradas, salidas y estados de asistencia del personal");
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
        pnlFormulario.add(txtFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 71, 180, 30));

        lblFechaHint.setFont(new java.awt.Font("sansserif", java.awt.Font.ITALIC, 11));
        lblFechaHint.setForeground(new java.awt.Color(120, 120, 120));
        lblFechaHint.setText("Formato: yyyy-MM-dd");
        pnlFormulario.add(lblFechaHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 104, 200, -1));

        lblHoraEntrada.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblHoraEntrada.setText("Hora Entrada:");
        pnlFormulario.add(lblHoraEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 75, 110, -1));

        txtHoraEntrada.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtHoraEntrada.setToolTipText("Formato: HH:mm (ej: 08:00)");
        pnlFormulario.add(txtHoraEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 71, 120, 30));

        lblHoraSalida.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblHoraSalida.setText("Hora Salida:");
        pnlFormulario.add(lblHoraSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 75, 100, -1));

        txtHoraSalida.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtHoraSalida.setToolTipText("Formato: HH:mm (ej: 17:00)");
        pnlFormulario.add(txtHoraSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(715, 71, 120, 30));

        lblHoraHint.setFont(new java.awt.Font("sansserif", java.awt.Font.ITALIC, 11));
        lblHoraHint.setForeground(new java.awt.Color(120, 120, 120));
        lblHoraHint.setText("Formato horas: HH:mm");
        pnlFormulario.add(lblHoraHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 104, 200, -1));

        lblEstado.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblEstado.setText("Estado:");
        pnlFormulario.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 120, -1));

        cboEstado.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        cboEstado.setModel(new DefaultComboBoxModel<>(new String[]{
            "Presente", "Ausente", "Tardanza", "Permiso", "Vacaciones"
        }));
        pnlFormulario.add(cboEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 126, 200, 30));

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
        btnRegistrar.addActionListener(evt -> registrarAsistencia());
        pnlFormulario.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 130, 36));

        btnActualizar.setBackground(new java.awt.Color(15, 69, 141));
        btnActualizar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(evt -> actualizarAsistencia());
        pnlFormulario.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 260, 130, 36));

        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(evt -> eliminarAsistencia());
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
            "Registros de Asistencia",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("sansserif", java.awt.Font.BOLD, 13),
            new java.awt.Color(9, 144, 120)));
        pnlTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTablaTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 12));
        lblTablaTitle.setForeground(new java.awt.Color(100, 100, 100));
        lblTablaTitle.setText("Haga clic en una fila para seleccionar y editar");
        pnlTabla.add(lblTablaTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 400, -1));

        tblAsistencia.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Empleado", "Fecha", "H. Entrada", "H. Salida", "Estado", "Observaciones"}
        ) {
            boolean[] canEdit = {false, false, false, false, false, false, false};
            @Override public boolean isCellEditable(int row, int col) { return canEdit[col]; }
        });
        tblAsistencia.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        tblAsistencia.setRowHeight(26);
        tblAsistencia.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblAsistencia.getTableHeader().setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        tblAsistencia.getTableHeader().setBackground(new java.awt.Color(9, 144, 120));
        tblAsistencia.getTableHeader().setForeground(new java.awt.Color(255, 255, 255));
        tblAsistencia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });
        tblAsistencia.getColumnModel().getColumn(0).setMaxWidth(50);
        tblAsistencia.getColumnModel().getColumn(3).setMaxWidth(90);
        tblAsistencia.getColumnModel().getColumn(4).setMaxWidth(90);

        jScrollPane2.setViewportView(tblAsistencia);
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

    private void registrarAsistencia() {
        if (!validarCampos()) return;
        Asistencia a = construirAsistencia();
        if (asistenciaDAO.insertar(a)) {
            JOptionPane.showMessageDialog(this, "Asistencia registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la asistencia. Verifique las tablas de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarAsistencia() {
        if (idAsistenciaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        Asistencia a = construirAsistencia();
        a.setIdAsistencia(idAsistenciaSeleccionada);
        if (asistenciaDAO.actualizar(a)) {
            JOptionPane.showMessageDialog(this, "Asistencia actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la asistencia.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarAsistencia() {
        if (idAsistenciaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar este registro de asistencia?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (asistenciaDAO.eliminar(idAsistenciaSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Registro eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void seleccionarFila() {
        int fila = tblAsistencia.getSelectedRow();
        if (fila == -1) return;
        DefaultTableModel modelo = (DefaultTableModel) tblAsistencia.getModel();
        idAsistenciaSeleccionada = (int) modelo.getValueAt(fila, 0);
        String empleadoStr = (String) modelo.getValueAt(fila, 1);
        Object fecha = modelo.getValueAt(fila, 2);
        String horaE = (String) modelo.getValueAt(fila, 3);
        String horaS = (String) modelo.getValueAt(fila, 4);
        String estado = (String) modelo.getValueAt(fila, 5);
        String obs = (String) modelo.getValueAt(fila, 6);

        for (int i = 0; i < cboEmpleado.getItemCount(); i++) {
            String item = cboEmpleado.getItemAt(i);
            if (item != null && item.contains(empleadoStr)) {
                cboEmpleado.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(fecha != null ? fecha.toString() : "");
        txtHoraEntrada.setText(horaE != null ? horaE : "");
        txtHoraSalida.setText(horaS != null ? horaS : "");
        for (int i = 0; i < cboEstado.getItemCount(); i++) {
            if (cboEstado.getItemAt(i).equals(estado)) {
                cboEstado.setSelectedIndex(i);
                break;
            }
        }
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

    private Asistencia construirAsistencia() {
        Asistencia a = new Asistencia();
        a.setIdEmpleado(getIdEmpleadoSeleccionado());
        a.setFecha(Date.valueOf(txtFecha.getText().trim()));
        String horaE = txtHoraEntrada.getText().trim();
        String horaS = txtHoraSalida.getText().trim();
        a.setHoraEntrada(horaE.isEmpty() ? null : horaE);
        a.setHoraSalida(horaS.isEmpty() ? null : horaS);
        a.setEstado((String) cboEstado.getSelectedItem());
        a.setObservaciones(txtObservaciones.getText().trim());
        return a;
    }

    // Variables declaration//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cboEmpleado;
    private javax.swing.JComboBox<String> cboEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblEmpleado;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFechaHint;
    private javax.swing.JLabel lblHoraEntrada;
    private javax.swing.JLabel lblHoraHint;
    private javax.swing.JLabel lblHoraSalida;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTablaTitle;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblAsistencia;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHoraEntrada;
    private javax.swing.JTextField txtHoraSalida;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
