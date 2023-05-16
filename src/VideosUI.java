import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ConectorDB.conectar ();
            String showCatsQuery = "SELECT * FROM categories";
            Object[] categoryRow = new Object[4];
            PreparedStatement ps = ConectorDB.dbConnect.prepareStatement (showCatsQuery);
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
            VideosController.showVideos (vidTable);
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
                    VideosController.createVideo (
                            categoriesCombo,
                            nameField,
                            authField,
                            descArea,
                            fileField,
                            imageField,
                            materialField,
                            recommendedCheck
                    );
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (36, 102, 75, 255));
                    alertLabel.setText ("VIDEO AGREGADO CORRECTAMENTE");

                    nameField.setText (""); authField.setText (""); descArea.setText ("");
                    fileField.setText (""); imageField.setText (""); materialField.setText ("");
                    recommendedCheck.setSelected (false);

                    VideosController.showVideos (vidTable);
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
                    nameField.setBackground (Color.RED);
                    authField.setBackground (Color.RED);
                } else {
                    VideosController.UpdateDTO updateVideoDTO = new VideosController.UpdateDTO (
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
                    VideosController.updateVideo (updateVideoDTO);
                    alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                    alertLabel.setForeground (new Color (36, 102, 75, 255));
                    alertLabel.setText ("VIDEO ACTUALIZADO CORRECTAMENTE");
                    VideosController.showVideos (vidTable);
                    nameField.setBackground (Color.WHITE);
                    authField.setBackground (Color.WHITE);
                    nameField.setText (""); authField.setText (""); descArea.setText ("");
                    fileField.setText (""); imageField.setText (""); materialField.setText ("");
                    recommendedCheck.setSelected (false);
                }
            }
        };

        MouseAdapter onDeleteVideo = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            VideosController.deleteVideo(vidTable);
                alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 18));
                alertLabel.setForeground (new Color (8, 0, 223, 255));
                alertLabel.setText ("VIDEO BORRADO CORRECTAMENTE");
                VideosController.showVideos (vidTable);
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
