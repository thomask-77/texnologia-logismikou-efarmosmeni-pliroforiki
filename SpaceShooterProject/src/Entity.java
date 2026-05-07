import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
    protected int x, y, width, height;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Επιστρέφει το Hitbox για τις συγκρούσεις
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // --- ΠΡΟΣΘΕΣΕ ΑΥΤΕΣ ΤΙΣ ΔΥΟ ΓΡΑΜΜΕΣ ---
    public int getX() { return x; }
    public int getY() { return y; }
    // --------------------------------------

    public abstract void update();
    public abstract void draw(Graphics g);
}