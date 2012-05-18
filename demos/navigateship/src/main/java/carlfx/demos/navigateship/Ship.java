package carlfx.demos.navigateship;

import carlfx.gameengine.Sprite;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * A space ship with 32 directions
 * When two atoms collide each will fade and become removed from the scene. The
 * method called implode() implements a fade transition effect.
 *
 * @author cdea
 */
public class Ship extends Sprite {

    /**
     * 360 degree turn
     */
    private final static int TWO_PI_DEGREES = 360;

    /**
     * Number of ship frames and directions the ship is pointing nose
     */
    private final static int NUM_DIRECTIONS = 32;

    /**
     * The angle of one direction (adjacent directions) (11.25 degrees)
     */
    private final static float UNIT_ANGLE_PER_FRAME = ((float) TWO_PI_DEGREES / NUM_DIRECTIONS);

    /**
     * Amount of time it takes the ship to move 180 degrees in milliseconds.
     */
    private final static int MILLIS_TURN_SHIP_180_DEGREES = 300;

    /**
     * When the ship turns on each direction one amount of time for one frame or turn of the ship. (18.75 milliseconds)
     */
    private final static float MILLIS_PER_FRAME = (float) MILLIS_TURN_SHIP_180_DEGREES / (NUM_DIRECTIONS / 2);

    /**
     * All possible turn directions Clockwise, Counter Clockwise, or Neither when the user clicks mouse around ship
     */
    private enum DIRECTION {
        CLOCKWISE, COUNTER_CLOCKWISE, NEITHER
    }

    /**
     * Velocity amount used vector when ship moves forward. scale vector of ship. See flipBook translateX and Y.
     */
    private final static float THRUST_AMOUNT = 3.3f;

    /***/
    private final static float MISSILE_THRUST_AMOUNT = 6.3F;

    /**
     * Angle in degrees to rotate ship.
     */


    /**
     * Current turning direction. default is NEITHER. Clockwise and Counter Clockwise.
     */
    private DIRECTION turnDirection = DIRECTION.NEITHER;

    /**
     * The current starting position of the vector or coordinate where the nose of the ship is pointing towards.
     */
    private Vec u; // current or start vector

    /**
     * All ImageViews of all the possible image frames for each direction the ship is pointing. ie: 32 directions.
     */
    private final List<RotatedShipImage> directionalShips = new ArrayList<>();

    /**
     * The Timeline instance to animate the ship rotating using images. This is an optical illusion similar to page
     * flipping as each frame is displayed the previous visible attribute is set to false. No rotation is happening.
     */
    private Timeline rotateShipTimeline;

    /**
     * The current index into the list of ImageViews representing each direction of the ship. Zero is the ship
     * pointing to the right or zero degrees.
     */
    private int uIndex = 0;

    /**
     * The end index into the list of ImageViews representing each direction of the ship. Zero is the ship
     * pointing to the right or zero degrees.
     */
    private int vIndex = 0;

    /**
     * The spot where the user has right clicked letting the engine check the ship's center is in this area.
     */
    private final Circle stopArea = new Circle();

    /**
     * A group contain all of the ship image view nodes.
     */
    private final Group flipBook = new Group();

    public Ship() {

        // Load one image.
        Image shipImage = new Image(getClass().getClassLoader().getResource("ship.png").toExternalForm(), true);
        stopArea.setRadius(40);
        RotatedShipImage prev = null;

        // create all the number of directions based on a unit angle. 360 divided by NUM_DIRECTIONS
        for (int i = 0; i < NUM_DIRECTIONS; i++) {
            RotatedShipImage imageView = new RotatedShipImage();
            imageView.setImage(shipImage);
            imageView.setRotate(-1 * i * UNIT_ANGLE_PER_FRAME);
            imageView.setCache(true);
            imageView.setCacheHint(CacheHint.SPEED);
            imageView.setManaged(false);
            imageView.prev = prev;
            imageView.setVisible(false);
            directionalShips.add(imageView);
            if (prev != null) {
                prev.next = imageView;
            }
            prev = imageView;
            flipBook.getChildren().add(imageView);
        }
        RotatedShipImage firstShip = directionalShips.get(0);
        firstShip.prev = prev;
        prev.next = firstShip;
        // set javafx node to an image
        firstShip.setVisible(true);
        node = flipBook;
        flipBook.setTranslateX(200);
        flipBook.setTranslateY(300);

    }

    /**
     * Change the velocity of the atom particle.
     */
    @Override
    public void update() {
        flipBook.setTranslateX(flipBook.getTranslateX() + vX);
        flipBook.setTranslateY(flipBook.getTranslateY() + vY);

        if (stopArea.contains(getCenterX(), getCenterY())) {
            vX = 0;
            vY = 0;
        }

    }


    private RotatedShipImage getCurrentShipImage() {
        return directionalShips.get(uIndex);
    }

    /**
     * The center X coordinate of the current visible image. See <code>getCurrentShipImage()</code> method.
     *
     * @return The scene or screen X coordinate.
     */
    public double getCenterX() {
        RotatedShipImage shipImage = getCurrentShipImage();
        return node.getTranslateX() + (shipImage.getBoundsInLocal().getWidth() / 2);
    }

    /**
     * The center Y coordinate of the current visible image. See <code>getCurrentShipImage()</code> method.
     *
     * @return The scene or screen Y coordinate.
     */
    public double getCenterY() {
        RotatedShipImage shipImage = getCurrentShipImage();
        return node.getTranslateY() + (shipImage.getBoundsInLocal().getHeight() / 2);
    }

    /**
     * Determines the angle between it's starting position and ending position (Similar to a clock's second hand).
     * When the user is shooting the ship nose will point in the direction of the mouse press using the primary button.
     * When the user is thrusting to a location on the screen the right click mouse will pass true to the thrust
     * parameter.
     *
     * @param screenX The mouse press' screen x coordinate.
     * @param screenY The mouse press' screen ycoordinate.
     * @param thrust  Thrust ship forward or not. True move forward otherwise false.
     */
    public void plotCourse(double screenX, double screenY, boolean thrust) {
        // get center of ship
        double sx = getCenterX();
        double sy = getCenterY();

        // get user's new turn position based on mouse click
        Vec v = new Vec(screenX, screenY, sx, sy);
        if (u == null) {
            u = new Vec(1, 0);
        }


        double atan2RadiansU = Math.atan2(u.y, u.x);
        double atan2DegreesU = Math.toDegrees(atan2RadiansU);

        double atan2RadiansV = Math.atan2(v.y, v.x);
        double atan2DegreesV = Math.toDegrees(atan2RadiansV);

        double angleBetweenUAndV = atan2DegreesV - atan2DegreesU;


        // if abs value is greater than 180 move counter clockwise
        //(or opposite of what is determined)
        double absAngleBetweenUAndV = Math.abs(angleBetweenUAndV);
        boolean goOtherWay = false;
        if (absAngleBetweenUAndV > 180) {
            if (angleBetweenUAndV < 0) {
                turnDirection = DIRECTION.COUNTER_CLOCKWISE;
                goOtherWay = true;
            } else if (angleBetweenUAndV > 0) {
                turnDirection = DIRECTION.CLOCKWISE;
                goOtherWay = true;
            } else {
                turnDirection = Ship.DIRECTION.NEITHER;
            }
        } else {
            if (angleBetweenUAndV < 0) {
                turnDirection = Ship.DIRECTION.CLOCKWISE;
            } else if (angleBetweenUAndV > 0) {
                turnDirection = Ship.DIRECTION.COUNTER_CLOCKWISE;
            } else {
                turnDirection = Ship.DIRECTION.NEITHER;
            }
        }

        double degreesToMove = absAngleBetweenUAndV;
        if (goOtherWay) {
            degreesToMove = TWO_PI_DEGREES - absAngleBetweenUAndV;
        }

        //int q = v.quadrant();

        uIndex = Math.round((float) (atan2DegreesU / UNIT_ANGLE_PER_FRAME));
        if (uIndex < 0) {
            uIndex = NUM_DIRECTIONS + uIndex;
        }
        vIndex = Math.round((float) (atan2DegreesV / UNIT_ANGLE_PER_FRAME));
        if (vIndex < 0) {
            vIndex = NUM_DIRECTIONS + vIndex;
        }
        String debugMsg = turnDirection +
                " U [m(" + u.mx + ", " + u.my + ")  => c(" + u.x + ", " + u.y + ")] " +
                " V [m(" + v.mx + ", " + v.my + ")  => c(" + v.x + ", " + v.y + ")] " +
                " start angle: " + atan2DegreesU +
                " end angle:" + atan2DegreesV +
                " Angle between: " + degreesToMove +
                " Start index: " + uIndex +
                " End index: " + vIndex;

        System.out.println(debugMsg);

        if (thrust) {
            vX = Math.cos(atan2RadiansV) * THRUST_AMOUNT;
            vY = -Math.sin(atan2RadiansV) * THRUST_AMOUNT;
        }
        turnShip();

        u = v;
    }

    private void turnShip() {

        final Duration oneFrameAmt = Duration.millis(MILLIS_PER_FRAME);
        RotatedShipImage startImage = directionalShips.get(uIndex);
        RotatedShipImage endImage = directionalShips.get(vIndex);
        List<KeyFrame> frames = new ArrayList<>();

        RotatedShipImage currImage = startImage;

        int i = 1;
        while (true) {

            final Node displayNode = currImage;

            KeyFrame oneFrame = new KeyFrame(oneFrameAmt.multiply(i),
                    new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(javafx.event.ActionEvent event) {
                            // make all ship images invisible
                            for (RotatedShipImage shipImg : directionalShips) {
                                shipImg.setVisible(false);
                            }
                            // make current ship image visible
                            displayNode.setVisible(true);

                            // update the current index
                            //uIndex = directionalShips.indexOf(displayNode);
                        }
                    }); // oneFrame

            frames.add(oneFrame);

            if (currImage == endImage) {
                break;
            }
            if (turnDirection == DIRECTION.CLOCKWISE) {
                currImage = currImage.prev;
            }
            if (turnDirection == DIRECTION.COUNTER_CLOCKWISE) {
                currImage = currImage.next;
            }
            i++;
        }


        if (rotateShipTimeline != null) {
            rotateShipTimeline.stop();
            rotateShipTimeline.getKeyFrames().clear();
            rotateShipTimeline.getKeyFrames().addAll(frames);
        } else {
            // sets the game world's game loop (Timeline)
            rotateShipTimeline = TimelineBuilder.create()
                    .keyFrames(frames)
                    .build();

        }

        rotateShipTimeline.playFromStart();


    }

    /**
     * Stops the ship from thrusting forward.
     *
     * @param screenX the screen's X coordinate to stop the ship.
     * @param screenY the screen's Y coordinate to stop the ship.
     */
    public void applyTheBrakes(double screenX, double screenY) {
        stopArea.setCenterX(screenX);
        stopArea.setCenterY(screenY);
    }

    public Missile fire() {
        Missile m1 = new Missile(Color.RED);
        // velocity vector of the missile
        m1.vX = Math.cos(Math.toRadians(uIndex * UNIT_ANGLE_PER_FRAME)) * MISSILE_THRUST_AMOUNT;
        m1.vY = -Math.sin(Math.toRadians(uIndex * UNIT_ANGLE_PER_FRAME)) * MISSILE_THRUST_AMOUNT;

        // make the missile launch in the direction of the current direction of the ship nose. based on the
        // current frame (uIndex) into the list of image view nodes.
        RotatedShipImage shipImage = directionalShips.get(uIndex);

        // start to appear in the center of the ship to come out the direction of the nose of the ship.
        double offsetX = (shipImage.getBoundsInLocal().getWidth() - m1.node.getBoundsInLocal().getWidth()) / 2;
        double offsetY = (shipImage.getBoundsInLocal().getHeight() - m1.node.getBoundsInLocal().getHeight()) / 2;

        // initial launch of the missile
        m1.node.setTranslateX(node.getTranslateX() + offsetX + m1.vX);
        m1.node.setTranslateY(node.getTranslateY() + offsetY + m1.vY);
        return m1;
    }


}
