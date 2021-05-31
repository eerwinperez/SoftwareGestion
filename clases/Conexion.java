package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection Conectar() {

        try {
            //String url = "jdbc:sqlite:D:\\Erwin\\ApacheNetbeans\\GraficasJireh\\Base de datos\\BD_Jireh.db";
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/jireh", "root", "");
            
            return conexion;
         
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (null);
    }

    public static void main(String[] args) {
        Conectar();
    }
}
