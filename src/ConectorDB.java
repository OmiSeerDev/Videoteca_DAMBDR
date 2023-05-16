import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class ConectorDB {
    Connection dbConnect;
    static  PreparedStatement ps;
    static  ResultSet res;
    boolean isAdminLoggedIn;
    boolean userLoggedIn;
    static class UpdateDTO {
        JTable vidTable;
        JTextField name;
        JComboBox<Stack<Object>> category;
        JTextField author;
        JTextArea description;
        JTextField file;
        JTextField image;
        JTextField material;
        JTextField createdAt;
        JCheckBox recommended;

        public UpdateDTO (JTable vidTable,
                          JTextField name,
                          JComboBox<Stack<Object>> category,
                          JTextField author,
                          JTextArea description,
                          JTextField file,
                          JTextField image,
                          JTextField material,
                          JTextField createdAt,
                          JCheckBox recommended
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
            this.recommended = recommended;
        }
    }

    public void conectar () {
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

    public void showVideos (JTable vidTable) {
        String[] cols = {"ID", "NOMBRE", "AUTOR", "CATEGORIA", "AGREGADO EL", "RECOMENDADO", "DESCRIPCIÓN"};
        DefaultTableModel model = new DefaultTableModel ();

        model.addColumn ("id");
        model.addColumn ("name");
        model.addColumn ("author");
        model.addColumn ("category");
        model.addColumn ("created_at");
        model.addColumn ("recommended");
        model.addColumn ("description");

        vidTable.setModel (model);
        String readQuery = "SELECT * FROM videos INNER JOIN categories ON videos.category_id = categories.id ORDER BY videos.id ASC";
        Object[] data = new Object[7];
        PreparedStatement read;

        try {
            model.addRow (cols);
            conectar ();
            read = dbConnect.prepareStatement (readQuery);
            ResultSet rs = read.executeQuery ();
            while (rs.next ()) {
                data[0] = rs.getInt (1);
                data[1] = rs.getString (3);
                data[2] = rs.getString (4);
                data[3] = rs.getString (13);
                data[4] = rs.getString (10);
                data[5] = (Objects.equals (rs.getString (11), "1")) ? "Sí" : "No";
                data[6] = rs.getString (5);
                model.addRow (data);
                vidTable.setModel (model);
            }
        } catch (SQLException ex) {
            throw new RuntimeException (ex + "No se pudieron mostrar los videos");
        }
    }

    public void updateVideo ( UpdateDTO updateVideoDTO ){
        try {
            int selectRow = updateVideoDTO.vidTable.getSelectedRow ();
            conectar ();

            String updateQuery = "UPDATE videos SET category_id= '"+updateVideoDTO.category+"', name ='"
                    + updateVideoDTO.name.getText () +"', author= '"
                    + updateVideoDTO.author.getText ()+ "', description= '"+ updateVideoDTO.description.getText ()+"', file ='"
                    +updateVideoDTO.file.getText ()+"', image = '"+updateVideoDTO.image.getText ()+"', material = '"
                    +updateVideoDTO.material.getText ()+"', number_visits = 0, createdAt = '"+updateVideoDTO.createdAt.getText ()+"', recommended = '"
                    +updateVideoDTO.recommended+"' WHERE id = '"
                    + updateVideoDTO.vidTable.getValueAt (selectRow,0)+"'";

            Statement update = dbConnect.createStatement ();
            update.execute (updateQuery);
        } catch (SQLException ex){
            throw new RuntimeException (ex);
        }
    }

    public void deleteVideo (JTable vidTable){
        try {
            int selectRow = vidTable.getSelectedRow ();

            String deleteQuery = "DELETE FROM videos WHERE name = '"+ vidTable.getValueAt (selectRow,1)+"'";
            conectar ();
            Statement delete = dbConnect.createStatement ();
            delete.execute (deleteQuery);
        } catch (SQLException ex){
            throw new RuntimeException (ex);
        }
    }

}
