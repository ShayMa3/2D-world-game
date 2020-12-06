package byow.Core;

public class Room {
    private Point lL;
    private Point uR;
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public Room(Point lowerLeft, Point upperRight) {
        lL = lowerLeft;
        uR = upperRight;
        up = false;
        down = false;
        left = false;
        right = false;
    }

    public boolean overlaps(Room other) {
        if (this.lL.getX() > other.uR.getX() || other.lL.getX() > this.uR.getX()) {
            return false;
        }
        if (this.lL.getY() > other.uR.getY() || other.lL.getY() > this.uR.getY()) {
            return false;
        }
        return true;
    }

    public boolean edgeOverlap(Room other) {
        if (this.lL.getX() > other.uR.getX() || other.lL.getX() > this.uR.getX()) {
            return false;
        }
        if (this.lL.getY() > other.uR.getY() || other.lL.getY() > this.uR.getY()) {
            return false;
        }
        return true;
    }

    public Hallway getHallUp(int length) {
        Point templL = new Point(((getuR().getX() + getlL().getX()) / 2) - 1, getuR().getY());
        Point tempuR = new Point(templL.getX() + 2, getuR().getY() + length);
        return new Hallway(templL, tempuR);
    }

    public Hallway getHallDown(int length) {
        Point templL = new Point(((getuR().getX() + getlL().getX()) / 2) - 1,
                getlL().getY() - length);
        Point tempuR = new Point(templL.getX() + 2, getlL().getY());
        return new Hallway(templL, tempuR);
    }

    public Hallway getHallLeft(int length) {
        Point templL = new Point(getlL().getX() - length,
                ((getuR().getY() + getlL().getY()) / 2) - 1);
        Point tempuR = new Point(getlL().getX(), templL.getY() + 2);
        return new Hallway(templL, tempuR);
    }

    public Hallway getHallRight(int length) {
        Point templL = new Point(getuR().getX(), ((getuR().getY() + getlL().getY()) / 2) - 1);
        Point tempuR = new Point(getuR().getX() + length, templL.getY() + 2);
        return new Hallway(templL, tempuR);
    }

    public boolean markedUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean markedDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean markedLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean markedRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public Point getlL() {
        return lL;
    }

    public Point getuR() {
        return uR;
    }

    public static void main(String[] args) {
        Room test = new Room(new Point(48, 6), new Point(50, 9)); //other
        Room test2 = new Room(new Point(48, 7), new Point(50, 11)); //this
        System.out.println(test2.overlaps(test));
    }
}
