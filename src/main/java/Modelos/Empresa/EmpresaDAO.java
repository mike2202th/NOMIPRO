/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos.Empresa;


import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {

    conexionMariaDB conectar = new conexionMariaDB();

    public Empresa obtenerEmpresa() {

        Empresa empresa = null;

        String sql = """
            SELECT
                id_empresa,
                rnc,
                razon_social,
                nombre_comercial,
                representante_legal,
                telefono,
                email,
                direccion,
                fecha_registro,
                estado,
                descripcion,
                logo
            FROM EMPRESA
            LIMIT 1
        """;

        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                empresa = new Empresa();

                empresa.setIdEmpresa(
                    rs.getInt("id_empresa")
                );

                empresa.setRnc(
                    rs.getString("rnc")
                );

                empresa.setRazonSocial(
                    rs.getString("razon_social")
                );

                empresa.setNombreComercial(
                    rs.getString("nombre_comercial")
                );

                empresa.setRepresentanteLegal(
                    rs.getString("representante_legal")
                );

                empresa.setTelefono(
                    rs.getString("telefono")
                );

                empresa.setEmail(
                    rs.getString("email")
                );

                empresa.setDireccion(
                    rs.getString("direccion")
                );

                empresa.setFechaRegistro(
                    rs.getDate("fecha_registro")
                );

                empresa.setEstado(
                    rs.getString("estado")
                );

                empresa.setDescripcion(
                    rs.getString("descripcion")
                );

                empresa.setLogo(
                    rs.getString("logo")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                "Error al obtener los datos de la empresa: "
                + e.getMessage()
            );

            e.printStackTrace();
        }

        return empresa;
    }
    
    
    // Metodos para aplicarlos 
    public boolean actualizarLogo(int idEmpresa, String nombreLogo) {

    String sql = """
        UPDATE EMPRESA
        SET logo = ?
        WHERE id_empresa = ?
    """;

    try (Connection cn = conectar.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, nombreLogo);
        ps.setInt(2, idEmpresa);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("Error al actualizar el logo: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}
