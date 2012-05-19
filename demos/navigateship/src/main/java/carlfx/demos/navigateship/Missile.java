package carlfx.demos.navigateship;

import javafx.scene.paint.Color;

/**
 * A missile projectile without the radial gradient.
 */
public class Missile extends Atom {
    public Missile(Color fill) {
        super(5, fill, false);
    }

    public Missile(int radius, Color fill) {
        super(radius, fill, true);
    }
}
