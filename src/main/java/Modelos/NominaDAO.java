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
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author maico
 */
public class NominaDAO {
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;

    /**
     * Registra la nómina principal y todos sus detalles correspondientes en una transacción.
     * 
     * @param idPeriodo      ID del período de nómina seleccionado (Módulo 7).
     * @param generadoPor    ID del usuario que genera la nómina (Módulo 2).
     * @param totalDevengado Suma total del devengado de todos los empleados procesados.
     * @param totalDeducc    Suma total de las deducciones de todos los empleados.
     * @param totalNeto      Suma total del salario neto a pagar.
     * @param detalles       Lista de objetos con los cálculos individuales de cada empleado.
     * @return true si se guardó correctamente, false si ocurrió un error.
     */
    public boolean generarNominaCompleta(int idPeriodo, int generadoPor, double totalDevengado, 
                                          double totalDeducc, double totalNeto, List<DetalleNomina> detalles) {
        
        String sqlNomina = "INSERT INTO NOMINAS (id_periodo, generado_por, total_devengado, total_deducciones, total_neto, estado) VALUES (?, ?, ?, ?, ?, 'BORRADOR')";
        
        String sqlDetalle = "INSERT INTO DETALLE_NOMINA (id_nomina, id_empleado, salario_base, dias_trabajados, " +
                            "total_horas_extras, total_bonificaciones, total_deducciones, salario_bruto, salario_neto, estado) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDIENTE')";

        Connection cn = null;
        PreparedStatement psNomina = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;

        try {
            cn = conectar.conectar();
            // Desactivar autocommit para iniciar la transacción
            cn.setAutoCommit(false);

            // 1. Insertar el encabezado de la nómina
            psNomina = cn.prepareStatement(sqlNomina, Statement.RETURN_GENERATED_KEYS);
            psNomina.setInt(1, idPeriodo);
            psNomina.setInt(2, generadoPor);
            psNomina.setDouble(3, totalDevengado);
            psNomina.setDouble(4, totalDeducc);
            psNomina.setDouble(5, totalNeto);
            
            psNomina.executeUpdate();

            // Obtener el ID autogenerado de la nómina
            rsKeys = psNomina.getGeneratedKeys();
            int idNominaGenerada = 0;
            if (rsKeys.next()) {
                idNominaGenerada = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la nómina creada.");
            }

            // 2. Insertar cada elemento (detalle de cada empleado)
            psDetalle = cn.prepareStatement(sqlDetalle);
            for (DetalleNomina det : detalles) {
                psDetalle.setInt(1, idNominaGenerada);
                psDetalle.setInt(2, det.getIdEmpleado());
                psDetalle.setBigDecimal(3, det.getSalarioBase());
                psDetalle.setBigDecimal(4, det.getDiasTrabajados());
                psDetalle.setBigDecimal(5, det.getTotalHorasExtras());
                psDetalle.setBigDecimal(6, det.getTotalBonificaciones());
                psDetalle.setBigDecimal(7, det.getTotalDeducciones());
                psDetalle.setBigDecimal(8, det.getSalarioBruto());
                psDetalle.setBigDecimal(9, det.getSalarioNeto());
                
                psDetalle.addBatch(); // Agregar al lote de transacciones
            }

            // Ejecutar inserción masiva de los detalles
            psDetalle.executeBatch();

            // Confirmar transacción completa
            cn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (cn != null) {
                try {
                    // Revertir cambios si algo falla
                    cn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Cerrar recursos de forma segura
            try {
                if (rsKeys != null) rsKeys.close();
                if (psNomina != null) psNomina.close();
                if (psDetalle != null) psDetalle.close();
                if (cn != null) {
                    cn.setAutoCommit(true);
                    cn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    

