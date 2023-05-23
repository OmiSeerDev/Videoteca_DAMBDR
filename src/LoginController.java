import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class LoginController extends ConectorDB {
    public static boolean loginQuery (String email, String password) throws SQLException {
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
        return userLoggedIn;
    }
    public static boolean isAdminLogin (String email) throws SQLException {
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
