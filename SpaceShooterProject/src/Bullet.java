import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends Entity {
    private int speed = 10;

    public Bullet(int x, int y) {
        super(x, y, 5, 15); // Μικρό και μακρόστενο
    }

    @Override
    public void update() {
        y -= speed; // Κινείται προς τα πάνω
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }
}