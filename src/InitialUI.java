import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

import static java.lang.System.exit;

public class InitialUI extends JFrame {
    private JPanel Layer;
    private JButton loginButton;
    private JButton videosButton;
    private JButton categoriesButton;
    private JButton rolesButton;
    private JButton exitButton;
    private JPasswordField passwordField;
    private JFormattedTextField emailField;
    private JPanel loginPanel;
    private JButton ingresarButton;
    private JTable vistas;
    private JButton logoutBtn;
    private JLabel emailLabel;
    private JLabel passwdLabel;
    private JLabel alertLabel;
    private JLabel roleTbLabel;
    ConectorDB dbConnect;
    VideosUI vidUI;

    InitialUI (){
        setContentPane (Layer);
        exitButton.setBackground (new Color (255,0,0));
        dbConnect = new ConectorDB ();
        ConectorDB.conectar ();
        loginPanel.setVisible (false);
        rolesButton.setVisible (false);
        roleTbLabel.setVisible (false);
        alertLabel.setVisible (false);
        vistas.setVisible (false);
        categoriesButton.setVisible (false);
        videosButton.setVisible (false);
        logoutBtn.setVisible (false);

        MouseAdapter onClickRole = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                vistas.setVisible (true);
                roleTbLabel.setVisible (true);

                String[] cols = {"ID", "NAME"};
                DefaultTableModel model = new DefaultTableModel ();

                model.addColumn ("id");
                model.addColumn ("name");

                model.addRow (cols);
                Object[] obj = new Object[2];
                try {
                    PreparedStatement ps = ConectorDB.dbConnect.prepareStatement ("SELECT * FROM roles");
                    ResultSet res = ps.executeQuery ();
                    while (res.next ()) {
                        obj[0] = res.getString (1);
                        obj[1] = res.getString (2);
                        model.addRow (obj);
                        vistas.setModel (model);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException (ex);
                }
            }
        };

        MouseAdapter onTryLogin = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                String userInput = emailField.getText ();
                try {
                    if (dbConnect.isAdminLogin (userInput)) {
                        rolesButton.setVisible (true);
                    }
                    if (dbConnect.loginQuery (userInput, passwordField.getText ())) {
                        logoutBtn.setVisible (true);
                        loginButton.setVisible (false);
                        ingresarButton.setVisible (false);
                        videosButton.setVisible (true);

                        emailField.setVisible (false);
                        emailLabel.setVisible (false);

                        passwordField.setVisible (false);
                        passwdLabel.setVisible (false);

                        alertLabel.setText ("Sesión iniciada");
                        alertLabel.setForeground (new Color (0, 255, 0));
                        alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 14));
                    } else {
                        alertLabel.setText ("Acceso inválido. Intente de nuevo");
                        alertLabel.setForeground (new Color (255, 0, 0));
                        alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 14));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException (ex);
                }
            }
        };

        MouseAdapter onLogin = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                loginPanel.setVisible (true);
                alertLabel.setVisible (true);
            }
        };

        MouseAdapter onLogout = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
                emailField.setVisible (true);
                emailLabel.setVisible (true);
                passwordField.setVisible (true);
                passwdLabel.setVisible (true);

                rolesButton.setVisible (false);
                roleTbLabel.setVisible (false);
                loginButton.setVisible (true);
                ingresarButton.setVisible (true);
                logoutBtn.setVisible (false);

                alertLabel.setText ("Sesión terminada");
                alertLabel.setForeground (new Color (255, 255, 0));
                alertLabel.setFont (new Font ("Arial Narrow", Font.BOLD, 14));
                exit(-1);
            }
        };

        MouseAdapter onVideoAccess = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            vidUI = new VideosUI ();
            vidUI.setVisible (true);
            Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
            vidUI.setSize (screen);
            vidUI.setLocationRelativeTo (null);
            }
        };

        MouseAdapter onExit = new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked (e);
            dispose ();
            exit(-1);
            }
        };

        rolesButton.addMouseListener (onClickRole);
        videosButton.addMouseListener (onVideoAccess);
        loginButton.addMouseListener (onLogin);
        exitButton.addMouseListener (onExit);
        ingresarButton.addMouseListener (onTryLogin);
        logoutBtn.addMouseListener (onLogout);

    }

}
