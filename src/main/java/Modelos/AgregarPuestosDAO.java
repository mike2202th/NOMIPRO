/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author maico
 */
public class AgregarPuestosDAO {
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;

    public boolean agregarPuesto(AgregarPuestos puesto) {

        String sql = """
            INSERT INTO PUESTOS
            (
                id_departamento,
                nombre_puesto,
                salario_minimo,
                salario_maximo,
                descripcion
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        try {

            cn = conectar.conectar();

            ps = cn.prepareStatement(sql);

            ps.setInt(1, puesto.getIdDepartamento());
            ps.setString(2, puesto.getNombrePuesto());
            ps.setDouble(3, puesto.getSalarioMinimo());
            ps.setDouble(4, puesto.getSalarioMaximo());
            ps.setString(5, puesto.getDescripcion());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                "Error al agregar puesto: " + e.getMessage()
            );

            return false;
        }
    }
}
