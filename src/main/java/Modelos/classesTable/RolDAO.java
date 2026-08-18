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
public class RolDAO {
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs = null;
    public List<Rol> listarParaCombo() {

        List<Rol> lista = new ArrayList<>();

        String sql = """
            SELECT id_rol, nombre
            FROM ROLES
            WHERE estado = 'ACTIVO'
            ORDER BY nombre
        """;

        try {

            while (rs.next()) {

                Rol rol = new Rol(
                        rs.getInt("id_rol"),
                        rs.getString("nombre")
                );
            rol.setIdRol(rs.getInt("id_rol"));
            rol.setNombreRol(rs.getString("nombre_rol"));
                lista.add(rol);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al cargar roles: "
                    + e.getMessage()
            );
        }

        return lista;
    }
}
