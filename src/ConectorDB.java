import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class ConectorDB {
    static Connection dbConnect;
    static  PreparedStatement ps;
    static  ResultSet res;
    boolean isAdminLoggedIn;
    boolean userLoggedIn;
    static class UpdateDTO {
        String DATE_FORMAT = "YYYY-MM-dd hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        JTable vidTable;
        JTextField name;
        JComboBox<Integer> category;
        JTextField author;
        JTextArea description;
        JTextField file;
        JTextField image;
        JTextField material;
        JTextField createdAt;
        JCheckBox recommend;
        String recommended;

        public UpdateDTO (JTable vidTable,
                          JTextField name,
                          JComboBox<Integer> category,
                          JTextField author,
                          JTextArea description,
                          JTextField file,
                          JTextField image,
                          JTextField material,
                          JTextField createdAt,
                          JCheckBox recommend
        ) {
            this.vidTable = vidTable;
            this.name = name;
            this.category = category;
            this.author = author;
            this.description = description;
            this.file = file;
            this.image = image;
            this.material = material;
            this.createdAt = createdAt;
            this.recommend = recommend;
            this.recommended = recommend.isSelected () ? "1" : "0";
            createdAt.setText (dateFormat.format (new Date ()));
        }
    }

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

    public boolean loginQuery (String email, String password) throws SQLException {
        conectar ();
        byte[] pwdbytes = password.getBytes (StandardCharsets.UTF_8);
        try {
            ps = dbConnect.prepareStatement ("SELECT login, password FROM users WHERE login ='" + email + "'");
            res= ps.executeQuery ();
            while (res.next ()) {
                Blob queriedPaswBlob = res.getBlob ("password");
                byte[] queriedPasswd = queriedPaswBlob.getBytes (1, (int) queriedPaswBlob.length ());
                userLoggedIn = Objects.equals (res.getString ("login"), email) &&
                        Arrays.equals (queriedPasswd, pwdbytes);
            }
        } catch (SQLException sqlEx){
            throw new RuntimeException (sqlEx);
        }
        return this.userLoggedIn;
    }
    public boolean isAdminLogin (String email) throws SQLException {
        conectar ();
        String loginQuery = "SELECT role_id FROM users WHERE login =\"" + email + "\"";
        try {
            ps = dbConnect.prepareStatement (loginQuery);
            res= ps.executeQuery ();
            while (res.next ()) {
                String admin = res.getString ("role_id");
                isAdminLoggedIn = Objects.equals (admin, "1");

            }
        } catch (SQLException sqlEx){
            throw new RuntimeException (sqlEx);
        }
        return isAdminLoggedIn;
    }

}
