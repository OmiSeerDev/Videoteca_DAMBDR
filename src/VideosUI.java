import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class VideosUI extends JFrame {
    private JTextField nameField;
    private JTextField authField;
    private JTextArea descArea;
    private JButton addVButton;
    private JButton delVButton;
    private JTextPane vidDescr;
    private JTable vidTable;
    private JLabel vidNameLabel;
    private JLabel vidAuthLabel;
    private JLabel vidCatgLabel;
    private JPanel videoWindow;
    private JButton cancelButton;
    private JButton mostrarButton;
    private JLabel alertLabel;
    private JButton updVButton;
    ConectorDB dbConnect = new ConectorDB ();

    public void createVideo (){
        String videoName = nameField.getText ();
        String videoAuthor = authField.getText ();
        String description = descArea.getText ();
        try {
            dbConnect.conectar ();
            String DATE_FORMAT = "YYYY-MM-dd hh:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);
            String createVidQuery = "INSERT INTO videos (id, category_id, name, author, description," +
                    " file, image, material, number_visits, created_at, recommended)" +
                    " VALUES (default ,2 ,'" + videoName + "', '" + videoAuthor + "', '" + description +
                    "', null, null, null, null, '" + dateFormat.format (new Date ()) + "', null)";

            Statement ps = dbConnect.dbConnect.createStatement ();
            ps.execute (createVidQuery);
        }catch (SQLException e) {
            throw new RuntimeException (e);
        }

    }

    public void selectVideo (JTable vidTable) {
        try {
            int fila = vidTable.getSelectedRow ();

            if(fila >= 0){
                vidNameLabel.setText ("Título: " + vidTable.getValueAt(fila,1));
                vidAuthLabel.setText ("Autor: " + vidTable.getValueAt (fila, 2));
                vidCatgLabel.setText ("Categoría: " + vidTable.getValueAt (fila, 3));
                vidDescr.setText("Descripción:\n" + vidTable.getValueAt (fila,6));

            }
        } catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }



    VideosUI(){
        setContentPane(videoWindow);
        vidDescr.setEditable (false);
        MouseAdapter onCancel = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            dispose ();
            }
        };

        MouseAdapter onSelectRow =  new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            selectVideo (vidTable);
            }
        };

        MouseAdapter onVideoClick = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            dbConnect.showVideos (vidTable);
            }
        };

        MouseAdapter onCreateVideo = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                if (Objects.equals (nameField.getText (), "") || Objects.equals (authField.getText (), "")) {
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (255,0,0));
                    alertLabel.setText ("EL NOMBRE Y AUTOR DEL VIDEO NO DEBEN ESTAR VACÍOS");
                } else {
                    createVideo ();
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (36, 102, 75, 255));
                    alertLabel.setText ("VIDEO AGREGADO CORRECTAMENTE");
                    nameField.setText (""); authField.setText (""); descArea.setText ("");
                }
            }
        };

        MouseAdapter onUpdateVideo = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                if (Objects.equals (nameField.getText (), "") || Objects.equals (authField.getText (), "")) {
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (255, 0, 0));
                    alertLabel.setText ("EL NOMBRE Y AUTOR DEL VIDEO NO DEBEN ESTAR VACÍOS");
                } else {
                    dbConnect.updateVideo (vidTable, nameField, authField, descArea);
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (36, 102, 75, 255));
                    alertLabel.setText ("VIDEO ACTUALIZADO CORRECTAMENTE");
                }
            }
        };

        MouseAdapter onDeleteVideo = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            dbConnect.deleteVideo(vidTable);
                alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                alertLabel.setForeground (new Color (8, 0, 223, 255));
                alertLabel.setText ("VIDEO BORRADO CORRECTAMENTE");
            }
        };

        addVButton.addMouseListener (onCreateVideo);
        delVButton.addMouseListener (onDeleteVideo);
        updVButton.addMouseListener(onUpdateVideo);
        mostrarButton.addMouseListener (onVideoClick);
        cancelButton.addMouseListener (onCancel);
        vidTable.addMouseListener (onSelectRow);
    }
}
