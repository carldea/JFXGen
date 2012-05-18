package carlfx.demos.navigateship;

import javafx.scene.image.ImageView;

/**
 * Represents a double link list to assist in the rotation of the ship.
 * This helps to move clockwise and counter clockwise.
 */
public class RotatedShipImage extends ImageView {
    public RotatedShipImage next;
    public RotatedShipImage prev;
}
