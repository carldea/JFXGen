package carlfx.demos.atomsmasher;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.animation.FadeTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.RadialGradientBuilder;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.util.Duration;

/**
 * A spherical looking object (Atom) with a random radius, color, and velocity.
 * When two atoms collide each will fade and become removed from the scene. The
 * method called implode() implements a fade transition effect.
 * 
 * @author cdea
 */
public class Atom extends Sprite {

    public Atom(double radius, Color fill) {
        Circle sphere = CircleBuilder.create()
                .centerX(radius)
                .centerY(radius)
                .radius(radius)
                .cache(true)
                .build();
        
        RadialGradient rgrad = RadialGradientBuilder.create()
                    .centerX(sphere.getCenterX() - sphere.getRadius() / 3)
                    .centerY(sphere.getCenterY() - sphere.getRadius() / 3)
                    .radius(sphere.getRadius())
                    .proportional(false)
                    .stops(new Stop(0.0, fill), new Stop(1.0, Color.BLACK))
                    .build();            
        
        sphere.setFill(rgrad);
        
        // set javafx node to a circle
        node = sphere;
            
    }
    
    /**
     * Change the velocity of the atom particle.
     */
    @Override
    public void update() {
        node.setTranslateX(node.getTranslateX() + vX);
        node.setTranslateY(node.getTranslateY() + vY);
    }
    
    @Override
    public boolean collide(Sprite other) {
        if (other instanceof Atom) {
            return collide((Atom)other);
        }
       return false;
    }
    
    /**
     * When encountering another Atom to determine if they collided.
     * @param other Another atom
     * @return boolean true if this atom and other atom has collided, 
     * otherwise false.
     */
    private boolean collide(Atom other) {
        
        // if an object is hidden they didn't collide.
        if (!node.isVisible() || 
            !other.node.isVisible() || 
            this == other) {
            return false;
        }
        
        // determine it's size
        Circle otherSphere = other.getAsCircle();
        Circle thisSphere =  getAsCircle();
        double dx = otherSphere.getTranslateX() - thisSphere.getTranslateX();
        double dy = otherSphere.getTranslateY() - thisSphere.getTranslateY();
        double distance = Math.sqrt( dx * dx + dy * dy );
        double minDist  = otherSphere.getRadius() + thisSphere.getRadius() + 3;

        return (distance < minDist);
    }

    /**
     * Returns a node casted as a JavaFX Circle shape. 
     * @return Circle shape representing JavaFX node for convenience.
     */
    public Circle getAsCircle() {
        return (Circle) node;
    }
    
    /**
     * Animate an implosion. Once done remove from the game world
     * @param gameWorld - game world
     */
    public void implode(final GameWorld gameWorld) {
        vX = vY = 0;
        FadeTransitionBuilder.create()
            .node(node)
            .duration(Duration.millis(300))
            .fromValue(node.getOpacity())
            .toValue(0)
            .onFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    isDead = true;
                    gameWorld.getSceneNodes().getChildren().remove(node);
                }
            })
            .build()
            .play();
    }
}
