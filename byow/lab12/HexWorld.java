package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);


    public static void addHexagon(int side, int xPos, int yPos, TETile[][] tiles) {
        int rowLength = side;
        int midLength = midLength(side);

        while (rowLength <= midLength) {
            for (int i = xPos; i < xPos + rowLength; i++) {
                tiles[i][yPos] = TETile.colorVariant(Tileset.SAND, 10, 100, 100, RANDOM);
            }
            rowLength += 2;
            xPos -= 1;
            yPos -= 1;
        }

        rowLength -= 2;
        xPos += 1;
        while (rowLength >= side) {
            for (int i = xPos; i < xPos + rowLength; i++) {
                tiles[i][yPos] = TETile.colorVariant(Tileset.LOCKED_DOOR, 10, 100, 100, RANDOM);
            }
            rowLength -= 2;
            xPos += 1;
            yPos -= 1;
        }
    }

    private static int midLength(int length) {
        return length + (length - 1) * 2;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        addHexagon(10, 25, 25, world);

        ter.renderFrame(world);
    }


}
