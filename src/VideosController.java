import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class VideosController extends ConectorDB{
    public static void createVideo (
            JComboBox<Integer> categoriesCombo,
            JTextField nameField,
            JTextField authField,
            JTextArea descArea,
            JTextField fileField,
            JTextField imageField,
            JTextField materialField,
            JCheckBox recommendedCheck
    ){
        String DATE_FORMAT = "YYYY-MM-dd hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);

        String category = categoriesCombo.getSelectedItem ().toString ();
        String videoName = nameField.getText ();
        String videoAuthor = authField.getText ();
        String description = descArea.getText ();
        String file = fileField.getText ();
        String image = imageField.getText ();
        String material = materialField.getText ();
        String fecha = dateFormat.format (new Date ());
        String recommended = recommendedCheck.isSelected () ? "1" : "0";
        try {
            conectar ();

            String createVidQuery = "INSERT INTO videos (id, category_id, name, author, description," +
                    " file, image, material, number_visits, created_at, recommended)" +
                    " VALUES (default , "+category+" ,'" + videoName + "', '" + videoAuthor + "', '" + description +
                    "', '" + file + "', '"+ image +"', '" + material+"', 0, '" + fecha + "', "+recommended+")";

            Statement ps = dbConnect.createStatement ();
            ps.execute (createVidQuery);
        }catch (SQLException e) {
            throw new RuntimeException (e);
        }

    }
    public static void showVideos (JTable vidTable) {
        String[] cols = {"ID", "NOMBRE", "AUTOR", "CATEGORIA", "AGREGADO EL", "RECOMENDADO",
                "DESCRIPCIÓN","ARCHIVO", "IMAGEN", "MATERIAL", "VISITAS"};
        DefaultTableModel model = new DefaultTableModel ();

        model.addColumn ("id");
        model.addColumn ("name");
        model.addColumn ("author");
        model.addColumn ("category");
        model.addColumn ("created_at");
        model.addColumn ("recommended");
        model.addColumn ("description");
        model.addColumn ("file");
        model.addColumn ("image");
        model.addColumn ("material");
        model.addColumn ("number_visits");

        vidTable.setModel (model);
        String readQuery = "SELECT * FROM videos INNER JOIN categories ON videos.category_id = categories.id ORDER BY videos.id ASC";
        Object[] data = new Object[11];
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
                data[7] = rs.getString (6);
                data[8] = rs.getString (7);
                data[9] = rs.getString (8);
                data[10] = rs.getInt (9);
                model.addRow (data);
                vidTable.setModel (model);
            }
        } catch (SQLException ex) {
            throw new RuntimeException (ex + "No se pudieron mostrar los videos");
        }
    }

    public static void updateVideo (UpdateDTO updateVideoDTO){
        try {
            int selectRow = updateVideoDTO.vidTable.getSelectedRow ();
            conectar ();

            String updateQuery = "UPDATE videos SET category_id= "+ (updateVideoDTO.category.getSelectedIndex ()) +", name ='"
                    + updateVideoDTO.name.getText () +"', author= '"
                    + updateVideoDTO.author.getText ()+ "', description= '"+ updateVideoDTO.description.getText ()+"', file ='"
                    +updateVideoDTO.file.getText ()+"', image = '"+updateVideoDTO.image.getText ()+"', material = '"
                    +updateVideoDTO.material.getText ()+"', number_visits = 0, created_at = '"
                    +updateVideoDTO.createdAt.getText ()+"', recommended = "
                    +updateVideoDTO.recommended+" WHERE id = '"
                    + updateVideoDTO.vidTable.getValueAt (selectRow,0)+"'";

            Statement update = dbConnect.createStatement ();
            update.execute (updateQuery);
        } catch (SQLException ex){
            throw new RuntimeException (ex);
        }
    }

    public static void deleteVideo (JTable vidTable){
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
