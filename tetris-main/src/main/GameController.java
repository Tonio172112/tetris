package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

public class GameController {
	
	private AIController aiController;
    private Tetromino currentTetromino;
    
    // Area de Juego
    final int WIDTH = 360;
    final int HEIGHT = 720;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;
    
    private Player player;

    public GameController() {
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;
        
        // Inicializar al jugador en el centro del �rea de juego
        player = new Player(left_x + WIDTH/2 - 28, bottom_y-72);
        aiController = new AIController(this);
        currentTetromino = new Tetromino(/* inicializa una pieza */);	
    }

    public void update() {
    	// La IA toma el control del tetromino
        aiController.findBestMove(currentTetromino);
        currentTetromino.moveDown(); // El tetromino sigue cayendo
        player.update();
    }

    public void draw(Graphics2D g2){
        // Dibujar Area de Juego
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-2, top_y-2, WIDTH+4, HEIGHT+4);

        // Dibujar Cuadrado Siguiente Pieza
        g2.setColor(Color.yellow);
        int x = right_x + 100;
        int y = top_y + 50;
        g2.drawRect(x, y, 200, 150);
        g2.setFont(new Font("Lucida Console", Font.ITALIC, 50));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();
        String text = "NEXT";
        int textWidth = fm.stringWidth(text);
        int textX = x + (200 - textWidth) / 2;
        int textY = y - 10;
        g2.setColor(Color.yellow);
        g2.drawString(text, textX, textY);
        
     // Dibujar el área de juego y el tetromino
        currentTetromino.draw(g2);

        // Dibujar al jugador
        player.draw(g2);
    }

    public void keyPressed(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_LEFT:
                player.setMovingLeft(true);
                System.out.println("Tecla izquierda presionada");
                break;
            case KeyEvent.VK_RIGHT:
                player.setMovingRight(true);
                System.out.println("Tecla derecha presionada");
                break;
            case KeyEvent.VK_UP:
                player.jump();
                System.out.println("Tecla salto presionada");
                break;
            case KeyEvent.VK_SPACE:
                player.jump(); // Permitir que el salto se ejecute tambi�n con espacio
                break;
        }
    }

    public void keyReleased(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_LEFT:
                player.setMovingLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                player.setMovingRight(false);
                break;
        }
    }
    
    public int getWidth() {
        return WIDTH;  // Devuelve el ancho del área de juego
    }
}

public class Tetromino {
    private int[][] shape; // Matriz que representa la forma del tetromino
    private Color color;   // Color del tetromino
    private int x, y;      // Posición actual del tetromino

    // Constructor
    public Tetromino(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.x = GameController.left_x + GameController.WIDTH / 2;
        this.y = GameController.top_y;
    }

    // Rotar el tetromino 90 grados en sentido horario
    public void rotate() {
        int[][] rotatedShape = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                rotatedShape[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = rotatedShape;
    }

    // Mover el tetromino a la izquierda
    public void moveLeft() {
        x -= 30; // Ajusta el valor para que se mueva correctamente en la cuadrícula
    }

    // Mover el tetromino a la derecha
    public void moveRight() {
        x += 30;
    }

    // Mover el tetromino hacia abajo
    public void moveDown() {
        y += 30;
    }

    // Dibujar el tetromino en la pantalla
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    g2.fillRect(x + j * 30, y + i * 30, 30, 30); // Tamaño de los bloques del tetromino
                }
            }
        }
    }
}

public class AIController {

    private GameController gameController;
    
    public AIController(GameController gameController) {
        this.gameController = gameController;
    }

    // Evaluar la calidad de una posición dada para un tetromino
    public int evaluateBoard(Tetromino tetromino) {
        int score = 0;

        // Simular la colocación del tetromino en el tablero
        // 1. Evaluar la altura total del tablero
        score -= getMaxHeight();

        // 2. Evaluar el número de huecos
        score -= countHoles();

        // 3. Evaluar el número de líneas completas
        score += countCompleteLines();

        return score;
    }

    // Obtener la altura máxima del tablero
    private int getMaxHeight() {
        // Lógica para calcular la altura máxima del tablero
        return 0;
    }

    // Contar los huecos en el tablero (espacios vacíos bajo bloques)
    private int countHoles() {
        // Lógica para contar los huecos
        return 0;
    }

    // Contar las líneas completas
    private int countCompleteLines() {
        // Lógica para contar las líneas completas
        return 0;
    }

    // Encontrar la mejor posición y rotación para el tetromino
    public void findBestMove(Tetromino tetromino) {
        int bestScore = Integer.MIN_VALUE;
        Tetromino bestTetromino = null;
        int bestX = 0;
        int bestRotation = 0;

        // Simular cada posible posición y rotación del tetromino
        for (int rotation = 0; rotation < 4; rotation++) {
            for (int x = 0; x < gameController.getWidth(); x++) {
                Tetromino testTetromino = tetromino.clone();
                testTetromino.rotate(rotation);
                testTetromino.setX(x);
                
                int score = evaluateBoard(testTetromino);

                // Si esta posición es mejor, guárdala
                if (score > bestScore) {
                    bestScore = score;
                    bestTetromino = testTetromino;
                    bestX = x;
                    bestRotation = rotation;
                }
            }
        }

        // Mover el tetromino a la mejor posición y rotación encontrada
        tetromino.setX(bestX);
        tetromino.rotate(bestRotation);
    }
}

