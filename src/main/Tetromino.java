
package main;
import java.awt.Color;
import java.util.Random;
public class Tetromino {
    private int[][] shape; // La forma del tetromino
    private Color color;   // Color del tetromino
    private int x;         // Posición x en el tablero
    private int y;         // Posición y en el tablero
    // Constructor
    public Tetromino(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.x = 0; // Posición inicial en x
        this.y = 0; // Posición inicial en y
    }
    // Método para rotar el tetromino
    public void rotate() {
        int[][] newShape = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                newShape[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = newShape;
    }

    public static Tetromino createRandomTetromino() {
        Random random = new Random();
        int tetrominoType = random.nextInt(7);
        switch (tetrominoType) {
            case 0: return createI();
            case 1: return createO();
            case 2: return createT();
            case 3: return createJ();
            case 4: return createL();
            case 5: return createS();
            case 6: return createZ();
            default: return createI(); // Fallback
        }
    }
    // Métodos para mover el tetromino
    public void moveLeft() {
        x--;
    }
    public void moveRight() {
        x++;
    }
    public void moveDown() {
        y++;
    }
    // Getters
    public int[][] getShape() {
        return shape;
    }
    public Color getColor() {
        return color;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    // Métodos estáticos para crear los 7 tetrominos
    public static Tetromino createI() {
        return new Tetromino(new int[][]{
            {1, 1, 1, 1}
        }, Color.CYAN);
    }
    public static Tetromino createO() {
        return new Tetromino(new int[][]{
            {1, 1},
            {1, 1}
        }, Color.YELLOW);
    }
    public static Tetromino createT() {
        return new Tetromino(new int[][]{
            {0, 1, 0},
            {1, 1, 1}
        }, Color.MAGENTA);
    }
    public static Tetromino createJ() {
        return new Tetromino(new int[][]{
            {1, 0, 0},
            {1, 1, 1}
        }, Color.BLUE);
    }
    public static Tetromino createL() {
        return new Tetromino(new int[][]{
            {0, 0, 1},
            {1, 1, 1}
        }, Color.ORANGE);
    }
    public static Tetromino createS() {
        return new Tetromino(new int[][]{
            {0, 1, 1},
            {1, 1, 0}
        }, Color.GREEN);
    }
    public static Tetromino createZ() {
        return new Tetromino(new int[][]{
            {1, 1, 0},
            {0, 1, 1}
        }, Color.RED);
    }
}
