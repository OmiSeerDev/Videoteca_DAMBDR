import javax.swing.*;

public class Main {
    public static void main (String[] args) {
        SwingUtilities.invokeLater (()-> {
            InitialUI mainFrame = new InitialUI ();
            mainFrame.setVisible(true);
            mainFrame.setSize(316,480);
            mainFrame.setLocation(450,150);
            mainFrame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE );
        });
    }
}