
package ni.edu.uam.demologin.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnection implements IConnection {

    private static final Logger LOGGER =
            Logger.getLogger(PostgreSQLConnection.class.getName());

    private static final String URL =
            "jdbc:postgresql://localhost:5432/estudiante";

    private static final String usuario = "postgres";
    private static final String password = "1234";

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    URL,
                    usuario,
                    password
            );

        } catch (SQLException ex) {
            // Muestra la causa real del error de conexión
            LOGGER.log(
                    Level.SEVERE,
                    "No se pudo conectar con PostgreSQL.",
                    ex
            );
            // Evita devolver una conexión nula
            throw new IllegalStateException(
                    "Error al conectar con la base de datos estudiante.",
                    ex
            );
        }
    }
}

