import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Bullet extends Entity {
    private int speed = 10;
    private BufferedImage image; // Η εικόνα του laser

    public Bullet(int x, int y) {
        // Διαστάσεις: 10 πλάτος, 30 ύψος (μπορείς να τις αλλάξεις αν το laser σου είναι πιο παχύ/μακρύ)
        super(x, y, 50, 70); 
        
        // Φόρτωση της εικόνας bullet.png
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/images/bullet.png"));
        } catch (IOException e) {
            System.out.println("Δεν βρέθηκε η εικόνα bullet.png!");
        }
    }

    @Override
    public void update() {
        y -= speed; // Κινείται προς τα πάνω
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            // Ζωγραφίζει το PNG στις διαστάσεις που ορίσαμε (10x30)
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Plan B: Αν δεν βρει την εικόνα, ζωγραφίζει ένα κίτρινο laser
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }
}