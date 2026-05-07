import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter Retro");
        GamePanel gamePanel = new GamePanel();
        
        frame.add(gamePanel);
        frame.pack(); // Προσαρμόζει το παράθυρο στο μέγεθος του JPanel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Κεντράρει το παράθυρο στην οθόνη
        frame.setResizable(false);
        frame.setVisible(true);
    }
}