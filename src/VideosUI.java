import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private JTable categoriesTable;
    private JTextField fileField;
    private JTextField imageField;
    private JTextField materialField;
    private JTextField createdField;
    private JCheckBox recommendedCheck;
    private JComboBox<Integer>categoriesCombo;
    private JTextField visitsField;
    ConectorDB dbConnect = new ConectorDB ();


    public void showCategoriesTable (JTable categoriesTable) {
        String[] cols = {"ID", "CATEGORÍA", "TIPO", "DESCRIPCIÓN"};
        DefaultTableModel model = new DefaultTableModel ();

        model.addColumn ("id");
        model.addColumn ("name");
        model.addColumn ("type");
        model.addColumn ("description");
        categoriesTable.setModel (model);

        try {
            model.addRow (cols);
            dbConnect.conectar ();
            String showCatsQuery = "SELECT * FROM categories";
            Object[] categoryRow = new Object[4];
            PreparedStatement ps = dbConnect.dbConnect.prepareStatement (showCatsQuery);
            ResultSet cats = ps.executeQuery ();
            while (cats.next ()) {
                categoryRow[0] = cats.getInt (1);
                categoryRow[1] = cats.getString (2);
                categoryRow[2] = cats.getString (3);
                categoryRow[3] = cats.getString (4);
                model.addRow (categoryRow);
                vidTable.setModel (model);
                try {
                    Integer[] catElements = new Integer[1];
                    for (int i = 0; i < catElements.length; i++) {
                    catElements[i] = (Integer) categoryRow[0];
                    categoriesCombo.addItem (catElements[i]);
                    }
                } catch (NullPointerException ex) {
                    throw new RuntimeException (ex);
                }
            }
        } catch (SQLException sqlEx) {
            throw new RuntimeException (sqlEx);
        }
    }

    public void createVideo (){
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
            dbConnect.conectar ();

            String createVidQuery = "INSERT INTO videos (id, category_id, name, author, description," +
                    " file, image, material, number_visits, created_at, recommended)" +
                    " VALUES (default , "+category+" ,'" + videoName + "', '" + videoAuthor + "', '" + description +
                    "', '" + file + "', '"+ image +"', '" + material+"', 0, '" + fecha + "', "+recommended+")";

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
        visitsField.setEnabled (false);
        createdField.setEnabled (false);
        vidTable.setVisible (false);

        addVButton.setVisible (false);
        delVButton.setVisible (false);
        updVButton.setVisible (false);

        showCategoriesTable (categoriesTable);
        categoriesTable.setVisible (false);
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

        MouseAdapter onShowClick = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                vidTable.setVisible (true);

                categoriesTable.setVisible (true);
            dbConnect.showVideos (vidTable);
                addVButton.setVisible (true);
                delVButton.setVisible (true);
                updVButton.setVisible (true);
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
                    dbConnect.showVideos (vidTable);
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
                    ConectorDB.UpdateDTO updateVideoDTO = new ConectorDB.UpdateDTO (
                            vidTable,
                            nameField,
                            categoriesCombo,
                            authField,
                            descArea,
                            fileField,
                            imageField,
                            materialField,
                            createdField,
                            recommendedCheck
                    );
                    dbConnect.updateVideo (updateVideoDTO);
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (36, 102, 75, 255));
                    alertLabel.setText ("VIDEO ACTUALIZADO CORRECTAMENTE");
                    dbConnect.showVideos (vidTable);
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
                dbConnect.showVideos (vidTable);
            }
        };

        addVButton.addMouseListener (onCreateVideo);
        delVButton.addMouseListener (onDeleteVideo);
        updVButton.addMouseListener(onUpdateVideo);
        mostrarButton.addMouseListener (onShowClick);
        cancelButton.addMouseListener (onCancel);
        vidTable.addMouseListener (onSelectRow);
    }
}
