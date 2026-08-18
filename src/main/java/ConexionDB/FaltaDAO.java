package ConexionDB;

import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de faltas de empleados.
 */
public class FaltaDAO {

    private conexionMariaDB conexion = new conexionMariaDB();

    /**
     * Inserta un nuevo registro de falta.
     */
    public boolean insertar(Falta f) {
        String sql = "INSERT INTO FALTAS (id_empleado, fecha, tipo_falta, justificada, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdEmpleado());
            ps.setDate(2, f.getFecha());
            ps.setString(3, f.getTipoFalta());
            ps.setBoolean(4, f.isJustificada());
            ps.setString(5, f.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar falta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de falta existente.
     */
    public boolean actualizar(Falta f) {
        String sql = "UPDATE FALTAS SET id_empleado=?, fecha=?, tipo_falta=?, "
                   + "justificada=?, observaciones=? WHERE id_falta=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdEmpleado());
            ps.setDate(2, f.getFecha());
            ps.setString(3, f.getTipoFalta());
            ps.setBoolean(4, f.isJustificada());
            ps.setString(5, f.getObservaciones());
            ps.setInt(6, f.getIdFalta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar falta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de falta por su ID.
     */
    public boolean eliminar(int idFalta) {
        String sql = "DELETE FROM FALTAS WHERE id_falta=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFalta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar falta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los registros de faltas con nombre del empleado.
     */
    public List<Falta> listarTodas() {
        List<Falta> lista = new ArrayList<>();
        String sql = "SELECT f.id_falta, f.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "f.fecha, f.tipo_falta, f.justificada, f.observaciones "
                   + "FROM FALTAS f "
                   + "JOIN EMPLEADOS e ON f.id_empleado = e.id_empleado "
                   + "ORDER BY f.fecha DESC";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Falta f = new Falta();
                f.setIdFalta(rs.getInt("id_falta"));
                f.setIdEmpleado(rs.getInt("id_empleado"));
                f.setNombreEmpleado(rs.getString("nombre_empleado"));
                f.setFecha(rs.getDate("fecha"));
                f.setTipoFalta(rs.getString("tipo_falta"));
                f.setJustificada(rs.getBoolean("justificada"));
                f.setObservaciones(rs.getString("observaciones"));
                lista.add(f);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar faltas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un registro de falta por su ID.
     */
    public Falta obtenerPorId(int idFalta) {
        String sql = "SELECT f.id_falta, f.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "f.fecha, f.tipo_falta, f.justificada, f.observaciones "
                   + "FROM FALTAS f "
                   + "JOIN EMPLEADOS e ON f.id_empleado = e.id_empleado "
                   + "WHERE f.id_falta = ?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFalta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Falta f = new Falta();
                    f.setIdFalta(rs.getInt("id_falta"));
                    f.setIdEmpleado(rs.getInt("id_empleado"));
                    f.setNombreEmpleado(rs.getString("nombre_empleado"));
                    f.setFecha(rs.getDate("fecha"));
                    f.setTipoFalta(rs.getString("tipo_falta"));
                    f.setJustificada(rs.getBoolean("justificada"));
                    f.setObservaciones(rs.getString("observaciones"));
                    return f;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener falta: " + e.getMessage());
        }
        return null;
    }
}
