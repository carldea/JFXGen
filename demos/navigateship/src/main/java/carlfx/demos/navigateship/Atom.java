package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.animation.FadeTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
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


    /**
     * Constructor will create a optionally create a gradient fill
     * circle shape. This sprite will contain a JavaFX Circle node.
     *
     * @param radius The radius of the circular shape.
     * @param fill Fill color inside circle.
     * @param gradientFill boolean to fill shape as gradient.
     */
    public Atom(double radius, Color fill, boolean gradientFill) {
        Circle sphere = CircleBuilder.create()
                .centerX(radius)
                .centerY(radius)
                .radius(radius)
                .fill(fill)
                .cache(true)
                .cacheHint(CacheHint.SPEED)
                .build();
        if (gradientFill) {
            RadialGradient rgrad = RadialGradientBuilder.create()
                    .centerX(sphere.getCenterX() - sphere.getRadius() / 3)
                    .centerY(sphere.getCenterY() - sphere.getRadius() / 3)
                    .radius(sphere.getRadius())
                    .proportional(false)
                    .stops(new Stop(0.0, Color.WHITE), new Stop(1.0, fill))
                    .build();

            sphere.setFill(rgrad);
        }
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
       return other instanceof Atom && collide((Atom) other);
    }
    
    /**
     * When encountering another Atom to determine if they collided.
     * This uses the distance formula from their center and radius.
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
        Point2D otherCenter = otherSphere.localToScene(otherSphere.getCenterX(), otherSphere.getCenterY());
        Point2D thisCenter = thisSphere.localToScene(thisSphere.getCenterX(), thisSphere.getCenterY());
        double dx = otherCenter.getX() - thisCenter.getX();
        double dy = otherCenter.getY() - thisCenter.getY();
        double distance = Math.sqrt( dx * dx + dy * dy );
        double minDist  = otherSphere.getRadius() + thisSphere.getRadius();

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
