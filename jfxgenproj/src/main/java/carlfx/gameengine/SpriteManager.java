package carlfx.gameengine;

import java.util.*;

/**
 * Sprite manager is responsible for holding all sprite objects, and cleaning up
 * sprite objects to be removed. All collections are used by the JavaFX 
 * application thread. During each cycle (animation frame) sprite management
 * occurs. This assists the user of the API to not have to create lists to 
 * later be garbage collected. Should provide some performance gain.
 * @author cdea
 */
public class SpriteManager {
    /** All the sprite objects currently in play */
    private final static List<Sprite> GAME_ACTORS = new ArrayList<>();
    
    /** A global single threaded list used to check collision against other 
     * sprite objects.
     */
    private final static List<Sprite> CHECK_COLLISION_LIST = new ArrayList<>();
    
    /** A global single threaded set used to cleanup or remove sprite objects
     * in play.
     */
    private final static Set<Sprite> CLEAN_UP_SPRITES = new HashSet<>();
    
    /** */
    public List<Sprite> getAllSprites() {
        return GAME_ACTORS;
    }
    
    /**
     * VarArgs of sprite objects to be added to the game.
     * @param sprites 
     */
    public void addSprites(Sprite... sprites) {       
        GAME_ACTORS.addAll(Arrays.asList(sprites));
    }
    
    /**
     * VarArgs of sprite objects to be removed from the game.
     * @param sprites 
     */
    public void removeSprites(Sprite... sprites) {       
        GAME_ACTORS.removeAll(Arrays.asList(sprites));
    }
    
    /** Returns a set of sprite objects to be removed from the GAME_ACTORS. 
     * @return CLEAN_UP_SPRITES
     */    
    public Set<Sprite> getSpritesToBeRemoved() {
        return CLEAN_UP_SPRITES;
    }
    
 /**
     * Adds sprite objects to be removed
     * @param sprites varargs of sprite objects.
     */
    public void addSpritesToBeRemoved(Sprite... sprites) {
        if (sprites.length > 1) {
            CLEAN_UP_SPRITES.addAll(Arrays.asList((Sprite[]) sprites));
        } else {
            CLEAN_UP_SPRITES.add(sprites[0]);
        }
    }

    /**
     * Returns a list of sprite objects to assist in collision checks.
     * This is a temporary and is a copy of all current sprite objects
     * (copy of GAME_ACTORS).
     * @return CHECK_COLLISION_LIST
     */
    public List<Sprite> getCollisionsToCheck() {
        return CHECK_COLLISION_LIST;
    }

    /**
     * Clears the list of sprite objects in the collision check collection
     * (CHECK_COLLISION_LIST).
     */
    public void resetCollisionsToCheck() {
        CHECK_COLLISION_LIST.clear();
        CHECK_COLLISION_LIST.addAll(GAME_ACTORS);
    }

    /**
     * Removes sprite objects and nodes from all
     * temporary collections such as:
     * CLEAN_UP_SPRITES.
     * The sprite to be removed will also be removed from the
     * list of all sprite objects called (GAME_ACTORS).
     */
    public void cleanupSprites() {

        // remove from actors list
        GAME_ACTORS.removeAll(CLEAN_UP_SPRITES);

        // reset the clean up sprites
        CLEAN_UP_SPRITES.clear();
    }
}
