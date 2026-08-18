/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

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
public class DetalleNominaDAO {
    
    conexionMariaDB conectar = new conexionMariaDB();

    public List<Object[]> obtenerDetallesPorNomina(int idNomina) {

        List<Object[]> datos = new ArrayList<>();

        String sql = "SELECT "
                + "d.id_empleado, "
                + "CONCAT(e.nombres, ' ', e.apellidos) AS nombre_empleado, "
                + "d.salario_base, "
                + "d.total_bonificaciones, "
                + "d.total_deducciones, "
                + "d.salario_neto, "
                + "d.estado "
                + "FROM detalle_nomina d "
                + "INNER JOIN empleado e ON d.id_empleado = e.id_empleado "
                + "WHERE d.id_nomina = ?";

        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // Pasar el ID de la nómina al ?
            ps.setInt(1, idNomina);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Object[] fila = {
                        rs.getInt("id_empleado"),
                        rs.getString("nombre_empleado"),
                        rs.getBigDecimal("salario_base"),
                        rs.getBigDecimal("total_bonificaciones"),
                        rs.getBigDecimal("total_deducciones"),
                        rs.getBigDecimal("salario_neto"),
                        rs.getString("estado")
                    };

                    datos.add(fila);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la nómina: "
                    + e.getMessage());
        }

        return datos;
    }
}

