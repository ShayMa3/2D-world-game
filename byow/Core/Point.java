package byow.Core;

import java.io.Serializable;

public class Point implements Serializable {

    private int xPos;
    private int yPos;

    public Point(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public void setX(int xXPos) {
        this.xPos = xXPos;
    }

    public void setY(int yYPos) {
        this.yPos = yYPos;
    }

}
