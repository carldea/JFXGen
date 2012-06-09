package carlfx.demos.navigateship;

import carlfx.gameengine.GameWorld;
import carlfx.gameengine.Sprite;
import javafx.animation.FadeTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
     * @param radius       The radius of the circular shape.
     * @param fill         Fill color inside circle.
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
        collisionBounds = sphere;

    }

    /**
     * Change the velocity of the atom particle.
     */
    @Override
    public void update() {
        node.setTranslateX(node.getTranslateX() + vX);
        node.setTranslateY(node.getTranslateY() + vY);
    }

    /**
     * Returns a node casted as a JavaFX Circle shape.
     *
     * @return Circle shape representing JavaFX node for convenience.
     */
    public Circle getAsCircle() {
        return (Circle) node;
    }

    /**
     * Animate an implosion. Once done remove from the game world
     *
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

    public void handleDeath(GameWorld gameWorld) {
        implode(gameWorld);
        super.handleDeath(gameWorld);
    }
}
