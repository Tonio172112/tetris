
package main;

import java.util.Random;

public class TetrisAI {
	private Tetromino currentTetromino;
    private GameController gameController;
    private static final int BLOCK_SIZE = GameController.BLOCK_SIZE;
    private long lastDropTime;
    private static final long DROP_DELAY = 500;
    
    // Pesos para la evaluación de posiciones
    private static final double HEIGHT_WEIGHT = -0.310066;    // Reducido para penalizar menos la altura
    private static final double LINES_WEIGHT = 0.960666;      // Aumentado para priorizar completar líneas
    private static final double HOLES_WEIGHT = -0.75663;      // Aumentado para evitar huecos
    private static final double BUMPINESS_WEIGHT = -0.484483; //

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
            
            int nextY = currentTetromino.getY() + BLOCK_SIZE;
            if (gameController.canMoveDown(currentTetromino, nextY)) {
                currentTetromino.setY(nextY);
                moveTowardsBestPosition();
            } else {
                if (!gameController.placeTetromino(currentTetromino)) {
                    gameController.setGameOver(true);
                }
                spawnNewTetromino();
            }
        }
    }

    private void moveTowardsBestPosition() {
        int bestX = findBestPosition();
        int currentX = currentTetromino.getX();
        
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
    

 // Método principal para encontrar la mejor posición
    private int findBestPosition() {
        if (currentTetromino == null) return 0;
        
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestX = currentTetromino.getX();
        int bestRotation = 0;
        Random rand = new Random();
        
        // Probamos todas las rotaciones posibles
        for (int rotation = 0; rotation < 4; rotation++) {
            int[][] shape = currentTetromino.getShape();
            int minX = GameController.left_x;
            int maxX = GameController.right_x - (shape[0].length * BLOCK_SIZE);
            
            // Probamos todas las posiciones X posibles
            for (int x = minX; x <= maxX; x += BLOCK_SIZE) {
                int y = findDropPosition(x, shape);
                if (y >= 0) {
                    double score = evaluatePosition(x, y, shape);
                    
                    // Añadir un pequeño factor aleatorio para evitar comportamiento repetitivo
                    score += (rand.nextDouble() * 0.1 - 0.05);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestRotation = rotation;
                    }
                }
            }
            
            currentTetromino.rotate();
        }
        
        // Restaurar la rotación óptima
        while (bestRotation > 0) {
            currentTetromino.rotate();
            bestRotation--;
        }
        
        return bestX;
    }
    
 // Encuentra la posición Y más baja posible para una posición X dada
    private int findDropPosition(int x, int[][] shape) {
        int y = GameController.top_y;
        while (gameController.canMoveDown(currentTetromino, y)) {
            y += BLOCK_SIZE;
        }
        return y - BLOCK_SIZE;
    }
    
    // Evalúa una posición potencial
    private double evaluatePosition(int x, int y, int[][] shape) {
        int[][] simulatedBoard = simulateBoard(x, y, shape);
        
        int completedLines = countCompletedLines(simulatedBoard);
        int holes = countHoles(simulatedBoard);
        int aggregateHeight = calculateAggregateHeight(simulatedBoard);
        int bumpiness = calculateBumpiness(simulatedBoard);
        double distribution = evaluateHorizontalDistribution(simulatedBoard);
        
        return (LINES_WEIGHT * completedLines) +
               (HOLES_WEIGHT * holes) +
               (HEIGHT_WEIGHT * aggregateHeight) +
               (BUMPINESS_WEIGHT * bumpiness) +
               (0.5 * distribution); // Factor de peso para la distribución
    }
    
 // Simula el tablero después de colocar una pieza
    private int[][] simulateBoard(int x, int y, int[][] shape) {
        int[][] boardCopy = new int[gameController.getBoard().length][];
        for (int i = 0; i < boardCopy.length; i++) {
            boardCopy[i] = gameController.getBoard()[i].clone();
        }
        
        int gridX = (x - GameController.left_x) / BLOCK_SIZE;
        int gridY = (y - GameController.top_y) / BLOCK_SIZE;
        
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    boardCopy[gridY + row][gridX + col] = 1;
                }
            }
        }
        
        return boardCopy;
    }
    
 // Cuenta el número de líneas completas
    private int countCompletedLines(int[][] board) {
        int lines = 0;
        for (int row = 0; row < board.length; row++) {
            boolean complete = true;
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 0) {
                    complete = false;
                    break;
                }
            }
            if (complete) lines++;
        }
        return lines;
    }
    
    // Cuenta el número de huecos (espacios vacíos con bloques encima)
    private int countHoles(int[][] board) {
        int holes = 0;
        for (int col = 0; col < board[0].length; col++) {
            boolean blockFound = false;
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] != 0) {
                    blockFound = true;
                } else if (blockFound) {
                    holes++;
                }
            }
        }
        return holes;
    }
    
 // Calcula la altura agregada de todas las columnas
    private int calculateAggregateHeight(int[][] board) {
        int totalHeight = 0;
        for (int col = 0; col < board[0].length; col++) {
            totalHeight += getColumnHeight(board, col);
        }
        return totalHeight;
    }

    // Calcula la "rugosidad" del tablero (diferencias de altura entre columnas adyacentes)
    private int calculateBumpiness(int[][] board) {
        int bumpiness = 0;
        int prevHeight = getColumnHeight(board, 0);
        
        for (int col = 1; col < board[0].length; col++) {
            int height = getColumnHeight(board, col);
            bumpiness += Math.abs(height - prevHeight);
            prevHeight = height;
        }
        
        return bumpiness;
    }

    // Obtiene la altura de una columna específica
    private int getColumnHeight(int[][] board, int col) {
        for (int row = 0; row < board.length; row++) {
            if (board[row][col] != 0) {
                return board.length - row;
            }
        }
        return 0;
    }

    private void spawnNewTetromino() {
        currentTetromino = Tetromino.createRandomTetromino();
        // Calculamos la posición inicial centrada
        int startX = GameController.left_x + 
                    (GameController.WIDTH / 2) - 
                    (currentTetromino.getShape()[0].length * BLOCK_SIZE / 2);
        currentTetromino.setX(startX);
        // Comenzamos desde la parte superior del área de juego
        currentTetromino.setY(GameController.top_y);
    }
    
    private double evaluateHorizontalDistribution(int[][] board) {
        int boardWidth = board[0].length;
        double[] columnFill = new double[boardWidth];
        double totalFill = 0;
        
        // Calcular el porcentaje de llenado de cada columna
        for (int col = 0; col < boardWidth; col++) {
            int blockCount = 0;
            for (int row = 0; row < board.length; row++) {
                if (board[row][col] != 0) {
                    blockCount++;
                }
            }
            columnFill[col] = (double) blockCount / board.length;
            totalFill += columnFill[col];
        }
        
        // Calcular la desviación del llenado ideal (uniforme)
        double idealFill = totalFill / boardWidth;
        double distribution = 0;
        for (int col = 0; col < boardWidth; col++) {
            distribution -= Math.abs(columnFill[col] - idealFill);
        }
        
        return distribution;
    }
    
    private double evaluateLineCompletion(int[][] board) {
        double score = 0;
        int[] rowFill = new int[board.length];
        
        // Contar bloques en cada fila
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] != 0) {
                    rowFill[row]++;
                }
            }
        }
        
        // Dar puntuación basada en qué tan cerca está cada fila de completarse
        for (int row = 0; row < board.length; row++) {
            if (rowFill[row] > 7) { // Si la fila está casi completa
                score += (rowFill[row] * 0.2); // Bonus por filas casi completas
            }
        }
        
        return score;
    }
    
    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }
}
