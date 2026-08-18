/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos.classesTable;

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
public class EmpleadoDAO {
    
    
    
    public List<Empleado> listarParaCombo() {
    
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs = null;
    
    List<Empleado> lista = new ArrayList<>();

    String sql = """
        SELECT id_empleado, nombres, apellidos
        FROM EMPLEADO
        WHERE estado = 'ACTIVO'
        ORDER BY nombres, apellidos
    """;

    try {

        while (rs.next()) {

            Empleado empleado = new Empleado(
                    rs.getInt("id_empleado"),
                    rs.getString("nombres"),
                    rs.getString("apellidos")
            );

            empleado.setIdEmpleado(rs.getInt("id_empleado"));
            empleado.setNombres(rs.getString("nombres"));
            empleado.setApellidos(rs.getString("apellidos"));
            lista.add(empleado);
        }

    } catch (SQLException e) {
        System.out.println("Error al cargar empleados: "
                + e.getMessage());
    }

    return lista;
}
}
