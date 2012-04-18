package carlfx.demos.atomsmasher;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import static javafx.animation.Animation.Status.RUNNING;
import static javafx.animation.Animation.Status.STOPPED;

/**
 * This is a simple game world simulating a bunch of spheres looking
 * like atomic particles colliding with each other. When the game loop begins
 * the user will notice random spheres (atomic particles) floating and 
 * colliding. The user is able to press a button to generate more 
 * atomic particles. Also, the user can freeze the game. 
 * 
 * @author cdea
 */
public class AtomSmasher extends GameWorld {
    /** Read only field to show the number of sprite objects are on the field*/
    private final static Label NUM_SPRITES_FIELD = new Label();
    
    public AtomSmasher(int fps, String title){
        super(fps, title);
    }
    
    /**
     * Initialize the game world by adding sprite objects.
     * @param primaryStage 
     */
    @Override
    public void initialize(final Stage primaryStage) {
        // Sets the window title
        primaryStage.setTitle(getWindowTitle());
        
        // Create the scene
        setSceneNodes(new Group());
        setGameSurface(new Scene(getSceneNodes(), 640, 580));
        primaryStage.setScene(getGameSurface());
        
        // Create many spheres
        generateManySpheres(150);
        
        // Display the number of spheres visible.
        // Create a button to add more spheres.
        // Create a button to freeze the game loop.
        final Timeline gameLoop = getGameLoop();
        VBox stats = VBoxBuilder.create()
            .spacing(5)
            .translateX(10)
            .translateY(10)
            .children(HBoxBuilder.create()
                .spacing(5)
                .children(new Label("Number of Particles: "), // show no. particles
                    NUM_SPRITES_FIELD).build(),

                    // button to build more spheres
                    ButtonBuilder.create()
                        .text("Regenerate")
                        .onMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent arg0) {
                                generateManySpheres(150);
                            }}).build(),

                    // button to freeze game loop
                    ButtonBuilder.create()
                        .text("Freeze/Resume")
                        .onMousePressed(new EventHandler<MouseEvent>() {
                            
                            @Override
                            public void handle(MouseEvent arg0) {
                                switch (gameLoop.getStatus()) {
                                    case RUNNING:
                                        gameLoop.stop();
                                        break;
                                    case STOPPED:
                                        gameLoop.play();
                                        break;
                                }
                            }}).build()
            ).build(); // (VBox) stats on children
        
        // lay down the controls
        getSceneNodes().getChildren().add(stats);
    }

    
    /**
     * Make some more space spheres (Atomic particles)
     */
    private void generateManySpheres(int numSpheres) {
        Random rnd = new Random();
        Scene gameSurface = getGameSurface();
        for (int i=0; i<numSpheres; i++) {
            Color c = Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            Atom b = new Atom(rnd.nextInt(15) + 5, c);
            Circle circle = b.getAsCircle();
            // random 0 to 2 + (.0 to 1) * random (1 or -1)
            b.vX = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
            b.vY = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);

            // random x between 0 to width of scene
            double newX = rnd.nextInt((int) gameSurface.getWidth());
            
            // check for the right of the width newX is greater than width 
            // minus radius times 2(width of sprite)
            if (newX > (gameSurface.getWidth() - (circle.getRadius() * 2))) {
                newX = gameSurface.getWidth() - (circle.getRadius()  * 2);
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
            
            // add to actors in play (sprite objects)
            getSpriteManager().addSprites(b);
            
            // add sprite's 
            getSceneNodes().getChildren().add(0, b.node);
            
        }
    }
    
    /**
     * Each sprite will update it's velocity and bounce off wall borders.
     * @param sprite - An atomic particle (a sphere).
     */
    @Override
    protected void handleUpdate(Sprite sprite) {
        if (sprite instanceof Atom) {
            Atom sphere = (Atom) sprite;

            // advance the spheres velocity
            sphere.update();

            // bounce off the walls when outside of boundaries
            if (sphere.node.getTranslateX() > (getGameSurface().getWidth()  -
                sphere.node.getBoundsInParent().getWidth()) ||
                sphere.node.getTranslateX() < 0 ) {                 sphere.vX = sphere.vX * -1;             }             if (sphere.node.getTranslateY() > getGameSurface().getHeight()-
                sphere.node.getBoundsInParent().getHeight() ||
                sphere.node.getTranslateY() < 0) {
                sphere.vY = sphere.vY * -1;
            }
        }
    }

    /**
     * How to handle the collision of two sprite objects. Stops the particle
     * by zeroing out the velocity if a collision occurred.
     * @param spriteA
     * @param spriteB
     * @return
     */
    @Override
    protected boolean handleCollision(Sprite spriteA, Sprite spriteB) {
        if (spriteA.collide(spriteB)) {
            ((Atom)spriteA).implode(this);
            ((Atom)spriteB).implode(this);
            getSpriteManager().addSpritesToBeRemoved(spriteA, spriteB);
            return true;
        }
        return false;
    }

    /**
     * Remove dead things.
     */
    @Override
    protected void cleanupSprites() {
        // removes from the scene and backend store
        super.cleanupSprites();

        // let user know how many sprites are showing.
        NUM_SPRITES_FIELD.setText(String.valueOf(getSpriteManager().getAllSprites().size()));

    }
}
