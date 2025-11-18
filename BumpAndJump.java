import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BumpAndJump extends JPanel implements ActionListener, KeyListener {

    // Game window
    JFrame frame;

    // Player car position
    int playerX = 200;
    final int PLAYER_Y = 400;
    final int PLAYER_WIDTH = 40;
    final int PLAYER_HEIGHT = 60;

    // Game loop timer
    Timer timer = new Timer(16, this);  // ~60 FPS

    // Obstacles (simple rectangles)
    ArrayList<Rectangle> obstacles = new ArrayList<>();
    Random rand = new Random();

    boolean leftPressed = false;
    boolean rightPressed = false;

    boolean running = true;

    public BumpAndJump() {
        frame = new JFrame("Bump & Jump (ESC to Quit)");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!running) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("GAME OVER!", 130, 250);
            return;
        }

        // Background road
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Road lines
        g.setColor(Color.WHITE);
        for (int i = 0; i < getHeight(); i += 40) {
            g.fillRect(getWidth() / 2 - 5, i, 10, 20);
        }

        // Draw player car
        g.setColor(Color.BLUE);
        g.fillRect(playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);

        // Draw obstacles
        g.setColor(Color.RED);
        for (Rectangle r : obstacles) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;

        // Move player
        if (leftPressed) playerX -= 5;
        if (rightPressed) playerX += 5;

        // Keep player inside screen
        playerX = Math.max(0, Math.min(getWidth() - PLAYER_WIDTH, playerX));

        // Randomly generate obstacles
        if (rand.nextInt(20) == 0) {  // Adjust for difficulty
            obstacles.add(new Rectangle(randNextLane(), -50, 40, 50));
        }

        // Move obstacles
        Iterator<Rectangle> it = obstacles.iterator();
        while (it.hasNext()) {
            Rectangle r = it.next();
            r.y += 5; // Scroll speed

            // Collision detection
            Rectangle player = new Rectangle(playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
            if (player.intersects(r)) {
                running = false;
                repaint();
                return;
            }

            // Remove off-screen obstacles
            if (r.y > getHeight()) it.remove();
        }

        repaint();
    }

    private int randNextLane() {
        int[] lanes = {100, 200, 300};
        return lanes[rand.nextInt(lanes.length)];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;

        // ESC quits
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BumpAndJump::new);
    }
}
