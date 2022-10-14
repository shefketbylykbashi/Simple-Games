package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class Asteroids extends Application {

    private Pane root;
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private GameObject player;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(600, 600);

        player = new Player();
        player.setVelocity(new Point2D(1, 0));

        addGameObject(player, 300, 300);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        enemies.add(enemy);
        addGameObject(enemy, x, y);
    }

    private void addGameObject(GameObject gameObject, double x, double y) {
        gameObject.getView().setTranslateX(x);
        gameObject.getView().setTranslateY(y);
        root.getChildren().add(gameObject.getView());
    }

    private void onUpdate() {
        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);

                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }
        }

        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);

        player.update();

        if (Math.random() < 0.02) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();

        primaryStage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                player.rotateLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                player.rotateRight();
            } else if (event.getCode() == KeyCode.SPACE) {
                Bullet bullet = new Bullet();
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Bullet extends GameObject {
    public Bullet() {
        super(new Circle(5, 5, 5, Color.BROWN));
    }
}

class Enemy extends GameObject {
    public Enemy() {
        super(new Circle(15, 15, 15, Color.RED));
    }
}

class GameObject {

    private Node view;
    private Point2D velocity = new Point2D(0, 0);
    private boolean alive = true;

    public GameObject(Node view) {
        this.view = view;
    }

    public void update() {
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }

    public Node getView() {
        return view;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getRotate() {
        return view.getRotate();
    }

    public void rotateRight() {
        view.setRotate(view.getRotate() + 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public void rotateLeft() {
        view.setRotate(view.getRotate() - 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public boolean isColliding(GameObject gameObject) {
        return getView().getBoundsInParent().intersects(gameObject.getView().getBoundsInParent());
    }
}

class Player extends GameObject {
    public Player() {
        super(new Rectangle(40, 20, Color.BLUE));
    }
}

