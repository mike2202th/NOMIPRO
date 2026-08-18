package ConexionDB;

import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de asistencia de empleados.
 */
public class AsistenciaDAO {

    private conexionMariaDB conexion = new conexionMariaDB();

    /**
     * Inserta un nuevo registro de asistencia.
     */
    public boolean insertar(Asistencia a) {
        String sql = "INSERT INTO ASISTENCIA (id_empleado, fecha, hora_entrada, hora_salida, estado, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, a.getIdEmpleado());
            ps.setDate(2, a.getFecha());
            ps.setString(3, a.getHoraEntrada());
            ps.setString(4, a.getHoraSalida());
            ps.setString(5, a.getEstado());
            ps.setString(6, a.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar asistencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de asistencia existente.
     */
    public boolean actualizar(Asistencia a) {
        String sql = "UPDATE ASISTENCIA SET id_empleado=?, fecha=?, hora_entrada=?, "
                   + "hora_salida=?, estado=?, observaciones=? WHERE id_asistencia=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, a.getIdEmpleado());
            ps.setDate(2, a.getFecha());
            ps.setString(3, a.getHoraEntrada());
            ps.setString(4, a.getHoraSalida());
            ps.setString(5, a.getEstado());
            ps.setString(6, a.getObservaciones());
            ps.setInt(7, a.getIdAsistencia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar asistencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de asistencia por su ID.
     */
    public boolean eliminar(int idAsistencia) {
        String sql = "DELETE FROM ASISTENCIA WHERE id_asistencia=?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar asistencia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los registros de asistencia con nombre del empleado.
     */
    public List<Asistencia> listarTodas() {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT a.id_asistencia, a.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "a.fecha, a.hora_entrada, a.hora_salida, a.estado, a.observaciones "
                   + "FROM ASISTENCIA a "
                   + "JOIN EMPLEADOS e ON a.id_empleado = e.id_empleado "
                   + "ORDER BY a.fecha DESC";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setIdAsistencia(rs.getInt("id_asistencia"));
                a.setIdEmpleado(rs.getInt("id_empleado"));
                a.setNombreEmpleado(rs.getString("nombre_empleado"));
                a.setFecha(rs.getDate("fecha"));
                a.setHoraEntrada(rs.getString("hora_entrada"));
                a.setHoraSalida(rs.getString("hora_salida"));
                a.setEstado(rs.getString("estado"));
                a.setObservaciones(rs.getString("observaciones"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar asistencias: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un registro de asistencia por su ID.
     */
    public Asistencia obtenerPorId(int idAsistencia) {
        String sql = "SELECT a.id_asistencia, a.id_empleado, "
                   + "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nombre_empleado, "
                   + "a.fecha, a.hora_entrada, a.hora_salida, a.estado, a.observaciones "
                   + "FROM ASISTENCIA a "
                   + "JOIN EMPLEADOS e ON a.id_empleado = e.id_empleado "
                   + "WHERE a.id_asistencia = ?";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Asistencia a = new Asistencia();
                    a.setIdAsistencia(rs.getInt("id_asistencia"));
                    a.setIdEmpleado(rs.getInt("id_empleado"));
                    a.setNombreEmpleado(rs.getString("nombre_empleado"));
                    a.setFecha(rs.getDate("fecha"));
                    a.setHoraEntrada(rs.getString("hora_entrada"));
                    a.setHoraSalida(rs.getString("hora_salida"));
                    a.setEstado(rs.getString("estado"));
                    a.setObservaciones(rs.getString("observaciones"));
                    return a;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener asistencia: " + e.getMessage());
        }
        return null;
    }
}
