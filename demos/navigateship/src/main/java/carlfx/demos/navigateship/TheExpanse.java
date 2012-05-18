package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Random;

/**
 * This is a simple game world simulating a bunch of spheres looking
 * like atomic particles colliding with each other. When the game loop begins
 * the user will notice random spheres (atomic particles) floating and
 * colliding. The user will navigate his/her ship by right clicking the mouse to
 * trust forward and left click to fire weapon to atoms.
 *
 * @author cdea
 */
public class TheExpanse extends GameWorld {

    // mouse pt label
    Label mousePtLabel = new Label();

    // mouse press pt label
    Label mousePressPtLabel = new Label();

    TextField xCoordinate = new TextField("234");
    TextField yCoordinate = new TextField("200");
    Button moveShipButton = new Button("Rotate ship");

    Ship myShip = new Ship();


    public TheExpanse(int fps, String title) {
        super(fps, title);
    }

    /**
     * Initialize the game world by adding sprite objects.
     *
     * @param primaryStage The game window or primary stage.
     */
    @Override
    public void initialize(final Stage primaryStage) {
        // Sets the window title
        primaryStage.setTitle(getWindowTitle());
        //primaryStage.setFullScreen(true);

        // Create the scene
        setSceneNodes(new Group());
        setGameSurface(new Scene(getSceneNodes(), 800, 600));
        getGameSurface().setFill(Color.BLACK);
        primaryStage.setScene(getGameSurface());
        // Setup Game input
        setupInput(primaryStage);


        // Create many spheres
        generateManySpheres(2);

        // Display the number of spheres visible.
        // Create a button to add more spheres.
        // Create a button to freeze the game loop.
        //final Timeline gameLoop = getGameLoop();
        getSpriteManager().addSprites(myShip);
        getSceneNodes().getChildren().add(myShip.node);

        // mouse point
        VBox stats = new VBox();

        HBox row1 = new HBox();
        mousePtLabel.setTextFill(Color.WHITE);
        row1.getChildren().add(mousePtLabel);
        HBox row2 = new HBox();
        mousePressPtLabel.setTextFill(Color.WHITE);
        row2.getChildren().add(mousePressPtLabel);

        stats.getChildren().add(row1);
        stats.getChildren().add(row2);

        // mouse point
        HBox enterCoord1 = new HBox();
        enterCoord1.getChildren().add(xCoordinate);
        enterCoord1.getChildren().add(yCoordinate);
        enterCoord1.getChildren().add(moveShipButton);
        stats.getChildren().add(enterCoord1);
        moveShipButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                double x = Double.parseDouble(xCoordinate.getText());
                double y = Double.parseDouble(yCoordinate.getText());
                myShip.plotCourse(x, y, false);
            }
        });

        // ===================================================
        // Debugging purposes
        // uncomment to test mouse press and rotation angles.
        //getSceneNodes().getChildren().add(stats);
    }

    /**
     * Sets up the mouse input.
     *
     * @param primaryStage The primary stage (app window).
     */
    private void setupInput(Stage primaryStage) {
        System.out.println("Ship's center is (" + myShip.getCenterX() + ", " + myShip.getCenterY() + ")");

        EventHandler fireOrMove = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressPtLabel.setText("Mouse Press PT = (" + event.getX() + ", " + event.getY() + ")");
                if (event.getButton() == MouseButton.PRIMARY) {
                    // Aim
                    myShip.plotCourse(event.getX(), event.getY(), false);
                    // fire
                    Missile m1 = myShip.fire();
                    getSpriteManager().addSprites(m1);
                    getSceneNodes().getChildren().add(0, m1.node);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    // determine when all atoms are not on the game surface. Ship should be one sprite left.
                    if (getSpriteManager().getAllSprites().size() <= 1) {
                        generateManySpheres(30);
                    }

                    // stop ship from moving forward
                    myShip.applyTheBrakes(event.getX(), event.getY());
                    // move forward and rotate ship
                    myShip.plotCourse(event.getX(), event.getY(), true);
                }

            }
        };


        // Initialize input
        primaryStage.getScene().setOnMousePressed(fireOrMove);
        //addEventHandler(MouseEvent.MOUSE_PRESSED, me);

        // set up stats
        EventHandler showMouseMove = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePtLabel.setText("Mouse PT = (" + event.getX() + ", " + event.getY() + ")");
            }
        };

        primaryStage.getScene().setOnMouseMoved(showMouseMove);
    }

    /**
     * Make some more space spheres (Atomic particles)
     *
     * @param numSpheres The number of random sized, color, and velocity atoms to generate.
     */
    private void generateManySpheres(int numSpheres) {
        Random rnd = new Random();
        Scene gameSurface = getGameSurface();
        for (int i = 0; i < numSpheres; i++) {
            Color c = Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            Atom b = new Atom(rnd.nextInt(15) + 5, c, true);
            Circle circle = b.getAsCircle();
            // random 0 to 2 + (.0 to 1) * random (1 or -1)
            b.vX = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
            b.vY = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);

            // random x between 0 to width of scene
            double newX = rnd.nextInt((int) gameSurface.getWidth());

            // check for the right of the width newX is greater than width 
            // minus radius times 2(width of sprite)
            if (newX > (gameSurface.getWidth() - (circle.getRadius() * 2))) {
                newX = gameSurface.getWidth() - (circle.getRadius() * 2);
            }

            // check for the bottom of screen the height newY is greater than height 
            // minus radius times 2(height of sprite)
            double newY = rnd.nextInt((int) gameSurface.getHeight());
            if (newY > (gameSurface.getHeight() - (circle.getRadius() * 2))) {
                newY = gameSurface.getHeight() - (circle.getRadius() * 2);
            }

            circle.setTranslateX(newX);
            circle.setTranslateY(newY);
            circle.setVisible(true);
            circle.setId(b.toString());
            circle.setCache(true);
            circle.setCacheHint(CacheHint.SPEED);
            circle.setManaged(false);
            // add to actors in play (sprite objects)
            getSpriteManager().addSprites(b);

            // add sprite's 
            getSceneNodes().getChildren().add(0, b.node);

        }
    }

    /**
     * Each sprite will update it's velocity and bounce off wall borders.
     *
     * @param sprite - An atomic particle (a sphere).
     */
    @Override
    protected void handleUpdate(Sprite sprite) {
        // advance object
        sprite.update();
        if (sprite instanceof Missile) {
            removeMissiles((Missile) sprite);
        } else {
            bounceOffWalls(sprite);
        }
    }

    /**
     * Change the direction of the moving object when it encounters the walls.
     *
     * @param sprite The sprite to update based on the wall boundaries.
     *               TODO The ship has got issues.
     */
    private void bounceOffWalls(Sprite sprite) {
        // bounce off the walls when outside of boundaries

        Node displayNode;
        if (sprite instanceof Ship) {
            displayNode = sprite.node;//((Ship)sprite).getCurrentShipImage();
        } else {
            displayNode = sprite.node;
        }
        // Get the group node's X and Y but use the ImageView to obtain the width.
        if (sprite.node.getTranslateX() > (getGameSurface().getWidth() - displayNode.getBoundsInParent().getWidth()) ||
                displayNode.getTranslateX() < 0) {

            // bounce the opposite direction
            sprite.vX = sprite.vX * -1;

        }
        // Get the group node's X and Y but use the ImageView to obtain the height.
        if (sprite.node.getTranslateY() > getGameSurface().getHeight() - displayNode.getBoundsInParent().getHeight() ||
                sprite.node.getTranslateY() < 0) {
            sprite.vY = sprite.vY * -1;
        }
    }

    /**
     * Remove missiles when they reach the wall boundaries.
     *
     * @param missile The missile to remove based on the wall boundaries.
     */
    private void removeMissiles(Missile missile) {
        // bounce off the walls when outside of boundaries
        if (missile.node.getTranslateX() > (getGameSurface().getWidth() -
                missile.node.getBoundsInParent().getWidth()) ||
                missile.node.getTranslateX() < 0) {

            getSpriteManager().addSpritesToBeRemoved(missile);
            getSceneNodes().getChildren().remove(missile.node);

        }
        if (missile.node.getTranslateY() > getGameSurface().getHeight() -
                missile.node.getBoundsInParent().getHeight() ||
                missile.node.getTranslateY() < 0) {

            getSpriteManager().addSpritesToBeRemoved(missile);
            getSceneNodes().getChildren().remove(missile.node);
        }
    }

    /**
     * How to handle the collision of two sprite objects. Stops the particle
     * by zeroing out the velocity if a collision occurred.
     *
     * @param spriteA Sprite from the first list.
     * @param spriteB Sprite from the second list.
     * @return boolean returns a true if the two sprites have collided otherwise false.
     */
    @Override
    protected boolean handleCollision(Sprite spriteA, Sprite spriteB) {
        if (spriteA != spriteB) {
            if (spriteA.collide(spriteB)) {
                if (spriteA instanceof Atom && spriteB instanceof Atom) {

                    ((Atom) spriteA).implode(this); // will remove from the Scene onFinish()
                    ((Atom) spriteB).implode(this);
                    getSpriteManager().addSpritesToBeRemoved(spriteA, spriteB);

                    return true;
                }
            }
        }

        return false;
    }

}
