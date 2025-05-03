package Prueba_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BaseDeDatos {

    private static final String URL = "jdbc:mysql://localhost:3306/prueba";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "";
    private static Connection conexion;

    public static Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
