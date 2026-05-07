import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends Entity {
    private int speed = 7;
    private BufferedImage image; // Εδώ θα αποθηκευτεί η εικόνα

    public Player(int x, int y) {
        super(x, y, 40, 40); // Μπορείς να αλλάξεις το 40, 40 αν η εικόνα σου είναι μεγαλύτερη/μικρότερη
        
        // Φόρτωση της εικόνας
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/player.png"));
        } catch (IOException e) {
            System.out.println("Δεν βρέθηκε η εικόνα player.png!");
            e.printStackTrace();
        }
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    public void moveRight() {
        x += speed;
        if (x > 745) x = 745;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            // Αν βρήκε την εικόνα, τη ζωγραφίζει στις διαστάσεις του hitbox (width, height)
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Αν γίνει κάποιο λάθος και δεν τη βρει, ζωγραφίζει το παλιό γαλάζιο τετράγωνο (σαν Plan B)
            g.setColor(Color.CYAN);
            g.fillRect(x, y, width, height);
        }
    }
}