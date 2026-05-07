import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter Retro");
        GamePanel gamePanel = new GamePanel();

        // Χρησιμοποιούμε BorderLayout για να γεμίζει το GamePanel όλο το frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);

        // Αρχικό μέγεθος (pack() θα το κάνει 800x600)
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Κεντράρισμα στην οθόνη

        // --- Η ΔΙΟΡΘΩΣΗ: ΕΝΕΡΓΟΠΟΙΗΣΗ ΤΟΥ RESIZE ΚΑΙ ΤΟΥ MAXIMIZE BUTTON ---
        frame.setResizable(true); 

        frame.setVisible(true);
    }
}