import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // URL de conexión a la base de datos MySQL.
    // host, puerto, y el nombre de la BD
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_login";
    private static final String USER = "root"; // nombre de usuario de BD, ponga su nombre de usuario
    private static final String PASSWORD = ""; // contraseña de BD, coloque su contraseña si es que tiene docente


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
