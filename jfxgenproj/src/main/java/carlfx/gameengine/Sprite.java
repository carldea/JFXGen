package carlfx.gameengine;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * The Sprite class represents a image or node to be displayed.
 * In a 2D game a sprite will contain a velocity for the image to
 * move across the scene area. The game loop will call the update()
 * and collide() method at every interval of a key frame. A list of
 * animations can be used during different situations in the game
 * such as rocket thrusters, walking, jumping, etc.
 *
 * @author cdea
 */
public abstract class Sprite {

    /**
     * Current display node
     */
    public Node node;

    /**
     * velocity vector x direction
     */
    public double vX = 0;

    /**
     * velocity vector y direction
     */
    public double vY = 0;

    /**
     * dead?
     */
    public boolean isDead = false;

    /**
     * collision shape
     */
    public Circle collisionBounds;

    /**
     * Updates this sprite object's velocity, or animations.
     */
    public abstract void update();

    /**
     * Did this sprite collide into the other sprite?
     *
     * @param other - The other sprite.
     * @return boolean - Whether this or the other sprite collided, otherwise false.
     */
    public boolean collide(Sprite other) {

        if (collisionBounds == null || other.collisionBounds == null) {
            return false;
        }

        // determine it's size
        Circle otherSphere = other.collisionBounds;
        Circle thisSphere = collisionBounds;
        Point2D otherCenter = otherSphere.localToScene(otherSphere.getCenterX(), otherSphere.getCenterY());
        Point2D thisCenter = thisSphere.localToScene(thisSphere.getCenterX(), thisSphere.getCenterY());
        double dx = otherCenter.getX() - thisCenter.getX();
        double dy = otherCenter.getY() - thisCenter.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDist = otherSphere.getRadius() + thisSphere.getRadius();

        return (distance < minDist);
    }

    public void handleDeath(GameWorld gameWorld) {
        gameWorld.getSpriteManager().addSpritesToBeRemoved(this);
    }
}
