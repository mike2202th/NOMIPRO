/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import Modelos.classesTable.Empleado;
import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maico
 */
public class AgregarUsuarioDAO {
    
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;
        ResultSet rs;
    
    public boolean agregarUsuario(int idEmpleado, int idRol, String usuario, String contrasena) {
        String sql = """
            INSERT INTO USUARIO
            (
                id_empleado,
                id_rol,
                usuario,
                contrasena,
                estado
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        try {
            cn = conectar.conectar();
            ps = cn.prepareStatement(sql);

            ps.setInt(1, idEmpleado);
            ps.setInt(2, idRol);
            ps.setString(3, usuario);
            ps.setString(4, contrasena);
            ps.setString(5, "ACTIVO");

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
            return false;
        } finally {
            // Cerrar recursos de forma segura
            try {
                if (ps != null) ps.close();
                if (cn != null) cn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Opcional: Método para obtener la lista de empleados y llenarlos en el JComboBox
     */
    public List<Empleado> obtenerEmpleadosParaCombo() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id_empleado, nombre, apellido FROM EMPLEADOS WHERE estado = 'ACTIVO'";

        try {
            cn = conectar.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Empleado emp = new Empleado();
                emp.setIdEmpleado(rs.getInt("id_empleado"));
                emp.setNombres(rs.getString("nombre"));
                emp.setApellidos(rs.getString("apellido"));
                lista.add(emp);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (cn != null) cn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return lista;
    }
}
