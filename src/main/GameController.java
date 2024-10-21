package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class GameController {
    final static int WIDTH = 360;
    final static int HEIGHT = 720;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;
    public static final int BLOCK_SIZE = 30;

    private Player player;
    private TetrisAI ai;
    private int[][] board;
    private boolean gameOver;

    public GameController() {
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        board = new int[20][10];

        player = new Player(left_x + WIDTH / 2 - 28, bottom_y - 72);
        ai = new TetrisAI(this);
        this.gameOver = false;
    }

    public void update() {
        if (!gameOver) {
            player.update();
            ai.update();
            checkCollisionWithPlayer();
            clearLines();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        drawBoard(g2);
        Tetromino currentTetromino = ai.getCurrentTetromino();
        if (currentTetromino != null) {
            drawTetromino(g2, currentTetromino);
        }

        if (gameOver) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("GAME OVER", GamePanel.WIDTH / 2 - 100, GamePanel.HEIGHT / 2);
        }

        player.draw(g2);
    }

    private void drawBoard(Graphics2D g2) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] != 0) {
                    g2.setColor(Color.GRAY);
                    int x = left_x + (col * 30);
                    int y = top_y + (row * 30);
                    g2.fillRect(x, y, 30, 30);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(x, y, 30, 30);
                }
            }
        }
    }

    private void drawTetromino(Graphics2D g2, Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        g2.setColor(tetromino.getColor());
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int x = tetromino.getX() + (col * 30);
                    int y = tetromino.getY() + (row * 30);
                    g2.fillRect(x, y, 30, 30);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(x, y, 30, 30);
                    g2.setColor(tetromino.getColor());
                }
            }
        }
    }

    public boolean canMoveDown(Tetromino tetromino, int nextY) {
        int[][] shape = tetromino.getShape();
        int gridX = (tetromino.getX() - left_x) / BLOCK_SIZE;
        int gridY = (nextY - top_y) / BLOCK_SIZE;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    // Verificar que la fila no esté fuera de los límites del tablero
                    if (gridY + row >= board.length) {
                        return false; // La pieza está en el borde inferior.
                    }
                    // Verificar colisión con piezas fijas en el tablero
                    if (board[gridY + row][gridX + col] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean placeTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        int gridX = (tetromino.getX() - left_x) / BLOCK_SIZE;
        int gridY = (tetromino.getY() - top_y) / BLOCK_SIZE;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    if (gridY + row < 0) {
                        return false; // Game Over
                    }
                    board[gridY + row][gridX + col] = 1;
                }
            }
        }
        return true;
    }

    public void clearLines() {
        for (int row = board.length - 1; row >= 0; row--) {
            boolean isLineFull = true;
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 0) {
                    isLineFull = false;
                    break;
                }
            }

            if (isLineFull) {
                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }
                board[0] = new int[board[0].length];
                row++; // Verificar de nuevo la línea después de haber movido las filas
            }
        }
    }

    public boolean isPositionEmpty(int gridX, int gridY) {
        if (gridY < 0 || gridY >= board.length || gridX < 0 || gridX >= board[0].length) {
            return false;
        }
        return board[gridY][gridX] == 0;
    }

    public void keyPressed(int keyCode) {
        if (!gameOver) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    player.setMovingLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.setMovingRight(true);
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_SPACE:
                    player.jump();
                    break;
            }
        }
    }

    public void keyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                player.setMovingLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                player.setMovingRight(false);
                break;
        }
    }
    
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int[][] getBoard() {
        return board;
    }
    
    private void checkCollisionWithPlayer() {
        Rectangle playerBounds = player.getBounds();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] != 0) {
                    Rectangle blockBounds = new Rectangle(
                        left_x + col * BLOCK_SIZE,
                        top_y + row * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE
                    );
                    if (playerBounds.intersects(blockBounds)) {
                        gameOver = true;
                        return;
                    }
                }
            }
        }
    }
}