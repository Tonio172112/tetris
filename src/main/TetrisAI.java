package main;

import java.util.Random;

public class TetrisAI {
    private Tetromino currentTetromino;
    private GameController gameController;
    private static final int BLOCK_SIZE = GameController.BLOCK_SIZE;
    private long lastDropTime;
    private static final long DROP_DELAY = 1000; // 1 segundo entre caídas

    public TetrisAI(GameController gameController) {
        this.gameController = gameController;
        this.lastDropTime = System.currentTimeMillis();
        spawnNewTetromino();
    }

    public void update() {
        if (currentTetromino == null) {
            spawnNewTetromino();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime >= DROP_DELAY) {
            lastDropTime = currentTime;
            
            // Mover hacia abajo si es posible
            int nextY = currentTetromino.getY() + BLOCK_SIZE;
            if (gameController.canMoveDown(currentTetromino, nextY)) {
                currentTetromino.setY(nextY);
            } else {
                // Colocar la pieza en el tablero si ya no puede bajar más
                if (gameController.placeTetromino(currentTetromino)) {
                    spawnNewTetromino();
                } else {
                    // Game Over si la pieza no puede ser colocada
                    gameController.setGameOver(true);
                }
            }
        }

        // Mover horizontalmente hacia la mejor posición
        moveTowardsBestPosition();
    }
 

    private void moveTowardsBestPosition() {
        int bestX = findBestPosition();
        int currentX = currentTetromino.getX();

        // Mover hacia la posición óptima
        if (bestX < currentX && canMoveLeft()) {
            currentTetromino.setX(currentX - BLOCK_SIZE);
        } else if (bestX > currentX && canMoveRight()) {
            currentTetromino.setX(currentX + BLOCK_SIZE);
        }
    }

    private boolean canMoveLeft() {
        return isValidPosition(currentTetromino.getX() - BLOCK_SIZE, currentTetromino.getY());
    }

    private boolean canMoveRight() {
        return isValidPosition(currentTetromino.getX() + BLOCK_SIZE, currentTetromino.getY());
    }

    private boolean isValidPosition(int x, int y) {
        int[][] shape = currentTetromino.getShape();
        int gridX = (x - GameController.left_x) / BLOCK_SIZE;
        int gridY = (y - GameController.top_y) / BLOCK_SIZE;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    if (gridX + col < 0 || gridX + col >= 10 || gridY + row >= 20) {
                        return false;
                    }
                    if (gridY + row >= 0 && !gameController.isPositionEmpty(gridX + col, gridY + row)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int findBestPosition() {
        return GameController.left_x + (GameController.WIDTH / 2) - 
               (currentTetromino.getShape()[0].length * BLOCK_SIZE / 2);
    }

    private void spawnNewTetromino() {
        currentTetromino = Tetromino.createRandomTetromino();
        int startX = GameController.left_x + 
                    (GameController.WIDTH / 2) - 
                    (currentTetromino.getShape()[0].length * BLOCK_SIZE / 2);
        currentTetromino.setX(startX);
        currentTetromino.setY(GameController.top_y);
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }
}
