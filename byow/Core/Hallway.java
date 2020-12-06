package byow.Core;

/** A subset of a room that is only size 1 in width.
 *  Class generates hallway that is either up, down, right, or left.
 */
public class Hallway extends Room {
    private int open;

    /** Constructs a hallway with a lower left and upper right
     * point inherited from the Room class.
     */
    public Hallway(Point lowerLeft, Point upperRight) {
        super(lowerLeft, upperRight);
        open = -1;
    }

    /** Opens a side of the hallway */
    public int getOpen() {
        return open;
    }

    /** Sets a side of the hallway open */
    public void setOpen(int open) {
        this.open = open;
    }

    /** Places a hallway on the up side of a room */
    public Room getRoomUp(int width, int height) {
        int hallMidWidth = (getuR().getX() + getlL().getX()) / 2;
        Point templL = new Point(hallMidWidth - (width / 2), getuR().getY());
        Point tempuR = new Point(hallMidWidth + (width / 2), getuR().getY() + height);
        return new Room(templL, tempuR);
    }

    /** Places a hallway on the down side of a room */
    public Room getRoomDown(int width, int height) {
        int hallMidWidth = (getuR().getX() + getlL().getX()) / 2;
        Point templL = new Point(hallMidWidth - (width / 2), getlL().getY() - height);
        Point tempuR = new Point(hallMidWidth + (width / 2), getlL().getY());
        return new Room(templL, tempuR);
    }

    /** Places a hallway on the left side of a room */
    public Room getRoomLeft(int width, int height) {
        int hallMidHeight = (getuR().getY() + getlL().getY()) / 2;
        Point templL = new Point(getlL().getX() - width, hallMidHeight - (height / 2));
        Point tempuR = new Point(getlL().getX(), hallMidHeight + (height / 2));
        return new Room(templL, tempuR);
    }

    /** Places a hallway on the right side of a room */
    public Room getRoomRight(int width, int height) {
        int hallMidHeight = (getuR().getY() + getlL().getY()) / 2;
        Point templL = new Point(getuR().getX(), hallMidHeight - (height / 2));
        Point tempuR = new Point(getuR().getX() + width, hallMidHeight + (height / 2));
        return new Room(templL, tempuR);
    }
}
