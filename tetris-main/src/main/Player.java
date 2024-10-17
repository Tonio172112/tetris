package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {
    private int x, y;
    private int width = 56;
    private int height = 72;
    private int speed = 5;
    private int jumpStrength = 15;
    private int yVelocity = 0;
    private boolean isJumping = false;
    private static final int GRAVITY = 1;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (movingLeft) {
            moveLeft();
        }
        if (movingRight) {
            moveRight();
        }
        if (isJumping) {
            y -= yVelocity;
            yVelocity -= GRAVITY;
            if (y >= GameController.bottom_y - height) {
                y = GameController.bottom_y - height;
                isJumping = false;
                yVelocity = 0;
            }
        }
    }

    public void moveLeft() {
        x -= speed;
        // Ajuste preciso al borde izquierdo
        if (x < GameController.left_x) {
            x = GameController.left_x;
        }
    }

    public void moveRight() {
        x += speed;
        // Ajuste preciso al borde derecho
        if (x + width > GameController.right_x) {
            x = GameController.right_x - width;
        }
    }


    public void jump() {
        if (!isJumping) {
            isJumping = true;
            yVelocity = jumpStrength;
            System.out.println("Jugador saltando. Velocidad Y inicial: " + yVelocity);
        }
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
