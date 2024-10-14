package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {
    private int x, y;
    private int width = 30;
    private int height = 50;
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
        if (x < GameController.left_x) {
            x = GameController.left_x;
        }
        System.out.println("Jugador moviéndose a la izquierda. Nueva posición X: " + x);
    }

    public void moveRight() {
        x += speed;
        if (x > GameController.right_x - width) {
            x = GameController.right_x - width;
        }
        System.out.println("Jugador moviéndose a la derecha. Nueva posición X: " + x);
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