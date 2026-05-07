import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player extends Entity {
    private int speed = 10;
    private BufferedImage image; 

    public Player(int x, int y) {
        super(x, y, 40, 40); 
        
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/images/player.png"));
        } catch (IOException e) {
            System.out.println("Δεν βρέθηκε η εικόνα player.png!");
        }
    }

    public void moveLeft() {
        x -= speed;
    }

    public void moveRight() {
        x += speed;
    }

    // --- ΟΙ ΔΥΟ ΝΕΕΣ ΜΕΘΟΔΟΙ ΠΟΥ ΠΡΟΣΘΕΣΑΜΕ ---
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }
    // ------------------------------------------

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(x, y, width, height);
        }
    }
}