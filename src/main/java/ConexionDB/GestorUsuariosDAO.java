/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionDB;

import ConexionDB.GestorUsuarios;
import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author maico
 */
public class GestorUsuariosDAO {

    public List<GestorUsuarios> listarUsuarios() {

        List<GestorUsuarios> lista = new ArrayList<>();

        String sql = """
            SELECT 
                u.id_usuario,
                CONCAT(e.nombres, ' ', e.apellidos) AS nombre,
                u.usuario,
                r.nombre_rol AS rol
            FROM USUARIO u
            INNER JOIN EMPLEADO e 
                ON u.id_empleado = e.id_empleado
            INNER JOIN ROLES r 
                ON u.id_rol = r.id_rol
            ORDER BY e.nombres, e.apellidos
        """;

        conexionMariaDB conectar = new conexionMariaDB();

        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                GestorUsuarios usuario = new GestorUsuarios();

                usuario.setIdUsuario(
                    rs.getInt("id_usuario")
                );

                usuario.setNombre(
                    rs.getString("nombre")
                );

                usuario.setUsuario(
                    rs.getString("usuario")
                );

                usuario.setRol(
                    rs.getString("rol")
                );

                lista.add(usuario);
            }

        } catch (Exception e) {

            System.out.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }
}