import javax.swing.*;

public class Main {
    public static void main (String[] args) {
        SwingUtilities.invokeLater (()-> {
            Videoteca mainFrame = new Videoteca ();
            mainFrame.setVisible(true);
            mainFrame.setSize(550,400);
            mainFrame.setLocation(450,150);
            mainFrame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        });
    }
}