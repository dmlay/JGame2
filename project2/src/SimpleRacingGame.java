import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SimpleRacingGame extends JFrame {
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private final int CAR_WIDTH = 50;
    private final int CAR_HEIGHT = 100;
    private final int OBSTACLE_WIDTH = 50;
    private final int OBSTACLE_HEIGHT = 100;
    private final int CAR_X_START = WINDOW_WIDTH / 2 - CAR_WIDTH / 2;
    private final int CAR_Y_START = WINDOW_HEIGHT - CAR_HEIGHT - 10;

    private int carX = CAR_X_START;
    private int carY = CAR_Y_START;
    private int score = 0;
    private Timer timer;
    private boolean gameOver = false;

    private java.util.List<Rectangle> obstacles;
    private Random random;

    public SimpleRacingGame() {
        setTitle("Simple Racing Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        obstacles = new ArrayList<>();
        random = new Random();

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT && carX > 0) {
                        carX -= 20;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && carX < WINDOW_WIDTH - CAR_WIDTH) {
                        carX += 20;
                    } else if (e.getKeyCode() == KeyEvent.VK_UP && carY > 0) {
                        carY -= 20;
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN && carY < WINDOW_HEIGHT - CAR_HEIGHT) {
                        carY += 20;
                    }
                }
            }
        });

        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                gamePanel.repaint();
            }
        });
        timer.start();
    }

    private void updateGame() {
        if (random.nextInt(10) == 0) {
            obstacles.add(new Rectangle(random.nextInt(WINDOW_WIDTH - OBSTACLE_WIDTH), -OBSTACLE_HEIGHT, OBSTACLE_WIDTH, OBSTACLE_HEIGHT));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obstacle = obstacles.get(i);
            obstacle.y += 10;
            if (obstacle.y > WINDOW_HEIGHT) {
                obstacles.remove(i);
                i--;
                score++;
            }
        }

        for (Rectangle obstacle : obstacles) {
            if (obstacle.intersects(carX, carY, CAR_WIDTH, CAR_HEIGHT)) {
                gameOver = true;
                timer.stop();
                showGameOverDialog();
                break;
            }
        }
    }

    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showOptionDialog(this, "Game Over! Your score: " + score + "\nDo you want to restart?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        });
    }

    private void restartGame() {
        carX = CAR_X_START;
        carY = CAR_Y_START;
        score = 0;
        obstacles.clear();
        gameOver = false;
        timer.start();
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.GRAY);

            g.setColor(Color.BLUE);
            g.fillRect(carX, carY, CAR_WIDTH, CAR_HEIGHT);

            g.setColor(Color.RED);
            for (Rectangle obstacle : obstacles) {
                g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);

            if (gameOver) {
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Game Over", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleRacingGame game = new SimpleRacingGame();
            game.setVisible(true);
        });
    }
}
