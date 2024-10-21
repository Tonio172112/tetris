package main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    GameController gc;

    public GamePanel() {
        // Configuracion del Panel
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(this);
        gc = new GameController();
        startGame();  // Iniciar el juego autom�ticamente
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // Bucle de Juego
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){
        gc.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        gc.draw(g2);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        gc.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gc.keyReleased(e.getKeyCode());
    }
}