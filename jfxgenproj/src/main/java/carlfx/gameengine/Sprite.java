package carlfx.gameengine;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.scene.Node;

/**
 * The Sprite class represents a image or node to be displayed.
 * In a 2D game a sprite will contain a velocity for the image to 
 * move across the scene area. The game loop will call the update()
 * and collide() method at every interval of a key frame. A list of 
 * animations can be used during different situations in the game
 * such as rocket thrusters, walking, jumping, etc.
 * @author cdea
 */
public abstract class Sprite {
    
    /** Animation for the node */
    public List<Animation> animations = new ArrayList<>();
    
    /** Current display node */
    public Node node;
    
    /** velocity vector x direction */
    public double vX = 0;
    
    /** velocity vector y direction */
    public double vY = 0;
    
    /** dead? */
    public boolean isDead = false;
    
    /**
     * Updates this sprite object's velocity, or animations.
     */
    public abstract void update();
    
    /**
     * Did this sprite collide into the other sprite?
     * 
     * @param other - The other sprite.
     * @return 
     */
    public boolean collide(Sprite other) {
        return false;
    } 
}
