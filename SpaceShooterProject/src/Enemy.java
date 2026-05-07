import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Enemy extends Entity {
    private int speed;
    private BufferedImage image;

    public Enemy(int x, int y, int speed) {
        super(x, y, 30, 30); // enemy size 
        this.speed = speed;
        
       
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/images/enemy.png"));
        } catch (IOException e) {
            System.out.println("Image enemy.png not found!");
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