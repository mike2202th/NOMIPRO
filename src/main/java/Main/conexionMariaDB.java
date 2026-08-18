package Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class conexionMariaDB {


    Connection cn;

    public Connection conectar() {
        try {

            Class.forName("org.mariadb.jdbc.Driver");

            cn = DriverManager.getConnection(
                "jdbc:mariadb://localhost:2202/SISTEMA_DE_NOMINA",
                "root",
                ""
            );

            System.out.println("Conexión exitosa a la base de datos.");

            return cn;

        } catch (ClassNotFoundException e) {

            System.out.println("No se encontró el driver de MariaDB.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Error al conectar con MariaDB.");
            e.printStackTrace();
        }

        return null;
    }
}
