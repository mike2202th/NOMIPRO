
package ConexionDB;

import Main.conexionMariaDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maico
 */
public class LoginDAO {
    conexionMariaDB conectar = new conexionMariaDB();
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;
   
   public String realizarLogin(String usuario, String clave) throws SQLException {
        cn = conectar.conectar();

        String sql = """
        SELECT CONCAT(e.nombres, ' ', e.apellidos) AS nombre
        FROM USUARIO u
        INNER JOIN EMPLEADO e 
            ON u.id_empleado = e.id_empleado
        WHERE u.usuario = ?
        AND u.contrasena = ?
        """;

        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, clave);
                
              try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Usuario encontrado.");
                    return rs.getString("nombre");
                }
                            }

        } catch (SQLException ex) {
            System.out.println("Error al realizar login:");
            ex.printStackTrace();
        }
           /* rs = ps.executeQuery();

            while (rs.next()) {
                cn.close();
                return true;
            }*/

         /*catch (SQLException ex) {
            Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }*/
        return null;

   }
}