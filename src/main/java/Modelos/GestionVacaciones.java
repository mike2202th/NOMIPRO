package Modelos;

import ConexionDB.Vacacion;
import ConexionDB.VacacionDAO;
import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana para la gestión de vacaciones de empleados.
 * Sigue el mismo diseño visual y estructura que las demás ventanas del sistema.
 */
public class GestionVacaciones extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GestionVacaciones.class.getName());

    private VacacionDAO vacacionDAO = new VacacionDAO();
    private conexionMariaDB conexion = new conexionMariaDB();
    private int idVacacionSeleccionada = -1;

    public GestionVacaciones() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("NOMIPRO - Gestión de Vacaciones");
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

    /** Carga todos los registros de vacaciones en la tabla. */
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tblVacaciones.getModel();
        modelo.setRowCount(0);
        for (Vacacion v : vacacionDAO.listarTodas()) {
            modelo.addRow(new Object[]{
                v.getIdVacacion(),
                v.getNombreEmpleado(),
                v.getFechaInicio(),
                v.getFechaFin(),
                v.getCantidadDias(),
                v.getEstado(),
                v.getObservaciones()
            });
        }
    }

    /** Calcula automáticamente los días entre las fechas ingresadas. */
    private void calcularDias() {
        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();
        if (!inicio.isEmpty() && !fin.isEmpty()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaI = LocalDate.parse(inicio, fmt);
                LocalDate fechaF = LocalDate.parse(fin, fmt);
                long dias = ChronoUnit.DAYS.between(fechaI, fechaF);
                if (dias >= 0) {
                    txtDias.setText(String.valueOf(dias));
                } else {
                    txtDias.setText("0");
                }
            } catch (DateTimeParseException ex) {
                txtDias.setText("");
            }
        }
    }

    /** Limpia todos los campos del formulario. */
    private void limpiarFormulario() {
        idVacacionSeleccionada = -1;
        cboEmpleado.setSelectedIndex(0);
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtDias.setText("");
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
        lblFechaInicio = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        lblFechaFin = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        lblDias = new javax.swing.JLabel();
        txtDias = new javax.swing.JTextField();
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
        pnlTabla = new javax.swing.JPanel();
        lblTablaTitle = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVacaciones = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Vacaciones");
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1100, 750));

        // ---- Header ----
        pnlHeader.setBackground(new java.awt.Color(9, 144, 120));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 28));
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Gestión de Vacaciones");
        pnlHeader.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 15, -1, -1));

        lblSubtitulo.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        lblSubtitulo.setForeground(new java.awt.Color(200, 255, 245));
        lblSubtitulo.setText("Registro y control de vacaciones del personal");
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

        lblFechaInicio.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblFechaInicio.setText("Fecha Inicio:");
        pnlFormulario.add(lblFechaInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 120, -1));

        txtFechaInicio.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtFechaInicio.setToolTipText("Formato: yyyy-MM-dd (ej: 2025-01-15)");
        txtFechaInicio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { calcularDias(); }
        });
        pnlFormulario.add(txtFechaInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 71, 180, 30));

        lblFechaFin.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblFechaFin.setText("Fecha Fin:");
        pnlFormulario.add(lblFechaFin, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 75, 90, -1));

        txtFechaFin.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtFechaFin.setToolTipText("Formato: yyyy-MM-dd (ej: 2025-01-30)");
        txtFechaFin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { calcularDias(); }
        });
        pnlFormulario.add(txtFechaFin, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 71, 180, 30));

        lblFechaHint.setFont(new java.awt.Font("sansserif", java.awt.Font.ITALIC, 11));
        lblFechaHint.setForeground(new java.awt.Color(120, 120, 120));
        lblFechaHint.setText("Formato: yyyy-MM-dd");
        pnlFormulario.add(lblFechaHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 104, 200, -1));

        lblDias.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblDias.setText("Días:");
        pnlFormulario.add(lblDias, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 75, 50, -1));

        txtDias.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtDias.setEditable(false);
        txtDias.setBackground(new java.awt.Color(230, 240, 255));
        pnlFormulario.add(txtDias, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 71, 80, 30));

        lblEstado.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblEstado.setText("Estado:");
        pnlFormulario.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 125, 120, -1));

        cboEstado.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        cboEstado.setModel(new DefaultComboBoxModel<>(new String[]{
            "Pendiente", "Aprobada", "Rechazada", "Completada"
        }));
        pnlFormulario.add(cboEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 121, 200, 30));

        lblObservaciones.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        lblObservaciones.setText("Observaciones:");
        pnlFormulario.add(lblObservaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 120, -1));

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(3);
        txtObservaciones.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtObservaciones);
        pnlFormulario.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 165, 620, 75));

        // Botones
        btnRegistrar.setBackground(new java.awt.Color(9, 144, 120));
        btnRegistrar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(evt -> registrarVacacion());
        pnlFormulario.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 130, 36));

        btnActualizar.setBackground(new java.awt.Color(15, 69, 141));
        btnActualizar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(evt -> actualizarVacacion());
        pnlFormulario.add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 260, 130, 36));

        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(evt -> eliminarVacacion());
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
            "Vacaciones Registradas",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("sansserif", java.awt.Font.BOLD, 13),
            new java.awt.Color(9, 144, 120)));
        pnlTabla.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTablaTitle.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 12));
        lblTablaTitle.setForeground(new java.awt.Color(100, 100, 100));
        lblTablaTitle.setText("Haga clic en una fila para seleccionar y editar");
        pnlTabla.add(lblTablaTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 400, -1));

        tblVacaciones.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Empleado", "Fecha Inicio", "Fecha Fin", "Días", "Estado", "Observaciones"}
        ) {
            boolean[] canEdit = {false, false, false, false, false, false, false};
            @Override public boolean isCellEditable(int row, int col) { return canEdit[col]; }
        });
        tblVacaciones.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 13));
        tblVacaciones.setRowHeight(26);
        tblVacaciones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVacaciones.getTableHeader().setFont(new java.awt.Font("sansserif", java.awt.Font.BOLD, 13));
        tblVacaciones.getTableHeader().setBackground(new java.awt.Color(9, 144, 120));
        tblVacaciones.getTableHeader().setForeground(new java.awt.Color(255, 255, 255));
        tblVacaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });
        tblVacaciones.getColumnModel().getColumn(0).setMaxWidth(50);
        tblVacaciones.getColumnModel().getColumn(4).setMaxWidth(60);

        jScrollPane2.setViewportView(tblVacaciones);
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

    /** Registra una nueva vacación en la base de datos. */
    private void registrarVacacion() {
        if (!validarCampos()) return;
        Vacacion v = construirVacacion();
        if (vacacionDAO.insertar(v)) {
            JOptionPane.showMessageDialog(this, "Vacación registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la vacación. Verifique las tablas de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualiza el registro de vacación seleccionado. */
    private void actualizarVacacion() {
        if (idVacacionSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una vacación de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        Vacacion v = construirVacacion();
        v.setIdVacacion(idVacacionSeleccionada);
        if (vacacionDAO.actualizar(v)) {
            JOptionPane.showMessageDialog(this, "Vacación actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la vacación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Elimina el registro de vacación seleccionado. */
    private void eliminarVacacion() {
        if (idVacacionSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una vacación de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar este registro de vacaciones?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (vacacionDAO.eliminar(idVacacionSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Vacación eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la vacación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Cuando se hace clic en una fila, carga los datos en el formulario. */
    private void seleccionarFila() {
        int fila = tblVacaciones.getSelectedRow();
        if (fila == -1) return;
        DefaultTableModel modelo = (DefaultTableModel) tblVacaciones.getModel();
        idVacacionSeleccionada = (int) modelo.getValueAt(fila, 0);
        String empleadoStr = (String) modelo.getValueAt(fila, 1);
        Object fechaI = modelo.getValueAt(fila, 2);
        Object fechaF = modelo.getValueAt(fila, 3);
        Object dias = modelo.getValueAt(fila, 4);
        String estado = (String) modelo.getValueAt(fila, 5);
        String obs = (String) modelo.getValueAt(fila, 6);

        // Seleccionar empleado en el combo
        for (int i = 0; i < cboEmpleado.getItemCount(); i++) {
            String item = cboEmpleado.getItemAt(i);
            if (item != null && item.contains(empleadoStr)) {
                cboEmpleado.setSelectedIndex(i);
                break;
            }
        }

        txtFechaInicio.setText(fechaI != null ? fechaI.toString() : "");
        txtFechaFin.setText(fechaF != null ? fechaF.toString() : "");
        txtDias.setText(dias != null ? dias.toString() : "");

        for (int i = 0; i < cboEstado.getItemCount(); i++) {
            if (cboEstado.getItemAt(i).equals(estado)) {
                cboEstado.setSelectedIndex(i);
                break;
            }
        }
        txtObservaciones.setText(obs != null ? obs : "");
    }

    /** Valida los campos obligatorios del formulario. */
    private boolean validarCampos() {
        if (getIdEmpleadoSeleccionado() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFechaInicio.getText().trim().isEmpty() || txtFechaFin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Las fechas de inicio y fin son obligatorias.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fechaI = LocalDate.parse(txtFechaInicio.getText().trim(), fmt);
            LocalDate fechaF = LocalDate.parse(txtFechaFin.getText().trim(), fmt);
            if (fechaF.isBefore(fechaI)) {
                JOptionPane.showMessageDialog(this, "La fecha de fin no puede ser anterior a la de inicio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd (ej: 2025-01-15).", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /** Construye un objeto Vacacion con los datos del formulario. */
    private Vacacion construirVacacion() {
        Vacacion v = new Vacacion();
        v.setIdEmpleado(getIdEmpleadoSeleccionado());
        v.setFechaInicio(Date.valueOf(txtFechaInicio.getText().trim()));
        v.setFechaFin(Date.valueOf(txtFechaFin.getText().trim()));
        String diasStr = txtDias.getText().trim();
        v.setCantidadDias(diasStr.isEmpty() ? 0 : Integer.parseInt(diasStr));
        v.setEstado((String) cboEstado.getSelectedItem());
        v.setObservaciones(txtObservaciones.getText().trim());
        return v;
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
    private javax.swing.JLabel lblDias;
    private javax.swing.JLabel lblEmpleado;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFechaFin;
    private javax.swing.JLabel lblFechaHint;
    private javax.swing.JLabel lblFechaInicio;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTablaTitle;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblVacaciones;
    private javax.swing.JTextField txtDias;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
