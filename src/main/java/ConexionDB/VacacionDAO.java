package ConexionDB;

import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de vacaciones de empleados.
 */
public class VacacionDAO {

    private conexionMariaDB conexion = new conexionMariaDB();

    /**
     * Inserta un nuevo registro de vacaciones en la base de datos.
     */
    public boolean insertar(Vacacion v) {
        String sql = "INSERT INTO VACACIONES (id_empleado, fecha_inicio, fecha_fin, cantidad_dias, estado, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdEmpleado());
            ps.setDate(2, v.getFechaInicio());
            ps.setDate(3, v.getFechaFin());
            ps.setInt(4, v.getCantidadDias());
            ps.setString(5, v.getEstado());
            ps.setString(6, v.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar vacación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de vacaciones existente.
     */
    public boolean actualizar(Vacacion v) {
        String sql = "UPDATE VACACIONES SET id_empleado=?, fecha_inicio=?, fecha_fin=?, "
                   + "cantidad_dias=?, estado=?, observaciones=? WHERE id_vacacion=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdEmpleado());
            ps.setDate(2, v.getFechaInicio());
            ps.setDate(3, v.getFechaFin());
            ps.setInt(4, v.getCantidadDias());
            ps.setString(5, v.getEstado());
            ps.setString(6, v.getObservaciones());
            ps.setInt(7, v.getIdVacacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar vacación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de vacaciones por su ID.
     */
    public boolean eliminar(int idVacacion) {
        String sql = "DELETE FROM VACACIONES WHERE id_vacacion=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVacacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar vacación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los registros de vacaciones con nombre del empleado.
     */
    public List<Vacacion> listarTodas() {
        List<Vacacion> lista = new ArrayList<>();
        String sql = "SELECT v.id_vacacion, v.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "v.fecha_inicio, v.fecha_fin, v.cantidad_dias, v.estado, v.observaciones "
                   + "FROM VACACIONES v "
                   + "JOIN EMPLEADOS e ON v.id_empleado = e.id_empleado "
                   + "ORDER BY v.fecha_inicio DESC";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vacacion v = new Vacacion();
                v.setIdVacacion(rs.getInt("id_vacacion"));
                v.setIdEmpleado(rs.getInt("id_empleado"));
                v.setNombreEmpleado(rs.getString("nombre_empleado"));
                v.setFechaInicio(rs.getDate("fecha_inicio"));
                v.setFechaFin(rs.getDate("fecha_fin"));
                v.setCantidadDias(rs.getInt("cantidad_dias"));
                v.setEstado(rs.getString("estado"));
                v.setObservaciones(rs.getString("observaciones"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar vacaciones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene una vacación específica por su ID.
     */
    public Vacacion obtenerPorId(int idVacacion) {
        String sql = "SELECT v.id_vacacion, v.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "v.fecha_inicio, v.fecha_fin, v.cantidad_dias, v.estado, v.observaciones "
                   + "FROM VACACIONES v "
                   + "JOIN EMPLEADOS e ON v.id_empleado = e.id_empleado "
                   + "WHERE v.id_vacacion = ?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVacacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vacacion v = new Vacacion();
                    v.setIdVacacion(rs.getInt("id_vacacion"));
                    v.setIdEmpleado(rs.getInt("id_empleado"));
                    v.setNombreEmpleado(rs.getString("nombre_empleado"));
                    v.setFechaInicio(rs.getDate("fecha_inicio"));
                    v.setFechaFin(rs.getDate("fecha_fin"));
                    v.setCantidadDias(rs.getInt("cantidad_dias"));
                    v.setEstado(rs.getString("estado"));
                    v.setObservaciones(rs.getString("observaciones"));
                    return v;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener vacación: " + e.getMessage());
        }
        return null;
    }
}
