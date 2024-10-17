package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

public class GameController {
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
        
        // Inicializar al jugador en el centro del área de juego
        player = new Player(left_x + WIDTH/2 - 28, bottom_y-72);
    }

    public void update() {
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
                player.jump(); // Permitir que el salto se ejecute también con espacio
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
}