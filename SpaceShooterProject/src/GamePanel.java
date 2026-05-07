import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
    
    // Προσθέσαμε το PAUSED στις καταστάσεις
    private enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER
    }
    private GameState currentState = GameState.MENU;

    private Timer timer;
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private int score = 0;
    private int highScore = 0;
    private Random random;
    private Preferences prefs;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private int currentEnemySpeed = 3;
    private int currentSpawnChance = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        this.addKeyListener(this);
        this.addMouseListener(this); 

        prefs = Preferences.userNodeForPackage(GamePanel.class);
        highScore = prefs.getInt("highscore", 0);

        random = new Random();
        initGame();

        timer = new Timer(16, this);
        timer.start();
    }

    private void initGame() {
        player = new Player(380, 500);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        score = 0;
        leftPressed = false;
        rightPressed = false;
        currentEnemySpeed = 3;
        currentSpawnChance = 5;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Το update γίνεται ΜΟΝΟ όταν παίζουμε
        if (currentState == GameState.PLAYING) {
            updateGame();
            checkCollisions();
        }
        repaint();
    }

    private void updateGame() {
        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight();

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).getY() < 0) {
                bullets.remove(i);
            }
        }

        if (random.nextInt(100) < currentSpawnChance) {
            enemies.add(new Enemy(random.nextInt(750), -30, currentEnemySpeed));
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
            if (enemies.get(i).getY() > 600) {
                enemies.remove(i);
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < bullets.size(); i++) {
            for (int j = 0; j < enemies.size(); j++) {
                if (bullets.get(i).getBounds().intersects(enemies.get(j).getBounds())) {
                    bullets.remove(i);
                    enemies.remove(j);
                    score += 10;
                    if (score % 50 == 0) {
                        currentEnemySpeed++;
                        currentSpawnChance++;
                    }
                    break;
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (player.getBounds().intersects(enemy.getBounds())) {
                currentState = GameState.GAMEOVER;
                if (score > highScore) {
                    highScore = score;
                    prefs.putInt("highscore", highScore);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentState == GameState.MENU) {
            drawMenu(g);
        } else if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
            drawGame(g);
            if (currentState == GameState.PAUSED) {
                drawPauseMenu(g);
            }
        } else if (currentState == GameState.GAMEOVER) {
            drawGameOver(g);
        }
    }

    // --- Βοηθητικές μέθοδοι ζωγραφικής για καθαρό κώδικα ---
    
    private void drawMenu(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("SPACE SHOOTER", 140, 200);
        drawButton(g, 250, 300, "PLAY");
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("High Score: " + highScore, 325, 420);
    }

    private void drawGame(Graphics g) {
        player.draw(g);
        for (Bullet bullet : bullets) bullet.draw(g);
        for (Enemy enemy : enemies) enemy.draw(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
        g.setColor(Color.YELLOW);
        g.drawString("High Score: " + highScore, 10, 60);
    }

    private void drawPauseMenu(Graphics g) {
        // Ημιδιαφανές φόντο
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, 800, 600);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSED", 300, 150);
        
        drawButton(g, 250, 220, "RESUME");
        drawButton(g, 250, 290, "RESTART");
        drawButton(g, 250, 360, "MAIN MENU");
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("GAME OVER", 210, 150);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Final Score: " + score, 280, 220);
        drawButton(g, 250, 300, "RESTART");
        drawButton(g, 250, 370, "MAIN MENU");
    }

    private void drawButton(Graphics g, int x, int y, String text) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, 300, 50);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, 300, 50);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        // Κεντράρισμα κειμένου (στο περίπου)
        int textX = x + 150 - (text.length() * 7); 
        g.drawString(text, textX, y + 35);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(); 
        int my = e.getY(); 

        if (currentState == GameState.MENU) {
            if (isInside(mx, my, 250, 300, 300, 50)) {
                initGame();
                currentState = GameState.PLAYING;
            }
        } 
        else if (currentState == GameState.PAUSED) {
            if (isInside(mx, my, 250, 220, 300, 50)) currentState = GameState.PLAYING; // Resume
            else if (isInside(mx, my, 250, 290, 300, 50)) { initGame(); currentState = GameState.PLAYING; } // Restart
            else if (isInside(mx, my, 250, 360, 300, 50)) currentState = GameState.MENU; // Main Menu
        }
        else if (currentState == GameState.GAMEOVER) {
            if (isInside(mx, my, 250, 300, 300, 50)) { initGame(); currentState = GameState.PLAYING; }
            else if (isInside(mx, my, 250, 370, 300, 50)) currentState = GameState.MENU;
        }
    }

    private boolean isInside(int mx, int my, int x, int y, int w, int h) {
        return (mx >= x && mx <= x + w && my >= y && my <= y + h);
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Έλεγχος για ESCAPE σε οποιαδήποτε φάση του παιχνιδιού
        if (key == KeyEvent.VK_ESCAPE) {
            if (currentState == GameState.PLAYING) currentState = GameState.PAUSED;
            else if (currentState == GameState.PAUSED) currentState = GameState.PLAYING;
        }

        if (currentState == GameState.MENU) {
            if (key == KeyEvent.VK_ENTER) { initGame(); currentState = GameState.PLAYING; }
        } 
        else if (currentState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_SPACE) bullets.add(new Bullet(player.getX() + 17, player.getY()));
        } 
        else if (currentState == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_ENTER) { initGame(); currentState = GameState.PLAYING; }
            else if (key == KeyEvent.VK_M) currentState = GameState.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override public void keyTyped(KeyEvent e) {}
}