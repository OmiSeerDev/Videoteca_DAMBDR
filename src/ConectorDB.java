import java.sql.*;


public class ConectorDB {
    static Connection dbConnect;
    static  PreparedStatement ps;
    static  ResultSet res;
    static boolean isAdminLoggedIn;
    static boolean userLoggedIn;

    public static void conectar () {
        String DB_URL = "jdbc:mysql://127.0.0.1:3306/videoteca_omejia";
        String USER = "rdbms_310005991";
        String PASSWORD = "";
        try {
            dbConnect = DriverManager.getConnection (DB_URL, USER, PASSWORD);
            System.out.println (":::Conexión a base de datos exitosa... En el puerto 3306::");
        } catch (SQLException sqlEx) {
            throw new RuntimeException (sqlEx);
        }
    }
}
