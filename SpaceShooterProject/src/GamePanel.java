import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    
    private enum GameState {
        MENU, DIFFICULTY_SELECT, PLAYING, PAUSED, OPTIONS, CONTROLS, ABOUT, GAMEOVER
    }
    private GameState currentState = GameState.MENU;
    private GameState lastState = GameState.MENU; 

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

    private int currentEnemySpeed;
    private int currentSpawnChance;
    
    private int baseEnemySpeed = 3;
    private int baseSpawnChance = 5;

    private BufferedImage backgroundImage;
    private BufferedImage titleImage;
    private int bgY = 0; 

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        this.addKeyListener(this);
        this.addMouseListener(this); 
        this.addMouseMotionListener(this);

        prefs = Preferences.userNodeForPackage(GamePanel.class);
        highScore = prefs.getInt("highscore", 0);
        random = new Random();
        
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/background.png"));
            titleImage = ImageIO.read(getClass().getResourceAsStream("/title.png"));
        } catch (IOException e) {
            System.out.println("Πρόβλημα στη φόρτωση των εικόνων!");
        }

        player = new Player(400, 500);
        player.setSpeed(10); 

        initGame();

        timer = new Timer(16, this);
        timer.start();
    }

    private void initGame() {
        int savedSpeed = (player != null) ? player.getSpeed() : 10;
        player = new Player(getWidth() / 2 - 20, getHeight() - 100);
        player.setSpeed(savedSpeed);
        
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        score = 0;
        leftPressed = false;
        rightPressed = false;
        
        currentEnemySpeed = baseEnemySpeed;
        currentSpawnChance = baseSpawnChance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != GameState.PAUSED && currentState != GameState.OPTIONS && currentState != GameState.DIFFICULTY_SELECT && currentState != GameState.ABOUT && currentState != GameState.CONTROLS) { 
            bgY += 1; 
            if (bgY >= getHeight() * 2) bgY = 0; 
        }

        if (currentState == GameState.PLAYING) {
            updateGame();
            checkCollisions();
        }
        repaint();
    }

    private void updateGame() {
        if (leftPressed && player.getX() > 0) player.moveLeft();
        if (rightPressed && player.getX() < getWidth() - 40) player.moveRight();

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).getY() < -50) bullets.remove(i);
        }

        if (random.nextInt(100) < currentSpawnChance) {
            // score after 700 more enemies on sides
            int spawnRange = (score < 700) ? (int)(getWidth() * 0.3) : (int)(getWidth() * 0.9);
            int startX = (getWidth() / 2) - (spawnRange / 2);
            int randomX = startX + random.nextInt(Math.max(1, spawnRange - 30));
            enemies.add(new Enemy(randomX, -50, currentEnemySpeed));
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
            if (enemies.get(i).getY() > getHeight() + 50) enemies.remove(i);
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < bullets.size(); i++) {
            for (int j = 0; j < enemies.size(); j++) {
                if (bullets.get(i).getBounds().intersects(enemies.get(j).getBounds())) {
                    bullets.remove(i); enemies.remove(j); score += 10;
                    if (score > 0 && score % 150 == 0) { 
                        if (currentEnemySpeed < 10) currentEnemySpeed++; 
                        if (currentSpawnChance < 15) currentSpawnChance++; 
                    }
                    break;
                }
            }
        }
        for (Enemy enemy : enemies) {
            if (player.getBounds().intersects(enemy.getBounds())) {
                currentState = GameState.GAMEOVER;
                if (score > highScore) { highScore = score; prefs.putInt("highscore", highScore); }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(); int h = getHeight();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, bgY, w, h, null);
            g.drawImage(backgroundImage, 0, bgY, w, -h, null); 
            g.drawImage(backgroundImage, 0, bgY - h * 2, w, h, null); 
        }
        
        if (currentState == GameState.MENU) drawMenu(g);
        else if (currentState == GameState.DIFFICULTY_SELECT) { drawMenu(g); drawDifficultyMenu(g); }
        else if (currentState == GameState.CONTROLS) { drawMenu(g); drawControlsMenu(g); }
        else if (currentState == GameState.ABOUT) { drawMenu(g); drawAboutMenu(g); }
        else if (currentState == GameState.PLAYING || currentState == GameState.PAUSED || currentState == GameState.OPTIONS) {
            drawGame(g);
            if (currentState == GameState.PAUSED) drawPauseMenu(g);
            else if (currentState == GameState.OPTIONS) drawOptionsMenu(g);
        } 
        else if (currentState == GameState.GAMEOVER) drawGameOver(g);
    }

    private void drawMenu(Graphics g) {
        int centerX = getWidth() / 2;
        if (titleImage != null) g.drawImage(titleImage, centerX - 250, 20, 500, 180, null);
        else {
            g.setColor(Color.GREEN); g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("SPACE SHOOTER", centerX - 250, 150);
        }
        
        // --- HIGH SCORE ΚΑΤΩ ΑΠΟ ΤΟΝ ΤΙΤΛΟ ---
        g.setColor(Color.YELLOW); 
        g.setFont(new Font("Arial", Font.BOLD, 22));
        String hsText = "High Score: " + highScore;
        int hsWidth = g.getFontMetrics().stringWidth(hsText);
        g.drawString(hsText, centerX - (hsWidth / 2), 235);
        
        // --- ΚΟΥΜΠΙΑ (ΜΕΤΑΤΟΠΙΣΜΕΝΑ ΠΙΟ ΚΑΤΩ) ---
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 270, "PLAY");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 330, "OPTIONS");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 390, "CONTROLS");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 450, "ABOUT");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 510, "EXIT");
    }

    private void drawDifficultyMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220)); g.fillRect(0, 0, getWidth(), getHeight());
        int centerX = getWidth() / 2;
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("SELECT DIFFICULTY", centerX - 260, 150);
        
        g.setColor(Color.GREEN); drawButton(g, centerX - 150, 220, "EASY");
        g.setColor(Color.YELLOW); drawButton(g, centerX - 150, 290, "NORMAL");
        g.setColor(Color.RED); drawButton(g, centerX - 150, 360, "HARD");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 430, "BACK");
    }

    private void drawControlsMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220)); g.fillRect(0, 0, getWidth(), getHeight());
        int centerX = getWidth() / 2;
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("CONTROLS", centerX - 140, 150);
        g.setFont(new Font("Arial", Font.BOLD, 25)); g.setColor(Color.LIGHT_GRAY);
        g.drawString("Move Left: LEFT ARROW", centerX - 160, 230);
        g.drawString("Move Right: RIGHT ARROW", centerX - 160, 280);
        g.drawString("Shoot: SPACEBAR", centerX - 160, 330);
        g.drawString("Pause / Back: ESCAPE", centerX - 160, 380);
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 450, "BACK");
    }
// ABOUT MENU
    private void drawAboutMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220)); 
        g.fillRect(0, 0, getWidth(), getHeight());
        
        int centerX = getWidth() / 2;
        FontMetrics fm;

       
        g.setColor(Color.WHITE); 
        g.setFont(new Font("Arial", Font.BOLD, 50));
        fm = g.getFontMetrics();
        String title = "ABOUT";
        g.drawString(title, centerX - (fm.stringWidth(title) / 2), 150);
        
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        g.setColor(Color.LIGHT_GRAY);
        fm = g.getFontMetrics(); 

  
        String s1 = "Space Shooter Retro";
        g.drawString(s1, centerX - (fm.stringWidth(s1) / 2), 230);

  
        String beta = "Beta Version 0.0.3";
        g.drawString(beta, centerX - (fm.stringWidth(beta) / 2), 280);

        
        String s2 = "Created for University of Macedonia";
        g.drawString(s2, centerX - (fm.stringWidth(s2) / 2), 330);
        
        // BACK
        g.setColor(Color.WHITE); 
        drawButton(g, centerX - 150, 450, "BACK");
    }

    private void drawGame(Graphics g) {
        player.draw(g);
        for (Bullet bullet : bullets) bullet.draw(g);
        for (Enemy enemy : enemies) enemy.draw(g);
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("High Score: " + highScore, 10, 60);
    }

    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150)); g.fillRect(0, 0, getWidth(), getHeight());
        int centerX = getWidth() / 2;
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSED", centerX - 100, 150);
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 220, "RESUME");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 290, "OPTIONS");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 360, "RESTART");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 430, "MAIN MENU");
    }

    private void drawOptionsMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220)); g.fillRect(0, 0, getWidth(), getHeight());
        int centerX = getWidth() / 2;
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("OPTIONS", centerX - 120, 150);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Player Speed: " + player.getSpeed(), centerX - 100, 250);
        int sliderX = centerX - 150; int sliderY = 280; int sliderW = 300; int sliderH = 30;
        g.setColor(Color.DARK_GRAY); g.fillRect(sliderX, sliderY, sliderW, sliderH);
        double percentage = (double)(player.getSpeed() - 3) / (15 - 3);
        g.setColor(Color.GREEN); g.fillRect(sliderX, sliderY, (int)(sliderW * percentage), sliderH);
        g.setColor(Color.WHITE); g.drawRect(sliderX, sliderY, sliderW, sliderH);
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 400, "BACK");
    }

    private void drawGameOver(Graphics g) {
        int centerX = getWidth() / 2;
        g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("GAME OVER", centerX - 170, 150);
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Final Score: " + score, centerX - 100, 220);
        g.setColor(Color.YELLOW); g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("High Score: " + highScore, centerX - 75, 260);
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 320, "RESTART");
        g.setColor(Color.WHITE); drawButton(g, centerX - 150, 390, "MAIN MENU");
    }

    private void drawButton(Graphics g, int x, int y, String text) {
        Color textColor = g.getColor(); 
        g.setColor(Color.DARK_GRAY); g.fillRect(x, y, 300, 50);
        g.setColor(textColor); g.drawRect(x, y, 300, 50);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        int textX = x + 150 - (text.length() * 7); g.drawString(text, textX, y + 35);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(); int my = e.getY(); int centerX = getWidth() / 2;
        if (currentState == GameState.MENU) {
            if (isInside(mx, my, centerX - 150, 270, 300, 50)) currentState = GameState.DIFFICULTY_SELECT;
            else if (isInside(mx, my, centerX - 150, 330, 300, 50)) { lastState = GameState.MENU; currentState = GameState.OPTIONS; }
            else if (isInside(mx, my, centerX - 150, 390, 300, 50)) currentState = GameState.CONTROLS;
            else if (isInside(mx, my, centerX - 150, 450, 300, 50)) currentState = GameState.ABOUT;
            else if (isInside(mx, my, centerX - 150, 510, 300, 50)) System.exit(0);
        } 
        else if (currentState == GameState.DIFFICULTY_SELECT) {
            if (isInside(mx, my, centerX - 150, 220, 300, 50)) { baseEnemySpeed = 2; baseSpawnChance = 3; initGame(); currentState = GameState.PLAYING; }
            else if (isInside(mx, my, centerX - 150, 290, 300, 50)) { baseEnemySpeed = 3; baseSpawnChance = 5; initGame(); currentState = GameState.PLAYING; }
            else if (isInside(mx, my, centerX - 150, 360, 300, 50)) { baseEnemySpeed = 5; baseSpawnChance = 8; initGame(); currentState = GameState.PLAYING; }
            else if (isInside(mx, my, centerX - 150, 430, 300, 50)) currentState = GameState.MENU;
        }
        else if (currentState == GameState.CONTROLS || currentState == GameState.ABOUT) {
            if (isInside(mx, my, centerX - 150, 450, 300, 50)) currentState = GameState.MENU;
        }
        else if (currentState == GameState.PAUSED) {
            if (isInside(mx, my, centerX - 150, 220, 300, 50)) currentState = GameState.PLAYING; 
            else if (isInside(mx, my, centerX - 150, 290, 300, 50)) { lastState = GameState.PAUSED; currentState = GameState.OPTIONS; }
            else if (isInside(mx, my, centerX - 150, 360, 300, 50)) { initGame(); currentState = GameState.PLAYING; } 
            else if (isInside(mx, my, centerX - 150, 430, 300, 50)) currentState = GameState.MENU; 
        } 
        else if (currentState == GameState.OPTIONS) {
            if (isInside(mx, my, centerX - 150, 400, 300, 50)) currentState = lastState;
        } 
        else if (currentState == GameState.GAMEOVER) {
            if (isInside(mx, my, centerX - 150, 320, 300, 50)) { initGame(); currentState = GameState.PLAYING; }
            else if (isInside(mx, my, centerX - 150, 390, 300, 50)) currentState = GameState.MENU;
        }
    }

    private void updateSliderLogic(int mx) {
        int centerX = getWidth() / 2; int sliderX = centerX - 150;
        mx = Math.max(sliderX, Math.min(mx, sliderX + 300));
        double pct = (double)(mx - sliderX) / 300;
        player.setSpeed(3 + (int)(pct * 12));
        repaint();
    }

    @Override public void mousePressed(MouseEvent e) { if (currentState == GameState.OPTIONS && e.getY() >= 270 && e.getY() <= 320) updateSliderLogic(e.getX()); }
    @Override public void mouseDragged(MouseEvent e) { if (currentState == GameState.OPTIONS && e.getY() >= 260 && e.getY() <= 330) updateSliderLogic(e.getX()); }
    private boolean isInside(int mx, int my, int x, int y, int w, int h) { return (mx >= x && mx <= x + w && my >= y && my <= y + h); }
    @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            if (currentState == GameState.PLAYING) currentState = GameState.PAUSED;
            else if (currentState == GameState.PAUSED) currentState = GameState.PLAYING;
            else if (currentState == GameState.OPTIONS) currentState = lastState;
            else if (currentState == GameState.DIFFICULTY_SELECT || currentState == GameState.ABOUT || currentState == GameState.CONTROLS) currentState = GameState.MENU;
        }
        if (currentState == GameState.MENU && key == KeyEvent.VK_ENTER) currentState = GameState.DIFFICULTY_SELECT; 
        else if (currentState == GameState.DIFFICULTY_SELECT) {
            if (key == KeyEvent.VK_1) { baseEnemySpeed = 2; baseSpawnChance = 3; initGame(); currentState = GameState.PLAYING; }
            if (key == KeyEvent.VK_2) { baseEnemySpeed = 3; baseSpawnChance = 5; initGame(); currentState = GameState.PLAYING; }
            if (key == KeyEvent.VK_3) { baseEnemySpeed = 5; baseSpawnChance = 8; initGame(); currentState = GameState.PLAYING; }
        }
        else if (currentState == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true; if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_SPACE) bullets.add(new Bullet(player.getX() - 3, player.getY()));
        } 
        else if (currentState == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_ENTER) { initGame(); currentState = GameState.PLAYING; }
            else if (key == KeyEvent.VK_M) currentState = GameState.MENU;
        }
    }

    @Override public void keyReleased(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false; if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false; }
    @Override public void keyTyped(KeyEvent e) {}
}