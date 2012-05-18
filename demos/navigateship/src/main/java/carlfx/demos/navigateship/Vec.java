package carlfx.demos.navigateship;

/**
 * This class represents a container class to hold a Vector in space and direction
 * the ship will move. Assuming the center of the ship is the origin the angles can
 * be determined by a unit circle via Cartesian coordinates.
 * When the user clicks on the screen the mouse coordinates or screen coordinates
 * will be stored into the mx and my instance variables.
 * The x and y data members are converted to cartesian coordinates before storing.
 *
 * I purposefully left out getters and setters. In gaming just keep things minimalistic.
 * @author cdea
 */
public class Vec {
    public double mx;
    public double my;

    public double x;
    public double y;

    /**
     * This is a default constructor which will take a Cartesian coordinate.
     * @param x  X coordinate of a point on a Cartesian system.
     * @param y  Y coordinate of a point on a Cartesian system.
     */
    public Vec(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor will convert mouse click points into Cartesian coordinates based on the sprite's center point as
     * the origin.
     * @param mx  Mouse press' screen X coordinate.
     * @param my  Mouse press' screen Y coordinate.
     * @param centerX Screen X coordinate of the center of the ship sprite.
     * @param centerY Screen Y coordinate of the center of the ship sprite.
     */
    public Vec(double mx, double my, double centerX, double centerY) {
        this.x = convertX(mx, centerX);
        this.y = convertY(my, centerY);
        this.mx = mx;
        this.my = my;
    }

    /**
     * Returns a Cartesian coordinate system's quadrant from 1 to 4.
     * <pre>
     *     first quadrant - 1 upper right
     *     second quadrant - 2 upper left
     *     third quadrant - 3 lower left
     *     fourth quadrant - 4 lower right
     * </pre>
     * @return int quadrant number 1 through 4
     */
    public int quadrant() {
        int q = 0;
        if (x > 0 && y > 0) {
            q =1;
        } else if (x < 0 && y > 0) {
            q = 2;
        } else if (x < 0 && y < 0) {
            q = 3;
        } else if (x > 0 && y < 0) {
            q = 4;
        }
        return q;
    }
    @Override
    public String toString(){
        return "(" + x + "," + y + ") quadrant=" + quadrant();
    }
    /**
     * Converts point's X screen coordinate into a Cartesian system.
     * @param mouseX Converts the mouse X coordinate into Cartesian system based on the ship center X (originX).
     * @param originX The ship center point's X coordinate.
     * @return  double value of a Cartesian system X coordinate based on the origin X.
     */
    static double convertX(double mouseX, double originX) {
        return mouseX - originX;
    }

    /**
     * Converts point's Y screen coordinate into a Cartesian system.
     * @param mouseY  Converts the mouse Y coordinate into Cartesian system based on the ship center Y (originY).
     * @param originY The ship center point's Y coordinate.
     * @return  double value of a Cartesian system Y coordinate based on the origin Y.
     */
    static double convertY(double mouseY, double originY) {
        return originY - mouseY;
    }

}
