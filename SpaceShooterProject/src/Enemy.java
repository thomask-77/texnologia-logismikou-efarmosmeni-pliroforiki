import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Enemy extends Entity {
    private int speed;
    private BufferedImage image; // Εδώ θα αποθηκευτεί η εικόνα

    public Enemy(int x, int y, int speed) {
        super(x, y, 30, 30); // Μπορείς να αλλάξεις το 30, 30 για το μέγεθος του εχθρού
        this.speed = speed;
        
        // Φόρτωση της εικόνας
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/enemy.png"));
        } catch (IOException e) {
            System.out.println("Δεν βρέθηκε η εικόνα enemy.png!");
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        y += speed;
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}