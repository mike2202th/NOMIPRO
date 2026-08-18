
package Main;

import Controlador.ControladorLogin;
import Modelos.DepartamentoItem;
import Modelos.Empresa.Empresa;
import Modelos.Empresa.EmpresaDAO;
import Vistas.Login1;
import Vistas.AdministrarUsuarios;
import Vistas.AgregarPuestosVista;
import Vistas.ConsultarDetallesNómina;
import Vistas.RegistroEmpleados;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.mariadb.jdbc.Connection;

public class Menu extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu.class.getName());
    private Map<String, Integer> periodosMap = new HashMap<>();
    
    /**
     * Creates new form Menu
     */
    public Menu(String nombreUsuario) {
 
        initComponents();
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
    jpnMain.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
        @Override
        protected int calculateTabAreaHeight(
                int tabPlacement,
                int horizRunCount,
                int maxTabHeight) {
            return 0;
        }
    });
       
        setTitle("NOMIPRO");
        
        txtBienvenidoUser.setText(nombreUsuario);
         btnEliminarPuestos.setEnabled(false);
        
        visibilidadMenu();
        cargarDatosEmpresa();
        
        configurarSeleccionDepartamento();
        
        cargarLogo();
        
        configurarBuscadorDepartamentos();
        buscarDepartamentos();
        
        cargarDepartamentosCombo();

        cargarPuestos();
        cmbDepartamentoPuesto.addActionListener(e -> {
            cargarPuestos();
        });
        configurarSeleccionPuesto();
        
        cargarFiltroDepartamentos();
        cargarFiltroCargos();
        cargarFiltroEstados();

        configurarFiltrosEmpleados();
        configurarBuscadorEmpleado();
        configurarSeleccionEmpleado();

        cargarEmpleados();
        cargarEstadisticasEmpleados();
        
        
        cargarPeriodos();
        cargarNominaPorPeriodo();
        

        
        visibilidadPaneles();
        
    }

    private Menu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    private void visibilidadMenu(){
    pnlEmpresa.setVisible(false);
    pnlRRHH.setVisible(false);
    pnlNomina.setVisible(false);
    pnlSeguridad.setVisible(false);
    pnlPago.setVisible(false);
    pnlAsistencia.setVisible(false);
    pnlReportes.setVisible(false);
    pnlPago.setVisible(false);
    pnlAdministracion.setVisible(false);
    }
    
        
    private void cargarDatosEmpresa() {

    EmpresaDAO dao = new EmpresaDAO();

    Empresa empresa = dao.obtenerEmpresa();

    if (empresa != null) {

        txtRNC.setText(
            empresa.getRnc()
        );

        txtRazonSocial.setText(
            empresa.getRazonSocial()
        );

        txtNombreComercial.setText(
            empresa.getNombreComercial()
        );

        txtRepresentanteLegal.setText(
            empresa.getRepresentanteLegal()
        );

        txtTelefono.setText(
            empresa.getTelefono()
        );

        txtEmail.setText(
            empresa.getEmail()
        );

        txtDireccion.setText(
            empresa.getDireccion()
        );

        if (empresa.getFechaRegistro() != null) {

            txtFechaRegistro.setText(
                empresa.getFechaRegistro().toString()
            );
        }

        cmbEstado.setSelectedItem(
            empresa.getEstado()
        );

        txtDescripcion.setText(
            empresa.getDescripcion()
        );
    }
}
    
    private void cargarLogo() {

    EmpresaDAO empresaDAO = new EmpresaDAO();
    Empresa empresa = empresaDAO.obtenerEmpresa();

    if (empresa == null) {
        System.out.println("No se encontró la empresa.");
        return;
    }

    String nombreLogo = empresa.getLogo();

    System.out.println("Logo obtenido de BD: [" + nombreLogo + "]");

    if (nombreLogo == null || nombreLogo.isBlank()) {
        System.out.println("El logo está vacío.");
        return;
    }

    URL url = getClass().getResource("/imagenes/" + nombreLogo);

    System.out.println("URL encontrada: " + url);

    if (url == null) {
        System.out.println("No se encontró la imagen.");
        return;
    }

    ImageIcon iconoOriginal = new ImageIcon(url);

    int ancho = txtLogo.getWidth();
    int alto = txtLogo.getHeight();

    System.out.println("Tamaño del JLabel: " + ancho + " x " + alto);

    if (ancho <= 0 || alto <= 0) {
        System.out.println("El JLabel todavía no tiene dimensiones.");
        return;
    }

    Image imagen = iconoOriginal.getImage();

    Image imagenEscalada = imagen.getScaledInstance(
            ancho,
            alto,
            Image.SCALE_SMOOTH
    );

    txtLogo.setIcon(new ImageIcon(imagenEscalada));
}

    private void visibilidadPaneles(){
        jpPuestos.setVisible(false);
        jpDetalleNominaEmpleado.setVisible(false);
    }

    private void cargarDepartamentos() {

    DefaultTableModel modelo = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    modelo.addColumn("ID");
    modelo.addColumn("Departamentos");
    modelo.addColumn("Funciones");
    modelo.addColumn("Estado");

    String sql = "SELECT id_departamento, nombre_departamento, descripcion, estado "
               + "FROM DEPARTAMENTO "
               + "WHERE estado = 'Activo' "
               + "ORDER BY id_departamento";
    
    conexionMariaDB conectar = new conexionMariaDB();
    
    try (java.sql.Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {

            Object[] fila = {
                rs.getInt("id_departamento"),
                rs.getString("nombre_departamento"),
                rs.getString("descripcion"),
                rs.getString("estado")
            };

            modelo.addRow(fila);
        }

      jtableDepartamentos.setModel(modelo);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Error al cargar los departamentos: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    }
    
    private void agregarDepartamento() {

    int idEmpresa = 1;
    String nombre = txtNombreDepartamento.getText().trim();
    String funciones = txtDepartamentoFunciones.getText().trim();
    String estado = cmbEstadoDepartamento.getSelectedItem().toString();

    if (nombre.isEmpty() || funciones.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Debe completar todos los campos."
        );
        return;
    }

    String sql = "INSERT INTO DEPARTAMENTO (id_empresa, nombre_departamento, descripcion, estado) "
               + "VALUES (?, ?, ?, ?)";

        conexionMariaDB conectar = new conexionMariaDB();
    
    try (java.sql.Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idEmpresa);
        ps.setString(2, nombre);
        ps.setString(3, funciones);
        ps.setString(4, estado);

        ps.executeUpdate();

        JOptionPane.showMessageDialog(
            this,
            "Departamento agregado correctamente."
        );

        // Limpiar campos
        txtNombreDepartamento.setText("");
        txtDepartamentoFunciones.setText("");
        cmbEstadoDepartamento.setSelectedItem("Activo");

        // Volver a cargar la tabla
        cargarDepartamentos();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Error al agregar el departamento: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void buscarDepartamentos() {

    String buscar = txtBuscarDepartamento.getText().trim();

    DefaultTableModel modelo = new DefaultTableModel();

    modelo.addColumn("ID");
    modelo.addColumn("Departamentos");
    modelo.addColumn("Funciones");
    modelo.addColumn("Estado");

    String sql = "SELECT id_departamento, nombre_departamento, descripcion, estado "
               + "FROM DEPARTAMENTO "
               + "WHERE estado = 'Activo' "
               + "AND nombre_departamento LIKE ? "
               + "ORDER BY id_departamento";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        String filtro = "%" + buscar + "%";

        ps.setString(1, filtro);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            modelo.addRow(new Object[]{
                rs.getInt("id_departamento"),
                rs.getString("nombre_departamento"),
                rs.getString("descripcion"),
                rs.getString("estado")
            });
        }

        jtableDepartamentos.setModel(modelo);

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al buscar departamentos: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void configurarBuscadorDepartamentos() {

    txtBuscarDepartamento.getDocument().addDocumentListener(
        new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarDepartamentos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarDepartamentos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarDepartamentos();
            }
        }
    );
}
 
    private int idDepartamentoEditar = -1;
    
    private void seleccionarDepartamento() {

    int fila = jtableDepartamentos.getSelectedRow();

    if (fila == -1) {
        return;
    }

    int filaModelo = jtableDepartamentos.convertRowIndexToModel(fila);

    idDepartamentoEditar = Integer.parseInt(
        jtableDepartamentos.getModel()
            .getValueAt(filaModelo, 0)
            .toString()
    );

    String nombre = jtableDepartamentos.getModel()
            .getValueAt(filaModelo, 1)
            .toString();

    String funciones = jtableDepartamentos.getModel()
            .getValueAt(filaModelo, 2)
            .toString();

    String estado = jtableDepartamentos.getModel()
            .getValueAt(filaModelo, 3)
            .toString();

    txtNombreDepartamento.setText(nombre);
    txtDepartamentoFunciones.setText(funciones);
    cmbEstadoDepartamento.setSelectedItem(estado);
}
    
    private void configurarSeleccionDepartamento() {

    jtableDepartamentos.getSelectionModel().addListSelectionListener(e -> {

        if (!e.getValueIsAdjusting()) {
            seleccionarDepartamento();
        }

    });
}
    
    private void actualizarDepartamento() {

    if (idDepartamentoEditar == -1) {

        JOptionPane.showMessageDialog(
            this,
            "Seleccione un departamento de la tabla para editar."
        );

        return;
    }

    String nombre = txtNombreDepartamento.getText().trim();
    String funciones = txtDepartamentoFunciones.getText().trim();
    String estado = cmbEstadoDepartamento.getSelectedItem().toString();

    if (nombre.isEmpty() || funciones.isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe completar todos los campos."
        );

        return;
    }

    String sql = "UPDATE DEPARTAMENTO "
               + "SET nombre_departamento = ?, "
               + "descripcion = ?, "
               + "estado = ? "
               + "WHERE id_departamento = ?";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ps.setString(2, funciones);
        ps.setString(3, estado);
        ps.setInt(4, idDepartamentoEditar);

        int filasActualizadas = ps.executeUpdate();

        if (filasActualizadas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Departamento actualizado correctamente."
            );

            limpiarCamposDepartamento();

            idDepartamentoEditar = -1;

            cargarDepartamentos();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el departamento."
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al actualizar el departamento: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void eliminarDepartamento() {

    if (idDepartamentoEditar == -1) {

        JOptionPane.showMessageDialog(
            this,
            "Seleccione un departamento de la tabla."
        );

        return;
    }

    int confirmar = JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro de que deseas eliminar este departamento?",
        "Confirmar acción",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirmar != JOptionPane.YES_OPTION) {
        return;
    }

    String sql = "UPDATE DEPARTAMENTO "
               + "SET estado = 'Inactivo' "
               + "WHERE id_departamento = ?";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idDepartamentoEditar);

        int filasActualizadas = ps.executeUpdate();

        if (filasActualizadas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Departamento puesto eliminado correctamente."
            );

            // Limpiar los campos
            limpiarCamposDepartamento();

            // Reiniciar el ID seleccionado
            idDepartamentoEditar = -1;

            // Actualizar la tabla
            cargarDepartamentos();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "No se pudo cambiar el estado del departamento.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al eliminar el departamento: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void limpiarCamposDepartamento() {

    txtNombreDepartamento.setText("");
    txtDepartamentoFunciones.setText("");

    if (cmbEstadoDepartamento.getItemCount() > 0) {
        cmbEstadoDepartamento.setSelectedItem("Activo");
    }

    jtableDepartamentos.clearSelection();
}
    
    private Map<String, Integer> departamentosMap = new HashMap<>();
    
    private void cargarDepartamentosCombo() {

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

            int idDepartamento = rs.getInt("id_departamento");
            String nombreDepartamento =
                    rs.getString("nombre_departamento");

            cmbDepartamentoPuesto.addItem(nombreDepartamento);

            departamentosMap.put(
                nombreDepartamento,
                idDepartamento
            );
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
    
    public void cargarPuestos() {

    DefaultTableModel modelo = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    modelo.addColumn("ID");
    modelo.addColumn("Puesto");
    modelo.addColumn("Descripción");
    modelo.addColumn("Salario Mínimo");
    modelo.addColumn("Salario Máximo");
    modelo.addColumn("Estado");

    String departamentoSeleccionado =
            (String) cmbDepartamentoPuesto.getSelectedItem();

    if (departamentoSeleccionado == null ||
        departamentoSeleccionado.isEmpty()) {

        jtablePuestos.setModel(modelo);
        return;
    }

    Integer idDepartamento =
            departamentosMap.get(departamentoSeleccionado);

    if (idDepartamento == null) {
        jtablePuestos.setModel(modelo);
        return;
    }

    String sql = "SELECT id_puesto, nombre_puesto, descripcion, "
               + "salario_minimo, salario_maximo, estado "
               + "FROM PUESTOS "
               + "WHERE id_departamento = ? "
               + "AND estado = 'ACTIVO' "
               + "ORDER BY id_puesto";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idDepartamento);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Object[] fila = {
                    rs.getInt("id_puesto"),
                    rs.getString("nombre_puesto"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("salario_minimo"),
                    rs.getBigDecimal("salario_maximo"),
                    rs.getString("estado")
                };

                modelo.addRow(fila);
            }
        }

        jtablePuestos.setModel(modelo);

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar los puestos: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void configurarSeleccionPuesto() {

    jtablePuestos.getSelectionModel().addListSelectionListener(e -> {

        if (!e.getValueIsAdjusting()) {

            boolean seleccionado =
                    jtablePuestos.getSelectedRow() != -1;

            btnEliminarPuestos.setEnabled(seleccionado);
        }
    });
}
    
    private void eliminarPuesto() {

    int fila = jtablePuestos.getSelectedRow();

    if (fila == -1) {

        JOptionPane.showMessageDialog(
            this,
            "Seleccione un puesto de la tabla.",
            "Advertencia",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    int filaModelo = jtablePuestos.convertRowIndexToModel(fila);

    int idPuesto = Integer.parseInt(
        jtablePuestos.getModel()
            .getValueAt(filaModelo, 0)
            .toString()
    );

    String nombrePuesto =
        jtablePuestos.getModel()
            .getValueAt(filaModelo, 1)
            .toString();

    int confirmar = JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro de poner como inactivo el puesto:\n\n"
        + nombrePuesto + "?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (confirmar != JOptionPane.YES_OPTION) {
        return;
    }

    String sql = "UPDATE PUESTOS "
               + "SET estado = 'INACTIVO' "
               + "WHERE id_puesto = ?";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idPuesto);

        int filasActualizadas = ps.executeUpdate();

        if (filasActualizadas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "El puesto fue puesto como Inactivo correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Actualizar la tabla
            cargarPuestos();

            // Deshabilitar botón
            btnEliminarPuestos.setEnabled(false);

        } else {

            JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el puesto.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al eliminar el puesto: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    public void cargarEmpleados() {

    DefaultTableModel modelo = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    modelo.addColumn("ID");
    modelo.addColumn("Nombres");
    modelo.addColumn("Apellidos");
    modelo.addColumn("Cédula");
    modelo.addColumn("Departamento");
    modelo.addColumn("Cargo");
    modelo.addColumn("Contrato");
    modelo.addColumn("Teléfono");
    modelo.addColumn("Email");
    modelo.addColumn("Salario Base");
    modelo.addColumn("Banco");
    modelo.addColumn("Cuenta Bancaria");
    modelo.addColumn("Estado");

    // =========================
    // OBTENER FILTROS
    // =========================

    String departamento =
            cmbFiltroDepartamento.getSelectedItem() != null
            ? cmbFiltroDepartamento.getSelectedItem().toString()
            : "Todos";

    String cargo =
            cmbFiltroCargo.getSelectedItem() != null
            ? cmbFiltroCargo.getSelectedItem().toString()
            : "Todos";

    String estado =
            cmbFiltroEstado.getSelectedItem() != null
            ? cmbFiltroEstado.getSelectedItem().toString()
            : "Todos";

    // Buscador
    String buscar = txtBuscarEmpleado.getText().trim();


    // =========================
    // CONSULTA
    // =========================

    StringBuilder sql = new StringBuilder();

    sql.append(
        "SELECT "
      + "e.id_empleado, "
      + "e.nombres, "
      + "e.apellidos, "
      + "e.cedula, "
      + "d.nombre_departamento, "
      + "p.nombre_puesto, "
      + "tc.nombre AS nombre_tipo_contrato, "
      + "e.telefono, "
      + "e.email, "
      + "e.salario_base, "
      + "e.banco, "
      + "e.cuenta_bancaria, "
      + "e.estado "
      + "FROM EMPLEADO e "
      + "INNER JOIN DEPARTAMENTO d "
      + "ON e.id_departamento = d.id_departamento "
      + "INNER JOIN PUESTOS p "
      + "ON e.id_puesto = p.id_puesto "
      + "INNER JOIN TIPOS_CONTRATO tc "
      + "ON e.id_tipo_contrato = tc.id_tipo_contrato "
      + "WHERE 1=1 "
    );


    // =========================
    // BUSCADOR POR NOMBRE
    // =========================

    if (!buscar.isEmpty()) {

        sql.append(
            "AND (e.nombres LIKE ? "
          + "OR e.apellidos LIKE ?) "
        );
    }


    // =========================
    // FILTRO DEPARTAMENTO
    // =========================

    if (!departamento.equals("Todos")) {

        sql.append(
            "AND d.nombre_departamento = ? "
        );
    }


    // =========================
    // FILTRO CARGO
    // =========================

    if (!cargo.equals("Todos")) {

        sql.append(
            "AND p.nombre_puesto = ? "
        );
    }


    // =========================
    // FILTRO ESTADO
    // =========================

    if (!estado.equals("Todos")) {

        sql.append(
            "AND e.estado = ? "
        );
    }


    sql.append(
        "ORDER BY e.id_empleado"
    );


    // =========================
    // CONEXIÓN
    // =========================

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps =
             cn.prepareStatement(sql.toString())) {

        int parametro = 1;


        // =========================
        // PARÁMETROS DEL BUSCADOR
        // =========================

        if (!buscar.isEmpty()) {

            String filtro = "%" + buscar + "%";

            ps.setString(parametro++, filtro);
            ps.setString(parametro++, filtro);
        }


        // =========================
        // DEPARTAMENTO
        // =========================

        if (!departamento.equals("Todos")) {

            ps.setString(
                parametro++,
                departamento
            );
        }


        // =========================
        // CARGO
        // =========================

        if (!cargo.equals("Todos")) {

            ps.setString(
                parametro++,
                cargo
            );
        }


        // =========================
        // ESTADO
        // =========================

        if (!estado.equals("Todos")) {

            ps.setString(
                parametro++,
                estado
            );
        }


        // =========================
        // EJECUTAR
        // =========================

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                modelo.addRow(new Object[]{

                    rs.getInt("id_empleado"),

                    rs.getString("nombres"),

                    rs.getString("apellidos"),

                    rs.getString("cedula"),

                    rs.getString("nombre_departamento"),

                    rs.getString("nombre_puesto"),

                    rs.getString("nombre_tipo_contrato"),

                    rs.getString("telefono"),

                    rs.getString("email"),

                    rs.getBigDecimal("salario_base"),

                    rs.getString("banco"),

                    rs.getString("cuenta_bancaria"),

                    rs.getString("estado")
                });
            }
        }

        jtableEmpleados.setModel(modelo);

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar los empleados: "
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void cargarFiltroDepartamentos() {

    cmbFiltroDepartamento.removeAllItems();

    cmbFiltroDepartamento.addItem("Todos");

    String sql =
        "SELECT nombre_departamento "
      + "FROM DEPARTAMENTO "
      + "ORDER BY nombre_departamento";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            cmbFiltroDepartamento.addItem(
                rs.getString("nombre_departamento")
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar departamentos: "
            + e.getMessage()
        );
    }
}
    
    private void cargarFiltroCargos() {

    cmbFiltroCargo.removeAllItems();

    cmbFiltroCargo.addItem("Todos");

    String sql =
        "SELECT nombre_puesto "
      + "FROM PUESTOS "
      + "WHERE estado = 'ACTIVO' "
      + "ORDER BY nombre_puesto";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            cmbFiltroCargo.addItem(
                rs.getString("nombre_puesto")
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar cargos: "
            + e.getMessage()
        );
    }
}
    
    private void cargarFiltroEstados() {

    cmbFiltroEstado.removeAllItems();

    cmbFiltroEstado.addItem("Todos");
    cmbFiltroEstado.addItem("ACTIVO");
    cmbFiltroEstado.addItem("INACTIVO");
    cmbFiltroEstado.addItem("SUSPENDIDO");
    cmbFiltroEstado.addItem("DESVINCULADO");
}
    
    private void configurarFiltrosEmpleados() {

    cmbFiltroDepartamento.addActionListener(e -> {
        cargarEmpleados();
    });

    cmbFiltroCargo.addActionListener(e -> {
        cargarEmpleados();
    });

    cmbFiltroEstado.addActionListener(e -> {
        cargarEmpleados();
    });
}
    
    private void configurarBuscadorEmpleado() {

    txtBuscarEmpleado.getDocument().addDocumentListener(
        new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarEmpleados();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarEmpleados();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarEmpleados();
            }
        }
    );
}
    
    private void cargarEstadisticasEmpleados() {

    String sql =
        "SELECT "
      + "COUNT(*) AS total, "
      + "SUM(CASE WHEN estado = 'ACTIVO' THEN 1 ELSE 0 END) AS activos, "
      + "SUM(CASE WHEN estado = 'INACTIVO' THEN 1 ELSE 0 END) AS inactivos "
      + "FROM EMPLEADO";

    conexionMariaDB conectar = new conexionMariaDB();

    try (java.sql.Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {

            int total = rs.getInt("total");
            int activos = rs.getInt("activos");
            int inactivos = rs.getInt("inactivos");

            txtTotalEmpleados.setText(String.valueOf(total));
            txtEmpleadosActivos.setText(String.valueOf(activos));
            txtEmpleadosInactivos.setText(String.valueOf(inactivos));
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar las estadísticas: "
            + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void configurarSeleccionEmpleado() {

    jtableEmpleados.getSelectionModel().addListSelectionListener(e -> {

        if (!e.getValueIsAdjusting()) {

            boolean seleccionado =
                jtableEmpleados.getSelectedRow() != -1;

            btnEliminarEmpleado.setEnabled(seleccionado);
        }
    });
}
    
    private void eliminarEmpleado() {

    int fila = jtableEmpleados.getSelectedRow();

    if (fila == -1) {

        JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar un empleado.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    int filaModelo =
        jtableEmpleados.convertRowIndexToModel(fila);

    int idEmpleado =
        Integer.parseInt(
            jtableEmpleados
                .getModel()
                .getValueAt(filaModelo, 0)
                .toString()
        );

    String nombre =
        jtableEmpleados
            .getModel()
            .getValueAt(filaModelo, 1)
            .toString();

    String apellido =
        jtableEmpleados
            .getModel()
            .getValueAt(filaModelo, 2)
            .toString();


    int respuesta = JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro de poner como INACTIVO al empleado?\n\n"
      + "Empleado: "
      + nombre
      + " "
      + apellido,
        "Confirmar operación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );


    if (respuesta != JOptionPane.YES_OPTION) {
        return;
    }


    String sql =
        "UPDATE EMPLEADO "
      + "SET estado = 'INACTIVO', "
      + "fecha_salida = CURDATE() "
      + "WHERE id_empleado = ?";


    conexionMariaDB conectar =
        new conexionMariaDB();


    try (java.sql.Connection cn =
            conectar.conectar();
         PreparedStatement ps =
            cn.prepareStatement(sql)) {


        ps.setInt(1, idEmpleado);

        int filas =
            ps.executeUpdate();


        if (filas > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Empleado marcado como INACTIVO correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );


            // Actualizar tabla
            cargarEmpleados();

            // Actualizar estadísticas
            cargarEstadisticasEmpleados();


            // Deshabilitar botón
            btnEliminarEmpleado.setEnabled(false);
        }


    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al desactivar el empleado:\n"
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void cargarNominaPorPeriodo() {

    DefaultTableModel modelo = new DefaultTableModel() {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    modelo.addColumn("ID Empleado");
    modelo.addColumn("Empleado");
    modelo.addColumn("Salario");
    modelo.addColumn("Bonificaciones");
    modelo.addColumn("Deducciones");
    modelo.addColumn("Neto");
    modelo.addColumn("Estado");

    String periodoSeleccionado =
        (String) cmbPeriodoNomina.getSelectedItem();

    if (periodoSeleccionado == null ||
        periodoSeleccionado.isEmpty()) {

        jtableNomina.setModel(modelo);
        return;
    }

    Integer idPeriodo =
        periodosMap.get(periodoSeleccionado);

    if (idPeriodo == null) {

        jtableNomina.setModel(modelo);
        return;
    }

    String sql =
        "SELECT "
      + "    e.id_empleado, "
      + "    CONCAT(e.nombres, ' ', e.apellidos) AS empleado, "
      + "    dn.salario_base, "
      + "    dn.total_bonificaciones, "
      + "    dn.total_deducciones, "
      + "    dn.salario_neto, "
      + "    dn.estado "
      + "FROM DETALLE_NOMINA dn "
      + "INNER JOIN EMPLEADO e "
      + "    ON dn.id_empleado = e.id_empleado "
      + "INNER JOIN NOMINAS n "
      + "    ON dn.id_nomina = n.id_nomina "
      + "WHERE n.id_periodo = ? "
      + "ORDER BY e.id_empleado";

    conexionMariaDB conectar =
        new conexionMariaDB();

    try (java.sql.Connection cn =
            conectar.conectar();
         PreparedStatement ps =
            cn.prepareStatement(sql)) {

        ps.setInt(1, idPeriodo);

        try (ResultSet rs =
                ps.executeQuery()) {

            while (rs.next()) {

                Object[] fila = {

                    rs.getInt("id_empleado"),

                    rs.getString("empleado"),

                    rs.getBigDecimal("salario_base"),

                    rs.getBigDecimal(
                        "total_bonificaciones"
                    ),

                    rs.getBigDecimal(
                        "total_deducciones"
                    ),

                    rs.getBigDecimal(
                        "salario_neto"
                    ),

                    rs.getString("estado")
                };

                modelo.addRow(fila);
            }
        }

        jtableNomina.setModel(modelo);

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar la nómina:\n"
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void cargarPeriodos() {

    cmbPeriodoNomina.removeAllItems();

    periodosMap.clear();

    String sql =
        "SELECT id_periodo, nombre_periodo "
      + "FROM PERIODOS_NOMINA "
      + "ORDER BY fecha_inicio DESC";

    conexionMariaDB conectar =
            new conexionMariaDB();

    try (
        Connection cn = (Connection) conectar.conectar();
        PreparedStatement ps =
                cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {

            int id =
                    rs.getInt("id_periodo");

            String nombre =
                    rs.getString("nombre_periodo");

            cmbPeriodoNomina.addItem(nombre);

            periodosMap.put(
                nombre,
                id
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar períodos: "
          + e.getMessage()
        );
    }
}
    
    private void cargarDetalleNomina() {

    // ==========================================
    // VERIFICAR FILA SELECCIONADA
    // ==========================================

    int fila = jtableNomina.getSelectedRow();

    if (fila == -1) {

        JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar un empleado de la tabla.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    // Convertir por si la tabla tiene ordenamiento/filtros
    int filaModelo =
            jtableNomina.convertRowIndexToModel(fila);


    // ==========================================
    // OBTENER ID DEL EMPLEADO
    // ==========================================

    Object valorID =
            jtableNomina.getModel()
                    .getValueAt(filaModelo, 0);

    if (valorID == null) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo obtener el ID del empleado.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    int idEmpleado =
            Integer.parseInt(valorID.toString());


    // ==========================================
    // VERIFICAR PERÍODO
    // ==========================================

    String periodoSeleccionado =
            (String) cmbPeriodoNomina.getSelectedItem();

    if (periodoSeleccionado == null ||
        periodoSeleccionado.trim().isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar un período.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }


    Integer idPeriodo =
            periodosMap.get(periodoSeleccionado);

    if (idPeriodo == null) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo obtener el ID del período.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }


    // ==========================================
    // CONSULTA
    // ==========================================

    String sql =
        "SELECT "
      + "CONCAT(e.nombres, ' ', e.apellidos) AS empleado, "
      + "dn.salario_base, "
      + "dn.total_bonificaciones, "
      + "dn.total_deducciones, "
      + "dn.salario_neto, "

      + "COALESCE(SUM(CASE "
      + "WHEN UPPER(td.nombre) = 'AFP' "
      + "THEN de.monto ELSE 0 END), 0) AS afp, "

      + "COALESCE(SUM(CASE "
      + "WHEN UPPER(td.nombre) = 'ARS' "
      + "THEN de.monto ELSE 0 END), 0) AS ars, "

      + "COALESCE(SUM(CASE "
      + "WHEN UPPER(td.nombre) = 'ISR' "
      + "THEN de.monto ELSE 0 END), 0) AS isr, "

      + "COALESCE(SUM(CASE "
      + "WHEN UPPER(td.nombre) NOT IN "
      + "('AFP','ARS','ISR') "
      + "THEN de.monto ELSE 0 END), 0) "
      + "AS otras_deducciones "

      + "FROM DETALLE_NOMINA dn "

      + "INNER JOIN EMPLEADO e "
      + "ON dn.id_empleado = e.id_empleado "

      + "INNER JOIN NOMINAS n "
      + "ON dn.id_nomina = n.id_nomina "

      + "INNER JOIN PERIODOS_NOMINA pn "
      + "ON n.id_periodo = pn.id_periodo "

      + "LEFT JOIN DEDUCCIONES_EMPLEADO de "
      + "ON de.id_detalle_nomina = dn.id_detalle "
      + "AND de.estado = 'APLICADA' "

      + "LEFT JOIN TIPOS_DEDUCCION td "
      + "ON de.id_tipo_deduccion = td.id_tipo_deduccion "

      + "WHERE dn.id_empleado = ? "
      + "AND pn.id_periodo = ? "

      + "GROUP BY "
      + "e.nombres, "
      + "e.apellidos, "
      + "dn.salario_base, "
      + "dn.total_bonificaciones, "
      + "dn.total_deducciones, "
      + "dn.salario_neto";


    conexionMariaDB conectar =
            new conexionMariaDB();

    try (
        Connection cn = (Connection) conectar.conectar();
        PreparedStatement ps =
                cn.prepareStatement(sql)
    ) {

        ps.setInt(1, idEmpleado);
        ps.setInt(2, idPeriodo);

        ResultSet rs = ps.executeQuery();


        // ======================================
        // CARGAR DATOS
        // ======================================

        if (rs.next()) {

            lblNombreEmpleado.setText(
                rs.getString("empleado")
            );

            lblSalarioBase.setText(
                formatoDinero(
                    rs.getBigDecimal("salario_base")
                )
            );

            lblBonificaciones.setText(
                formatoDinero(
                    rs.getBigDecimal(
                        "total_bonificaciones"
                    )
                )
            );

            lblAFP.setText(
                formatoDinero(
                    rs.getBigDecimal("afp")
                )
            );

            lblARS.setText(
                formatoDinero(
                    rs.getBigDecimal("ars")
                )
            );

            lblISR.setText(
                formatoDinero(
                    rs.getBigDecimal("isr")
                )
            );

            lblOtrasDeducciones.setText(
                formatoDinero(
                    rs.getBigDecimal(
                        "otras_deducciones"
                    )
                )
            );

            lblTotalDescuentos.setText(
                formatoDinero(
                    rs.getBigDecimal(
                        "total_deducciones"
                    )
                )
            );

            lblSalarioNeto.setText(
                formatoDinero(
                    rs.getBigDecimal("salario_neto")
                )
            );


            // ==================================
            // MOSTRAR PANEL
            // ==================================

            jpDetalleNominaEmpleado.setVisible(true);

            jpDetalleNominaEmpleado.revalidate();
            jpDetalleNominaEmpleado.repaint();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "El empleado seleccionado "
              + "no tiene una nómina registrada "
              + "para el período seleccionado.",
                "Sin nómina",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al cargar el detalle:\n"
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }
}
    
    private void exportarNominaPDF() {

    String periodoSeleccionado =
            (String) cmbPeriodoNomina.getSelectedItem();

    if (periodoSeleccionado == null ||
        periodoSeleccionado.trim().isEmpty()) {

        JOptionPane.showMessageDialog(
            this,
            "Debe seleccionar un período de nómina.",
            "Validación",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    Integer idPeriodo =
            periodosMap.get(periodoSeleccionado);

    if (idPeriodo == null) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo identificar el período seleccionado.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    String rutaReporte =
            "src/reportes/DetalleNomina.jrxml";

    try {

        // Compilar el reporte
        JasperReport reporte =
                JasperCompileManager.compileReport(
                    rutaReporte
                );

        // Parámetros
        Map<String, Object> parametros =
                new HashMap<>();

        parametros.put(
            "ID_PERIODO",
            idPeriodo
        );

        parametros.put(
            "NOMBRE_PERIODO",
            periodoSeleccionado
        );

        // Conexión
        conexionMariaDB conectar = new conexionMariaDB();

        Connection cn = (Connection) conectar.conectar();

        // Llenar reporte
        JasperPrint impresion =
                JasperFillManager.fillReport(
                    reporte,
                    parametros,
                    cn
                );

        // Exportar PDF
        JFileChooser selector =
                new JFileChooser();

        selector.setDialogTitle(
            "Guardar reporte de nómina"
        );

        selector.setSelectedFile(
            new File(
                "Nomina_" +
                periodoSeleccionado +
                ".pdf"
            )
        );

        int resultado =
                selector.showSaveDialog(this);

        if (resultado ==
            JFileChooser.APPROVE_OPTION) {

            String ruta =
                    selector.getSelectedFile()
                            .getAbsolutePath();

            JasperExportManager.exportReportToPdfFile(
                impresion,
                ruta
            );

            JOptionPane.showMessageDialog(
                this,
                "PDF exportado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

        cn.close();

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
            this,
            "Error al exportar el PDF:\n"
          + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }
}
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel23 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel22 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        pnlEmpresa = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblEMpresa = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        pnlRRHH = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        lblRRHH = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        pnlReportes = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        lblReportes = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        pnlNomina = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblNomina = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lblAdministraciòn = new javax.swing.JLabel();
        pnlAdministracion = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        pnlAsistencia = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblAsistencia = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        pnlPago = new javax.swing.JPanel();
        lblMenuPago = new javax.swing.JLabel();
        lblPago = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel42 = new javax.swing.JPanel();
        lblSeguridad = new javax.swing.JLabel();
        pnlSeguridad = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jpnMain = new javax.swing.JTabbedPane();
        jpnEmpresa = new javax.swing.JPanel();
        jPanel44 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel45 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jPanel47 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        txtLogo = new javax.swing.JLabel();
        btnCambiarLogoEmpresa = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txtRNC = new javax.swing.JTextField();
        txtRazonSocial = new javax.swing.JTextField();
        txtNombreComercial = new javax.swing.JTextField();
        txtRepresentanteLegal = new javax.swing.JTextField();
        jPanel48 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JFormattedTextField();
        txtEmail = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtFechaRegistro = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jpnDeps = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtableDepartamentos = new javax.swing.JTable();
        jpPuestos = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtablePuestos = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        cmbDepartamentoPuesto = new javax.swing.JComboBox<>();
        jButton10 = new javax.swing.JButton();
        btnEliminarPuestos = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnPuestos = new javax.swing.JToggleButton();
        jLabel61 = new javax.swing.JLabel();
        txtBuscarDepartamento = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel64 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNombreDepartamento = new javax.swing.JTextField();
        cmbEstadoDepartamento = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtDepartamentoFunciones = new javax.swing.JTextField();
        jpnEmpleados = new javax.swing.JPanel();
        jPanel50 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jPanel51 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        txtBuscarEmpleado = new javax.swing.JTextField();
        cmbFiltroDepartamento = new javax.swing.JComboBox<>();
        jLabel69 = new javax.swing.JLabel();
        cmbFiltroCargo = new javax.swing.JComboBox<>();
        jLabel70 = new javax.swing.JLabel();
        cmbFiltroEstado = new javax.swing.JComboBox<>();
        jLabel71 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jtableEmpleados = new javax.swing.JTable();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jPanel52 = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        btnEliminarEmpleado = new javax.swing.JButton();
        txtTotalEmpleados = new javax.swing.JLabel();
        txtEmpleadosActivos = new javax.swing.JLabel();
        txtEmpleadosInactivos = new javax.swing.JLabel();
        jpnContratos = new javax.swing.JPanel();
        jpnHistorial = new javax.swing.JPanel();
        jPanel53 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jPanel54 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        jPanel55 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jPanel56 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel85 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel57 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jpnNomina = new javax.swing.JPanel();
        jPanel59 = new javax.swing.JPanel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        cmbPeriodoNomina = new javax.swing.JComboBox<>();
        jLabel98 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jLabel102 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jLabel103 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jLabel104 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jtableNomina = new javax.swing.JTable();
        jLabel105 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jpDetalleNominaEmpleado = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        lblNombreEmpleado = new javax.swing.JLabel();
        jPanel61 = new javax.swing.JPanel();
        jLabel109 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        lblSalarioBase = new javax.swing.JTextField();
        lblBonificaciones = new javax.swing.JTextField();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        lblISR = new javax.swing.JTextField();
        lblARS = new javax.swing.JTextField();
        lblOtrasDeducciones = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel113 = new javax.swing.JLabel();
        lblTotalDescuentos = new javax.swing.JTextField();
        jLabel114 = new javax.swing.JLabel();
        lblSalarioNeto = new javax.swing.JTextField();
        lblAFP = new javax.swing.JTextField();
        jLabel115 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jButton26 = new javax.swing.JButton();
        jLabel116 = new javax.swing.JLabel();
        jpnGenNomina = new javax.swing.JPanel();
        jPanel62 = new javax.swing.JPanel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jPanel63 = new javax.swing.JPanel();
        jLabel132 = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jLabel138 = new javax.swing.JLabel();
        jPanel68 = new javax.swing.JPanel();
        jLabel141 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jLabel135 = new javax.swing.JLabel();
        jTextField43 = new javax.swing.JTextField();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jPanel64 = new javax.swing.JPanel();
        jLabel122 = new javax.swing.JLabel();
        jPanel65 = new javax.swing.JPanel();
        jLabel123 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jLabel124 = new javax.swing.JLabel();
        jTextField38 = new javax.swing.JTextField();
        jLabel127 = new javax.swing.JLabel();
        jTextField40 = new javax.swing.JTextField();
        jTextField39 = new javax.swing.JTextField();
        jLabel125 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel126 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jPanel66 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel129 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jLabel130 = new javax.swing.JLabel();
        jComboBox13 = new javax.swing.JComboBox<>();
        jLabel131 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jpnConsultarNom = new javax.swing.JPanel();
        jPanel69 = new javax.swing.JPanel();
        jLabel136 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        jLabel139 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jComboBox14 = new javax.swing.JComboBox<>();
        jLabel142 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jComboBox15 = new javax.swing.JComboBox<>();
        jButton27 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jPanel73 = new javax.swing.JPanel();
        jLabel145 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblNominas = new javax.swing.JTable();
        jPanel74 = new javax.swing.JPanel();
        jTextField49 = new javax.swing.JTextField();
        jLabel149 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel148 = new javax.swing.JLabel();
        jLabel147 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jLabel146 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel152 = new javax.swing.JLabel();
        jTextField50 = new javax.swing.JTextField();
        jLabel155 = new javax.swing.JLabel();
        btnVerDetalles = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jPanel71 = new javax.swing.JPanel();
        jPanel76 = new javax.swing.JPanel();
        jLabel158 = new javax.swing.JLabel();
        jLabel159 = new javax.swing.JLabel();
        jLabel195 = new javax.swing.JLabel();
        jLabel196 = new javax.swing.JLabel();
        jLabel197 = new javax.swing.JLabel();
        jLabel198 = new javax.swing.JLabel();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel199 = new javax.swing.JLabel();
        jLabel200 = new javax.swing.JLabel();
        jLabel201 = new javax.swing.JLabel();
        jLabel202 = new javax.swing.JLabel();
        jLabel203 = new javax.swing.JLabel();
        jLabel204 = new javax.swing.JLabel();
        jLabel205 = new javax.swing.JLabel();
        jLabel206 = new javax.swing.JLabel();
        jLabel207 = new javax.swing.JLabel();
        jLabel208 = new javax.swing.JLabel();
        jLabel209 = new javax.swing.JLabel();
        jLabel210 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jpnHorasExt = new javax.swing.JPanel();
        jpnDeducciones = new javax.swing.JPanel();
        jpnAFP = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        jTextField51 = new javax.swing.JTextField();
        jTextField52 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jPanel72 = new javax.swing.JPanel();
        jLabel154 = new javax.swing.JLabel();
        jLabel156 = new javax.swing.JLabel();
        jTextField53 = new javax.swing.JTextField();
        jLabel157 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jPanel75 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jLabel160 = new javax.swing.JLabel();
        jSeparator15 = new javax.swing.JSeparator();
        jLabel161 = new javax.swing.JLabel();
        jLabel162 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        jLabel164 = new javax.swing.JLabel();
        jLabel165 = new javax.swing.JLabel();
        jLabel166 = new javax.swing.JLabel();
        jLabel167 = new javax.swing.JLabel();
        jLabel168 = new javax.swing.JLabel();
        jblAFPDetalle = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JSeparator();
        jpnARS = new javax.swing.JPanel();
        jPanel77 = new javax.swing.JPanel();
        jLabel169 = new javax.swing.JLabel();
        jLabel170 = new javax.swing.JLabel();
        jLabel171 = new javax.swing.JLabel();
        jLabel172 = new javax.swing.JLabel();
        jTextField54 = new javax.swing.JTextField();
        jTextField55 = new javax.swing.JTextField();
        jComboBox16 = new javax.swing.JComboBox<>();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jPanel78 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        jLabel173 = new javax.swing.JLabel();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel174 = new javax.swing.JLabel();
        jLabel175 = new javax.swing.JLabel();
        jLabel176 = new javax.swing.JLabel();
        jLabel177 = new javax.swing.JLabel();
        jLabel178 = new javax.swing.JLabel();
        jLabel179 = new javax.swing.JLabel();
        jLabel180 = new javax.swing.JLabel();
        jLabel181 = new javax.swing.JLabel();
        jblAFPDetalle1 = new javax.swing.JLabel();
        jButton33 = new javax.swing.JButton();
        jPanel79 = new javax.swing.JPanel();
        jLabel182 = new javax.swing.JLabel();
        jLabel183 = new javax.swing.JLabel();
        jTextField56 = new javax.swing.JTextField();
        jLabel184 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable10 = new javax.swing.JTable();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jPanel80 = new javax.swing.JPanel();
        jLabel185 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jpnISR = new javax.swing.JPanel();
        jPanel81 = new javax.swing.JPanel();
        jLabel194 = new javax.swing.JLabel();
        jLabel211 = new javax.swing.JLabel();
        jPanel84 = new javax.swing.JPanel();
        jLabel212 = new javax.swing.JLabel();
        jLabel213 = new javax.swing.JLabel();
        jLabel214 = new javax.swing.JLabel();
        jLabel215 = new javax.swing.JLabel();
        jLabel216 = new javax.swing.JLabel();
        jLabel217 = new javax.swing.JLabel();
        jLabel218 = new javax.swing.JLabel();
        jLabel219 = new javax.swing.JLabel();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jLabel220 = new javax.swing.JLabel();
        jTextField61 = new javax.swing.JTextField();
        jLabel221 = new javax.swing.JLabel();
        jTextField62 = new javax.swing.JTextField();
        jLabel222 = new javax.swing.JLabel();
        jTextField63 = new javax.swing.JTextField();
        jLabel223 = new javax.swing.JLabel();
        jTextField64 = new javax.swing.JTextField();
        jTextField65 = new javax.swing.JTextField();
        jLabel224 = new javax.swing.JLabel();
        jTextField66 = new javax.swing.JTextField();
        jTextField67 = new javax.swing.JTextField();
        jPanel85 = new javax.swing.JPanel();
        jLabel225 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTable11 = new javax.swing.JTable();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jpnTSS = new javax.swing.JPanel();
        jPanel89 = new javax.swing.JPanel();
        jLabel227 = new javax.swing.JLabel();
        jPanel90 = new javax.swing.JPanel();
        jLabel228 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel226 = new javax.swing.JLabel();
        jLabel229 = new javax.swing.JLabel();
        jLabel230 = new javax.swing.JLabel();
        jLabel231 = new javax.swing.JLabel();
        jLabel232 = new javax.swing.JLabel();
        jLabel233 = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel234 = new javax.swing.JLabel();
        jLabel235 = new javax.swing.JLabel();
        jLabel236 = new javax.swing.JLabel();
        jLabel237 = new javax.swing.JLabel();
        jLabel238 = new javax.swing.JLabel();
        jLabel239 = new javax.swing.JLabel();
        jLabel240 = new javax.swing.JLabel();
        jTextField68 = new javax.swing.JTextField();
        jTextField69 = new javax.swing.JTextField();
        jTextField70 = new javax.swing.JTextField();
        jLabel242 = new javax.swing.JLabel();
        jTextField71 = new javax.swing.JTextField();
        jLabel243 = new javax.swing.JLabel();
        jTextField72 = new javax.swing.JTextField();
        jLabel244 = new javax.swing.JLabel();
        jTextField73 = new javax.swing.JTextField();
        jLabel245 = new javax.swing.JLabel();
        jTextField74 = new javax.swing.JTextField();
        jTextField75 = new javax.swing.JTextField();
        jLabel246 = new javax.swing.JLabel();
        jTextField76 = new javax.swing.JTextField();
        jLabel247 = new javax.swing.JLabel();
        jTextField77 = new javax.swing.JTextField();
        jLabel248 = new javax.swing.JLabel();
        jTextField78 = new javax.swing.JTextField();
        jLabel249 = new javax.swing.JLabel();
        jTextField79 = new javax.swing.JTextField();
        jLabel250 = new javax.swing.JLabel();
        jTextField80 = new javax.swing.JTextField();
        jLabel251 = new javax.swing.JLabel();
        jLabel252 = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        jPanel91 = new javax.swing.JPanel();
        jLabel241 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTable12 = new javax.swing.JTable();
        jButton53 = new javax.swing.JButton();
        jPanel92 = new javax.swing.JPanel();
        jLabel253 = new javax.swing.JLabel();
        jLabel254 = new javax.swing.JLabel();
        jLabel255 = new javax.swing.JLabel();
        jLabel256 = new javax.swing.JLabel();
        jLabel257 = new javax.swing.JLabel();
        jLabel258 = new javax.swing.JLabel();
        jTextField81 = new javax.swing.JTextField();
        jTextField82 = new javax.swing.JTextField();
        jTextField83 = new javax.swing.JTextField();
        jTextField84 = new javax.swing.JTextField();
        jTextField85 = new javax.swing.JTextField();
        jTextField86 = new javax.swing.JTextField();
        jLabel259 = new javax.swing.JLabel();
        jLabel260 = new javax.swing.JLabel();
        jLabel261 = new javax.swing.JLabel();
        jLabel262 = new javax.swing.JLabel();
        jLabel263 = new javax.swing.JLabel();
        jLabel264 = new javax.swing.JLabel();
        jLabel265 = new javax.swing.JLabel();
        jLabel266 = new javax.swing.JLabel();
        jLabel267 = new javax.swing.JLabel();
        jTextField87 = new javax.swing.JTextField();
        jLabel268 = new javax.swing.JLabel();
        jLabel269 = new javax.swing.JLabel();
        jTextField88 = new javax.swing.JTextField();
        jTextField89 = new javax.swing.JTextField();
        jLabel270 = new javax.swing.JLabel();
        jLabel271 = new javax.swing.JLabel();
        jLabel273 = new javax.swing.JLabel();
        jLabel274 = new javax.swing.JLabel();
        jLabel272 = new javax.swing.JLabel();
        jTextField90 = new javax.swing.JTextField();
        jTextField91 = new javax.swing.JTextField();
        jLabel275 = new javax.swing.JLabel();
        jLabel276 = new javax.swing.JLabel();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jLabel277 = new javax.swing.JLabel();
        jTextField92 = new javax.swing.JTextField();
        jLabel278 = new javax.swing.JLabel();
        jpnAsistencias = new javax.swing.JPanel();
        jpnVacaciones = new javax.swing.JPanel();
        jpnLicencias = new javax.swing.JPanel();
        jpnPermisos = new javax.swing.JPanel();
        jpnRegistrarPago = new javax.swing.JPanel();
        jPanel97 = new javax.swing.JPanel();
        jLabel290 = new javax.swing.JLabel();
        jPanel98 = new javax.swing.JPanel();
        jLabel291 = new javax.swing.JLabel();
        jPanel99 = new javax.swing.JPanel();
        jLabel292 = new javax.swing.JLabel();
        jLabel293 = new javax.swing.JLabel();
        jLabel294 = new javax.swing.JLabel();
        jPanel100 = new javax.swing.JPanel();
        jLabel295 = new javax.swing.JLabel();
        jLabel296 = new javax.swing.JLabel();
        jLabel297 = new javax.swing.JLabel();
        jPanel101 = new javax.swing.JPanel();
        jLabel298 = new javax.swing.JLabel();
        jLabel299 = new javax.swing.JLabel();
        jLabel300 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTable14 = new javax.swing.JTable();
        jButton57 = new javax.swing.JButton();
        jPanel102 = new javax.swing.JPanel();
        jLabel301 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        jTable15 = new javax.swing.JTable();
        jLabel302 = new javax.swing.JLabel();
        jTextField93 = new javax.swing.JTextField();
        jLabel303 = new javax.swing.JLabel();
        jTextField94 = new javax.swing.JTextField();
        jLabel304 = new javax.swing.JLabel();
        jTextField95 = new javax.swing.JTextField();
        jLabel306 = new javax.swing.JLabel();
        jLabel307 = new javax.swing.JLabel();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jComboBox19 = new javax.swing.JComboBox<>();
        jComboBox20 = new javax.swing.JComboBox<>();
        jpnReportes = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel82 = new javax.swing.JPanel();
        jPanel83 = new javax.swing.JPanel();
        jLabel186 = new javax.swing.JLabel();
        jLabel187 = new javax.swing.JLabel();
        jLabel188 = new javax.swing.JLabel();
        jLabel189 = new javax.swing.JLabel();
        jLabel190 = new javax.swing.JLabel();
        jPanel87 = new javax.swing.JPanel();
        jLabel191 = new javax.swing.JLabel();
        jLabel192 = new javax.swing.JLabel();
        jLabel193 = new javax.swing.JLabel();
        jPanel88 = new javax.swing.JPanel();
        jLabel279 = new javax.swing.JLabel();
        jLabel280 = new javax.swing.JLabel();
        jLabel281 = new javax.swing.JLabel();
        jPanel93 = new javax.swing.JPanel();
        jLabel282 = new javax.swing.JLabel();
        jLabel283 = new javax.swing.JLabel();
        jLabel284 = new javax.swing.JLabel();
        jPanel94 = new javax.swing.JPanel();
        jLabel285 = new javax.swing.JLabel();
        jLabel286 = new javax.swing.JLabel();
        jLabel287 = new javax.swing.JLabel();
        jButton44 = new javax.swing.JButton();
        jLabel288 = new javax.swing.JLabel();
        jButton45 = new javax.swing.JButton();
        jPanel40 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtBienvenidoUser = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setPreferredSize(new java.awt.Dimension(1920, 1030));

        jScrollPane2.setBorder(null);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setViewportBorder(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(215, 1030));

        jPanel22.setBackground(new java.awt.Color(1, 75, 67));
        jPanel22.setMinimumSize(new java.awt.Dimension(220, 900));
        jPanel22.setPreferredSize(new java.awt.Dimension(200, 1250));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setBackground(new java.awt.Color(123, 216, 230));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/image-0 (4).png"))); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel19.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 240, 70));

        jPanel22.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 100));

        jPanel21.setBackground(new java.awt.Color(249, 236, 229));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlEmpresa.setBackground(new java.awt.Color(255, 226, 223));

        jLabel10.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel10.setText("- Datos de la Empresa");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel13.setText("- Departamentos y Funciones");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlEmpresaLayout = new javax.swing.GroupLayout(pnlEmpresa);
        pnlEmpresa.setLayout(pnlEmpresaLayout);
        pnlEmpresaLayout.setHorizontalGroup(
            pnlEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmpresaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEmpresaLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlEmpresaLayout.setVerticalGroup(
            pnlEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmpresaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.add(pnlEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 50));

        lblEMpresa.setBackground(new java.awt.Color(15, 69, 141));
        lblEMpresa.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblEMpresa.setForeground(new java.awt.Color(15, 69, 141));
        lblEMpresa.setText("Empresa");
        lblEMpresa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblEMpresa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEMpresaMouseClicked(evt);
            }
        });
        jPanel21.add(lblEMpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 210, 90));

        jPanel5.setBackground(new java.awt.Color(249, 236, 229));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlRRHH.setBackground(new java.awt.Color(255, 226, 223));

        jLabel14.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel14.setText("- Empleados");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel15.setText("- Historial Salarial");
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel30.setText("- Contratos y Documentos");
        jLabel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel30MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlRRHHLayout = new javax.swing.GroupLayout(pnlRRHH);
        pnlRRHH.setLayout(pnlRRHHLayout);
        pnlRRHHLayout.setHorizontalGroup(
            pnlRRHHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRRHHLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRRHHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel30))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        pnlRRHHLayout.setVerticalGroup(
            pnlRRHHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRRHHLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(pnlRRHH, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 80));

        lblRRHH.setBackground(new java.awt.Color(15, 69, 141));
        lblRRHH.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblRRHH.setForeground(new java.awt.Color(15, 69, 141));
        lblRRHH.setText("Recursos Humanos");
        lblRRHH.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblRRHH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRRHHMouseClicked(evt);
            }
        });
        jPanel5.add(lblRRHH, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 210, 130));

        jPanel7.setBackground(new java.awt.Color(249, 236, 229));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlReportes.setBackground(new java.awt.Color(255, 226, 223));

        jLabel29.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel29.setText("- Reportes ");
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel29MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlReportesLayout = new javax.swing.GroupLayout(pnlReportes);
        pnlReportes.setLayout(pnlReportesLayout);
        pnlReportesLayout.setHorizontalGroup(
            pnlReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addContainerGap(121, Short.MAX_VALUE))
        );
        pnlReportesLayout.setVerticalGroup(
            pnlReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel7.add(pnlReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 30));

        lblReportes.setBackground(new java.awt.Color(15, 69, 141));
        lblReportes.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblReportes.setForeground(new java.awt.Color(15, 69, 141));
        lblReportes.setText("Reportes");
        lblReportes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblReportes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblReportesMouseClicked(evt);
            }
        });
        jPanel7.add(lblReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 880, 210, 70));

        jPanel9.setBackground(new java.awt.Color(249, 236, 229));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlNomina.setBackground(new java.awt.Color(255, 226, 223));

        jLabel16.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel16.setText("- Nomina");
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel17.setText("- Generar Nomina");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel18.setText("- Consultar Nomina");
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel19.setText("- Horas Extras");
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel20.setText("- Deducciones");
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlNominaLayout = new javax.swing.GroupLayout(pnlNomina);
        pnlNomina.setLayout(pnlNominaLayout);
        pnlNominaLayout.setHorizontalGroup(
            pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        pnlNominaLayout.setVerticalGroup(
            pnlNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addGap(12, 12, 12))
        );

        jPanel9.add(pnlNomina, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 120));

        lblNomina.setBackground(new java.awt.Color(15, 69, 141));
        lblNomina.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblNomina.setForeground(new java.awt.Color(15, 69, 141));
        lblNomina.setText("Nómina");
        lblNomina.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblNomina.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNominaMouseClicked(evt);
            }
        });
        jPanel9.add(lblNomina, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 210, 160));

        jPanel13.setBackground(new java.awt.Color(249, 236, 229));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAdministraciòn.setBackground(new java.awt.Color(15, 69, 141));
        lblAdministraciòn.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblAdministraciòn.setForeground(new java.awt.Color(15, 69, 141));
        lblAdministraciòn.setText("Administraciòn");
        lblAdministraciòn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAdministraciòn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdministraciònMouseClicked(evt);
            }
        });
        jPanel13.add(lblAdministraciòn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        pnlAdministracion.setBackground(new java.awt.Color(255, 226, 223));

        jLabel22.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel22.setText("- Usuarios");
        jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel22MouseClicked(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel23.setText("- Roles y Permisos");

        jLabel24.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel24.setText("- Auditoria");

        javax.swing.GroupLayout pnlAdministracionLayout = new javax.swing.GroupLayout(pnlAdministracion);
        pnlAdministracion.setLayout(pnlAdministracionLayout);
        pnlAdministracionLayout.setHorizontalGroup(
            pnlAdministracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdministracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAdministracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        pnlAdministracionLayout.setVerticalGroup(
            pnlAdministracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAdministracionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addGap(34, 34, 34))
        );

        jPanel13.add(pnlAdministracion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 70));

        jPanel22.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 960, 210, 110));

        jPanel15.setBackground(new java.awt.Color(249, 236, 229));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlAsistencia.setBackground(new java.awt.Color(255, 226, 223));

        jLabel21.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel21.setText("- Asistencia");
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel26.setText("- Vacaciones");
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel26MouseClicked(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel27.setText("- Licencias");
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel28.setText("- Permisos");
        jLabel28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel28MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlAsistenciaLayout = new javax.swing.GroupLayout(pnlAsistencia);
        pnlAsistencia.setLayout(pnlAsistenciaLayout);
        pnlAsistenciaLayout.setHorizontalGroup(
            pnlAsistenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAsistenciaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAsistenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        pnlAsistenciaLayout.setVerticalGroup(
            pnlAsistenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAsistenciaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.add(pnlAsistencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 90));

        lblAsistencia.setBackground(new java.awt.Color(15, 69, 141));
        lblAsistencia.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblAsistencia.setForeground(new java.awt.Color(15, 69, 141));
        lblAsistencia.setText("Asistencia");
        lblAsistencia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAsistencia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAsistenciaMouseClicked(evt);
            }
        });
        jPanel15.add(lblAsistencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 660, 210, 130));

        jPanel17.setBackground(new java.awt.Color(249, 236, 229));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlPago.setBackground(new java.awt.Color(255, 226, 223));

        lblMenuPago.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        lblMenuPago.setText("- Registrar Pago");
        lblMenuPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMenuPagoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlPagoLayout = new javax.swing.GroupLayout(pnlPago);
        pnlPago.setLayout(pnlPagoLayout);
        pnlPagoLayout.setHorizontalGroup(
            pnlPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMenuPago)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        pnlPagoLayout.setVerticalGroup(
            pnlPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMenuPago)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel17.add(pnlPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 28, 190, 30));

        lblPago.setBackground(new java.awt.Color(15, 69, 141));
        lblPago.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblPago.setForeground(new java.awt.Color(15, 69, 141));
        lblPago.setText("Pago");
        lblPago.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPagoMouseClicked(evt);
            }
        });
        jPanel17.add(lblPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 800, 210, 70));

        jButton1.setBackground(new java.awt.Color(204, 255, 255));
        jButton1.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton1.setText("Cerrar Sesión");
        jButton1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jButton1.setOpaque(true);
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel22.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 1130, -1, -1));

        jPanel42.setBackground(new java.awt.Color(249, 236, 229));
        jPanel42.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSeguridad.setBackground(new java.awt.Color(15, 69, 141));
        lblSeguridad.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblSeguridad.setForeground(new java.awt.Color(15, 69, 141));
        lblSeguridad.setText("Seguridad Social");
        lblSeguridad.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSeguridad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSeguridadMouseClicked(evt);
            }
        });
        jPanel42.add(lblSeguridad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        pnlSeguridad.setBackground(new java.awt.Color(255, 226, 223));

        jLabel36.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel36.setText("- AFP");
        jLabel36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel36MouseClicked(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel37.setText("- ARS");
        jLabel37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel37MouseClicked(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel38.setText("- ISR");
        jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel38MouseClicked(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel39.setText("- TSS");
        jLabel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel39MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlSeguridadLayout = new javax.swing.GroupLayout(pnlSeguridad);
        pnlSeguridad.setLayout(pnlSeguridadLayout);
        pnlSeguridadLayout.setHorizontalGroup(
            pnlSeguridadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeguridadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSeguridadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel37)
                    .addComponent(jLabel39)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        pnlSeguridadLayout.setVerticalGroup(
            pnlSeguridadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSeguridadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addGap(12, 12, 12))
        );

        jPanel42.add(pnlSeguridad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 90));

        jPanel22.add(jPanel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, 210, 130));

        jScrollPane2.setViewportView(jPanel22);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );

        jpnMain.setBackground(new java.awt.Color(255, 255, 255));
        jpnMain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(9, 144, 120), 3, true));
        jpnMain.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jpnMain.setAutoscrolls(true);
        jpnMain.setOpaque(true);
        jpnMain.setPreferredSize(new java.awt.Dimension(1200, 900));

        jpnEmpresa.setBackground(new java.awt.Color(255, 255, 255));

        jPanel44.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setFont(new java.awt.Font("Roboto", 3, 32)); // NOI18N
        jLabel25.setText("Datos de la Empresa");

        jLabel40.setFont(new java.awt.Font("Roboto", 3, 32)); // NOI18N
        jLabel40.setText("Estado:");

        cmbEstado.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));
        cmbEstado.setEnabled(false);

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106))
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(jLabel25)
                    .addComponent(cmbEstado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOpaque(true);

        jLabel41.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel41.setText("Descripción");

        txtDescripcion.setEditable(false);
        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        txtDescripcion.setRows(5);
        jScrollPane3.setViewportView(txtDescripcion);

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(111, 111, 111))
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jPanel47.setBackground(new java.awt.Color(249, 236, 229));

        jLabel42.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel42.setText("Información General");
        jLabel42.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        txtLogo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnCambiarLogoEmpresa.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        btnCambiarLogoEmpresa.setText("Cambiar Logo");
        btnCambiarLogoEmpresa.addActionListener(this::btnCambiarLogoEmpresaActionPerformed);

        jLabel44.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel44.setText("Razón Social:");

        jLabel45.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel45.setText("Nombre Comercial:");

        jLabel46.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel46.setText("RNC:");

        jLabel48.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel48.setText("Representante Legal:");

        txtRNC.setEditable(false);
        txtRNC.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        txtRazonSocial.setEditable(false);
        txtRazonSocial.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        txtNombreComercial.setEditable(false);
        txtNombreComercial.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        txtRepresentanteLegal.setEditable(false);
        txtRepresentanteLegal.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(txtLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCambiarLogoEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel47Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtRepresentanteLegal, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel47Layout.createSequentialGroup()
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRNC, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel47Layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombreComercial, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 22, Short.MAX_VALUE))
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRazonSocial)))
                .addGap(42, 42, 42))
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(btnCambiarLogoEmpresa))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel47Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addGap(18, 18, 18)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(txtRNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(txtNombreComercial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(txtRepresentanteLegal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71))
        );

        jPanel48.setBackground(new java.awt.Color(249, 236, 229));

        jLabel49.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel49.setText("Información de Contacto y Ubicación");

        jLabel50.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel50.setText("Teléfono Principal:");

        jLabel51.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel51.setText("Correo Electrónico:");

        jLabel52.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel52.setText("Dirección :");

        jLabel53.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel53.setText("Fecha de Registro:");

        txtTelefono.setEditable(false);
        txtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));

        txtEmail.setEditable(false);
        txtEmail.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        txtDireccion.setEditable(false);
        txtDireccion.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        txtFechaRegistro.setEditable(false);
        txtFechaRegistro.setFont(new java.awt.Font("sansserif", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel48Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(txtEmail)
                            .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel48Layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel48Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel49)
                .addGap(65, 65, 65)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jpnEmpresaLayout = new javax.swing.GroupLayout(jpnEmpresa);
        jpnEmpresa.setLayout(jpnEmpresaLayout);
        jpnEmpresaLayout.setHorizontalGroup(
            jpnEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnEmpresaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnEmpresaLayout.createSequentialGroup()
                        .addGroup(jpnEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel44, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1))
                        .addContainerGap())
                    .addGroup(jpnEmpresaLayout.createSequentialGroup()
                        .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        jpnEmpresaLayout.setVerticalGroup(
            jpnEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnEmpresaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpnEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addComponent(jSeparator2))
                .addGap(12, 12, 12))
        );

        jpnMain.addTab("Datos de la Empresa", jpnEmpresa);

        jpnDeps.setBackground(new java.awt.Color(255, 255, 255));
        jpnDeps.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel54.setFont(new java.awt.Font("sansserif", 3, 24)); // NOI18N
        jLabel54.setText("Departamentos de la Empresa");
        jpnDeps.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 20, 380, -1));

        jtableDepartamentos.setAutoCreateRowSorter(true);
        jtableDepartamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Departamentos", "Funciones", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtableDepartamentos);

        jpnDeps.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 360, 795, 315));

        jpPuestos.setBackground(new java.awt.Color(249, 236, 229));

        jLabel57.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel57.setText("Puestos del Departamento");

        jtablePuestos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Departamento", "Puestos", "Estado", "Salario Minimo", "Salario Maximo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jtablePuestos);

        jLabel55.setFont(new java.awt.Font("sansserif", 3, 18)); // NOI18N
        jLabel55.setText("Seleccione el Deparmento:");

        cmbDepartamentoPuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contabilidad", "2- Produccion y Operaciones", "3- Marketing ", "4- Ventas", "5- Tecnologia" }));
        cmbDepartamentoPuesto.addActionListener(this::cmbDepartamentoPuestoActionPerformed);

        jButton10.setText("Agregar Puestos");
        jButton10.addActionListener(this::jButton10ActionPerformed);

        btnEliminarPuestos.setText("Eliminar");
        btnEliminarPuestos.addActionListener(this::btnEliminarPuestosActionPerformed);

        javax.swing.GroupLayout jpPuestosLayout = new javax.swing.GroupLayout(jpPuestos);
        jpPuestos.setLayout(jpPuestosLayout);
        jpPuestosLayout.setHorizontalGroup(
            jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPuestosLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel55)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPuestosLayout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPuestosLayout.createSequentialGroup()
                        .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpPuestosLayout.createSequentialGroup()
                                .addGap(128, 128, 128)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEliminarPuestos)
                                .addGap(145, 145, 145)))
                        .addGap(32, 32, 32))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPuestosLayout.createSequentialGroup()
                        .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbDepartamentoPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(184, 184, 184))))
        );
        jpPuestosLayout.setVerticalGroup(
            jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPuestosLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel57)
                .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPuestosLayout.createSequentialGroup()
                        .addGap(60, 157, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10)
                            .addComponent(btnEliminarPuestos))
                        .addGap(21, 21, 21))
                    .addGroup(jpPuestosLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jpPuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel55)
                            .addComponent(cmbDepartamentoPuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jpnDeps.add(jpPuestos, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 60, -1, 640));

        jButton4.setText("Actualizar Información");
        jButton4.addActionListener(this::jButton4ActionPerformed);
        jpnDeps.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 690, 152, -1));

        btnPuestos.setText("Puestos");
        btnPuestos.addActionListener(this::btnPuestosActionPerformed);
        jpnDeps.add(btnPuestos, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 180, 127, -1));

        jLabel61.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel61.setText("Buscar:");
        jpnDeps.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 326, -1, -1));
        jpnDeps.add(txtBuscarDepartamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 310, 315, 40));

        jButton6.setText("Editar");
        jButton6.addActionListener(this::jButton6ActionPerformed);
        jpnDeps.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 690, 129, -1));

        jLabel64.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel64.setText(" Gestión de las Areas Organizativas de la Empresa");
        jpnDeps.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(1144, 20, 490, -1));

        jButton11.setText("Eliminar");
        jButton11.addActionListener(this::jButton11ActionPerformed);
        jpnDeps.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 690, -1, -1));

        jButton46.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jButton46.setText("Agregar Departamento");
        jButton46.addActionListener(this::jButton46ActionPerformed);
        jpnDeps.add(jButton46, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, -1));

        jLabel2.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel2.setText("Departamento: ");
        jpnDeps.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));
        jpnDeps.add(txtNombreDepartamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 210, 40));

        cmbEstadoDepartamento.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        cmbEstadoDepartamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));
        jpnDeps.add(cmbEstadoDepartamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 110, -1));

        jLabel3.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel3.setText("Funciones:");
        jpnDeps.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        txtDepartamentoFunciones.addActionListener(this::txtDepartamentoFuncionesActionPerformed);
        jpnDeps.add(txtDepartamentoFunciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 350, 40));

        jpnMain.addTab("Deps y Funciones", jpnDeps);

        jpnEmpleados.setBackground(new java.awt.Color(255, 255, 255));

        jPanel50.setBackground(new java.awt.Color(249, 236, 229));

        jLabel65.setFont(new java.awt.Font("Roboto", 3, 28)); // NOI18N
        jLabel65.setText("Empleados");

        jLabel66.setFont(new java.awt.Font("Roboto", 3, 28)); // NOI18N
        jLabel66.setText("Consulta y Gestión del Personal de la Empresa");

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel66)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel51.setBackground(new java.awt.Color(249, 236, 229));

        jLabel67.setFont(new java.awt.Font("Roboto", 3, 28)); // NOI18N
        jLabel67.setText("Buscar Empleado");

        jLabel68.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel68.setText("Buscar:");

        cmbFiltroDepartamento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contabilidad", "2- Produccion y Operaciones", "3- Marketing ", "4- Ventas", "5- Tecnologia" }));
        cmbFiltroDepartamento.addActionListener(this::cmbFiltroDepartamentoActionPerformed);

        jLabel69.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel69.setText("Departamentos:");

        cmbFiltroCargo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contador", "2- Auxiliar Contable", "3- Analista Financiero", "4- Supervisor de Produccion", "5- Operario", "6- Encargado de Operaciones", "7- Gerente de Marketing", "8- Analista de Marketing", "9- Gerente de Ventas", "10- Vendedor", "11- Soporte Tecnico" }));

        jLabel70.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel70.setText("Cargo:");

        cmbFiltroEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "Activo", "Inactivo", "Suspendido", "Desvinculado" }));

        jLabel71.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jLabel71.setText("Estado:");

        jtableEmpleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombres", "Apellidos", "Cédula", "Departamento", "Cargo", "Contrato", "Telefóno", "Email", "Salario Base", "Banco", "Cuenta Bancaria", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jtableEmpleados);

        jLabel73.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel73.setText("Total Empleados:");

        jLabel74.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel74.setText("Activos:");

        jLabel75.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel75.setText("Inactivos:");

        jPanel52.setBackground(new java.awt.Color(249, 236, 229));

        jButton14.setText("Registrar Empelado");
        jButton14.addActionListener(this::jButton14ActionPerformed);

        jButton15.setText("Ver Empleado");

        jButton16.setText("Editar");

        jButton17.setText("Actualizar");

        btnEliminarEmpleado.setText("Eliminar");
        btnEliminarEmpleado.addActionListener(this::btnEliminarEmpleadoActionPerformed);

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton15)
                .addGap(18, 18, 18)
                .addComponent(jButton16)
                .addGap(18, 18, 18)
                .addComponent(jButton17)
                .addGap(18, 18, 18)
                .addComponent(btnEliminarEmpleado)
                .addContainerGap())
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton14)
                    .addComponent(jButton15)
                    .addComponent(jButton16)
                    .addComponent(jButton17)
                    .addComponent(btnEliminarEmpleado))
                .addGap(19, 19, 19))
        );

        txtTotalEmpleados.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        txtTotalEmpleados.setAlignmentX(2.0F);
        txtTotalEmpleados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtEmpleadosActivos.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        txtEmpleadosActivos.setAlignmentX(2.0F);
        txtEmpleadosActivos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtEmpleadosInactivos.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        txtEmpleadosInactivos.setAlignmentX(2.0F);
        txtEmpleadosInactivos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel51Layout.createSequentialGroup()
                            .addComponent(jLabel73)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTotalEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(95, 95, 95)
                            .addComponent(jLabel74)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtEmpleadosActivos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(125, 125, 125)
                            .addComponent(jLabel75)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtEmpleadosInactivos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel51Layout.createSequentialGroup()
                            .addComponent(jLabel69)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbFiltroDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(38, 38, 38)
                            .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbFiltroCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(33, 33, 33)
                            .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1594, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(txtBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(cmbFiltroDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70)
                    .addComponent(cmbFiltroCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel71)
                    .addComponent(cmbFiltroEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel51Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel73)
                                    .addComponent(txtTotalEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel51Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEmpleadosActivos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel74)
                                        .addComponent(jLabel75))
                                    .addComponent(txtEmpleadosInactivos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jpnEmpleadosLayout = new javax.swing.GroupLayout(jpnEmpleados);
        jpnEmpleados.setLayout(jpnEmpleadosLayout);
        jpnEmpleadosLayout.setHorizontalGroup(
            jpnEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnEmpleadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpnEmpleadosLayout.setVerticalGroup(
            jpnEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnEmpleadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpnMain.addTab("Empleados", jpnEmpleados);

        jpnContratos.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnContratosLayout = new javax.swing.GroupLayout(jpnContratos);
        jpnContratos.setLayout(jpnContratosLayout);
        jpnContratosLayout.setHorizontalGroup(
            jpnContratosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1640, Short.MAX_VALUE)
        );
        jpnContratosLayout.setVerticalGroup(
            jpnContratosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("Contra y Docs", jpnContratos);

        jpnHistorial.setBackground(new java.awt.Color(255, 255, 255));

        jLabel76.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel76.setText("Historial Salarial ");

        jLabel77.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel77.setText("Consulta y Seguimiento de los Cambios Salariales de los Empleados");

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel76)
                .addGap(18, 18, 18)
                .addComponent(jLabel77)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jLabel78.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel78.setText("Empleado");

        jLabel79.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel79.setText("Buscar Empleado:");

        jTextField7.setText("jTextField1");

        jLabel80.setText("Icon");

        jPanel55.setBackground(new java.awt.Color(255, 255, 255));

        jLabel81.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel81.setText("Empleado:");

        jTextField12.setEditable(false);

        jLabel31.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel31.setText("ID:");

        jTextField13.setEditable(false);

        jLabel82.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel82.setText("Cargo:");

        jTextField14.setEditable(false);

        jLabel83.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel83.setText("Departamento:");

        jTextField15.setEditable(false);

        jLabel84.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel84.setText("Estado:");

        jTextField16.setEditable(false);

        javax.swing.GroupLayout jPanel55Layout = new javax.swing.GroupLayout(jPanel55);
        jPanel55.setLayout(jPanel55Layout);
        jPanel55Layout.setHorizontalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel55Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel55Layout.createSequentialGroup()
                        .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField15))
                    .addGroup(jPanel55Layout.createSequentialGroup()
                        .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel55Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
        );
        jPanel55Layout.setVerticalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel55Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel82)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel80))
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel78)
                        .addGap(39, 39, 39)
                        .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel79)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80)))
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Fecha de Cambio", "Salario Anterior", "Salario Nuevo", "Variación", "Motivo", "Aprovado Por"
            }
        ));
        jScrollPane7.setViewportView(jTable5);

        jLabel85.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel85.setText("Salario Actual: ");

        jTextField17.setEditable(false);

        jLabel86.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel86.setText("Desde:");

        jTextField18.setEditable(false);
        jTextField18.addActionListener(this::jTextField18ActionPerformed);

        jLabel87.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel87.setText("Historial de Salarios por Empleado");

        jButton19.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton19.setText("Ver Detalle");

        jButton20.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton20.setText("Exportar");

        jButton21.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton21.setText("Registrar Cambio Salarial");

        jPanel57.setBackground(new java.awt.Color(255, 255, 255));

        jLabel88.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel88.setText("Registrar Cambio Salarial");

        jLabel89.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel89.setText("Empleado:");

        jTextField19.setEditable(false);

        jLabel90.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel90.setText("Salario Actual:");

        jTextField20.setEditable(false);

        jLabel91.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel91.setText("Nuevo Salario:");

        jLabel92.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel92.setText("Fecha Efectiva:");

        jLabel93.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel93.setText("Motivo:");
        jLabel93.setToolTipText("");

        jComboBox6.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ascenso", "Evaluación al Merito", "Ajuste Inflación", "Aumento del Salario Mínimo", "Cambio de Puesto", "Correción del Sistema", " " }));
        jComboBox6.addActionListener(this::jComboBox6ActionPerformed);

        jButton22.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton22.setText("Cancelar ");

        jButton23.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        jButton23.setText("Guardar Cambios");
        jButton23.addActionListener(this::jButton23ActionPerformed);

        javax.swing.GroupLayout jPanel57Layout = new javax.swing.GroupLayout(jPanel57);
        jPanel57.setLayout(jPanel57Layout);
        jPanel57Layout.setHorizontalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel57Layout.createSequentialGroup()
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel57Layout.createSequentialGroup()
                        .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel57Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel57Layout.createSequentialGroup()
                                        .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel57Layout.createSequentialGroup()
                                        .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel92, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel91, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField20)
                                            .addComponent(jTextField21)
                                            .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel57Layout.createSequentialGroup()
                                        .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel57Layout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addComponent(jLabel88)))
                        .addGap(0, 92, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel57Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton23)))
                .addContainerGap())
        );
        jPanel57Layout.setVerticalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel57Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel88)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22)
                    .addComponent(jButton23))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel56Layout = new javax.swing.GroupLayout(jPanel56);
        jPanel56.setLayout(jPanel56Layout);
        jPanel56Layout.setHorizontalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel56Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel87))
                    .addGroup(jPanel56Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 752, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel56Layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(jLabel85)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(84, 84, 84)
                                .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel56Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addComponent(jButton19)
                                .addGap(60, 60, 60)
                                .addComponent(jButton20)
                                .addGap(79, 79, 79)
                                .addComponent(jButton21)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(264, 264, 264))
        );
        jPanel56Layout.setVerticalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel56Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel87)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jButton20)
                    .addComponent(jButton21))
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jpnHistorialLayout = new javax.swing.GroupLayout(jpnHistorial);
        jpnHistorial.setLayout(jpnHistorialLayout);
        jpnHistorialLayout.setHorizontalGroup(
            jpnHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnHistorialLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpnHistorialLayout.setVerticalGroup(
            jpnHistorialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnHistorialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpnMain.addTab("Historial Salarial", jpnHistorial);

        jpnNomina.setBackground(new java.awt.Color(255, 255, 255));

        jPanel59.setBackground(new java.awt.Color(249, 236, 229));

        jLabel94.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        jLabel94.setText("Nómina");

        jLabel95.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        jLabel95.setText("Gestión de la Nómina del Período Actual");

        jPanel60.setBackground(new java.awt.Color(255, 255, 255));
        jPanel60.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel96.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel96.setText("Período Actual");

        jLabel97.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel97.setText("Período:");

        jLabel98.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel98.setText("Desde:");

        jTextField23.setEditable(false);
        jTextField23.addActionListener(this::jTextField23ActionPerformed);

        jLabel99.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel99.setText("Estado:");

        jTextField24.setEditable(false);

        jLabel100.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel100.setText("Hasta:");

        jTextField25.setEditable(false);

        jLabel101.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel101.setText("Empleados");

        jLabel102.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel102.setText("Salario Bruto");

        jLabel103.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel103.setText("Descuentos");

        jLabel104.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel104.setText("Salario Neto");

        jLabel117.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel117.setText("RD$");

        jLabel118.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel118.setText("RD$");

        jLabel119.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel119.setText("RD$");

        javax.swing.GroupLayout jPanel60Layout = new javax.swing.GroupLayout(jPanel60);
        jPanel60.setLayout(jPanel60Layout);
        jPanel60Layout.setHorizontalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel60Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel96)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel60Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel60Layout.createSequentialGroup()
                        .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPeriodoNomina, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel60Layout.createSequentialGroup()
                        .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField23)
                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(18, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel60Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel118)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField28, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addComponent(jTextField26)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel117, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel119, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField27, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                        .addComponent(jTextField29)))
                .addGap(90, 90, 90))
        );
        jPanel60Layout.setVerticalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel60Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel96)
                .addGap(18, 18, 18)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(cmbPeriodoNomina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel99)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(86, 86, 86)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(jLabel102))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel117))
                .addGap(68, 68, 68)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel103, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel104))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119)
                    .addComponent(jLabel118))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        jtableNomina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Código Empleado", "Empleado", "Salario", "Bonif.", "Deducciones", "Neto", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jtableNomina);

        jLabel105.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel105.setText("Detalle de Nómina");

        jButton24.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton24.setText("Ver Detalles");
        jButton24.addActionListener(this::jButton24ActionPerformed);

        jButton25.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton25.setText("Exportar PDF");
        jButton25.addActionListener(this::jButton25ActionPerformed);

        javax.swing.GroupLayout jPanel59Layout = new javax.swing.GroupLayout(jPanel59);
        jPanel59.setLayout(jPanel59Layout);
        jPanel59Layout.setHorizontalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel59Layout.createSequentialGroup()
                                .addGap(146, 146, 146)
                                .addComponent(jButton24)
                                .addGap(57, 57, 57)
                                .addComponent(jButton25)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel59Layout.createSequentialGroup()
                                .addGap(135, 135, 135)
                                .addComponent(jLabel105)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel59Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        jPanel59Layout.setVerticalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel105)
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton24)
                            .addComponent(jButton25))
                        .addGap(33, 33, 33))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel59Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel95)
                        .addGap(19, 19, 19)
                        .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );

        jSeparator4.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setOpaque(true);

        jpDetalleNominaEmpleado.setBackground(new java.awt.Color(255, 255, 255));
        jpDetalleNominaEmpleado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153), 2));
        jpDetalleNominaEmpleado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel106.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel106.setText("Detalle de Nómina");
        jpDetalleNominaEmpleado.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 29, 218, -1));

        lblNombreEmpleado.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        lblNombreEmpleado.setText("Michael Alexander Cabrera Feliz");
        jpDetalleNominaEmpleado.add(lblNombreEmpleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 122, 228, -1));

        jPanel61.setBackground(new java.awt.Color(255, 255, 255));

        jLabel109.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel109.setText("Salario Base                  RD$");

        jLabel108.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel108.setText("Bonificaciones              RD$");

        lblSalarioBase.setEditable(false);

        lblBonificaciones.setEditable(false);
        lblBonificaciones.addActionListener(this::lblBonificacionesActionPerformed);

        jLabel110.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel110.setText("ARS                                  RD$");

        jLabel111.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel111.setText("ISR                                   RD$");

        jLabel112.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel112.setText("Otras                                RD$");

        lblISR.setEditable(false);

        lblARS.setEditable(false);

        lblOtrasDeducciones.setEditable(false);

        jSeparator5.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator5.setOpaque(true);

        jLabel113.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel113.setText("Total Descuentos         RD$");

        lblTotalDescuentos.setEditable(false);

        jLabel114.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel114.setText("Salario Neto                    RD$");

        lblSalarioNeto.setEditable(false);

        lblAFP.setEditable(false);

        jLabel115.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jLabel115.setText("AFP                                  RD$");

        jSeparator6.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator6.setOpaque(true);

        javax.swing.GroupLayout jPanel61Layout = new javax.swing.GroupLayout(jPanel61);
        jPanel61.setLayout(jPanel61Layout);
        jPanel61Layout.setHorizontalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel61Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel61Layout.createSequentialGroup()
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel108, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel109, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblBonificaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(lblSalarioBase)))
                            .addGroup(jPanel61Layout.createSequentialGroup()
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel114))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTotalDescuentos, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                    .addComponent(lblSalarioNeto)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel61Layout.createSequentialGroup()
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel110)
                                    .addComponent(jLabel111)
                                    .addComponent(jLabel112)
                                    .addComponent(jLabel115))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblAFP, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(lblISR)
                                    .addComponent(lblARS)
                                    .addComponent(lblOtrasDeducciones)
                                    .addComponent(jSeparator5)))))
                    .addComponent(jSeparator6))
                .addContainerGap())
        );
        jPanel61Layout.setVerticalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel109)
                    .addComponent(lblSalarioBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(lblBonificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAFP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel115))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblARS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblISR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOtrasDeducciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel112))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel113)
                    .addComponent(lblTotalDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(lblSalarioNeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );

        jpDetalleNominaEmpleado.add(jPanel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 147, -1, -1));

        jButton26.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton26.setText("Cerrar");
        jButton26.addActionListener(this::jButton26ActionPerformed);
        jpDetalleNominaEmpleado.add(jButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(281, 528, -1, -1));

        jLabel116.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel116.setText("Empleado");
        jpDetalleNominaEmpleado.add(jLabel116, new org.netbeans.lib.awtextra.AbsoluteConstraints(114, 70, 92, -1));

        javax.swing.GroupLayout jpnNominaLayout = new javax.swing.GroupLayout(jpnNomina);
        jpnNomina.setLayout(jpnNominaLayout);
        jpnNominaLayout.setHorizontalGroup(
            jpnNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                .addComponent(jpDetalleNominaEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );
        jpnNominaLayout.setVerticalGroup(
            jpnNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addGroup(jpnNominaLayout.createSequentialGroup()
                        .addComponent(jPanel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jpnNominaLayout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jpDetalleNominaEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpnMain.addTab("Nómina", jpnNomina);

        jpnGenNomina.setBackground(new java.awt.Color(255, 255, 255));

        jLabel120.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel120.setText("Generar Nómina");

        jLabel121.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel121.setText("Procesamiento y Cálculo de la Nómina  ");

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel121)
                    .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel132.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel132.setText("3. Conceptos a Calcular");

        jPanel67.setBackground(new java.awt.Color(255, 255, 255));
        jPanel67.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jCheckBox3.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox3.setText("Salario Base");
        jCheckBox3.addActionListener(this::jCheckBox3ActionPerformed);

        jCheckBox4.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox4.setText("AFP");

        jCheckBox5.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox5.setText("Otras Deducciones");

        jCheckBox6.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox6.setText("Horas Extras");

        jCheckBox7.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox7.setText("Bonificaciones");

        jCheckBox8.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox8.setText("ARS");

        jCheckBox9.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        jCheckBox9.setText("ISR");
        jCheckBox9.setToolTipText("");

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel67Layout.createSequentialGroup()
                        .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(100, 100, 100)
                        .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(90, 90, 90)
                        .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox7, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addGroup(jPanel67Layout.createSequentialGroup()
                                .addComponent(jCheckBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jCheckBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(64, 64, 64))
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7))
                .addGap(32, 32, 32)
                .addGroup(jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox8)
                    .addComponent(jCheckBox9))
                .addGap(33, 33, 33)
                .addComponent(jCheckBox5)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jLabel138.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel138.setText("Resumen del Procesamiento");

        jPanel68.setBackground(new java.awt.Color(255, 255, 255));
        jPanel68.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel141.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel141.setText("Empleados : ");

        jTextField45.setEditable(false);

        jLabel133.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel133.setText("Bruto Estimado:");

        jLabel134.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel134.setText("RD$");

        jTextField42.setEditable(false);

        jLabel135.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel135.setText("Estado:");

        jTextField43.setEditable(false);

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel141)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel134)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField42)
                .addGap(24, 24, 24)
                .addComponent(jLabel135)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel68Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel134)
                        .addComponent(jLabel135)
                        .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel141)
                        .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel133)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jButton28.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton28.setText("Vista Previa");

        jButton29.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton29.setText("Generar Nómina");

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(jButton28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton29)
                .addGap(138, 138, 138))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createSequentialGroup()
                        .addComponent(jLabel132)
                        .addGap(221, 221, 221))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel63Layout.createSequentialGroup()
                        .addComponent(jLabel138)
                        .addGap(168, 168, 168)))
                .addGap(26, 26, 26))
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel132)
                .addGap(18, 18, 18)
                .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel138)
                .addGap(18, 18, 18)
                .addComponent(jPanel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton28)
                    .addComponent(jButton29))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel64.setPreferredSize(new java.awt.Dimension(799, 600));

        jLabel122.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel122.setText("1. Configuración del Período");

        jPanel65.setBackground(new java.awt.Color(255, 255, 255));
        jPanel65.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel123.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel123.setText("Período de Nómina");

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel124.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel124.setText("Fecha desde");

        jTextField38.setEditable(false);

        jLabel127.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel127.setText("Estado del Período: ");

        jTextField40.setEditable(false);

        jTextField39.setEditable(false);
        jTextField39.addActionListener(this::jTextField39ActionPerformed);

        jLabel125.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel125.setText("Fecha hasta");

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mensual", "Quincenal", "Semanal" }));

        jLabel126.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel126.setText("Tipo");

        javax.swing.GroupLayout jPanel65Layout = new javax.swing.GroupLayout(jPanel65);
        jPanel65.setLayout(jPanel65Layout);
        jPanel65Layout.setHorizontalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel65Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(jLabel127)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel123, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(419, 419, 419))
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel124, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(68, 68, 68)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField39, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel126, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel65Layout.setVerticalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel123)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel125, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel124))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField39)
                            .addComponent(jTextField38, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(jLabel126)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel127)
                    .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        jLabel128.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel128.setText("2. Empleados a Procesar");

        jPanel66.setBackground(new java.awt.Color(255, 255, 255));
        jPanel66.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jCheckBox1.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jCheckBox1.setText("Todos los Empleados Activos");
        jCheckBox1.addActionListener(this::jCheckBox1ActionPerformed);

        jLabel129.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel129.setText("Departamentos:");

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Contabilidad", "2- Produccion y Operaciones", "3- Marketing ", "4- Ventas", "5- Tecnologia" }));
        jComboBox12.addActionListener(this::jComboBox12ActionPerformed);

        jLabel130.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel130.setText("Tipo de Contrato:");

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1- Indefinido", "2- Temporal", "3- Pasantia" }));

        jLabel131.setFont(new java.awt.Font("Roboto", 3, 16)); // NOI18N
        jLabel131.setText("Empleados Seleccionados: ");

        jTextField41.setEditable(false);

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel66Layout.createSequentialGroup()
                        .addComponent(jLabel131)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel66Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel129)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(jLabel130)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel66Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jCheckBox1)
                .addGap(28, 28, 28)
                .addGroup(jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel130)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel129))
                .addGap(41, 41, 41)
                .addGroup(jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel131)
                    .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel64Layout = new javax.swing.GroupLayout(jPanel64);
        jPanel64.setLayout(jPanel64Layout);
        jPanel64Layout.setHorizontalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                        .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                        .addComponent(jLabel122)
                        .addGap(223, 223, 223))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                        .addComponent(jLabel128)
                        .addGap(254, 254, 254))))
        );
        jPanel64Layout.setVerticalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel122)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel128)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jSeparator7.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setOpaque(true);

        javax.swing.GroupLayout jpnGenNominaLayout = new javax.swing.GroupLayout(jpnGenNomina);
        jpnGenNomina.setLayout(jpnGenNominaLayout);
        jpnGenNominaLayout.setHorizontalGroup(
            jpnGenNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnGenNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnGenNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnGenNominaLayout.createSequentialGroup()
                        .addComponent(jPanel64, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jpnGenNominaLayout.setVerticalGroup(
            jpnGenNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnGenNominaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpnGenNominaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel64, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                    .addComponent(jSeparator7))
                .addContainerGap())
        );

        jpnMain.addTab("Generar Nómina", jpnGenNomina);

        jpnConsultarNom.setBackground(new java.awt.Color(255, 255, 255));

        jLabel136.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        jLabel136.setText("Consultar Nómina");

        jLabel137.setFont(new java.awt.Font("Roboto", 1, 26)); // NOI18N
        jLabel137.setText("Consulta de Períodos y Comprobantes de Nómina");
        jLabel137.setToolTipText("");

        jPanel70.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel139.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel139.setText("Filtros de Búsqueda");

        jLabel140.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel140.setText("Período:");

        jLabel142.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel142.setText("Desde:");

        jTextField44.setEditable(false);
        jTextField44.addActionListener(this::jTextField44ActionPerformed);

        jLabel143.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel143.setText("Estado:");

        jLabel144.setFont(new java.awt.Font("Roboto", 3, 14)); // NOI18N
        jLabel144.setText("Hasta:");

        jTextField47.setEditable(false);

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Mensual", "Quincenal", "Semanal" }));

        jButton27.setText("Limpiar Campos");

        jButton30.setText("Buscar ");

        javax.swing.GroupLayout jPanel70Layout = new javax.swing.GroupLayout(jPanel70);
        jPanel70.setLayout(jPanel70Layout);
        jPanel70Layout.setHorizontalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel70Layout.createSequentialGroup()
                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel70Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel70Layout.createSequentialGroup()
                                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(266, 266, 266)
                                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel144, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField47)
                                    .addComponent(jComboBox15, 0, 120, Short.MAX_VALUE)))
                            .addComponent(jLabel139)))
                    .addGroup(jPanel70Layout.createSequentialGroup()
                        .addGap(270, 270, 270)
                        .addComponent(jButton27)
                        .addGap(103, 103, 103)
                        .addComponent(jButton30)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel70Layout.setVerticalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel70Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel139)
                .addGap(18, 18, 18)
                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel140)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel143)
                    .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel142)
                    .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel144)
                    .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton27)
                    .addComponent(jButton30))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel73.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel145.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel145.setText("Historial de Nóminas");

        tblNominas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Nomina", "Período", "Fecha de Inicio", "Fecha de Fin", "Empleados", "Salario Bruto", "Estado"
            }
        ));
        jScrollPane9.setViewportView(tblNominas);

        javax.swing.GroupLayout jPanel73Layout = new javax.swing.GroupLayout(jPanel73);
        jPanel73.setLayout(jPanel73Layout);
        jPanel73Layout.setHorizontalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel73Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel73Layout.createSequentialGroup()
                        .addComponent(jLabel145)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel73Layout.setVerticalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel73Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel145)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel74.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField49.setEditable(false);

        jLabel149.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel149.setText("Deducciones:");

        jTextField48.setEditable(false);

        jLabel148.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel148.setText("RD$");

        jLabel147.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel147.setText("Bruto Estimado:");

        jTextField46.setEditable(false);

        jLabel146.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel146.setText("Empleados : ");

        jLabel150.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel150.setText("Resumen del Período Seleccionado");

        jLabel151.setFont(new java.awt.Font("sansserif", 2, 14)); // NOI18N
        jLabel151.setText("Salario Neto:");

        jLabel152.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel152.setText("RD$");

        jTextField50.setEditable(false);

        jLabel155.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel155.setText("RD$");

        btnVerDetalles.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        btnVerDetalles.setText("Ver Detalles");
        btnVerDetalles.addActionListener(this::btnVerDetallesActionPerformed);

        jButton32.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton32.setText("Exportar PDF");

        javax.swing.GroupLayout jPanel74Layout = new javax.swing.GroupLayout(jPanel74);
        jPanel74.setLayout(jPanel74Layout);
        jPanel74Layout.setHorizontalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel74Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel74Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel74Layout.createSequentialGroup()
                                .addComponent(jLabel151, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel152)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel74Layout.createSequentialGroup()
                                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnVerDetalles)
                                    .addGroup(jPanel74Layout.createSequentialGroup()
                                        .addComponent(jLabel146)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(95, 95, 95)
                                        .addComponent(jLabel147, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel74Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel148)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(jLabel149)
                                        .addGap(4, 4, 4)
                                        .addComponent(jLabel155)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel74Layout.createSequentialGroup()
                                        .addGap(128, 128, 128)
                                        .addComponent(jButton32))))))
                    .addComponent(jLabel150))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        jPanel74Layout.setVerticalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel74Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel150)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel146)
                    .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel147)
                    .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148)
                    .addComponent(jLabel149)
                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel155))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel151)
                    .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel152))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVerDetalles)
                    .addComponent(jButton32))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel69Layout = new javax.swing.GroupLayout(jPanel69);
        jPanel69.setLayout(jPanel69Layout);
        jPanel69Layout.setHorizontalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel69Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel69Layout.createSequentialGroup()
                        .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel136, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel137))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel69Layout.createSequentialGroup()
                        .addGroup(jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel74, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel73, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel69Layout.setVerticalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel69Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel136)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel137)
                .addGap(19, 19, 19)
                .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(293, 293, 293))
        );

        jPanel76.setBackground(new java.awt.Color(255, 255, 255));
        jPanel76.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153), 2));

        jLabel158.setBackground(new java.awt.Color(204, 255, 255));
        jLabel158.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel158.setText("Nómina -");

        jLabel159.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel159.setText("Desde:");

        jLabel195.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel195.setText("Estado:");

        jLabel196.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel196.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel197.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel197.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel198.setFont(new java.awt.Font("Roboto", 2, 18)); // NOI18N
        jLabel198.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jSeparator14.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator14.setOpaque(true);

        jLabel199.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel199.setText("Hasta:");

        jLabel200.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel200.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel201.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel201.setText("Empleados Procesados:");

        jLabel202.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel202.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel203.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel203.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel204.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel204.setText("Salario Bruto:                 RD$");

        jLabel205.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel205.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel206.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel206.setText("Total Bonificaciones: RD$");

        jLabel207.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel207.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel208.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel208.setText("Total Deducciones:      RD$");

        jLabel209.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel209.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel210.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel210.setText("Salario Neto:                   RD$");

        javax.swing.GroupLayout jPanel76Layout = new javax.swing.GroupLayout(jPanel76);
        jPanel76.setLayout(jPanel76Layout);
        jPanel76Layout.setHorizontalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator14, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel76Layout.createSequentialGroup()
                        .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel195)
                            .addComponent(jLabel159))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel196, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel76Layout.createSequentialGroup()
                                .addComponent(jLabel197, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel199)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel200, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel76Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel158)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel198, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel76Layout.createSequentialGroup()
                        .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel76Layout.createSequentialGroup()
                                .addComponent(jLabel210)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel209, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel76Layout.createSequentialGroup()
                                .addComponent(jLabel208)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel207, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel76Layout.createSequentialGroup()
                                .addComponent(jLabel206)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel205, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel76Layout.createSequentialGroup()
                                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel204, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel201, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel202, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel203, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(89, 89, 89))))
        );
        jPanel76Layout.setVerticalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel158, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel198, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel195)
                    .addComponent(jLabel196, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel159)
                    .addComponent(jLabel197, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel199)
                    .addComponent(jLabel200, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel201)
                    .addComponent(jLabel202, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel204)
                    .addComponent(jLabel203, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel206)
                    .addComponent(jLabel205, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel208)
                    .addComponent(jLabel207, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel210)
                    .addComponent(jLabel209, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(121, 121, 121))
        );

        javax.swing.GroupLayout jPanel71Layout = new javax.swing.GroupLayout(jPanel71);
        jPanel71.setLayout(jPanel71Layout);
        jPanel71Layout.setHorizontalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel71Layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jPanel76, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );
        jPanel71Layout.setVerticalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel71Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jPanel76, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(174, Short.MAX_VALUE))
        );

        jSeparator8.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setOpaque(true);

        javax.swing.GroupLayout jpnConsultarNomLayout = new javax.swing.GroupLayout(jpnConsultarNom);
        jpnConsultarNom.setLayout(jpnConsultarNomLayout);
        jpnConsultarNomLayout.setHorizontalGroup(
            jpnConsultarNomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnConsultarNomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpnConsultarNomLayout.setVerticalGroup(
            jpnConsultarNomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnConsultarNomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnConsultarNomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, 739, Short.MAX_VALUE)
                    .addComponent(jPanel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator8)
        );

        jpnMain.addTab("Consultar Nomina", jpnConsultarNom);

        jpnHorasExt.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnHorasExtLayout = new javax.swing.GroupLayout(jpnHorasExt);
        jpnHorasExt.setLayout(jpnHorasExtLayout);
        jpnHorasExtLayout.setHorizontalGroup(
            jpnHorasExtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnHorasExtLayout.setVerticalGroup(
            jpnHorasExtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("Horas Extras", jpnHorasExt);

        jpnDeducciones.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnDeduccionesLayout = new javax.swing.GroupLayout(jpnDeducciones);
        jpnDeducciones.setLayout(jpnDeduccionesLayout);
        jpnDeduccionesLayout.setHorizontalGroup(
            jpnDeduccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnDeduccionesLayout.setVerticalGroup(
            jpnDeduccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("Deducciones", jpnDeducciones);

        jpnAFP.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(249, 236, 229));

        jLabel8.setFont(new java.awt.Font("Roboto", 3, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(15, 69, 141));
        jLabel8.setText("Administradoras de Fondos y Pensiones");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(946, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(14, 14, 14))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel9.setText("Registrar AFP");

        jLabel62.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel62.setText("Nombre AFP:");

        jLabel63.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel63.setText("RNC:");

        jLabel153.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel153.setText("Estado:");

        jTextField51.setText("\n");

        jTextField52.setText("\n");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton34.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton34.setText("Limpiar Campos");

        jButton35.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton35.setText("Registrar");

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel49Layout.createSequentialGroup()
                            .addComponent(jLabel62)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel49Layout.createSequentialGroup()
                            .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel153, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField52)
                                .addGroup(jPanel49Layout.createSequentialGroup()
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGap(324, 324, 324)
                        .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jButton35)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(jTextField52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel153)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton34)
                    .addComponent(jButton35))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel154.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel154.setText("AFP Registradas");

        jLabel156.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel156.setText("Buscar:");

        jTextField53.setText("\n");

        jLabel157.setText("icon");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre AFP", "RNC", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable3);

        jButton36.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton36.setText("Editar");

        jButton37.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton37.setText("Ver Detalles");

        jButton38.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton38.setText("Activar / Desactivar");

        javax.swing.GroupLayout jPanel72Layout = new javax.swing.GroupLayout(jPanel72);
        jPanel72.setLayout(jPanel72Layout);
        jPanel72Layout.setHorizontalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 904, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel72Layout.createSequentialGroup()
                                .addComponent(jLabel156)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel157))))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(jButton36)
                        .addGap(119, 119, 119)
                        .addComponent(jButton38)
                        .addGap(129, 129, 129)
                        .addComponent(jButton37))
                    .addGroup(jPanel72Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel154, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel72Layout.setVerticalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel154)
                .addGap(18, 18, 18)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel156)
                    .addComponent(jTextField53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel157))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton36)
                    .addComponent(jButton37)
                    .addComponent(jButton38))
                .addGap(34, 34, 34))
        );

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Empleado", "Empleado", "Cédula", "No. Afiliado", "Inicio", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(jTable8);

        jLabel160.setFont(new java.awt.Font("sansserif", 3, 24)); // NOI18N
        jLabel160.setText("Empleados Afiliados -");

        jSeparator15.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator15.setOpaque(true);

        jLabel161.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel161.setText("AFP: ");

        jLabel162.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel162.setText("jLabel162");

        jLabel163.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel163.setText("RNC: ");

        jLabel164.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel164.setText("jLabel164");

        jLabel165.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel165.setText("Estado:");

        jLabel166.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel166.setText("jLabel166");

        jLabel167.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel167.setText("Empleados Afiliados: ");

        jLabel168.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel168.setText("jLabel168");

        jblAFPDetalle.setFont(new java.awt.Font("sansserif", 3, 24)); // NOI18N
        jblAFPDetalle.setText("jLabel169");

        jButton2.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton2.setText("Cerrar");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel160)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jblAFPDetalle))
                    .addGroup(jPanel75Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel75Layout.createSequentialGroup()
                                .addComponent(jLabel161)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel162))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel75Layout.createSequentialGroup()
                                .addComponent(jLabel163)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel164))
                            .addGroup(jPanel75Layout.createSequentialGroup()
                                .addComponent(jLabel165)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel166))
                            .addGroup(jPanel75Layout.createSequentialGroup()
                                .addComponent(jLabel167)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel168)))))
                .addContainerGap(12, Short.MAX_VALUE))
            .addComponent(jSeparator15)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel75Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(273, 273, 273))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel75Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel160)
                    .addComponent(jblAFPDetalle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel161)
                    .addComponent(jLabel162))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel163)
                    .addComponent(jLabel164))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel165)
                    .addComponent(jLabel166))
                .addGap(55, 55, 55)
                .addGroup(jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel167)
                    .addComponent(jLabel168))
                .addGap(36, 36, 36)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jSeparator10.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator10.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator10)
            .addComponent(jPanel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpnAFPLayout = new javax.swing.GroupLayout(jpnAFP);
        jpnAFP.setLayout(jpnAFPLayout);
        jpnAFPLayout.setHorizontalGroup(
            jpnAFPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnAFPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnAFPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpnAFPLayout.setVerticalGroup(
            jpnAFPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnAFPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpnMain.addTab("AFP", jpnAFP);

        jpnARS.setBackground(new java.awt.Color(255, 255, 255));

        jLabel169.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel169.setText("Registrar ARS");

        jLabel170.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel170.setText("Nombre ARS:");

        jLabel171.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel171.setText("RNC:");

        jLabel172.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel172.setText("Estado:");

        jTextField54.setText("\n");

        jTextField55.setText("\n");

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton39.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton39.setText("Limpiar Campos");

        jButton40.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton40.setText("Registrar");

        javax.swing.GroupLayout jPanel77Layout = new javax.swing.GroupLayout(jPanel77);
        jPanel77.setLayout(jPanel77Layout);
        jPanel77Layout.setHorizontalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel77Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel169, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel77Layout.createSequentialGroup()
                            .addComponent(jLabel170)
                            .addGap(5, 5, 5)
                            .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel77Layout.createSequentialGroup()
                            .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel171, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel172, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField55)
                                .addGroup(jPanel77Layout.createSequentialGroup()
                                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE)))))
                    .addGroup(jPanel77Layout.createSequentialGroup()
                        .addGap(324, 324, 324)
                        .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jButton40)))
                .addContainerGap(328, Short.MAX_VALUE))
        );
        jPanel77Layout.setVerticalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel77Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel169)
                .addGap(18, 18, 18)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel170)
                    .addComponent(jTextField54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel171)
                    .addComponent(jTextField55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel172)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton39)
                    .addComponent(jButton40))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Empleado", "Empleado", "Cédula", "No. Afiliado", "Inicio", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(jTable9);

        jLabel173.setFont(new java.awt.Font("sansserif", 3, 24)); // NOI18N
        jLabel173.setText("Empleados Afiliados -");

        jSeparator16.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator16.setOpaque(true);

        jLabel174.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel174.setText("ARS: ");

        jLabel175.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel175.setText("jLabel162");

        jLabel176.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel176.setText("RNC: ");

        jLabel177.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel177.setText("jLabel164");

        jLabel178.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel178.setText("Estado:");

        jLabel179.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel179.setText("jLabel166");

        jLabel180.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel180.setText("Empleados Afiliados: ");

        jLabel181.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel181.setText("jLabel168");

        jblAFPDetalle1.setFont(new java.awt.Font("sansserif", 3, 24)); // NOI18N
        jblAFPDetalle1.setText("jLabel169");

        jButton33.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton33.setText("Cerrar");
        jButton33.addActionListener(this::jButton33ActionPerformed);

        javax.swing.GroupLayout jPanel78Layout = new javax.swing.GroupLayout(jPanel78);
        jPanel78.setLayout(jPanel78Layout);
        jPanel78Layout.setHorizontalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel78Layout.createSequentialGroup()
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel78Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel173)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jblAFPDetalle1))
                    .addGroup(jPanel78Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel78Layout.createSequentialGroup()
                                .addComponent(jLabel174)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel175))
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel78Layout.createSequentialGroup()
                                .addComponent(jLabel176)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel177))
                            .addGroup(jPanel78Layout.createSequentialGroup()
                                .addComponent(jLabel178)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel179))
                            .addGroup(jPanel78Layout.createSequentialGroup()
                                .addComponent(jLabel180)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel181)))))
                .addContainerGap(16, Short.MAX_VALUE))
            .addComponent(jSeparator16)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel78Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton33)
                .addGap(275, 275, 275))
        );
        jPanel78Layout.setVerticalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel78Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel173)
                    .addComponent(jblAFPDetalle1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel174)
                    .addComponent(jLabel175))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel176)
                    .addComponent(jLabel177))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel178)
                    .addComponent(jLabel179))
                .addGap(42, 42, 42)
                .addGroup(jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel180)
                    .addComponent(jLabel181))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jButton33)
                .addGap(20, 20, 20))
        );

        jLabel182.setFont(new java.awt.Font("Roboto", 3, 18)); // NOI18N
        jLabel182.setText("ARS Registradas");

        jLabel183.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel183.setText("Buscar:");

        jTextField56.setText("\n");

        jLabel184.setText("icon");

        jTable10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nombre AFP", "RNC", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane12.setViewportView(jTable10);

        jButton41.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton41.setText("Editar");

        jButton42.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton42.setText("Ver Detalles");

        jButton43.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton43.setText("Activar / Desactivar");

        javax.swing.GroupLayout jPanel79Layout = new javax.swing.GroupLayout(jPanel79);
        jPanel79.setLayout(jPanel79Layout);
        jPanel79Layout.setHorizontalGroup(
            jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel79Layout.createSequentialGroup()
                .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 904, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel79Layout.createSequentialGroup()
                                .addComponent(jLabel183)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel184))))
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(jButton41)
                        .addGap(119, 119, 119)
                        .addComponent(jButton43)
                        .addGap(129, 129, 129)
                        .addComponent(jButton42))
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel182, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel79Layout.setVerticalGroup(
            jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel79Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel182)
                .addGap(18, 18, 18)
                .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel183)
                    .addComponent(jTextField56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel184))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton41)
                    .addComponent(jButton42)
                    .addComponent(jButton43))
                .addGap(34, 34, 34))
        );

        jLabel185.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel185.setText("Administradoras de Riesgo de Salud");

        javax.swing.GroupLayout jPanel80Layout = new javax.swing.GroupLayout(jPanel80);
        jPanel80.setLayout(jPanel80Layout);
        jPanel80Layout.setHorizontalGroup(
            jPanel80Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel80Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel185, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel80Layout.setVerticalGroup(
            jPanel80Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel80Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel185, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jSeparator11.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator11.setOpaque(true);

        javax.swing.GroupLayout jpnARSLayout = new javax.swing.GroupLayout(jpnARS);
        jpnARS.setLayout(jpnARSLayout);
        jpnARSLayout.setHorizontalGroup(
            jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnARSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnARSLayout.createSequentialGroup()
                        .addGroup(jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel77, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel79, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnARSLayout.setVerticalGroup(
            jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnARSLayout.createSequentialGroup()
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jPanel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnARSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpnARSLayout.createSequentialGroup()
                            .addComponent(jPanel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator11))
                .addContainerGap())
        );

        jpnMain.addTab("ARS", jpnARS);

        jpnISR.setBackground(new java.awt.Color(255, 255, 255));

        jLabel194.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel194.setText("ISR");

        jLabel211.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel211.setText("Escalas de Impuesto Sobre la Renta");

        javax.swing.GroupLayout jPanel81Layout = new javax.swing.GroupLayout(jPanel81);
        jPanel81.setLayout(jPanel81Layout);
        jPanel81Layout.setHorizontalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel211)
                    .addComponent(jLabel194))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel81Layout.setVerticalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel194)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel211)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel212.setFont(new java.awt.Font("sansserif", 3, 18)); // NOI18N
        jLabel212.setText("Configuración de Escala");

        jLabel213.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel213.setText("Límite Inferior");

        jLabel214.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel214.setText("Límite Superior");

        jLabel215.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel215.setText("Porcentaje");

        jLabel216.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel216.setText("Monto Fijo");

        jLabel217.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel217.setText("Exceso sobre");

        jLabel218.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel218.setText("Fecha desde");

        jLabel219.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel219.setText("Fecha hasta");

        jButton48.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton48.setText("Limpiar");

        jButton49.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton49.setText("Guardar Escala");

        jLabel220.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel220.setText("RD$");

        jTextField61.addActionListener(this::jTextField61ActionPerformed);

        jLabel221.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel221.setText("RD$");

        jTextField62.addActionListener(this::jTextField62ActionPerformed);

        jLabel222.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel222.setText("RD$");

        jTextField63.addActionListener(this::jTextField63ActionPerformed);

        jLabel223.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel223.setText("RD$");

        jTextField64.addActionListener(this::jTextField64ActionPerformed);

        jTextField65.addActionListener(this::jTextField65ActionPerformed);

        jLabel224.setText("%");

        javax.swing.GroupLayout jPanel84Layout = new javax.swing.GroupLayout(jPanel84);
        jPanel84.setLayout(jPanel84Layout);
        jPanel84Layout.setHorizontalGroup(
            jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel84Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel84Layout.createSequentialGroup()
                        .addComponent(jLabel213)
                        .addGap(40, 40, 40)
                        .addComponent(jLabel220)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel84Layout.createSequentialGroup()
                        .addComponent(jLabel214)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel221)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel212)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel84Layout.createSequentialGroup()
                        .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel216)
                            .addComponent(jLabel217)
                            .addComponent(jLabel215))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel84Layout.createSequentialGroup()
                                .addComponent(jLabel223)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel84Layout.createSequentialGroup()
                                .addComponent(jLabel222)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel84Layout.createSequentialGroup()
                                        .addComponent(jTextField65)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel224))
                                    .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel84Layout.createSequentialGroup()
                        .addComponent(jLabel219)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel84Layout.createSequentialGroup()
                        .addComponent(jLabel218)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(318, 318, 318))
            .addGroup(jPanel84Layout.createSequentialGroup()
                .addGap(411, 411, 411)
                .addComponent(jButton48)
                .addGap(51, 51, 51)
                .addComponent(jButton49)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel84Layout.setVerticalGroup(
            jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel84Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel212)
                .addGap(27, 27, 27)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel213)
                    .addComponent(jLabel218)
                    .addComponent(jLabel220)
                    .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel221)
                        .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel214)
                        .addComponent(jLabel219)
                        .addComponent(jTextField67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel215)
                    .addComponent(jTextField65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel224))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel222)
                        .addComponent(jTextField63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel216))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel223)
                        .addComponent(jTextField64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel217))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton48)
                    .addComponent(jButton49))
                .addGap(27, 27, 27))
        );

        jLabel225.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel225.setText("Escalas ISR");

        jTable11.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Desde", "Hasta", "Porcentaje", "Monto Fijo", "Vigencia"
            }
        ));
        jScrollPane13.setViewportView(jTable11);

        jButton50.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton50.setText("Editar");

        jButton51.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton51.setText("Actualizar");

        jButton52.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton52.setText("Eliminar");

        javax.swing.GroupLayout jPanel85Layout = new javax.swing.GroupLayout(jPanel85);
        jPanel85.setLayout(jPanel85Layout);
        jPanel85Layout.setHorizontalGroup(
            jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel85Layout.createSequentialGroup()
                .addGroup(jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel85Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel225)))
                    .addGroup(jPanel85Layout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(jButton50)
                        .addGap(108, 108, 108)
                        .addComponent(jButton51)
                        .addGap(109, 109, 109)
                        .addComponent(jButton52)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel85Layout.setVerticalGroup(
            jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel85Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel225)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton50)
                    .addComponent(jButton51)
                    .addComponent(jButton52))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout jpnISRLayout = new javax.swing.GroupLayout(jpnISR);
        jpnISR.setLayout(jpnISRLayout);
        jpnISRLayout.setHorizontalGroup(
            jpnISRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnISRLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnISRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnISRLayout.createSequentialGroup()
                        .addGroup(jpnISRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 585, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnISRLayout.setVerticalGroup(
            jpnISRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnISRLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel84, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpnMain.addTab("ISR", jpnISR);

        jpnTSS.setBackground(new java.awt.Color(255, 255, 255));

        jLabel227.setText("Parámetros de la Tesorería de la Seguridad Social");

        javax.swing.GroupLayout jPanel89Layout = new javax.swing.GroupLayout(jPanel89);
        jPanel89.setLayout(jPanel89Layout);
        jPanel89Layout.setHorizontalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel89Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel227)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel89Layout.setVerticalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel89Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel227)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jLabel228.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel228.setText("Configuración de la TSS Actual ");

        jSeparator9.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator9.setOpaque(true);

        jLabel226.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel226.setText("Vigencia:");

        jLabel229.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel229.setText("Estado:");

        jLabel230.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel230.setText("Referencia Salarial Nacional:");

        jLabel231.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel231.setText("Tope Cotizable Pensión:");

        jLabel232.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel232.setText("Tope Cotizable Salud:");

        jLabel233.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel233.setText("Tope Riesgo Laborales:");

        jSeparator13.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator13.setOpaque(true);

        jLabel234.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel234.setText("Aportes");

        jLabel235.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel235.setText("Salud Empleado");

        jLabel236.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel236.setText("Salud Empleador");

        jLabel237.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel237.setText("AFP Empleado");

        jLabel238.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel238.setText("AFP Empleador");

        jLabel239.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel239.setText("Riesgos Laborales");

        jLabel240.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel240.setText("INFOTEP Empleador");

        jLabel242.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel242.setText("RD$");

        jTextField71.addActionListener(this::jTextField71ActionPerformed);

        jLabel243.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel243.setText("RD$");

        jTextField72.addActionListener(this::jTextField72ActionPerformed);

        jLabel244.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel244.setText("RD$");

        jTextField73.addActionListener(this::jTextField73ActionPerformed);

        jLabel245.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel245.setText("RD$");

        jTextField74.addActionListener(this::jTextField74ActionPerformed);

        jTextField75.setEditable(false);
        jTextField75.addActionListener(this::jTextField75ActionPerformed);

        jLabel246.setText("%");

        jTextField76.setEditable(false);
        jTextField76.addActionListener(this::jTextField76ActionPerformed);

        jLabel247.setText("%");

        jTextField77.setEditable(false);
        jTextField77.addActionListener(this::jTextField77ActionPerformed);

        jLabel248.setText("%");

        jTextField78.setEditable(false);
        jTextField78.addActionListener(this::jTextField78ActionPerformed);

        jLabel249.setText("%");

        jTextField79.setEditable(false);
        jTextField79.addActionListener(this::jTextField79ActionPerformed);

        jLabel250.setText("%");

        jTextField80.setEditable(false);
        jTextField80.addActionListener(this::jTextField80ActionPerformed);

        jLabel251.setText("%");

        jLabel252.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel252.setText("-");

        javax.swing.GroupLayout jPanel90Layout = new javax.swing.GroupLayout(jPanel90);
        jPanel90.setLayout(jPanel90Layout);
        jPanel90Layout.setHorizontalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel90Layout.createSequentialGroup()
                                    .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel233)
                                            .addComponent(jLabel231))
                                        .addComponent(jLabel232))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel90Layout.createSequentialGroup()
                                            .addComponent(jLabel243)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jTextField72, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel90Layout.createSequentialGroup()
                                            .addComponent(jLabel244)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jTextField73, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel90Layout.createSequentialGroup()
                                            .addComponent(jLabel245)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jTextField74, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel90Layout.createSequentialGroup()
                                    .addComponent(jLabel230)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel242)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel90Layout.createSequentialGroup()
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel90Layout.createSequentialGroup()
                                        .addComponent(jLabel229)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel90Layout.createSequentialGroup()
                                        .addComponent(jLabel226)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel252, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel90Layout.createSequentialGroup()
                                .addComponent(jLabel240)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField80)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel251))
                            .addGroup(jPanel90Layout.createSequentialGroup()
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel235)
                                    .addComponent(jLabel236)
                                    .addComponent(jLabel237)
                                    .addComponent(jLabel238)
                                    .addComponent(jLabel239))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel90Layout.createSequentialGroup()
                                        .addComponent(jTextField79)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel250))
                                    .addGroup(jPanel90Layout.createSequentialGroup()
                                        .addComponent(jTextField78)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel249))
                                    .addGroup(jPanel90Layout.createSequentialGroup()
                                        .addComponent(jTextField77)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel248))
                                    .addGroup(jPanel90Layout.createSequentialGroup()
                                        .addComponent(jTextField76)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel247))
                                    .addGroup(jPanel90Layout.createSequentialGroup()
                                        .addComponent(jTextField75)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel246)))))))
                .addGap(223, 223, 223))
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addComponent(jSeparator13)
                        .addContainerGap())
                    .addComponent(jSeparator9)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createSequentialGroup()
                        .addComponent(jLabel228)
                        .addGap(146, 146, 146))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createSequentialGroup()
                        .addComponent(jLabel234)
                        .addGap(259, 259, 259))))
        );
        jPanel90Layout.setVerticalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel228)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel226)
                    .addComponent(jTextField68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel252)
                    .addComponent(jTextField69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel229)
                    .addComponent(jTextField70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel230)
                    .addComponent(jLabel242)
                    .addComponent(jTextField71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addComponent(jLabel232)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel231)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel233))
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel243)
                            .addComponent(jTextField72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel244)
                            .addComponent(jTextField73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel245)
                            .addComponent(jTextField74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel234)
                .addGap(27, 27, 27)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createSequentialGroup()
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createSequentialGroup()
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel235)
                                    .addComponent(jTextField75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel246))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel236)
                                    .addComponent(jTextField76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel247))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel237)
                                    .addComponent(jTextField77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel248))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel238))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel249)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel239))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel250)))
                .addGap(18, 18, 18)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel240)
                    .addComponent(jTextField80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel251))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator12.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator12.setOpaque(true);

        jLabel241.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel241.setText("Historial de Configuraciones TSS");

        jTable12.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha desde", "Fecha Hasta", "Referencia Salarial Nac", "Estado", "Acción"
            }
        ));
        jScrollPane14.setViewportView(jTable12);

        jButton53.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton53.setText("Nueva TSS");

        javax.swing.GroupLayout jPanel91Layout = new javax.swing.GroupLayout(jPanel91);
        jPanel91.setLayout(jPanel91Layout);
        jPanel91Layout.setHorizontalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14)
                .addContainerGap())
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabel241)
                .addContainerGap(120, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel91Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton53)
                .addGap(231, 231, 231))
        );
        jPanel91Layout.setVerticalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jLabel241)
                .addGap(74, 74, 74)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton53)
                .addGap(32, 32, 32))
        );

        jLabel253.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel253.setText("Salud Empleado");

        jLabel254.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel254.setText("Salud Empleador");

        jLabel255.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel255.setText("AFP Empleado");

        jLabel256.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel256.setText("AFP Empleador");

        jLabel257.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel257.setText("Riesgos Laborales");

        jLabel258.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel258.setText("INFOTEP Empleador");

        jTextField81.setEditable(false);
        jTextField81.addActionListener(this::jTextField81ActionPerformed);

        jTextField82.setEditable(false);
        jTextField82.addActionListener(this::jTextField82ActionPerformed);

        jTextField83.setEditable(false);
        jTextField83.addActionListener(this::jTextField83ActionPerformed);

        jTextField84.setEditable(false);
        jTextField84.addActionListener(this::jTextField84ActionPerformed);

        jTextField85.setEditable(false);
        jTextField85.addActionListener(this::jTextField85ActionPerformed);

        jTextField86.setEditable(false);
        jTextField86.addActionListener(this::jTextField86ActionPerformed);

        jLabel259.setText("%");

        jLabel260.setText("%");

        jLabel261.setText("%");

        jLabel262.setText("%");

        jLabel263.setText("%");

        jLabel264.setText("%");

        jLabel265.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel265.setText("Tope Riesgo Laborales:");

        jLabel266.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel266.setText("Tope Cotizable Pensión:");

        jLabel267.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel267.setText("Tope Cotizable Salud:");

        jTextField87.addActionListener(this::jTextField87ActionPerformed);

        jLabel268.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel268.setText("RD$");

        jLabel269.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel269.setText("RD$");

        jTextField88.addActionListener(this::jTextField88ActionPerformed);

        jTextField89.addActionListener(this::jTextField89ActionPerformed);

        jLabel270.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel270.setText("RD$");

        jLabel271.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel271.setText("Vigencia");

        jLabel273.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel273.setText("Porcentajes");

        jLabel274.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel274.setText("Topes Cotizables");

        jLabel272.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel272.setText("Desde:");

        jLabel275.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel275.setText("Hasta:");

        jLabel276.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel276.setText("Nueva TSS");

        jButton54.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton54.setText("Cancelar");

        jButton55.setFont(new java.awt.Font("sansserif", 3, 12)); // NOI18N
        jButton55.setText("Guardar Cambios");
        jButton55.addActionListener(this::jButton55ActionPerformed);

        jLabel277.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel277.setText("Referencia Salarial Nacional:");

        jTextField92.addActionListener(this::jTextField92ActionPerformed);

        jLabel278.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel278.setText("RD$");

        javax.swing.GroupLayout jPanel92Layout = new javax.swing.GroupLayout(jPanel92);
        jPanel92.setLayout(jPanel92Layout);
        jPanel92Layout.setHorizontalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel276)
                .addGap(129, 129, 129))
            .addGroup(jPanel92Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel92Layout.createSequentialGroup()
                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel271)
                            .addComponent(jLabel274)
                            .addGroup(jPanel92Layout.createSequentialGroup()
                                .addComponent(jLabel272)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel275)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField91, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel92Layout.createSequentialGroup()
                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel92Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel253)
                                            .addComponent(jLabel254)
                                            .addComponent(jLabel255)
                                            .addComponent(jLabel256)
                                            .addComponent(jLabel257))
                                        .addGap(28, 28, 28)
                                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addComponent(jTextField85)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel260))
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addComponent(jTextField84)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel261))
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addComponent(jTextField83)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel262))
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addComponent(jTextField82)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel263))
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addComponent(jTextField81)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel264))))
                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel92Layout.createSequentialGroup()
                                                .addGap(46, 46, 46)
                                                .addComponent(jButton54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel258))
                                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jTextField86)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel259))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                                                .addGap(63, 63, 63)
                                                .addComponent(jButton55)
                                                .addGap(49, 49, 49))))))
                            .addGroup(jPanel92Layout.createSequentialGroup()
                                .addComponent(jLabel273)
                                .addGap(41, 41, 41))
                            .addGroup(jPanel92Layout.createSequentialGroup()
                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel266)
                                            .addComponent(jLabel265)
                                            .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel92Layout.createSequentialGroup()
                                                    .addComponent(jLabel268)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jTextField87, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(jLabel267))
                                            .addGroup(jPanel92Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                                        .addComponent(jLabel269)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField88, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                                        .addComponent(jLabel270)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                    .addGroup(jPanel92Layout.createSequentialGroup()
                                        .addComponent(jLabel277)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel278)))
                                .addGap(4, 4, 4)
                                .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel92Layout.setVerticalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel276)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel271)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel272)
                    .addComponent(jTextField90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel275)
                    .addComponent(jTextField91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel277)
                    .addComponent(jTextField92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel278))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jLabel274)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel267)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel268)
                    .addComponent(jTextField87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel266)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel269)
                    .addComponent(jTextField88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel265)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel270)
                    .addComponent(jTextField89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel273)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                        .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel253)
                                    .addComponent(jTextField81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel264))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel254)
                                    .addComponent(jTextField82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel263))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel255)
                                    .addComponent(jTextField83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel262))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel256))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel261)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel257))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel260)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel258)
                    .addComponent(jTextField86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel259))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton54)
                    .addComponent(jButton55))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout jpnTSSLayout = new javax.swing.GroupLayout(jpnTSS);
        jpnTSS.setLayout(jpnTSSLayout);
        jpnTSSLayout.setHorizontalGroup(
            jpnTSSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnTSSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpnTSSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel89, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpnTSSLayout.createSequentialGroup()
                        .addComponent(jPanel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel92, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpnTSSLayout.setVerticalGroup(
            jpnTSSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnTSSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnTSSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator12)
                    .addComponent(jPanel90, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel91, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel92, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jpnMain.addTab("TSS", jpnTSS);

        jpnAsistencias.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnAsistenciasLayout = new javax.swing.GroupLayout(jpnAsistencias);
        jpnAsistencias.setLayout(jpnAsistenciasLayout);
        jpnAsistenciasLayout.setHorizontalGroup(
            jpnAsistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnAsistenciasLayout.setVerticalGroup(
            jpnAsistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("ASISTENCIAS", jpnAsistencias);

        jpnVacaciones.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnVacacionesLayout = new javax.swing.GroupLayout(jpnVacaciones);
        jpnVacaciones.setLayout(jpnVacacionesLayout);
        jpnVacacionesLayout.setHorizontalGroup(
            jpnVacacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnVacacionesLayout.setVerticalGroup(
            jpnVacacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("VACACIONES", jpnVacaciones);

        jpnLicencias.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnLicenciasLayout = new javax.swing.GroupLayout(jpnLicencias);
        jpnLicencias.setLayout(jpnLicenciasLayout);
        jpnLicenciasLayout.setHorizontalGroup(
            jpnLicenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnLicenciasLayout.setVerticalGroup(
            jpnLicenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("LICENCIAS ", jpnLicencias);

        jpnPermisos.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnPermisosLayout = new javax.swing.GroupLayout(jpnPermisos);
        jpnPermisos.setLayout(jpnPermisosLayout);
        jpnPermisosLayout.setHorizontalGroup(
            jpnPermisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpnPermisosLayout.setVerticalGroup(
            jpnPermisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("PERMISOS", jpnPermisos);

        jpnRegistrarPago.setBackground(new java.awt.Color(255, 255, 255));

        jPanel97.setBackground(new java.awt.Color(255, 255, 255));

        jLabel290.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel290.setText("Pagos");

        javax.swing.GroupLayout jPanel97Layout = new javax.swing.GroupLayout(jPanel97);
        jPanel97.setLayout(jPanel97Layout);
        jPanel97Layout.setHorizontalGroup(
            jPanel97Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel97Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel290)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel97Layout.setVerticalGroup(
            jPanel97Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel97Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel290)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel291.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel291.setText("Pagos del Período");

        jPanel99.setBackground(new java.awt.Color(255, 255, 255));
        jPanel99.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));

        jLabel292.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel292.setText("Pagos");

        jLabel293.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel293.setText("Realizados");

        jLabel294.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel99Layout = new javax.swing.GroupLayout(jPanel99);
        jPanel99.setLayout(jPanel99Layout);
        jPanel99Layout.setHorizontalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel99Layout.createSequentialGroup()
                .addGroup(jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel99Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel292))
                    .addGroup(jPanel99Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel294, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel293))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel99Layout.setVerticalGroup(
            jPanel99Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel99Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel292)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel293)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jLabel294, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        jPanel100.setBackground(new java.awt.Color(255, 255, 255));
        jPanel100.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));

        jLabel295.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel295.setText("Pagado");

        jLabel296.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel296.setText("Monto");

        jLabel297.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel100Layout = new javax.swing.GroupLayout(jPanel100);
        jPanel100.setLayout(jPanel100Layout);
        jPanel100Layout.setHorizontalGroup(
            jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel100Layout.createSequentialGroup()
                .addGroup(jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel100Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel295)
                            .addComponent(jLabel296)))
                    .addGroup(jPanel100Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel297, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel100Layout.setVerticalGroup(
            jPanel100Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel100Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel296)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel295)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel297, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jPanel101.setBackground(new java.awt.Color(255, 255, 255));
        jPanel101.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));

        jLabel298.setFont(new java.awt.Font("sansserif", 3, 14)); // NOI18N
        jLabel298.setText("Pendiente");

        jLabel299.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel101Layout = new javax.swing.GroupLayout(jPanel101);
        jPanel101.setLayout(jPanel101Layout);
        jPanel101Layout.setHorizontalGroup(
            jPanel101Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel101Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel101Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel298, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel299, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel101Layout.setVerticalGroup(
            jPanel101Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel101Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel298)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel299, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jLabel300.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel300.setText("Últimos Pagos");

        jTable14.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Nómina", "Método", "Monto", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane16.setViewportView(jTable14);

        jButton57.setText("Registrar Pago");

        javax.swing.GroupLayout jPanel98Layout = new javax.swing.GroupLayout(jPanel98);
        jPanel98.setLayout(jPanel98Layout);
        jPanel98Layout.setHorizontalGroup(
            jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel98Layout.createSequentialGroup()
                .addGroup(jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel98Layout.createSequentialGroup()
                        .addGroup(jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel98Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel291))
                            .addGroup(jPanel98Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel300)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel98Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel98Layout.createSequentialGroup()
                                .addComponent(jPanel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                                .addComponent(jPanel100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(152, 152, 152)
                                .addComponent(jPanel101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane16, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
            .addGroup(jPanel98Layout.createSequentialGroup()
                .addGap(280, 280, 280)
                .addComponent(jButton57)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel98Layout.setVerticalGroup(
            jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel98Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel291)
                .addGap(18, 18, 18)
                .addGroup(jPanel98Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel101, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel99, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel300)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton57)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel301.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel301.setText("Historial de Pagos");

        jTable15.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nómina", "Fecha", "Método", "Monto", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane17.setViewportView(jTable15);

        jLabel302.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel302.setText("Desde:");

        jLabel303.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel303.setText("Hasta:");

        jLabel304.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel304.setText("Buscar:");

        jLabel306.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel306.setText("Estado:");

        jLabel307.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel307.setText("Método:");

        jButton58.setText("Buscar");

        jButton59.setText("Ver Comprobante");

        jButton60.setText("Ver Detalle");

        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel102Layout = new javax.swing.GroupLayout(jPanel102);
        jPanel102.setLayout(jPanel102Layout);
        jPanel102Layout.setHorizontalGroup(
            jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel102Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel102Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel102Layout.createSequentialGroup()
                        .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel102Layout.createSequentialGroup()
                                .addComponent(jLabel304)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel301))
                        .addContainerGap())
                    .addGroup(jPanel102Layout.createSequentialGroup()
                        .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel102Layout.createSequentialGroup()
                                .addComponent(jLabel307)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox19, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel306))
                            .addGroup(jPanel102Layout.createSequentialGroup()
                                .addComponent(jLabel302)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel303)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel102Layout.createSequentialGroup()
                                .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton58)
                                .addGap(157, 157, 157))
                            .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel102Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton60)
                .addGap(74, 74, 74)
                .addComponent(jButton59)
                .addGap(207, 207, 207))
        );
        jPanel102Layout.setVerticalGroup(
            jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel102Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel301)
                .addGap(31, 31, 31)
                .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel304)
                    .addComponent(jTextField95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel302)
                    .addComponent(jTextField93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel303)
                    .addComponent(jTextField94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel306)
                    .addComponent(jButton58)
                    .addComponent(jLabel307)
                    .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel102Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton59)
                    .addComponent(jButton60))
                .addGap(60, 60, 60))
        );

        javax.swing.GroupLayout jpnRegistrarPagoLayout = new javax.swing.GroupLayout(jpnRegistrarPago);
        jpnRegistrarPago.setLayout(jpnRegistrarPagoLayout);
        jpnRegistrarPagoLayout.setHorizontalGroup(
            jpnRegistrarPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnRegistrarPagoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jpnRegistrarPagoLayout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jPanel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                .addComponent(jPanel102, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jpnRegistrarPagoLayout.setVerticalGroup(
            jpnRegistrarPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnRegistrarPagoLayout.createSequentialGroup()
                .addComponent(jPanel97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpnRegistrarPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel98, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jpnMain.addTab("Registrar Pago", jpnRegistrarPago);

        jpnReportes.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpnReportesLayout = new javax.swing.GroupLayout(jpnReportes);
        jpnReportes.setLayout(jpnReportesLayout);
        jpnReportesLayout.setHorizontalGroup(
            jpnReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1640, Short.MAX_VALUE)
        );
        jpnReportesLayout.setVerticalGroup(
            jpnReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );

        jpnMain.addTab("REPORTES", jpnReportes);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel82.setBackground(new java.awt.Color(255, 255, 255));

        jPanel83.setBackground(new java.awt.Color(9, 144, 120));
        jPanel83.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel186.setFont(new java.awt.Font("Roboto", 3, 36)); // NOI18N
        jLabel186.setForeground(new java.awt.Color(255, 255, 255));
        jLabel186.setText("Administrar");
        jPanel83.add(jLabel186, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 210, -1));

        jLabel187.setFont(new java.awt.Font("Roboto", 3, 36)); // NOI18N
        jLabel187.setForeground(new java.awt.Color(255, 255, 255));
        jLabel187.setText("Usuarios");
        jPanel83.add(jLabel187, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 160, -1));

        jLabel188.setText("Logo");
        jPanel83.add(jLabel188, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jLabel189.setFont(new java.awt.Font("sansserif", 1, 30)); // NOI18N
        jLabel189.setText("Gestor de Usuarios");

        jLabel190.setFont(new java.awt.Font("sansserif", 0, 20)); // NOI18N
        jLabel190.setText("Este espacio permite agregar y configurar los");

        jLabel191.setText("Nombre_Usuario");

        jLabel192.setText("Usuario");

        jLabel193.setText("Rol");

        javax.swing.GroupLayout jPanel87Layout = new javax.swing.GroupLayout(jPanel87);
        jPanel87.setLayout(jPanel87Layout);
        jPanel87Layout.setHorizontalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel87Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel87Layout.createSequentialGroup()
                        .addComponent(jLabel191)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel87Layout.createSequentialGroup()
                        .addComponent(jLabel192)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addComponent(jLabel193, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel87Layout.setVerticalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel87Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel191)
                .addGroup(jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel87Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel192))
                    .addGroup(jPanel87Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel193)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel279.setText("Nombre_Usuario");

        jLabel280.setText("Usuario");

        jLabel281.setText("Rol");

        javax.swing.GroupLayout jPanel88Layout = new javax.swing.GroupLayout(jPanel88);
        jPanel88.setLayout(jPanel88Layout);
        jPanel88Layout.setHorizontalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel88Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel88Layout.createSequentialGroup()
                        .addComponent(jLabel279)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel88Layout.createSequentialGroup()
                        .addComponent(jLabel280)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addComponent(jLabel281, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel88Layout.setVerticalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel88Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel279)
                .addGroup(jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel88Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel280))
                    .addGroup(jPanel88Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel281)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel282.setText("Nombre_Usuario");

        jLabel283.setText("Usuario");

        jLabel284.setText("Rol");

        javax.swing.GroupLayout jPanel93Layout = new javax.swing.GroupLayout(jPanel93);
        jPanel93.setLayout(jPanel93Layout);
        jPanel93Layout.setHorizontalGroup(
            jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel93Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel93Layout.createSequentialGroup()
                        .addComponent(jLabel282)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel93Layout.createSequentialGroup()
                        .addComponent(jLabel283)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addComponent(jLabel284, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel93Layout.setVerticalGroup(
            jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel93Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel282)
                .addGroup(jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel93Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel283))
                    .addGroup(jPanel93Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel284)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel285.setText("Nombre_Usuario");

        jLabel286.setText("Usuario");

        jLabel287.setText("Rol");

        javax.swing.GroupLayout jPanel94Layout = new javax.swing.GroupLayout(jPanel94);
        jPanel94.setLayout(jPanel94Layout);
        jPanel94Layout.setHorizontalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel94Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel94Layout.createSequentialGroup()
                        .addComponent(jLabel285)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel94Layout.createSequentialGroup()
                        .addComponent(jLabel286)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addComponent(jLabel287, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel94Layout.setVerticalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel94Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel285)
                .addGroup(jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel94Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel286))
                    .addGroup(jPanel94Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel287)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton44.setText("Agregar Usuario");

        jLabel288.setFont(new java.awt.Font("sansserif", 0, 20)); // NOI18N
        jLabel288.setText("usuarios del programa.");

        jButton45.setText("Cerrar");
        jButton45.addActionListener(this::jButton45ActionPerformed);

        javax.swing.GroupLayout jPanel82Layout = new javax.swing.GroupLayout(jPanel82);
        jPanel82.setLayout(jPanel82Layout);
        jPanel82Layout.setHorizontalGroup(
            jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel82Layout.createSequentialGroup()
                .addComponent(jPanel83, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel82Layout.createSequentialGroup()
                        .addGroup(jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel189, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel288)
                            .addComponent(jPanel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel82Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(jButton45)
                                .addGap(57, 57, 57)
                                .addComponent(jButton44)))
                        .addGap(60, 60, 60))
                    .addGroup(jPanel82Layout.createSequentialGroup()
                        .addComponent(jLabel190, javax.swing.GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel82Layout.setVerticalGroup(
            jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel83, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel82Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel189)
                .addGap(18, 18, 18)
                .addComponent(jLabel190)
                .addGap(4, 4, 4)
                .addComponent(jLabel288, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jPanel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jPanel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jPanel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jPanel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton45)
                    .addComponent(jButton44)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel82, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel82, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jpnMain.addTab("CONFIGURACION", jPanel4);

        jPanel40.setBackground(new java.awt.Color(123, 216, 230));

        jLabel11.setFont(new java.awt.Font("sansserif", 3, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(15, 69, 141));
        jLabel11.setText("¡Bienvenido!");

        txtBienvenidoUser.setFont(new java.awt.Font("sansserif", 3, 36)); // NOI18N
        txtBienvenidoUser.setForeground(new java.awt.Color(15, 69, 141));
        txtBienvenidoUser.setText("Michael Alexander Cabrera Feliz");

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBienvenidoUser)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(txtBienvenidoUser)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpnMain, javax.swing.GroupLayout.PREFERRED_SIZE, 1646, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnMain, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1072, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.TRAILING, 1926, 1926, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      cerrarSesion();
    }//GEN-LAST:event_jButton1ActionPerformed
    private void cerrarSesion(){
       
    int respuesta = JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro de que desea cerrar la sesión?",
        "Cerrar sesión",
        JOptionPane.YES_NO_OPTION
    );

    if (respuesta == JOptionPane.YES_OPTION) {

        dispose();

        Login1 login = new Login1();
        login.setLocationRelativeTo(null);
        login.setVisible(true);

        new ControladorLogin(login);
    }
    } 
    private void jTextField92ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField92ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField92ActionPerformed

    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton55ActionPerformed

    private void jTextField89ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField89ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField89ActionPerformed

    private void jTextField88ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField88ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField88ActionPerformed

    private void jTextField87ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField87ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField87ActionPerformed

    private void jTextField86ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField86ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField86ActionPerformed

    private void jTextField85ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField85ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField85ActionPerformed

    private void jTextField84ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField84ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField84ActionPerformed

    private void jTextField83ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField83ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField83ActionPerformed

    private void jTextField82ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField82ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField82ActionPerformed

    private void jTextField81ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField81ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField81ActionPerformed

    private void jTextField80ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField80ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField80ActionPerformed

    private void jTextField79ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField79ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField79ActionPerformed

    private void jTextField78ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField78ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField78ActionPerformed

    private void jTextField77ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField77ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField77ActionPerformed

    private void jTextField76ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField76ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField76ActionPerformed

    private void jTextField75ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField75ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField75ActionPerformed

    private void jTextField74ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField74ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField74ActionPerformed

    private void jTextField73ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField73ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField73ActionPerformed

    private void jTextField72ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField72ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField72ActionPerformed

    private void jTextField71ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField71ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField71ActionPerformed

    private void jTextField65ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField65ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField65ActionPerformed

    private void jTextField64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField64ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField64ActionPerformed

    private void jTextField63ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField63ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField63ActionPerformed

    private void jTextField62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField62ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField62ActionPerformed

    private void jTextField61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField61ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField61ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField44ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField44ActionPerformed

    private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox12ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jTextField39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField39ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField39ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void lblBonificacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBonificacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBonificacionesActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed

    System.out.println(
        "Fila seleccionada: "
        + jtableNomina.getSelectedRow()
    );

    cargarDetalleNomina();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jTextField23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField23ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jTextField18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField18ActionPerformed

    private void cmbFiltroDepartamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFiltroDepartamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbFiltroDepartamentoActionPerformed

    private void cmbDepartamentoPuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDepartamentoPuestoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDepartamentoPuestoActionPerformed

    private void btnCambiarLogoEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarLogoEmpresaActionPerformed
      
    JFileChooser selector = new JFileChooser();

    FileNameExtensionFilter filtro =
            new FileNameExtensionFilter(
                    "Imágenes (*.png, *.jpg, *.jpeg)",
                    "png", "jpg", "jpeg"
            );

    selector.setFileFilter(filtro);

    int resultado = selector.showOpenDialog(this);

    if (resultado != JFileChooser.APPROVE_OPTION) {
        return;
    }

    File archivoSeleccionado = selector.getSelectedFile();

    System.out.println("Imagen seleccionada: "
            + archivoSeleccionado.getAbsolutePath());

    // Nombre original del archivo
    String nombreArchivo = archivoSeleccionado.getName();

    // Carpeta donde están las imágenes del proyecto
    File carpetaDestino = new File(
            "src/main/resources/imagenes"
    );

    if (!carpetaDestino.exists()) {
        carpetaDestino.mkdirs();
    }  File archivoDestino =
            new File(carpetaDestino, nombreArchivo);

        try {
            Files.copy(
                      archivoSeleccionado.toPath(),
                      archivoDestino.toPath(),
                      StandardCopyOption.REPLACE_EXISTING
            );  } catch (IOException ex) {
            System.getLogger(Menu.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    // Obtener la empresa
    EmpresaDAO empresaDAO = new EmpresaDAO();
    Empresa empresa = empresaDAO.obtenerEmpresa();
    if (empresa == null) {
        JOptionPane.showMessageDialog(
                  this,
                  "No se encontró la empresa."
        );
        return;
    }
    // Actualizar BD
    boolean actualizado = empresaDAO.actualizarLogo(
              empresa.getIdEmpresa(),
              nombreArchivo
    );
    if (actualizado) {
        
        JOptionPane.showMessageDialog(
                  this,
                  "Logo actualizado correctamente."
        );
        
        // Actualizar el JLabel
        cargarLogo();
        
    } else {
        
        JOptionPane.showMessageDialog(
                  this,
                  "No se pudo actualizar el logo."
        );
    }
    }//GEN-LAST:event_btnCambiarLogoEmpresaActionPerformed

    private void lblEMpresaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEMpresaMouseClicked
        pnlEmpresa.setVisible(!pnlEmpresa.isVisible());
    }//GEN-LAST:event_lblEMpresaMouseClicked

    private void lblRRHHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRRHHMouseClicked
        pnlRRHH.setVisible(!pnlRRHH.isVisible());
    }//GEN-LAST:event_lblRRHHMouseClicked

    private void lblNominaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNominaMouseClicked
        pnlNomina.setVisible(!pnlNomina.isVisible());
    }//GEN-LAST:event_lblNominaMouseClicked

    private void lblSeguridadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSeguridadMouseClicked
        pnlSeguridad.setVisible(!pnlSeguridad.isVisible());
    }//GEN-LAST:event_lblSeguridadMouseClicked

    private void lblAsistenciaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAsistenciaMouseClicked
        pnlAsistencia.setVisible(!pnlAsistencia.isVisible());
    }//GEN-LAST:event_lblAsistenciaMouseClicked

    private void lblPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPagoMouseClicked
        pnlPago.setVisible(!pnlPago.isVisible());
    }//GEN-LAST:event_lblPagoMouseClicked

    private void lblReportesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReportesMouseClicked
       pnlReportes.setVisible(!pnlReportes.isVisible());
    }//GEN-LAST:event_lblReportesMouseClicked

    private void lblAdministraciònMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdministraciònMouseClicked
        pnlAdministracion.setVisible(!pnlAdministracion.isVisible());
    }//GEN-LAST:event_lblAdministraciònMouseClicked

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        jpnMain.setSelectedIndex(2);
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        jpnMain.setSelectedIndex(0);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        jpnMain.setSelectedIndex(1);
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel30MouseClicked
        jpnMain.setSelectedIndex(3);
    }//GEN-LAST:event_jLabel30MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        jpnMain.setSelectedIndex(4);
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        jpnMain.setSelectedIndex(5);
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        jpnMain.setSelectedIndex(6);
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        jpnMain.setSelectedIndex(7);
    }//GEN-LAST:event_jLabel18MouseClicked

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        jpnMain.setSelectedIndex(8);
    }//GEN-LAST:event_jLabel19MouseClicked

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
        jpnMain.setSelectedIndex(9);
    }//GEN-LAST:event_jLabel20MouseClicked

    private void jLabel36MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel36MouseClicked
        jpnMain.setSelectedIndex(9);
    }//GEN-LAST:event_jLabel36MouseClicked

    private void jLabel37MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel37MouseClicked
       jpnMain.setSelectedIndex(10);
    }//GEN-LAST:event_jLabel37MouseClicked

    private void jLabel38MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel38MouseClicked
        jpnMain.setSelectedIndex(11);
    }//GEN-LAST:event_jLabel38MouseClicked

    private void jLabel39MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel39MouseClicked
        jpnMain.setSelectedIndex(12);
    }//GEN-LAST:event_jLabel39MouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
       jpnMain.setSelectedIndex(13);
    }//GEN-LAST:event_jLabel21MouseClicked

    private void jLabel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseClicked
        jpnMain.setSelectedIndex(14);
    }//GEN-LAST:event_jLabel26MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        jpnMain.setSelectedIndex(15);
    }//GEN-LAST:event_jLabel27MouseClicked

    private void jLabel28MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel28MouseClicked
        jpnMain.setSelectedIndex(16);
    }//GEN-LAST:event_jLabel28MouseClicked

    private void lblMenuPagoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMenuPagoMouseClicked
        jpnMain.setSelectedIndex(17);
    }//GEN-LAST:event_lblMenuPagoMouseClicked

    private void jLabel29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseClicked
        jpnMain.setSelectedIndex(18);
    }//GEN-LAST:event_jLabel29MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
        AdministrarUsuarios Admin = new AdministrarUsuarios();
    
        Admin.setVisible(true);
    }//GEN-LAST:event_jLabel22MouseClicked

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed

    }//GEN-LAST:event_btnVerDetallesActionPerformed

    private void btnPuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPuestosActionPerformed
        jpPuestos.setVisible(true);
    }//GEN-LAST:event_btnPuestosActionPerformed

    private void txtDepartamentoFuncionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartamentoFuncionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartamentoFuncionesActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        agregarDepartamento();
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        actualizarDepartamento();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        eliminarDepartamento();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        AgregarPuestosVista ventana = new AgregarPuestosVista(this);
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void btnEliminarPuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPuestosActionPerformed
        eliminarPuesto();
    }//GEN-LAST:event_btnEliminarPuestosActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        RegistroEmpleados registrar = new RegistroEmpleados(this);
        
        registrar.setLocationRelativeTo(this);
        registrar.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void btnEliminarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarEmpleadoActionPerformed
         eliminarEmpleado();
    }//GEN-LAST:event_btnEliminarEmpleadoActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        exportarNominaPDF();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        jpDetalleNominaEmpleado.setVisible(false);
        jtableNomina.requestFocus();
    }//GEN-LAST:event_jButton26ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiarLogoEmpresa;
    private javax.swing.JButton btnEliminarEmpleado;
    private javax.swing.JButton btnEliminarPuestos;
    private javax.swing.JToggleButton btnPuestos;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JComboBox<String> cmbDepartamentoPuesto;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JComboBox<String> cmbEstadoDepartamento;
    private javax.swing.JComboBox<String> cmbFiltroCargo;
    private javax.swing.JComboBox<String> cmbFiltroDepartamento;
    private javax.swing.JComboBox<String> cmbFiltroEstado;
    private javax.swing.JComboBox<String> cmbPeriodoNomina;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox14;
    private javax.swing.JComboBox<String> jComboBox15;
    private javax.swing.JComboBox<String> jComboBox16;
    private javax.swing.JComboBox<String> jComboBox19;
    private javax.swing.JComboBox<String> jComboBox20;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel160;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel171;
    private javax.swing.JLabel jLabel172;
    private javax.swing.JLabel jLabel173;
    private javax.swing.JLabel jLabel174;
    private javax.swing.JLabel jLabel175;
    private javax.swing.JLabel jLabel176;
    private javax.swing.JLabel jLabel177;
    private javax.swing.JLabel jLabel178;
    private javax.swing.JLabel jLabel179;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel180;
    private javax.swing.JLabel jLabel181;
    private javax.swing.JLabel jLabel182;
    private javax.swing.JLabel jLabel183;
    private javax.swing.JLabel jLabel184;
    private javax.swing.JLabel jLabel185;
    private javax.swing.JLabel jLabel186;
    private javax.swing.JLabel jLabel187;
    private javax.swing.JLabel jLabel188;
    private javax.swing.JLabel jLabel189;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel190;
    private javax.swing.JLabel jLabel191;
    private javax.swing.JLabel jLabel192;
    private javax.swing.JLabel jLabel193;
    private javax.swing.JLabel jLabel194;
    private javax.swing.JLabel jLabel195;
    private javax.swing.JLabel jLabel196;
    private javax.swing.JLabel jLabel197;
    private javax.swing.JLabel jLabel198;
    private javax.swing.JLabel jLabel199;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel200;
    private javax.swing.JLabel jLabel201;
    private javax.swing.JLabel jLabel202;
    private javax.swing.JLabel jLabel203;
    private javax.swing.JLabel jLabel204;
    private javax.swing.JLabel jLabel205;
    private javax.swing.JLabel jLabel206;
    private javax.swing.JLabel jLabel207;
    private javax.swing.JLabel jLabel208;
    private javax.swing.JLabel jLabel209;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel210;
    private javax.swing.JLabel jLabel211;
    private javax.swing.JLabel jLabel212;
    private javax.swing.JLabel jLabel213;
    private javax.swing.JLabel jLabel214;
    private javax.swing.JLabel jLabel215;
    private javax.swing.JLabel jLabel216;
    private javax.swing.JLabel jLabel217;
    private javax.swing.JLabel jLabel218;
    private javax.swing.JLabel jLabel219;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel220;
    private javax.swing.JLabel jLabel221;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel223;
    private javax.swing.JLabel jLabel224;
    private javax.swing.JLabel jLabel225;
    private javax.swing.JLabel jLabel226;
    private javax.swing.JLabel jLabel227;
    private javax.swing.JLabel jLabel228;
    private javax.swing.JLabel jLabel229;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel230;
    private javax.swing.JLabel jLabel231;
    private javax.swing.JLabel jLabel232;
    private javax.swing.JLabel jLabel233;
    private javax.swing.JLabel jLabel234;
    private javax.swing.JLabel jLabel235;
    private javax.swing.JLabel jLabel236;
    private javax.swing.JLabel jLabel237;
    private javax.swing.JLabel jLabel238;
    private javax.swing.JLabel jLabel239;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel240;
    private javax.swing.JLabel jLabel241;
    private javax.swing.JLabel jLabel242;
    private javax.swing.JLabel jLabel243;
    private javax.swing.JLabel jLabel244;
    private javax.swing.JLabel jLabel245;
    private javax.swing.JLabel jLabel246;
    private javax.swing.JLabel jLabel247;
    private javax.swing.JLabel jLabel248;
    private javax.swing.JLabel jLabel249;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel250;
    private javax.swing.JLabel jLabel251;
    private javax.swing.JLabel jLabel252;
    private javax.swing.JLabel jLabel253;
    private javax.swing.JLabel jLabel254;
    private javax.swing.JLabel jLabel255;
    private javax.swing.JLabel jLabel256;
    private javax.swing.JLabel jLabel257;
    private javax.swing.JLabel jLabel258;
    private javax.swing.JLabel jLabel259;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel260;
    private javax.swing.JLabel jLabel261;
    private javax.swing.JLabel jLabel262;
    private javax.swing.JLabel jLabel263;
    private javax.swing.JLabel jLabel264;
    private javax.swing.JLabel jLabel265;
    private javax.swing.JLabel jLabel266;
    private javax.swing.JLabel jLabel267;
    private javax.swing.JLabel jLabel268;
    private javax.swing.JLabel jLabel269;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel270;
    private javax.swing.JLabel jLabel271;
    private javax.swing.JLabel jLabel272;
    private javax.swing.JLabel jLabel273;
    private javax.swing.JLabel jLabel274;
    private javax.swing.JLabel jLabel275;
    private javax.swing.JLabel jLabel276;
    private javax.swing.JLabel jLabel277;
    private javax.swing.JLabel jLabel278;
    private javax.swing.JLabel jLabel279;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel280;
    private javax.swing.JLabel jLabel281;
    private javax.swing.JLabel jLabel282;
    private javax.swing.JLabel jLabel283;
    private javax.swing.JLabel jLabel284;
    private javax.swing.JLabel jLabel285;
    private javax.swing.JLabel jLabel286;
    private javax.swing.JLabel jLabel287;
    private javax.swing.JLabel jLabel288;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel290;
    private javax.swing.JLabel jLabel291;
    private javax.swing.JLabel jLabel292;
    private javax.swing.JLabel jLabel293;
    private javax.swing.JLabel jLabel294;
    private javax.swing.JLabel jLabel295;
    private javax.swing.JLabel jLabel296;
    private javax.swing.JLabel jLabel297;
    private javax.swing.JLabel jLabel298;
    private javax.swing.JLabel jLabel299;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel300;
    private javax.swing.JLabel jLabel301;
    private javax.swing.JLabel jLabel302;
    private javax.swing.JLabel jLabel303;
    private javax.swing.JLabel jLabel304;
    private javax.swing.JLabel jLabel306;
    private javax.swing.JLabel jLabel307;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel100;
    private javax.swing.JPanel jPanel101;
    private javax.swing.JPanel jPanel102;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel73;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel78;
    private javax.swing.JPanel jPanel79;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JPanel jPanel82;
    private javax.swing.JPanel jPanel83;
    private javax.swing.JPanel jPanel84;
    private javax.swing.JPanel jPanel85;
    private javax.swing.JPanel jPanel87;
    private javax.swing.JPanel jPanel88;
    private javax.swing.JPanel jPanel89;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel90;
    private javax.swing.JPanel jPanel91;
    private javax.swing.JPanel jPanel92;
    private javax.swing.JPanel jPanel93;
    private javax.swing.JPanel jPanel94;
    private javax.swing.JPanel jPanel97;
    private javax.swing.JPanel jPanel98;
    private javax.swing.JPanel jPanel99;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable jTable10;
    private javax.swing.JTable jTable11;
    private javax.swing.JTable jTable12;
    private javax.swing.JTable jTable14;
    private javax.swing.JTable jTable15;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField52;
    private javax.swing.JTextField jTextField53;
    private javax.swing.JTextField jTextField54;
    private javax.swing.JTextField jTextField55;
    private javax.swing.JTextField jTextField56;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField63;
    private javax.swing.JTextField jTextField64;
    private javax.swing.JTextField jTextField65;
    private javax.swing.JTextField jTextField66;
    private javax.swing.JTextField jTextField67;
    private javax.swing.JTextField jTextField68;
    private javax.swing.JTextField jTextField69;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField70;
    private javax.swing.JTextField jTextField71;
    private javax.swing.JTextField jTextField72;
    private javax.swing.JTextField jTextField73;
    private javax.swing.JTextField jTextField74;
    private javax.swing.JTextField jTextField75;
    private javax.swing.JTextField jTextField76;
    private javax.swing.JTextField jTextField77;
    private javax.swing.JTextField jTextField78;
    private javax.swing.JTextField jTextField79;
    private javax.swing.JTextField jTextField80;
    private javax.swing.JTextField jTextField81;
    private javax.swing.JTextField jTextField82;
    private javax.swing.JTextField jTextField83;
    private javax.swing.JTextField jTextField84;
    private javax.swing.JTextField jTextField85;
    private javax.swing.JTextField jTextField86;
    private javax.swing.JTextField jTextField87;
    private javax.swing.JTextField jTextField88;
    private javax.swing.JTextField jTextField89;
    private javax.swing.JTextField jTextField90;
    private javax.swing.JTextField jTextField91;
    private javax.swing.JTextField jTextField92;
    private javax.swing.JTextField jTextField93;
    private javax.swing.JTextField jTextField94;
    private javax.swing.JTextField jTextField95;
    private javax.swing.JLabel jblAFPDetalle;
    private javax.swing.JLabel jblAFPDetalle1;
    private javax.swing.JPanel jpDetalleNominaEmpleado;
    private javax.swing.JPanel jpPuestos;
    private javax.swing.JPanel jpnAFP;
    private javax.swing.JPanel jpnARS;
    private javax.swing.JPanel jpnAsistencias;
    private javax.swing.JPanel jpnConsultarNom;
    private javax.swing.JPanel jpnContratos;
    private javax.swing.JPanel jpnDeducciones;
    private javax.swing.JPanel jpnDeps;
    private javax.swing.JPanel jpnEmpleados;
    private javax.swing.JPanel jpnEmpresa;
    private javax.swing.JPanel jpnGenNomina;
    private javax.swing.JPanel jpnHistorial;
    private javax.swing.JPanel jpnHorasExt;
    private javax.swing.JPanel jpnISR;
    private javax.swing.JPanel jpnLicencias;
    private javax.swing.JTabbedPane jpnMain;
    private javax.swing.JPanel jpnNomina;
    private javax.swing.JPanel jpnPermisos;
    private javax.swing.JPanel jpnRegistrarPago;
    private javax.swing.JPanel jpnReportes;
    private javax.swing.JPanel jpnTSS;
    private javax.swing.JPanel jpnVacaciones;
    private javax.swing.JTable jtableDepartamentos;
    private javax.swing.JTable jtableEmpleados;
    private javax.swing.JTable jtableNomina;
    private javax.swing.JTable jtablePuestos;
    private javax.swing.JTextField lblAFP;
    private javax.swing.JTextField lblARS;
    private javax.swing.JLabel lblAdministraciòn;
    private javax.swing.JLabel lblAsistencia;
    private javax.swing.JTextField lblBonificaciones;
    private javax.swing.JLabel lblEMpresa;
    private javax.swing.JTextField lblISR;
    private javax.swing.JLabel lblMenuPago;
    private javax.swing.JLabel lblNombreEmpleado;
    private javax.swing.JLabel lblNomina;
    private javax.swing.JTextField lblOtrasDeducciones;
    private javax.swing.JLabel lblPago;
    private javax.swing.JLabel lblRRHH;
    private javax.swing.JLabel lblReportes;
    private javax.swing.JTextField lblSalarioBase;
    private javax.swing.JTextField lblSalarioNeto;
    private javax.swing.JLabel lblSeguridad;
    private javax.swing.JTextField lblTotalDescuentos;
    private javax.swing.JPanel pnlAdministracion;
    private javax.swing.JPanel pnlAsistencia;
    private javax.swing.JPanel pnlEmpresa;
    private javax.swing.JPanel pnlNomina;
    private javax.swing.JPanel pnlPago;
    private javax.swing.JPanel pnlRRHH;
    private javax.swing.JPanel pnlReportes;
    private javax.swing.JPanel pnlSeguridad;
    private javax.swing.JTable tblNominas;
    private javax.swing.JLabel txtBienvenidoUser;
    private javax.swing.JTextField txtBuscarDepartamento;
    private javax.swing.JTextField txtBuscarEmpleado;
    private javax.swing.JTextField txtDepartamentoFunciones;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JLabel txtEmpleadosActivos;
    private javax.swing.JLabel txtEmpleadosInactivos;
    private javax.swing.JTextField txtFechaRegistro;
    private javax.swing.JLabel txtLogo;
    private javax.swing.JTextField txtNombreComercial;
    private javax.swing.JTextField txtNombreDepartamento;
    private javax.swing.JTextField txtRNC;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtRepresentanteLegal;
    private javax.swing.JFormattedTextField txtTelefono;
    private javax.swing.JLabel txtTotalEmpleados;
    // End of variables declaration//GEN-END:variables

    private String formatoDinero(BigDecimal bigDecimal) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
